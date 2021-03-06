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

package org.apache.aiaravata.application.catalog.data.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "GSISSH_PREJOBCOMMAND")
@IdClass(GSISSHPreJobCommandPK.class)
public class GSISSHPreJobCommand implements Serializable {
    @Id
    @Column(name = "SUBMISSION_ID")
    private String submissionID;
    @Id
    @Column(name = "COMMAND")
    private String command;

    @ManyToOne(cascade= CascadeType.MERGE)
    @JoinColumn(name = "SUBMISSION_ID")
    private GSISSHSubmission gsisshSubmission;

    public String getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(String submissionID) {
        this.submissionID = submissionID;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public GSISSHSubmission getGsisshSubmission() {
        return gsisshSubmission;
    }

    public void setGsisshSubmission(GSISSHSubmission gsisshSubmission) {
        this.gsisshSubmission = gsisshSubmission;
    }
}
