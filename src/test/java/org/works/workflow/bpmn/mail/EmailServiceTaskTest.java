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

package org.works.workflow.bpmn.mail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.test.Deployment;


/**
 * @author Joram Barrez
 */
public class EmailServiceTaskTest extends PluggableActivitiTestCase {
  
  /* Wiser is a fake email server for unit testing */
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  @Deployment
  public void testSendEmail() throws Exception {
    
    String from = "ordershipping@activiti.org";
    boolean male = true;
    String recipientName = "John Doe";
    String recipient = "johndoe@alfresco.com";
    Date now = new Date();
    String orderId = "123456";
    
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("sender", from);
    vars.put("recipient", recipient);
    vars.put("recipientName", recipientName);
    vars.put("male", male);
    vars.put("now", now);
    vars.put("orderId", orderId);
    
    runtimeService.startProcessInstanceByKey("sendMailExample", vars);
    
  }

}
