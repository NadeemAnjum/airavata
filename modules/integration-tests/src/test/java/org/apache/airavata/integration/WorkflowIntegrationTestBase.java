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

package org.apache.airavata.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.airavata.api.Airavata;
import org.apache.airavata.api.client.AiravataClientFactory;
import org.apache.airavata.model.error.AiravataClientConnectException;
import org.apache.airavata.model.error.AiravataClientException;
import org.apache.airavata.model.error.AiravataSystemException;
import org.apache.airavata.model.error.ExperimentNotFoundException;
import org.apache.airavata.model.error.InvalidRequestException;
import org.apache.airavata.client.AiravataAPIFactory;
import org.apache.airavata.client.api.AiravataAPI;
import org.apache.airavata.client.api.ApplicationManager;
import org.apache.airavata.client.api.ExperimentAdvanceOptions;
import org.apache.airavata.client.api.exception.AiravataAPIInvocationException;
import org.apache.airavata.client.api.exception.WorkflowAlreadyExistsException;
import org.apache.airavata.common.utils.ServerSettings;
import org.apache.airavata.common.utils.StringUtil;
import org.apache.airavata.commons.gfac.type.ApplicationDescription;
import org.apache.airavata.commons.gfac.type.HostDescription;
import org.apache.airavata.commons.gfac.type.ServiceDescription;
import org.apache.airavata.model.workspace.experiment.Experiment;
import org.apache.airavata.registry.api.impl.WorkflowExecutionDataImpl;
import org.apache.airavata.registry.api.workflow.ExperimentData;
import org.apache.airavata.registry.api.workflow.InputData;
import org.apache.airavata.registry.api.workflow.NodeExecutionData;
import org.apache.airavata.registry.api.workflow.OutputData;
import org.apache.airavata.registry.api.workflow.WorkflowNodeType;
import org.apache.airavata.workflow.model.component.ComponentException;
import org.apache.airavata.workflow.model.graph.GraphException;
import org.apache.airavata.workflow.model.wf.Workflow;
import org.apache.airavata.workflow.model.wf.WorkflowInput;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Since most of the workflow integration tests have common functionality to register, start and monitor workflows, this
 * class will act as the aggregation of those methods.
 */
public abstract class WorkflowIntegrationTestBase {

    protected final Logger log = LoggerFactory.getLogger(WorkflowIntegrationTestBase.class);

    protected int port;
    protected String serverUrl;
    protected String serverContextName;
    protected String gatewayName = "default";
    protected String userName = "admin";
    protected String password = "admin";
    private static final String MYPROXY_USER_NAME = "myproxy.user";
    private static final String MYPROXY_PWD = "myproxy.pass";
    private static final String CERT_LOCATION = "trusted.cert.location";


    public static final String THRIFT_SERVER_HOST = "localhost";
    public static final int THRIFT_SERVER_PORT = 8930;

    protected static final int TIME_OUT = 20000;
    protected static final int TRIES = 3;
    protected AiravataAPI airavataAPI;
    protected Airavata.Client client;
    protected ApplicationManager applicationManager;

    protected void log(String message) {
        log.info(message);
    }

    public Airavata.Client getClient() throws AiravataClientConnectException {
        if (client == null){
            client = AiravataClientFactory.createAiravataClient(THRIFT_SERVER_HOST, THRIFT_SERVER_PORT);
        }
        return client;
    }

    public Logger getLog() {
        return log;
    }

