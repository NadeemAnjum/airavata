///*
//*
//* Licensed to the Apache Software Foundation (ASF) under one
//* or more contributor license agreements.  See the NOTICE file
//* distributed with this work for additional information
//* regarding copyright ownership.  The ASF licenses this file
//* to you under the Apache License, Version 2.0 (the
//* "License"); you may not use this file except in compliance
//* with the License.  You may obtain a copy of the License at
//*
//*   http://www.apache.org/licenses/LICENSE-2.0
//*
//* Unless required by applicable law or agreed to in writing,
//* software distributed under the License is distributed on an
//* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//* KIND, either express or implied.  See the License for the
//* specific language governing permissions and limitations
//* under the License.
//*
//*/
//
//package org.apache.airavata.persistance.registry.jpa;
//
//import org.apache.airavata.persistance.registry.jpa.resources.*;
//
//import java.sql.Timestamp;
//import java.util.Calendar;
//
//public class GFacJobDataResourceTest extends AbstractResourceTest {
//    private WorkerResource workerResource;
//    private WorkflowDataResource workflowDataResource;
//    private ExperimentMetadataResource  experimentResource;
//
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        GatewayResource gatewayResource = super.getGatewayResource();
//        workerResource = super.getWorkerResource();
//
//        experimentResource = (ExperimentMetadataResource) gatewayResource.create(ResourceType.EXPERIMENT_METADATA);
//        experimentResource.setExpID("testExpID");
//        experimentResource.setExperimentName("testExpID");
//        experimentResource.setExecutionUser(workerResource.getUser());
//        experimentResource.setProject(new ProjectResource(workerResource, gatewayResource, "testProject"));
//        experimentResource.save();
//
//        workflowDataResource = (WorkflowDataResource) experimentResource.create(ResourceType.WORKFLOW_DATA);
//        workflowDataResource.setWorkflowInstanceID("testWFInstance");
//        workflowDataResource.setTemplateName("testTemplate");
//        workflowDataResource.setExperimentID("testExpID");
//        Calendar calender = Calendar.getInstance();
//        java.util.Date d = calender.getTime();
//        Timestamp timestamp = new Timestamp(d.getTime());
//        workflowDataResource.setLastUpdatedTime(timestamp);
//        workflowDataResource.save();
//    }
//
//    @Override
//    public void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    public void testSave() throws Exception {
//        GFacJobDataResource resource = (GFacJobDataResource)workflowDataResource.create(ResourceType.GFAC_JOB_DATA);
//        resource.setLocalJobID("testJobID");
//        resource.setApplicationDescID("testApplication");
//        resource.setMetadataResource(experimentResource);
//        resource.setNodeID("testNode");
//        resource.setHostDescID("testHost");
//        resource.setServiceDescID("testService");
//        resource.setStatus("testStatus");
//        resource.setJobData("testJobData");
//        resource.save();
//        assertTrue("GFac job data saved successfully", workerResource.isGFacJobExists("testJobID"));
////        workflowDataResource.remove(ResourceType.GFAC_JOB_DATA, "testJobID");
//    }
//}
