/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.jbpm.bpmn2.handler;

import java.util.*;

import org.jbpm.bpmn2.JbpmTestCase;
import org.jbpm.bpmn2.handler.LoggingTaskHandlerWrapper.InputParameter;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class LoggingTaskHandlerWrapperTest extends JbpmTestCase {
    
    private StatefulKnowledgeSession ksession;
    
    public LoggingTaskHandlerWrapperTest() {
        super(false);
    }

    @After
    public void dispose() {
        if (ksession != null) {
            ksession.dispose();
            ksession = null;
        }
    }

    @Test
    public void testLimitExceptionInfoList() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionThrowingServiceProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        
        LoggingTaskHandlerWrapper loggingTaskHandlerWrapper = new LoggingTaskHandlerWrapper(ServiceTaskHandler.class, 2);
        loggingTaskHandlerWrapper.setPrintStackTrace(false);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", loggingTaskHandlerWrapper);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serviceInputItem", "exception message");
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);

        int size = loggingTaskHandlerWrapper.getWorkItemExceptionInfoList().size(); 
        assertTrue( "WorkItemExceptionInfoList is too large: " + size, size == 2 );
    }
    
    @Test
    public void testFormatLoggingError() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionThrowingServiceProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        
        LoggingTaskHandlerWrapper loggingTaskHandlerWrapper = new LoggingTaskHandlerWrapper(ServiceTaskHandler.class, 2);
        loggingTaskHandlerWrapper.setLoggedMessageFormat("{0} - {1} - {2} - {3}");
        List<InputParameter> inputParameters = new ArrayList<LoggingTaskHandlerWrapper.InputParameter>();
        inputParameters.add(InputParameter.EXCEPTION_CLASS);
        inputParameters.add(InputParameter.WORK_ITEM_ID);
        inputParameters.add(InputParameter.WORK_ITEM_NAME);
        inputParameters.add(InputParameter.PROCESS_INSTANCE_ID);
        
        loggingTaskHandlerWrapper.setLoggedMessageInput(inputParameters);
        
        loggingTaskHandlerWrapper.setPrintStackTrace(false);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", loggingTaskHandlerWrapper);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serviceInputItem", "exception message");
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);
        ksession.startProcess("ServiceProcess", params);
    }

}
