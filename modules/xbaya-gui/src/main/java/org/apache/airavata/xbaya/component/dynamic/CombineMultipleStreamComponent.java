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

package org.apache.airavata.xbaya.component.dynamic;

import org.apache.airavata.xbaya.graph.Graph;
import org.apache.airavata.xbaya.graph.dynamic.CombineMultipleStreamNode;

public class CombineMultipleStreamComponent extends CepComponent {

    public static final String NAME = "Combine_Stream";

    public CombineMultipleStreamComponent() {
        super(NAME);
    }

    public CombineMultipleStreamNode createNode(Graph graph) {
        CombineMultipleStreamNode node = new CombineMultipleStreamNode(graph);

        // Copy some infomation from the component

        node.setName(getName());
        node.setComponent(new CepComponent());

        // Creates a unique ID for the node. This has to be after setName().
        node.createID();

        // Creat ports
        createPorts(node);

        return node;
    }

}