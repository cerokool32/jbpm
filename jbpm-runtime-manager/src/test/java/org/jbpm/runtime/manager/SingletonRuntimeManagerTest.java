package org.jbpm.runtime.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.Runtime;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.UserGroupCallback;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class SingletonRuntimeManagerTest {
    
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
    }
    
    @After
    public void teardown() {
        pds.close();
    }

    @Test
    public void testCreationOfSession() {
        SimpleRuntimeEnvironment environment = new SimpleRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 0);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();       
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
    }
    
    @Test
    public void testCreationOfSessionWithPersistence() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
    }
    
    @Test
    public void testReCreationOfSessionWithPersistence() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
        
        // recreate it once again to ensure it has right id
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
    }
    
    @Test
    public void testCreationOfMultipleSingletonManagerWithPersistence() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        // create first manager
        //-----------------------------------------
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager1");        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
        
        // create another manager
        //-----------------------------------------
        RuntimeManager manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager2");        
        assertNotNull(manager2);
        
        runtime = manager2.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getId();
        assertTrue(sessionId == 2);
        
        runtime = manager2.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager2.disposeRuntime(runtime);
        
        ksession = manager2.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager2.close();
        
        runtime = manager2.getRuntime(EmptyContext.get());
        assertNull(runtime);
        
        // recreate first manager
        //-----------------------------------------
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager1");        
        assertNotNull(manager);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
        
        // create another manager
        //-----------------------------------------
        manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager2");        
        assertNotNull(manager2);
        
        runtime = manager2.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getId();
        assertTrue(sessionId == 2);
        
        runtime = manager2.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager2.disposeRuntime(runtime);
        
        ksession = manager2.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager2.close();
        
        runtime = manager2.getRuntime(EmptyContext.get());
        assertNull(runtime);
    }
    
    @Test
    public void testCreationOfDuplicatedManagers() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        try {
            RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
            fail("Should fail as it's not allowed to have singleton manager with same identifier");
        } catch (IllegalStateException e) {
            
        }
        manager.close();
        
        // now it is possible to load the manager again
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        manager.close();
    }
}
