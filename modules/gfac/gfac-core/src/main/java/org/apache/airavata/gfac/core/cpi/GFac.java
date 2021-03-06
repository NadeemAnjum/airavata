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
package org.apache.airavata.gfac.core.cpi;

import org.apache.airavata.gfac.GFacException;
import org.apache.airavata.gfac.core.context.JobExecutionContext;

/**
 * This is the GFac CPI interface which needs to be implemented by an internal class, this simply have a single method to submit a job to
 * the resource, required data for the job has to be stored in registry prior to invoke this object.
 */
public interface GFac {

    /**
     * This is the job launching method outsiders of GFac can use, this will invoke the GFac handler chain and providers
     * And update the registry occordingly, so the users can query the database to retrieve status and output from Registry
     *
     * @param experimentID
     * @return boolean Successful acceptence of the jobExecution returns a true value
     * @throws org.apache.airavata.gfac.GFacException
     */
    public boolean submitJob(String experimentID,String taskID, String gatewayID) throws GFacException;

    /**
     * This method can be used in a handler to ivvoke outhandler asynchronously
     * @param jobExecutionContext
     * @throws GFacException
     */
    public void invokeOutFlowHandlers(JobExecutionContext jobExecutionContext) throws GFacException;

    /**
     * This method can be used to handle re-run case asynchronously
     * @param jobExecutionContext
     * @throws GFacException
     */
    public void reInvokeOutFlowHandlers(JobExecutionContext jobExecutionContext) throws GFacException;

    /**
     * This operation can be used to cancel an already running experiment
     * @param jobExecutionContext
     * @return Successful cancellation will return true
     * @throws GFacException
     */
    public boolean cancel(JobExecutionContext jobExecutionContext)throws GFacException;

}
