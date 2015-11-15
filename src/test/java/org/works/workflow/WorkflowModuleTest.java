package org.works.workflow;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.test.ActivitiRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@ContextConfiguration(locations = { "classpath:workflow/applicationContext-workflow-registry.xml" })
public class WorkflowModuleTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private FormService formService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ManagementService managementService;
	@Autowired
	public ActivitiRule activitiSpringRule;

	@Test
	public void testStartup() {
		Assert.notNull(repositoryService);
		Assert.notNull(runtimeService);
		Assert.notNull(formService);
		Assert.notNull(identityService);
		Assert.notNull(taskService);
		Assert.notNull(historyService);
		Assert.notNull(managementService);
		Assert.notNull(activitiSpringRule);
		
	}

}
