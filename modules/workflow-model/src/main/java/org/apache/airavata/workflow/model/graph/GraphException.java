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

package org.apache.airavata.workflow.model.graph;

import org.apache.airavata.workflow.model.exceptions.WorkflowException;

/**
 * The GraphException class represents an Exception related to a Graph. Mostly, it is thrown when parsing a graph XML.
 * 
 */
public class GraphException extends WorkflowException {

    private static final long serialVersionUID = 1L;

    /**
     *  
     */
    public GraphException() {
        super();
    }

    /**
     * @param message
     */
    public GraphException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public GraphException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }

}