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

package org.apache.aiaravata.application.catalog.data.impl;

import org.airavata.appcatalog.cpi.AppCatalogException;
import org.airavata.appcatalog.cpi.ComputeResource;
import org.apache.aiaravata.application.catalog.data.resources.*;
import org.apache.aiaravata.application.catalog.data.util.AppCatalogThriftConversion;
import org.apache.aiaravata.application.catalog.data.util.AppCatalogUtils;
import org.apache.airavata.model.computehost.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComputeResourceImpl implements ComputeResource {
    private final static Logger logger = LoggerFactory.getLogger(ComputeResourceImpl.class);

    @Override
    public String addComputeResource(ComputeResourceDescription description) throws AppCatalogException {
        try {
            ComputeHostResource computeHostResource = new ComputeHostResource();
            computeHostResource.setHostName(description.getHostName());
            computeHostResource.setResoureId(AppCatalogUtils.getID(description.getHostName()));
            description.setResourceId(computeHostResource.getResoureId());
            computeHostResource.setPreferredJobSubmissionProtocol(description.getPreferredJobSubmissionProtocol());
            computeHostResource.setDescription(description.getResourceDescription());
            computeHostResource.save();
            description.setResourceId(computeHostResource.getResoureId());

            Set<String> hostAliases = description.getHostAliases();
            if (hostAliases != null && !hostAliases.isEmpty()) {
                for (String alias : hostAliases) {
                    HostAliasResource aliasResource = new HostAliasResource();
                    aliasResource.setComputeHostResource(computeHostResource);
                    aliasResource.setResourceID(computeHostResource.getResoureId());
                    aliasResource.setAlias(alias);
                    aliasResource.save();
                }
            }

            Set<String> ipAddresses = description.getIpAddresses();
            if (ipAddresses != null && !ipAddresses.isEmpty()) {
                for (String ipAddress : ipAddresses) {
                    HostIPAddressResource ipAddressResource = new HostIPAddressResource();
                    ipAddressResource.setComputeHostResource(computeHostResource);
                    ipAddressResource.setResourceID(computeHostResource.getResoureId());
                    ipAddressResource.setIpaddress(ipAddress);
                    ipAddressResource.save();
                }
            }

            Map<String, JobSubmissionProtocol> jobSubmissionProtocols = description.getJobSubmissionProtocols();
            if (jobSubmissionProtocols != null && !jobSubmissionProtocols.isEmpty()) {
                for (String key : jobSubmissionProtocols.keySet()) {
                    JobSubmissionProtocol jobSubmissionProtocol = jobSubmissionProtocols.get(key);
                    JobSubmissionProtocolResource resource = new JobSubmissionProtocolResource();
                    resource.setResourceID(computeHostResource.getResoureId());
                    resource.setComputeHostResource(computeHostResource);
                    resource.setSubmissionID(key);
                    resource.setJobType(jobSubmissionProtocol.toString());
                    resource.save();
                }
            }
            Map<String, DataMovementProtocol> movementProtocols = description.getDataMovementProtocols();
            if (movementProtocols != null && !movementProtocols.isEmpty()) {
                for (String key : movementProtocols.keySet()) {
                    DataMovementProtocol dataMovementProtocol = movementProtocols.get(key);
                    DataMovementProtocolResource resource = new DataMovementProtocolResource();
                    resource.setResourceID(computeHostResource.getResoureId());
                    resource.setComputeHostResource(computeHostResource);
                    resource.setDataMoveID(key);
                    resource.setDataMoveType(dataMovementProtocol.toString());
                    resource.save();
                }
            }
            return computeHostResource.getResoureId();
        } catch (Exception e) {
            logger.error("Error while saving compute resource...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public String addSSHJobSubmission(String computeResourceId, SSHJobSubmission sshJobSubmission) throws AppCatalogException {
        try {
            SSHSubmissionResource resource = new SSHSubmissionResource();
            resource.setResourceID(computeResourceId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            String hostName = computeResource.getHostName();
            hostName = "SSH_" + hostName;
            resource.setSubmissionID(AppCatalogUtils.getID(hostName));
            resource.setSshPort(resource.getSshPort());
            resource.setResourceJobManager(sshJobSubmission.getResourceJobManager().toString());
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.save();
            sshJobSubmission.setJobSubmissionDataID(resource.getSubmissionID());
            return resource.getSubmissionID();
        }catch (Exception e) {
            logger.error("Error while saving SSH Job Submission...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void addSSHJobSubmissionProtocol(String computeResourceId, String jobSubmissionId) throws AppCatalogException {
        try {
            JobSubmissionProtocolResource resource = new JobSubmissionProtocolResource();
            resource.setResourceID(computeResourceId);
            resource.setSubmissionID(jobSubmissionId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.setJobType(JobSubmissionProtocol.SSH.toString());
            resource.save();
        }catch (Exception e){
            logger.error("Error while saving SSH Job Submission Protocol...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public String addGSISSHJobSubmission(String computeResourceId, GSISSHJobSubmission gsisshJobSubmission) throws AppCatalogException {
        try {
            GSISSHSubmissionResource resource = new GSISSHSubmissionResource();
            resource.setResourceID(computeResourceId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            String hostName = computeResource.getHostName();
            hostName = "GSISSH" + hostName;
            resource.setSubmissionID(AppCatalogUtils.getID(hostName));
            resource.setSshPort(resource.getSshPort());
            resource.setResourceJobManager(gsisshJobSubmission.getResourceJobManager().toString());
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.setInstalledPath(gsisshJobSubmission.getInstalledPath());
            resource.setMonitorMode(gsisshJobSubmission.getMonitorMode());
            resource.save();
            gsisshJobSubmission.setJobSubmissionDataID(resource.getSubmissionID());

            Set<String> exports = gsisshJobSubmission.getExports();
            if (exports != null && !exports.isEmpty()){
                for (String export : exports){
                    GSISSHExportResource exportResource = new GSISSHExportResource();
                    exportResource.setSubmissionID(resource.getSubmissionID());
                    exportResource.setExport(export);
                    exportResource.setGsisshSubmissionResource(resource);
                    exportResource.save();
                }
            }

            List<String> preJobCommands = gsisshJobSubmission.getPreJobCommands();
            if (preJobCommands != null && !preJobCommands.isEmpty()){
                for (String command : preJobCommands){
                    GSISSHPreJobCommandResource commandResource = new GSISSHPreJobCommandResource();
                    commandResource.setSubmissionID(resource.getSubmissionID());
                    commandResource.setCommand(command);
                    commandResource.setGsisshSubmissionResource(resource);
                    commandResource.save();
                }
            }

            List<String> postJobCommands = gsisshJobSubmission.getPostJobCommands();
            if (postJobCommands != null && !postJobCommands.isEmpty()){
                for (String command : postJobCommands){
                    GSISSHPostJobCommandResource commandResource = new GSISSHPostJobCommandResource();
                    commandResource.setSubmissionID(resource.getSubmissionID());
                    commandResource.setCommand(command);
                    commandResource.setGsisshSubmissionResource(resource);
                    commandResource.save();
                }
            }
            return resource.getSubmissionID();
        }catch (Exception e) {
            logger.error("Error while saving GSISSH Job Submission...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void addGSISSHJobSubmissionProtocol(String computeResourceId, String jobSubmissionId) throws AppCatalogException {
        try {
            JobSubmissionProtocolResource resource = new JobSubmissionProtocolResource();
            resource.setResourceID(computeResourceId);
            resource.setSubmissionID(jobSubmissionId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.setJobType(JobSubmissionProtocol.GSISSH.toString());
            resource.save();
        }catch (Exception e){
            logger.error("Error while saving GSISSH Job Submission Protocol...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public String addGlobusJobSubmission(String computeResourceId, GlobusJobSubmission globusJobSubmission) throws AppCatalogException {
        try {
            GlobusJobSubmissionResource resource = new GlobusJobSubmissionResource();
            resource.setResourceID(computeResourceId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            String hostName = computeResource.getHostName();
            hostName = "GLOBUS" + hostName;
            resource.setSubmissionID(AppCatalogUtils.getID(hostName));
            resource.setSecurityProtocol(globusJobSubmission.getSecurityProtocol().toString());
            resource.setResourceJobManager(globusJobSubmission.getResourceJobManager().toString());
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.save();
            globusJobSubmission.setJobSubmissionDataID(resource.getSubmissionID());
            List<String> globusGateKeeperEndPoint = globusJobSubmission.getGlobusGateKeeperEndPoint();
            if (globusGateKeeperEndPoint != null && !globusGateKeeperEndPoint.isEmpty()) {
                for (String endpoint : globusGateKeeperEndPoint) {
                    GlobusGKEndpointResource endpointResource = new GlobusGKEndpointResource();
                    endpointResource.setSubmissionID(resource.getSubmissionID());
                    endpointResource.setEndpoint(endpoint);
                    endpointResource.setGlobusJobSubmissionResource(resource);
                    endpointResource.save();
                }
            }
            return resource.getSubmissionID();
        } catch (Exception e) {
            logger.error("Error while saving Globus Job Submission...", e);
            throw new AppCatalogException(e);
        }

    }

    @Override
    public void addGlobusJobSubmissionProtocol(String computeResourceId, String jobSubmissionId) throws AppCatalogException {
        try {
            JobSubmissionProtocolResource resource = new JobSubmissionProtocolResource();
            resource.setResourceID(computeResourceId);
            resource.setSubmissionID(jobSubmissionId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.setJobType(JobSubmissionProtocol.GRAM.toString());
            resource.save();
        }catch (Exception e){
            logger.error("Error while saving Globus Job Submission Protocol...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public String addScpDataMovement(String computeResourceId, SCPDataMovement scpDataMovement) throws AppCatalogException {
        try {
            SCPDataMovementResource resource = new SCPDataMovementResource();
            resource.setResourceID(computeResourceId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            String hostName = computeResource.getHostName();
            hostName = "SCP" + hostName;
            resource.setDataMoveID(AppCatalogUtils.getID(hostName));
            resource.setSecurityProtocol(scpDataMovement.getSecurityProtocol().toString());
            resource.setSshPort(scpDataMovement.getSshPort());
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.save();
            scpDataMovement.setDataMovementDataID(resource.getDataMoveID());
            return resource.getDataMoveID();
        }catch (Exception e){
            logger.error("Error while saving SCP Data Movement...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void addScpDataMovementProtocol(String computeResourceId, String dataMoveId) throws AppCatalogException {
        try {
            DataMovementProtocolResource resource = new DataMovementProtocolResource();
            resource.setResourceID(computeResourceId);
            resource.setDataMoveID(dataMoveId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.setDataMoveType(DataMovementProtocol.SCP.toString());
            resource.save();
        }catch (Exception e){
            logger.error("Error while saving SCP data movement Protocol...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public String addGridFTPDataMovement(String computeResourceId, GridFTPDataMovement gridFTPDataMovement) throws AppCatalogException {
        try {
            GridFTPDataMovementResource resource = new GridFTPDataMovementResource();
            resource.setResourceID(computeResourceId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            String hostName = computeResource.getHostName();
            hostName = "SCP" + hostName;
            resource.setDataMoveID(AppCatalogUtils.getID(hostName));
            resource.setSecurityProtocol(gridFTPDataMovement.getSecurityProtocol().toString());
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.save();
            gridFTPDataMovement.setDataMovementDataID(resource.getDataMoveID());

            List<String> gridFTPEndPoint = gridFTPDataMovement.getGridFTPEndPoint();
            if (gridFTPEndPoint != null && !gridFTPEndPoint.isEmpty()) {
                for (String endpoint : gridFTPEndPoint) {
                    GridFTPDMEndpointResource endpointResource = new GridFTPDMEndpointResource();
                    endpointResource.setDataMoveId(resource.getDataMoveID());
                    endpointResource.setEndpoint(endpoint);
                    endpointResource.setGridFTPDataMovementResource(resource);
                    endpointResource.save();
                }
            }
            return resource.getDataMoveID();
        }catch (Exception e){
            logger.error("Error while saving GridFTP Data Movement...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void addGridFTPDataMovementProtocol(String computeResourceId, String dataMoveId) throws AppCatalogException {
        try {
            DataMovementProtocolResource resource = new DataMovementProtocolResource();
            resource.setResourceID(computeResourceId);
            resource.setDataMoveID(dataMoveId);
            ComputeResourceDescription computeResource = getComputeResource(computeResourceId);
            resource.setComputeHostResource(AppCatalogThriftConversion.getComputeHostResource(computeResource));
            resource.setDataMoveType(DataMovementProtocol.GridFTP.toString());
            resource.save();
        }catch (Exception e){
            logger.error("Error while saving GridFTP data movement Protocol...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public ComputeResourceDescription getComputeResource(String resourceId) throws AppCatalogException {
        try {
            ComputeHostResource resource = new ComputeHostResource();
            ComputeHostResource computeResource = (ComputeHostResource)resource.get(resourceId);
            return AppCatalogThriftConversion.getComputeHostDescription(computeResource);
        }catch (Exception e){
            logger.error("Error while retrieving compute resource...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public List<ComputeResourceDescription> getComputeResourceList(Map<String, String> filters) throws AppCatalogException {
        try {
            ComputeHostResource resource = new ComputeHostResource();
            for (String fieldName : filters.keySet() ){
                if (fieldName.equals(AbstractResource.ComputeResourceConstants.HOST_NAME)){
                    List<Resource> resources = resource.get(AbstractResource.ComputeResourceConstants.HOST_NAME, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getComputeDescriptionList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.ComputeResourceConstants.PREFERED_SUBMISSION_PROTOCOL)){
                    List<Resource> resources = resource.get(AbstractResource.ComputeResourceConstants.PREFERED_SUBMISSION_PROTOCOL, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getComputeDescriptionList(resources);
                    }
                }else {
                    logger.error("Unsupported field name for compute resource.", new IllegalArgumentException());
                    throw new IllegalArgumentException("Unsupported field name for compute resource.");
                }
            }
        }catch (Exception e){
            logger.error("Error while retrieving compute resource list...", e);
            throw new AppCatalogException(e);
        }
        return null;
    }

    @Override
    public GSISSHJobSubmission getGSISSHJobSubmission(String submissionId) throws AppCatalogException {
        try {
            GSISSHSubmissionResource resource = new GSISSHSubmissionResource();
            GSISSHSubmissionResource submissionResource = (GSISSHSubmissionResource)resource.get(submissionId);
            return AppCatalogThriftConversion.getGSISSHSubmissionDescription(submissionResource);
        }catch (Exception e){
            logger.error("Error while retrieving GSISSH Job Submission...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public List<GSISSHJobSubmission> getGSISSHJobSubmissionList(Map<String, String> filters) throws AppCatalogException {
        try {
            GSISSHSubmissionResource resource = new GSISSHSubmissionResource();
            for (String fieldName : filters.keySet() ){
                if (fieldName.equals(AbstractResource.GSISSHSubmissionConstants.RESOURCE_ID)){
                    List<Resource> resources = resource.get(AbstractResource.GSISSHSubmissionConstants.RESOURCE_ID, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getGSISSHSubmissionList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.GSISSHSubmissionConstants.RESOURCE_JOB_MANAGER)){
                    List<Resource> resources = resource.get(AbstractResource.GSISSHSubmissionConstants.RESOURCE_JOB_MANAGER, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getGSISSHSubmissionList(resources);
                    }
                }else {
                    logger.error("Unsupported field name for GSISSH Submission.", new IllegalArgumentException());
                    throw new IllegalArgumentException("Unsupported field name for GSISSH Submission.");
                }
            }
        }catch (Exception e){
            logger.error("Error while retrieving GSISSH Submission list...", e);
            throw new AppCatalogException(e);
        }
        return null;
    }

    @Override
    public GlobusJobSubmission getGlobusJobSubmission(String submissionId) throws AppCatalogException {
        try {
            GlobusJobSubmissionResource resource = new GlobusJobSubmissionResource();
            GlobusJobSubmissionResource submissionResource = (GlobusJobSubmissionResource)resource.get(submissionId);
            return AppCatalogThriftConversion.getGlobusJobSubmissionDescription(submissionResource);
        }catch (Exception e){
            logger.error("Error while retrieving Globus Job Submission...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public List<GlobusJobSubmission> getGlobusJobSubmissionList(Map<String, String> filters) throws AppCatalogException {
        try {
            GlobusJobSubmissionResource resource = new GlobusJobSubmissionResource();
            for (String fieldName : filters.keySet() ){
                if (fieldName.equals(AbstractResource.GlobusJobSubmissionConstants.RESOURCE_ID)){
                    List<Resource> resources = resource.get(AbstractResource.GlobusJobSubmissionConstants.RESOURCE_ID, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getGlobusSubmissionList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.GlobusJobSubmissionConstants.RESOURCE_JOB_MANAGER)){
                    List<Resource> resources = resource.get(AbstractResource.GlobusJobSubmissionConstants.RESOURCE_JOB_MANAGER, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getGlobusSubmissionList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.GlobusJobSubmissionConstants.SECURITY_PROTOCAL)){
                    List<Resource> resources = resource.get(AbstractResource.GlobusJobSubmissionConstants.SECURITY_PROTOCAL, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getGlobusSubmissionList(resources);
                    }
                }else {
                    logger.error("Unsupported field name for Globus Submission.", new IllegalArgumentException());
                    throw new IllegalArgumentException("Unsupported field name for Globus Submission.");
                }
            }
        }catch (Exception e){
            logger.error("Error while retrieving Globus Submission list...", e);
            throw new AppCatalogException(e);
        }
        return null;
    }

    @Override
    public SSHJobSubmission getSSHJobSubmission(String submissionId) throws AppCatalogException {
        try {
            SSHSubmissionResource resource = new SSHSubmissionResource();
            SSHSubmissionResource submissionResource = (SSHSubmissionResource)resource.get(submissionId);
            return AppCatalogThriftConversion.getSSHJobSubmissionDescription(submissionResource);
        }catch (Exception e){
            logger.error("Error while retrieving SSH Job Submission...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public List<SSHJobSubmission> getSSHJobSubmissionList(Map<String, String> filters) throws AppCatalogException {
        try {
            SSHSubmissionResource resource = new SSHSubmissionResource();
            for (String fieldName : filters.keySet() ){
                if (fieldName.equals(AbstractResource.SSHSubmissionConstants.RESOURCE_ID)){
                    List<Resource> resources = resource.get(AbstractResource.SSHSubmissionConstants.RESOURCE_ID, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getSSHSubmissionList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.SSHSubmissionConstants.RESOURCE_JOB_MANAGER)){
                    List<Resource> resources = resource.get(AbstractResource.SSHSubmissionConstants.RESOURCE_JOB_MANAGER, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getSSHSubmissionList(resources);
                    }
                }else {
                    logger.error("Unsupported field name for SSH Submission.", new IllegalArgumentException());
                    throw new IllegalArgumentException("Unsupported field name for SSH Submission.");
                }
            }
        }catch (Exception e){
            logger.error("Error while retrieving SSH Submission list...", e);
            throw new AppCatalogException(e);
        }
        return null;
    }

    @Override
    public SCPDataMovement getSCPDataMovement(String dataMoveId) throws AppCatalogException {
        try {
            SCPDataMovementResource resource = new SCPDataMovementResource();
            SCPDataMovementResource dataMovementResource = (SCPDataMovementResource)resource.get(dataMoveId);
            return AppCatalogThriftConversion.getSCPDataMovementDescription(dataMovementResource);
        }catch (Exception e){
            logger.error("Error while retrieving SCP Data Movement...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public List<SCPDataMovement> getSCPDataMovementList(Map<String, String> filters) throws AppCatalogException {
        try {
            SCPDataMovementResource resource = new SCPDataMovementResource();
            for (String fieldName : filters.keySet() ){
                if (fieldName.equals(AbstractResource.SCPDataMovementConstants.RESOURCE_ID)){
                    List<Resource> resources = resource.get(AbstractResource.SCPDataMovementConstants.RESOURCE_ID, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getSCPDataMovementList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.SCPDataMovementConstants.SECURITY_PROTOCOL)){
                    List<Resource> resources = resource.get(AbstractResource.SCPDataMovementConstants.SECURITY_PROTOCOL, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getSCPDataMovementList(resources);
                    }
                }else {
                    logger.error("Unsupported field name for SCP Data movement.", new IllegalArgumentException());
                    throw new IllegalArgumentException("Unsupported field name for SCP Data movement.");
                }
            }
        }catch (Exception e){
            logger.error("Error while retrieving SCP Data movement list...", e);
            throw new AppCatalogException(e);
        }
        return null;
    }

    @Override
    public GridFTPDataMovement getGridFTPDataMovement(String dataMoveId) throws AppCatalogException {
        try {
            GridFTPDataMovementResource resource = new GridFTPDataMovementResource();
            GridFTPDataMovementResource dataMovementResource = (GridFTPDataMovementResource)resource.get(dataMoveId);
            return AppCatalogThriftConversion.getGridFTPDataMovementDescription(dataMovementResource);
        }catch (Exception e){
            logger.error("Error while retrieving Grid FTP Data Movement...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public List<GridFTPDataMovement> getGridFTPDataMovementList(Map<String, String> filters) throws AppCatalogException {
        try {
            GridFTPDataMovementResource resource = new GridFTPDataMovementResource();
            for (String fieldName : filters.keySet() ){
                if (fieldName.equals(AbstractResource.GridFTPDataMovementConstants.RESOURCE_ID)){
                    List<Resource> resources = resource.get(AbstractResource.GridFTPDataMovementConstants.RESOURCE_ID, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getGridFTPDataMovementList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.GridFTPDataMovementConstants.SECURITY_PROTOCOL)){
                    List<Resource> resources = resource.get(AbstractResource.GridFTPDataMovementConstants.SECURITY_PROTOCOL, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                        return AppCatalogThriftConversion.getGridFTPDataMovementList(resources);
                    }
                }else {
                    logger.error("Unsupported field name for GridFTP Data movement.", new IllegalArgumentException());
                    throw new IllegalArgumentException("Unsupported field name for GridFTP Data movement.");
                }
            }
        }catch (Exception e){
            logger.error("Error while retrieving GridFTP Data movement list...", e);
            throw new AppCatalogException(e);
        }
        return null;
    }

    @Override
    public boolean isComputeResourceExists(String resourceId) throws AppCatalogException {
        try {
            ComputeHostResource resource = new ComputeHostResource();
            return resource.isExists(resourceId);
        }catch (Exception e){
            logger.error("Error while retrieving compute resource...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void removeComputeResource(String resourceId) throws AppCatalogException {
        try {
            ComputeHostResource resource = new ComputeHostResource();
            resource.remove(resourceId);
        }catch (Exception e){
            logger.error("Error while removing compute resource...", e);
            throw new AppCatalogException(e);
        }
    }
}