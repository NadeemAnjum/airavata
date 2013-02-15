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
package org.apache.airavata.core.gfac.services.impl;

import com.amazonaws.services.importexport.model.JobType;
import org.apache.airavata.client.AiravataAPIFactory;
import org.apache.airavata.client.api.AiravataAPI;
import org.apache.airavata.commons.gfac.type.*;
import org.apache.airavata.gfac.Constants;
import org.apache.airavata.gfac.GFacAPI;
import org.apache.airavata.gfac.GFacConfiguration;
import org.apache.airavata.gfac.GFacException;
import org.apache.airavata.gfac.context.ApplicationContext;
import org.apache.airavata.gfac.context.JobExecutionContext;
import org.apache.airavata.gfac.context.MessageContext;
import org.apache.airavata.schemas.gfac.*;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.*;

public class GramProviderTest {
    private JobExecutionContext jobExecutionContext;
    @Before
    public void setUp() throws Exception {
        URL resource = GramProviderTest.class.getClassLoader().getResource("gfac-config.xml");
        System.out.println(resource.getFile());
        GFacConfiguration gFacConfiguration = GFacConfiguration.create(new File(resource.getPath()),null,null);
        gFacConfiguration.setMyProxyLifeCycle(3600);
        gFacConfiguration.setMyProxyServer("myproxy.teragrid.org");
        gFacConfiguration.setMyProxyUser("ogce");
        gFacConfiguration.setMyProxyPassphrase("");
        gFacConfiguration.setTrustedCertLocation("/Users/lahirugunathilake/Downloads/certificates");
        //have to set InFlwo Handlers and outFlowHandlers
//        gFacConfiguration.setInHandlers(Arrays.asList(new String[] {"org.apache.airavata.gfac.handler.GramDirectorySetupHandler","org.apache.airavata.gfac.handler.GridFTPInputHandler"}));
//        gFacConfiguration.setOutHandlers(Arrays.asList(new String[] {"org.apache.airavata.gfac.handler.GridFTPOutputHandler"}));

        /*
           * Host
           */
        HostDescription host = new HostDescription(GlobusHostType.type);
        host.getType().setHostAddress("ranger.tacc.teragrid.org");
        host.getType().setHostName("ranger");
        ((GlobusHostType)host.getType()).setGlobusGateKeeperEndPointArray(new String[]{"gatekeeper.ranger.tacc.teragrid.org:2119/jobmanager-sge"});
        ((GlobusHostType)host.getType()).setGridFTPEndPointArray(new String[]{"gsiftp://gridftp.ranger.tacc.teragrid.org:2811/"});
        /*
           * App
           */
        ApplicationDescription appDesc = new ApplicationDescription(HpcApplicationDeploymentType.type);
        HpcApplicationDeploymentType app = (HpcApplicationDeploymentType)appDesc.getType();
        ApplicationDeploymentDescriptionType.ApplicationName name = ApplicationDeploymentDescriptionType.ApplicationName.Factory.newInstance();
        name.setStringValue("EchoLocal");
        app.setApplicationName(name);
        ProjectAccountType projectAccountType = app.addNewProjectAccount();
        projectAccountType.setProjectAccountNumber("TG-AST110064");

        QueueType queueType = app.addNewQueue();
        queueType.setQueueName("development");

        app.setCpuCount(1);
        app.setJobType(JobTypeType.SERIAL);
        app.setNodeCount(1);
        app.setProcessorsPerNode(1);

        /*
           * Use bat file if it is compiled on Windows
           */
        app.setExecutableLocation("/bin/echo");

        /*
           * Default tmp location
           */
        String tempDir = "/scratch/01437/ogce/test";
        String date = (new Date()).toString();
        date = date.replaceAll(" ", "_");
        date = date.replaceAll(":", "_");

        tempDir = tempDir + File.separator
                + "SimpleEcho" + "_" + date + "_" + UUID.randomUUID();

        System.out.println(tempDir);
        app.setScratchWorkingDirectory(tempDir);
        app.setStaticWorkingDirectory(tempDir);
        app.setInputDataDirectory(tempDir + File.separator + "inputData");
        app.setOutputDataDirectory(tempDir + File.separator + "outputData");
        app.setStandardOutput(tempDir + File.separator + app.getApplicationName().getStringValue() + ".stdout");
        app.setStandardError(tempDir + File.separator + app.getApplicationName().getStringValue() + ".stderr");


        /*
           * Service
           */
        ServiceDescription serv = new ServiceDescription();
        serv.getType().setName("SimpleEcho");

        List<InputParameterType> inputList = new ArrayList<InputParameterType>();
        InputParameterType input = InputParameterType.Factory.newInstance();
        input.setParameterName("echo_input");
        input.setParameterType(StringParameterType.Factory.newInstance());
        inputList.add(input);
        InputParameterType[] inputParamList = inputList.toArray(new InputParameterType[inputList
                .size()]);

        List<OutputParameterType> outputList = new ArrayList<OutputParameterType>();
        OutputParameterType output = OutputParameterType.Factory.newInstance();
        output.setParameterName("echo_output");
        output.setParameterType(StringParameterType.Factory.newInstance());
        outputList.add(output);
        OutputParameterType[] outputParamList = outputList
                .toArray(new OutputParameterType[outputList.size()]);

        serv.getType().setInputParametersArray(inputParamList);
        serv.getType().setOutputParametersArray(outputParamList);

        jobExecutionContext = new JobExecutionContext(gFacConfiguration,serv.getType().getName());
        ApplicationContext applicationContext = new ApplicationContext();
        jobExecutionContext.setApplicationContext(applicationContext);
        applicationContext.setServiceDescription(serv);
        applicationContext.setApplicationDeploymentDescription(appDesc);
        applicationContext.setHostDescription(host);

        MessageContext inMessage = new MessageContext();
        ActualParameter echo_input = new ActualParameter();
        ((StringParameterType)echo_input.getType()).setValue("echo_output=hello");
        inMessage.addParameter("echo_input", echo_input);

        jobExecutionContext.setInMessageContext(inMessage);

        MessageContext outMessage = new MessageContext();
        ActualParameter echo_out = new ActualParameter();
//		((StringParameterType)echo_input.getType()).setValue("echo_output=hello");
        outMessage.addParameter("echo_output", echo_out);

        jobExecutionContext.setOutMessageContext(outMessage);

    }

    @Test
    public void testGramProvider() throws GFacException {
//        GFacAPI gFacAPI = new GFacAPI();
//        gFacAPI.submitJob(jobExecutionContext);
//        MessageContext outMessageContext = jobExecutionContext.getOutMessageContext();
//        Assert.assertEquals(MappingFactory.toString((ActualParameter)outMessageContext.getParameter("echo_output")), "hello");
    }
}