    public int getPort() {
        return port;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getServerContextName() {
        return serverContextName;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public String getUserName() {
        return userName;
    }

    public AiravataAPI getAiravataAPI() throws AiravataAPIInvocationException {
        if (airavataAPI == null){
            airavataAPI = AiravataAPIFactory.getAPI(getGatewayName(), getUserName());
        }
        return airavataAPI;
    }

    public String getPassword() {
        return password;
    }

    public void setUpEnvironment() throws Exception {

        log("..................Validating myproxy username, password set properly .............................");
        System.setProperty("myproxy.user", "ogce");
        System.setProperty("myproxy.password", "/Users/chathuri/dev/airavata/cert/certificates");
        System.setProperty("trusted.cert.location", "/Users/chathuri/dev/airavata/cert/certificates");
        String myProxyUserName = System.getProperty("myproxy.user");
        String myProxyPassword = System.getProperty("myproxy.password");
        String certLocation = System.getProperty("trusted.cert.location");
        if (myProxyUserName == null || myProxyPassword == null || certLocation == null ) {
            log.error(">>>>>> Please run tests with my proxy user name, password and grid cert location. " +
                    "E.g :- mvn clean install -Duser=xxx -Dpwd=xxx -Dcert=/path<<<<<<<");
            throw new Exception("Need my proxy user name password to run tests.");
        }else {
            ServerSettings.setSetting(MYPROXY_USER_NAME, myProxyUserName);
            ServerSettings.setSetting(MYPROXY_PWD, myProxyPassword);
            ServerSettings.setSetting(CERT_LOCATION, certLocation);
        }

    }

    protected String createRegistryURL() {
        log("Reading test server configurations ...");

        String strPort = System.getProperty("test.server.port");

        if (strPort == null) {
            strPort = "8080";
        }

        String strHost = System.getProperty("test.server.url");

        if (strHost == null) {
            strHost = "localhost";
        }

        String strContext = System.getProperty("test.server.context");

        if (strContext == null) {
            strContext = "airavata";
        }

        port = Integer.parseInt(strPort);
        serverUrl = strHost;
        serverContextName = strContext;

        log("Configurations - port : " + port);
        log("Configurations - serverUrl : " + serverUrl);
        log("Configurations - serverContext : " + serverContextName);

        String registryURL = "http://" + serverUrl + ":" + port + "/" + serverContextName + "/services/registry";
        return registryURL;
    }

//    protected void checkServerStartup(AiravataAPI airavataAPI) throws Exception {
//
//        int tries = 0;
//
//        while (true) {
//
//            if (tries == TRIES) {
//                log("Server not responding. Cannot continue with integration tests ...");
//                throw new Exception("Server not responding !");
//            }
//
//            log("Checking server is running, try - " + tries);
//
//            URI eventingServiceURL = airavataAPI.getAiravataManager().getEventingServiceURL();
//
//            URI messageBoxServiceURL = airavataAPI.getAiravataManager().getMessageBoxServiceURL();
//
//            URI workflowInterpreterServiceURL = airavataAPI.getAiravataManager().getWorkflowInterpreterServiceURL();
//
//            if (eventingServiceURL == null || messageBoxServiceURL == null
//                    || workflowInterpreterServiceURL == null) {
//
//                log.info("Waiting till server initializes ........");
//                Thread.sleep(TIME_OUT);
//            } else {
//                break;
//            }
//
//            ++tries;
//        }
//
//    }

    protected String createExperiment (Experiment experiment) throws AiravataSystemException, InvalidRequestException, AiravataClientException, TException, AiravataClientConnectException {
        return getClient().createExperiment(experiment);
    }

    protected void launchExperiment (String expId) throws ExperimentNotFoundException, AiravataSystemException, InvalidRequestException, AiravataClientException, TException, AiravataClientConnectException {
        getClient().launchExperiment(expId, "testToken");
    }

    protected void executeExperiment(String workflowFilePath,
                                     List<String> inputs, List<String> outputs) throws GraphException,
            ComponentException, IOException, WorkflowAlreadyExistsException,
            AiravataAPIInvocationException, Exception {
        log("Saving workflow ...");

        Workflow workflow = new Workflow(getWorkflowComposeContent(workflowFilePath));
        if (!airavataAPI.getWorkflowManager().isWorkflowExists(workflow.getName())) {
            airavataAPI.getWorkflowManager().addWorkflow(workflow);
        }
        Assert.assertTrue(airavataAPI.getWorkflowManager().isWorkflowExists(workflow.getName()));

        log("Workflow setting up completed ...");

//        executeWorkflow(workflow, inputs, outputs);
    }


    protected void executeWorkflow(Workflow workflow, List<String> inputValues, List<String> outputValue) throws Exception {
        String experimentId = executeWorkflow(workflow, inputValues);
        airavataAPI.getExecutionManager().waitForExperimentTermination(experimentId);
        verifyOutput(experimentId, outputValue);
    }

    protected String executeWorkflow(Workflow workflow, List<String> inputValues) throws Exception {
        List<WorkflowInput> workflowInputs = setupInputs(workflow, inputValues);
        String workflowName = workflow.getName();
        ExperimentAdvanceOptions options = getAiravataAPI().getExecutionManager().createExperimentAdvanceOptions(
                workflowName, getUserName(), null);

        options.getCustomSecuritySettings().getCredentialStoreSecuritySettings().setTokenId("1234");

        String experimentId = getAiravataAPI().getExecutionManager().runExperiment(workflowName, workflowInputs, options);

        Assert.assertNotNull(experimentId);

        log.info("Run workflow completed ....");
        log.info("Starting monitoring ....");
        return experimentId;
    }


    protected void verifyOutput(String experimentId, List<String> outputVerifyingString) throws Exception {
        log.info("Experiment ID Returned : " + experimentId);

        ExperimentData experimentData = getAiravataAPI().getProvenanceManager().getExperimentData(experimentId);

        log.info("Verifying output ...");

        List<WorkflowExecutionDataImpl> workflowInstanceData = experimentData.getWorkflowExecutionDataList();

        Assert.assertFalse("Workflow instance data cannot be empty !", workflowInstanceData.isEmpty());

        for (WorkflowExecutionDataImpl data : workflowInstanceData) {
            List<NodeExecutionData> nodeDataList = data.getNodeDataList(WorkflowNodeType.WorkflowNode.OUTPUTNODE);
            Assert.assertFalse("Node execution data list cannot be empty !", nodeDataList.isEmpty());
            for (NodeExecutionData nodeData : nodeDataList) {
                for (InputData inputData : nodeData.getInputData()) {
                    String[] outputValues = StringUtil.getElementsFromString(inputData.getValue());
                    Assert.assertEquals(outputVerifyingString.size(), outputValues.length);
                    for (int i = 0; i < outputValues.length; i++) {
                        Assert.assertEquals(outputVerifyingString.get(i), outputValues[i]);
                    }
                }
            }
        }
    }

    protected void verifyOutput(String experimentId, String outputVerifyingString) throws Exception {
        log.info("Experiment ID Returned : " + experimentId);

        ExperimentData experimentData = getAiravataAPI().getProvenanceManager().getExperimentData(experimentId);

        log.info("Verifying output ...");

        List<WorkflowExecutionDataImpl> workflowInstanceData = experimentData.getWorkflowExecutionDataList();

        Assert.assertFalse("Workflow instance data cannot be empty !", workflowInstanceData.isEmpty());

        for (WorkflowExecutionDataImpl data : workflowInstanceData) {
            List<NodeExecutionData> nodeDataList = data.getNodeDataList();
            for (NodeExecutionData nodeData : nodeDataList) {

                Assert.assertFalse("Node execution data list cannot be empty !", nodeDataList.isEmpty());

                for (OutputData outputData : nodeData.getOutputData()) {
                    Assert.assertEquals("Airavata_Test", outputData.getValue());
                }
                for (InputData inputData : nodeData.getInputData()) {
                    Assert.assertEquals(outputVerifyingString, inputData.getValue());
                }
            }
        }
    }

    protected List<WorkflowInput> setupInputs(Workflow workflow, List<String> inputValues) throws Exception {
        List<WorkflowInput> workflowInputs = getAiravataAPI().getWorkflowManager().getWorkflowInputs(workflow.getName());

        Assert.assertEquals(workflowInputs.size(), inputValues.size());

        int i = 0;
        for (String valueString : inputValues) {
            workflowInputs.get(i).setValue(valueString);
            ++i;
        }
        return workflowInputs;
    }

    protected String getWorkflowComposeContent(String fileName) throws IOException {
        File file = getFile(fileName);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder buffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        log.debug("Workflow compose - " + buffer.toString());
        return buffer.toString();
    }

    protected File getFile(String fileName) {
        File f = new File(".");
        log.debug(f.getAbsolutePath());

        File file = new File(fileName);
        if (!file.exists()) {
            file = new File("modules/integration-tests/" + fileName);
        }
        return file;
    }

    /*
    * When running the tests multiple times in the same server, when the application, Host, service descriptions are
    * there already the tests fail. But using these functions for the tests prevents that from happening.
    * */
    protected void addHostDescriptor(HostDescription hostDescription) throws AiravataAPIInvocationException {
        applicationManager = getAiravataAPI().getApplicationManager();
        String hostName = hostDescription.getType().getHostName();
        if (!applicationManager.isHostDescriptorExists(hostName)){
            applicationManager.addHostDescription(hostDescription);
        }
    }

    protected void addServiceDescriptor(ServiceDescription serviceDescription, String serviceName) throws AiravataAPIInvocationException {
        applicationManager = getAiravataAPI().getApplicationManager();
        if (!applicationManager.isServiceDescriptorExists(serviceName)){
            applicationManager.addServiceDescription(serviceDescription);
        }
    }

    protected void addApplicationDescriptor(ApplicationDescription applicationDescription, ServiceDescription serviceDescription, HostDescription hostDescription, String appeName) throws AiravataAPIInvocationException {
        boolean descriptorExists = applicationManager.isApplicationDescriptorExists(serviceDescription.getType().getName(), hostDescription.getType().getHostName(), appeName);
        if (!descriptorExists) {
            applicationManager.addApplicationDescription(serviceDescription, hostDescription,
                    applicationDescription);
        }
    }

}
