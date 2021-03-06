/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

package org.apache.airavata.orchestrator.client;

import org.apache.airavata.client.AiravataAPIFactory;
import org.apache.airavata.client.api.AiravataAPI;
import org.apache.airavata.client.api.exception.AiravataAPIInvocationException;
import org.apache.airavata.client.tools.DocumentCreator;
import org.apache.airavata.common.exception.ApplicationSettingsException;
import org.apache.airavata.common.utils.AiravataUtils;
import org.apache.airavata.common.utils.AiravataZKUtils;
import org.apache.airavata.common.utils.Constants;
import org.apache.airavata.common.utils.ServerSettings;
import org.apache.airavata.orchestrator.client.util.Initialize;
import org.apache.airavata.orchestrator.cpi.OrchestratorService;
import org.apache.airavata.orchestrator.server.OrchestratorServer;
import org.apache.airavata.persistance.registry.jpa.impl.RegistryFactory;
import org.apache.airavata.registry.cpi.Registry;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.junit.Test;

public class OrchestratorClientFactoryTest {
    private DocumentCreator documentCreator;
    private OrchestratorService.Client orchestratorClient;
    private Registry registry;
    private int NUM_CONCURRENT_REQUESTS = 1;
    Initialize initialize;
    OrchestratorServer service;
    private static ServerCnxnFactory cnxnFactory;

    @Test
    public void setUp() {
    	AiravataUtils.setExecutionAsServer();
        initialize = new Initialize("registry-derby.sql");
        initialize.initializeDB();
        System.setProperty(Constants.ZOOKEEPER_SERVER_PORT,"2185");
        AiravataZKUtils.startEmbeddedZK(cnxnFactory);

        try {
            service = (new OrchestratorServer());
            service.start();
            registry = RegistryFactory.getDefaultRegistry();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        AiravataUtils.setExecutionAsServer();
        documentCreator = new DocumentCreator(getAiravataAPI());
        documentCreator.createLocalHostDocs();

        try {
            service.stop();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private AiravataAPI getAiravataAPI() {
        AiravataAPI airavataAPI = null;
            try {
                String systemUserName = ServerSettings.getDefaultUser();
                String gateway = ServerSettings.getDefaultUserGateway();
                airavataAPI = AiravataAPIFactory.getAPI(gateway, systemUserName);
            } catch (ApplicationSettingsException e) {
                e.printStackTrace();
            } catch (AiravataAPIInvocationException e) {
                e.printStackTrace();
            }
        return airavataAPI;
    }

    private void storeDescriptors() {

    }
}
