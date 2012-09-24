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

package org.apache.airavata.client.api;

import java.net.URI;
import java.util.List;

/**
 *  This interface contains set of methods to access the basic configurations of AiravataClient required to connect to
 *  Airvata services running at the backend
 */
public interface AiravataManager {
	
    /**
     * Return a list of GFaC service urls registered in the system
     * @return 0 or more urls
     * @throws AiravataAPIInvocationException
     */
	public List<URI> getGFaCURLs() throws AiravataAPIInvocationException;

    /**
     * Return a the first GFaC service url registered in the system
     * @return url or null if a url is not registered
     * @throws AiravataAPIInvocationException
     */
	public URI getGFaCURL() throws AiravataAPIInvocationException;

    /**
     * Return a the first GFaC service url registered in the system orelse return the <code>defaultURL</code>
     * @param defaultURL
     * @return url
     * @throws AiravataAPIInvocationException
     */
	public URI getGFaCURL(URI defaultURL) throws AiravataAPIInvocationException;

    /**
     * Return a list of Workflow Interpreter service urls registered in the system
     * @return 0 or more urls
     * @throws AiravataAPIInvocationException
     */
	public List<URI> getWorkflowInterpreterServiceURLs() throws AiravataAPIInvocationException;

    /**
     * Return a the first Workflow interpreter service url registered in the system
     * @return url or null if a url is not registered
     * @throws AiravataAPIInvocationException
     */
	public URI getWorkflowInterpreterServiceURL() throws AiravataAPIInvocationException;

    /**
     * Return a the first GFaC service url registered in the system orelse return the <code>defaultURL</code>
     * @param defaultURL
     * @return url
     * @throws AiravataAPIInvocationException
     */
	public URI getWorkflowInterpreterServiceURL(URI defaultURL) throws AiravataAPIInvocationException;

    /**
     * Return a list of Workflow Interpreter service urls registered in the system
     * @return 0 or more urls
     * @throws AiravataAPIInvocationException
     */
	public List<URI> getMessageBoxServiceURLs() throws AiravataAPIInvocationException;

    /**
     * Return a the first Workflow interpreter service url registered in the system
     * @return url or null if a url is not registered
     * @throws AiravataAPIInvocationException
     */
	public URI getMessageBoxServiceURL() throws AiravataAPIInvocationException;

    /**
     * Return a the first GFaC service url registered in the system orelse return the <code>defaultURL</code>
     * @param defaultURL
     * @return url
     * @throws AiravataAPIInvocationException
     */
	public URI getMessageBoxServiceURL(URI defaultURL) throws AiravataAPIInvocationException;

    /**
     * Return a list of Workflow Interpreter service urls registered in the system
     * @return 0 or more urls
     * @throws AiravataAPIInvocationException
     */
	public List<URI> getEventingServiceURLs() throws AiravataAPIInvocationException;

    /**
     * Return a the first Workflow interpreter service url registered in the system
     * @return url or null if a url is not registered
     * @throws AiravataAPIInvocationException
     */
	public URI getEventingServiceURL() throws AiravataAPIInvocationException;

    /**
     * Return a the first GFaC service url registered in the system orelse return the <code>defaultURL</code>
     * @param defaultURL
     * @return url
     * @throws AiravataAPIInvocationException
     */
	public URI getEventingServiceURL(URI defaultURL) throws AiravataAPIInvocationException;

    /**
     * Return the Airavata registry url 
     * @return url or null if url is not defined
     * @throws AiravataAPIInvocationException
     */
	public URI getRegistryURL() throws AiravataAPIInvocationException;

    /**
     * Return the Airavata registry url or return default url
     * @param defaultURL
     * @return url
     * @throws AiravataAPIInvocationException
     */
	public URI getRegistryURL(URI defaultURL) throws AiravataAPIInvocationException;
}