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

package org.apache.airavata.api.server.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.airavata.common.exception.ApplicationSettingsException;
import org.apache.airavata.common.utils.ServerSettings;
import org.apache.airavata.persistance.registry.jpa.ResourceType;
import org.apache.airavata.persistance.registry.jpa.ResourceUtils;
import org.apache.airavata.persistance.registry.jpa.resources.GatewayResource;
import org.apache.airavata.persistance.registry.jpa.resources.ProjectResource;
import org.apache.airavata.persistance.registry.jpa.resources.UserResource;
import org.apache.airavata.persistance.registry.jpa.resources.WorkerResource;
import org.apache.derby.drda.NetworkServerControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryInitUtil {
    public static final String CONFIGURATION_TABLE = "CONFIGURATION";
    private static final Logger logger = LoggerFactory.getLogger(RegistryInitUtil.class);
    public static final String REGISTRY_JDBC_DRIVER = "registry.jdbc.driver";
    public static final String REGISTRY_JDBC_URL = "registry.jdbc.url";
    public static final String REGISTRY_JDBC_USER = "registry.jdbc.user";
    public static final String REGISTRY_JDBC_PASSWORD = "registry.jdbc.password";
    public static final String START_DERBY_ENABLE = "start.derby.server.mode";
    public static final String DERBY_SERVER_MODE_SYS_PROPERTY = "derby.drda.startNetworkServer";
    public static final String DEFAULT_PROJECT_NAME = "default";
    private static NetworkServerControl server;
    private static JdbcStorage db;
    private static String jdbcURl;
    private static String jdbcDriver;
    private static String jdbcUser;
    private static String jdbcPassword;


    public static void initializeDB() {
        System.setProperty("registry.initialize.state", "0");
        try{
            jdbcDriver = ServerSettings.getSetting(REGISTRY_JDBC_DRIVER);
            jdbcURl = ServerSettings.getSetting(REGISTRY_JDBC_URL);
            jdbcUser = ServerSettings.getSetting(REGISTRY_JDBC_USER);
            jdbcPassword = ServerSettings.getSetting(REGISTRY_JDBC_PASSWORD);
            jdbcURl = jdbcURl + "?" + "user=" + jdbcUser + "&" + "password=" + jdbcPassword;
        } catch (ApplicationSettingsException e) {
            logger.error("Unable to read airavata server properties", e.getMessage());
        }

        if (getDBType(jdbcURl).equals("derby") && isDerbyStartEnabled()) {
            startDerbyInServerMode();
        }
        db = new JdbcStorage(10, 50, jdbcURl, jdbcDriver, true);

        Connection conn = null;
        try {
            conn = db.connect();
            if (!DatabaseCreator.isDatabaseStructureCreated(CONFIGURATION_TABLE, conn)) {
                DatabaseCreator.createRegistryDatabase("database_scripts/registry", conn);
                logger.info("New Database created for Registry");
            } else {
                logger.info("Database already created for Registry!");
            }
            try{
                GatewayResource gateway = (GatewayResource)ResourceUtils.createGateway(ServerSettings.getSystemUserGateway());
                gateway.save();
                UserResource user = ResourceUtils.createUser(ServerSettings.getSystemUser(), ServerSettings.getSystemUserPassword());
                user.save();
                WorkerResource workerResource = (WorkerResource)gateway.create(ResourceType.GATEWAY_WORKER);
                workerResource.setUser(user.getUserName());
                workerResource.save();
                ProjectResource projectResource = workerResource.createProject(DEFAULT_PROJECT_NAME);
                projectResource.setName(DEFAULT_PROJECT_NAME);
                projectResource.setGateway(gateway);
                projectResource.save();
            } catch (ApplicationSettingsException e) {
                logger.error("Unable to read airavata-server properties...", e.getMessage());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Database failure", e);
        } finally {
            db.closeConnection(conn);
            try {
                if(conn != null){
                    if (!conn.getAutoCommit()) {
                        conn.commit();
                    }
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Error while closing database connection...", e.getMessage(), e);
            }
        }
        System.setProperty("registry.initialize.state", "1");
    }

    public static String getDBType(String jdbcUrl){
        try{
            String cleanURI = jdbcUrl.substring(5);
            URI uri = URI.create(cleanURI);
            return uri.getScheme();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static boolean isDerbyStartEnabled(){
        try {
            String s = ServerSettings.getSetting(START_DERBY_ENABLE);
            if("true".equals(s)){
                return true;
            }
        }  catch (ApplicationSettingsException e) {
            logger.error("Unable to read airavata server properties", e.getMessage(), e);
            return false;
        }
        return false;
    }

    public static void startDerbyInServerMode() {
        try {
            System.setProperty(DERBY_SERVER_MODE_SYS_PROPERTY, "true");
            server = new NetworkServerControl(InetAddress.getByName("0.0.0.0"),
                    getPort(jdbcURl),
                    jdbcUser, jdbcPassword);
            java.io.PrintWriter consoleWriter = new java.io.PrintWriter(System.out, true);
            server.start(consoleWriter);
        } catch (IOException e) {
            logger.error("Unable to start Apache derby in the server mode! Check whether " +
                    "specified port is available");
        } catch (Exception e) {
            logger.error("Unable to start Apache derby in the server mode! Check whether " +
                    "specified port is available");
        }
    }
    
    public static void stopDerbyInServerMode() {
            System.setProperty(DERBY_SERVER_MODE_SYS_PROPERTY, "false");
            if (server!=null){
            	try {
					server.shutdown();
				} catch (Exception e) {
		            logger.error("Error when stopping the derby server : "+e.getLocalizedMessage());
				}
            }
    }

    public static int getPort(String jdbcURL){
        try{
            String cleanURI = jdbcURL.substring(5);
            URI uri = URI.create(cleanURI);
            return uri.getPort();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return -1;
        }
    }
}
