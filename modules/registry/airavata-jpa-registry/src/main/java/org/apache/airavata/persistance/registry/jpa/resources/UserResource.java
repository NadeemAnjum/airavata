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
package org.apache.airavata.persistance.registry.jpa.resources;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.airavata.common.utils.SecurityUtil;
import org.apache.airavata.persistance.registry.jpa.Resource;
import org.apache.airavata.persistance.registry.jpa.ResourceType;
import org.apache.airavata.persistance.registry.jpa.ResourceUtils;
import org.apache.airavata.persistance.registry.jpa.model.Users;
import org.apache.airavata.registry.api.exception.RegistrySettingsException;
import org.apache.airavata.registry.api.util.RegistrySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.airavata.registry.cpi.RegistryException;

import javax.persistence.EntityManager;

public class UserResource extends AbstractResource {
    private final static Logger logger = LoggerFactory.getLogger(UserResource.class);
    private String userName;
    private String password;
    /**
     *
     */
    public UserResource() {
    }

    /**
     *
     * @param userName user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return user name
     */
    public String getUserName() {
        return userName;
    }


    /**
     * User is a hypothical data structure.
     * @param type child resource type
     * @return child resource
     */
    public Resource create(ResourceType type) throws RegistryException {
        logger.error("Unsupported resource type for user resource.", new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param type child resource type
     * @param name child resource name
     */
    public void remove(ResourceType type, Object name) throws RegistryException{
        logger.error("Unsupported resource type for user resource.", new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param type child resource type
     * @param name child resource name
     * @return UnsupportedOperationException
     */
    public Resource get(ResourceType type, Object name) throws RegistryException {
        logger.error("Unsupported resource type for user resource.", new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param type child resource type
     * @return UnsupportedOperationException
     */
    public List<Resource> get(ResourceType type) throws RegistryException{
        logger.error("Unsupported resource type for user resource.", new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     * save user to the database
     */
    public void save() throws RegistryException {
        EntityManager em = null;
        try {
            em = ResourceUtils.getEntityManager();
            Users existingUser = em.find(Users.class, userName);
            em.close();

            em = ResourceUtils.getEntityManager();
            em.getTransaction().begin();
            Users user = new Users();
            user.setUser_name(userName);
            if (password != null && !password.equals("")) {
                try {
                    user.setPassword(SecurityUtil.digestString(password,
                            RegistrySettings.getSetting("default.registry.password.hash.method")));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("Error hashing default admin password. Invalid hash algorithm.", e);
                } catch (RegistrySettingsException e) {
                    throw new RuntimeException("Error reading hash algorithm from configurations", e);
                }
            }
            if (existingUser != null) {
                if (password != null && !password.equals("")) {
                    try {
                        existingUser.setPassword(SecurityUtil.digestString(password,
                                RegistrySettings.getSetting("default.registry.password.hash.method")));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException("Error hashing default admin password. Invalid hash algorithm.", e);
                    } catch (RegistrySettingsException e) {
                        throw new RuntimeException("Error reading hash algorithm from configurations", e);
                    }
                }
                user = em.merge(existingUser);
            } else {
                em.persist(user);
            }
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RegistryException(e);
        } finally {
            if (em != null && em.isOpen()) {
                if (em.getTransaction().isActive()){
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
    }

    /**
     *
     * @param type child resource type
     * @param name child resource name
     * @return UnsupportedOperationException
     */
    public boolean isExists(ResourceType type, Object name) throws RegistryException{
        logger.error("Unsupported resource type for user resource.", new UnsupportedOperationException());
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return  password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password  password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
