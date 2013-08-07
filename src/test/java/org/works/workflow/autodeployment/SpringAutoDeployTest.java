/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.works.workflow.autodeployment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManagerFactory;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.icegreen.greenmail.util.GreenMail;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
@ContextConfiguration(locations = { "classpath*:workflow/applicationContext-workflow-registry.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class SpringAutoDeployTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	RepositoryService repositoryService;
	@Autowired
	GreenMail greenMail;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	@Qualifier("workEntityManager")
	EntityManagerFactory workEntityManager;

	protected void setUp() throws Exception {
		greenMail.start();
		javaMailSender.send(new MimeMessagePreparator() {

			@Override
			public void prepare(MimeMessage msg) throws Exception {
				Address address = new InternetAddress("cchu@localhost");
				msg.setFrom(address);
				msg.setRecipient(RecipientType.TO, address);
				msg.setSubject("Spring Mail Sending...");
				msg.setText("####################################\n************************************\n####################################");

			}
		});

	}

	protected void tearDown() throws Exception {
		removeAllDeployments();

		MimeMessage[] msgs = greenMail.getReceivedMessages();

		Assert.assertNull(msgs);

		for (MimeMessage msg : msgs) {
			try {
				System.out.println(msg.getSubject());
				System.out.println(msg.getContent());
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		greenMail.stop();

	}

	@Test
	public void testBasicActivitiSpringIntegration() {
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();

		Set<String> processDefinitionKeys = new HashSet<String>();
		for (ProcessDefinition processDefinition : processDefinitions) {
			processDefinitionKeys.add(processDefinition.getKey());
		}

		Set<String> expectedProcessDefinitionKeys = new HashSet<String>();
		expectedProcessDefinitionKeys.add("a");
		expectedProcessDefinitionKeys.add("b");
		expectedProcessDefinitionKeys.add("c");

		Assert.assertEquals(expectedProcessDefinitionKeys, processDefinitionKeys);
	}

	@Test
	public void testNoRedeploymentForSpringContainerRestart() throws Exception {
		DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
		Assert.assertEquals(1, deploymentQuery.count());
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		Assert.assertEquals(3, processDefinitionQuery.count());

		// Creating a new app context with same resources doesn't lead to more
		// deployments
		Assert.assertEquals(1, deploymentQuery.count());
		Assert.assertEquals(3, processDefinitionQuery.count());
	}

	// Updating the bpmn20 file should lead to a new deployment when restarting
	// the Spring container
	@Test
	public void testResourceRedeploymentAfterProcessDefinitionChange() throws Exception {
		Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
		((AbstractXmlApplicationContext) applicationContext).destroy();

		String filePath = "workflow/activiti/process/autodeployment/autodeploy.a.bpmn20.xml";
		String originalBpmnFileContent = IoUtil.readFileAsString(filePath);
		String updatedBpmnFileContent = originalBpmnFileContent.replace("flow1", "fromStartToEndFlow");
		Assert.assertTrue(updatedBpmnFileContent.length() > originalBpmnFileContent.length());
		IoUtil.writeStringToFile(updatedBpmnFileContent, filePath);

		// Classic produced/consumer problem here:
		// The file is already written in Java, but not yet completely persisted
		// by the OS
		// Constructing the new app context reads the same file which is
		// sometimes not yet fully written to disk
		waitUntilFileIsWritten(filePath, updatedBpmnFileContent.length());

		// Reset file content such that future test are not seeing something
		// funny
		IoUtil.writeStringToFile(originalBpmnFileContent, filePath);

		// Assertions come AFTER the file write! Otherwise the process file is
		// messed up if the assertions fail.
		Assert.assertEquals(2, repositoryService.createDeploymentQuery().count());
		Assert.assertEquals(6, repositoryService.createProcessDefinitionQuery().count());
	}

	@Test
	public void testAutoDeployWithCreateDropOnCleanDb() {
		Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
		Assert.assertEquals(3, repositoryService.createProcessDefinitionQuery().count());
	}

	// --Helper methods
	// ----------------------------------------------------------

	private void removeAllDeployments() {
		for (Deployment deployment : repositoryService.createDeploymentQuery().list()) {
			repositoryService.deleteDeployment(deployment.getId(), true);
		}
	}

	private boolean waitUntilFileIsWritten(String filePath, int expectedBytes) throws URISyntaxException {
		while (IoUtil.getFile(filePath).length() != (long) expectedBytes) {
			try {
				wait(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}