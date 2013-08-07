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
package org.works.workflow.bpmn.scripttask;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;

/**
 * @author Joram Barrez
 * @author Christian Stettler
 */
public class ScriptTaskTest extends PluggableActivitiTestCase {

  @Deployment
  public void testSetScriptResultToProcessVariable() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("echo", "hello");
    variables.put("existingProcessVariableName", "one");

    ProcessInstance pi = runtimeService.startProcessInstanceByKey("setScriptResultToProcessVariable", variables);

    assertEquals("hello", runtimeService.getVariable(pi.getId(), "existingProcessVariableName"));
    assertEquals("hello", runtimeService.getVariable(pi.getId(), "newProcessVariableName"));
  }
}
