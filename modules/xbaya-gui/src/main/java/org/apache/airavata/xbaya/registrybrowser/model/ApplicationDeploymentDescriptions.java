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

package org.apache.airavata.xbaya.registrybrowser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.airavata.commons.gfac.type.ApplicationDeploymentDescription;
import org.apache.airavata.registry.api.Registry;
import org.apache.airavata.registry.api.exception.RegistryException;

public class ApplicationDeploymentDescriptions {
    private Registry registry;

    public ApplicationDeploymentDescriptions(Registry registry) {
        setRegistry(registry);
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public List<ApplicationDeploymentDescriptionWrap> getDescriptions() throws RegistryException {
        List<ApplicationDeploymentDescriptionWrap> list = new ArrayList<ApplicationDeploymentDescriptionWrap>();
        Map<ApplicationDeploymentDescription, String> deploymentDescriptions = getRegistry()
                .searchDeploymentDescription();
        for (ApplicationDeploymentDescription descriptionWrap : deploymentDescriptions.keySet()) {
            String[] descDetails = deploymentDescriptions.get(descriptionWrap).split("\\$");
            list.add(new ApplicationDeploymentDescriptionWrap(getRegistry(), descriptionWrap, descDetails[0],
                    descDetails[1]));
        }
        return list;
    }
}