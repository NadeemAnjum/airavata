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

package org.apache.airavata.xbaya;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.airavata.common.utils.XMLUtil;
import org.apache.airavata.workflow.tracking.client.Callback;
import org.apache.airavata.workflow.tracking.client.NotificationType;
import org.apache.airavata.wsmg.client.WseMsgBrokerClient;
import org.apache.airavata.xbaya.component.ComponentException;
import org.apache.airavata.xbaya.component.ws.WSComponentPort;
import org.apache.airavata.xbaya.gpel.GPELClient;
import org.apache.airavata.xbaya.gpel.script.BPELScript;
import org.apache.airavata.xbaya.gpel.script.BPELScriptType;
import org.apache.airavata.xbaya.graph.GraphException;
import org.apache.airavata.xbaya.lead.LEADTypes;
import org.apache.airavata.xbaya.lead.LEADWorkflowInvoker;
import org.apache.airavata.xbaya.security.SecurityUtil;
import org.apache.airavata.xbaya.security.UserX509Credential;
import org.apache.airavata.xbaya.security.XBayaSecurity;
import org.apache.airavata.xbaya.wf.Workflow;
import org.apache.airavata.xbaya.workflow.WorkflowEngineException;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.xmlbeans.XmlObject;
import org.gpel.client.GcInstance;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

import xsul.XmlConstants;
import xsul.invoker.gsi.GsiInvoker;
import xsul.lead.LeadContextHeader;
import xsul.ws_addressing.WsaEndpointReference;
import xsul5.wsdl.WsdlDefinitions;

public class GpelWorkflowClient {
    // implements
    // } Callback {
    //
    // /**
    // *
    // * @param workflowFileName
    // * @return
    // * @throws FileNotFoundException
    // * @throws IOException
    // * @throws GraphException
    // * @throws ComponentException
    // * @throws WorkflowEngineException
    // */
    // public static Workflow getWorkflow(String workflowFileName) throws FileNotFoundException, IOException,
    // GraphException, ComponentException, WorkflowEngineException {
    // BufferedReader in = new BufferedReader(new FileReader(workflowFileName));
    // String wf = "";
    // String line = in.readLine();
    // while (line != null) {
    // wf += line;
    // line = in.readLine();
    // }
    //
    // Workflow workflow = new Workflow(wf);
    // BPELScript bpel = new BPELScript(workflow);
    // bpel.create(BPELScriptType.GPEL);
    // workflow.setGpelProcess(bpel.getGpelProcess());
    // workflow.setWorkflowWSDL(bpel.getWorkflowWSDL().getWsdlDefinitions());
    // return workflow;
    //
    // }
    //
    // /**
    // *
    // * @param userName
    // * @param password
    // * @param topic
    // * @param workflow
    // * @param inputs
    // * @throws WorkflowEngineException
    // * @throws ComponentException
    // * @throws GraphException
    // */
    //
    // public static void invoke(String userName, String password, String topic, Workflow workflow,
    // List<WSComponentPort> inputs) throws WorkflowEngineException, ComponentException, GraphException {
    // invoke(userName, password, topic, workflow, inputs, null);
    //
    // }
    //
    // /**
    // * @param userName
    // * @param password
    // * @param topic
    // * @param workflow
    // * @param inputs
    // * @param leadContextHeader
    // * @throws WorkflowEngineException
    // * @throws ComponentException
    // * @throws GraphException
    // */
    // public static void invoke(String userName, String password, String topic, Workflow workflow,
    // List<WSComponentPort> inputs, LeadContextHeader leadContextHeader) throws WorkflowEngineException,
    // ComponentException, GraphException {
    //
    // for (Iterator iterator = inputs.iterator(); iterator.hasNext();) {
    // WSComponentPort componentPort = (WSComponentPort) iterator.next();
    // Object value = parseValue(componentPort, componentPort.getDefaultValue());
    // if (value == null) {
    // return;
    // }
    // componentPort.setValue(value);
    // }
    //
    // GSSCredential proxy = SecurityUtil.getGSSCredential(userName, password, XBayaConstants.DEFAULT_MYPROXY_SERVER);
    // UserX509Credential credential = new UserX509Credential(proxy, XBayaSecurity.getTrustedCertificates());
    //
    // GPELClient client = new GPELClient(XBayaConstants.DEFAULT_GPEL_ENGINE_URL, credential);
    // client.deploy(workflow, false);
    // GcInstance instance = client.instantiate(workflow, XBayaConstants.DEFAULT_DSC_URL, topic);
    // WsdlDefinitions wsdl = client.start(instance);
    //
    // LEADWorkflowInvoker invoker = null;
    // try {
    // if (leadContextHeader == null) {
    // leadContextHeader = getLeadContextHeader(topic, workflow, proxy, instance);
    // }
    //
    // URI messageBoxURL = XBayaConstants.DEFAULT_MESSAGE_BOX_URL;
    //
    // // create an invoker with LEAD Context
    // GsiInvoker secureInvoker = new GsiInvoker(proxy, XBayaSecurity.getTrustedCertificates());
    //
    // invoker = new LEADWorkflowInvoker(wsdl, leadContextHeader, messageBoxURL, secureInvoker);
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    //
    // invoker.setInputs(inputs);
    //
    // final LEADWorkflowInvoker workflowInvoker = invoker;
    //
    // new Thread() {
    // @Override
    // public synchronized void run() {
    // try {
    // boolean success = workflowInvoker.invoke();
    // if (success) {
    // XmlConstants.BUILDER.serializeToString(workflowInvoker.getOutputMessage());
    // } else {
    // XmlConstants.BUILDER.serializeToString(workflowInvoker.getFaultMessage());
    // }
    // } catch (XBayaException e) {
    // throw new RuntimeException(e);
    // }
    // }
    // }.start();
    //
    // }
    //
    // /**
    // * @param topic
    // * @param workflow
    // * @param proxy
    // * @param instance
    // * @return
    // * @throws GSSException
    // * @throws URISyntaxException
    // */
    // public static LeadContextHeader getLeadContextHeader(String topic, Workflow workflow, GSSCredential proxy,
    // GcInstance instance) throws GSSException, URISyntaxException {
    // LeadContextHeader leadContextHeader = new LeadContextHeader(topic, proxy.getName().toString());
    //
    // leadContextHeader.setXRegistryUrl(new URI(XBayaConstants.DEFAULT_XREGISTRY_URL.toString() + "?wsdl"));
    // leadContextHeader.setGfacUrl(new URI(XBayaConstants.DEFAULT_GFAC_URL.toString() + "?wsdl"));
    // leadContextHeader.setDscUrl(new URI("https://tyr09.cs.indiana.edu:25443/"));
    // // The followings might overwrite some variables.
    // leadContextHeader.setWorkflowTemplateId(new URI(workflow.getGPELTemplateID().toString()));
    // leadContextHeader.setWorkflowInstanceId(new URI(instance.getInstanceId().toString()));
    // leadContextHeader.setUserDn(proxy.getName().toString());
    // EndpointReference eventSink = WseMsgBrokerClient.createEndpointReference(
    // XBayaConstants.DEFAULT_BROKER_URL.toString(), topic);
    // WsaEndpointReference eprReference = new WsaEndpointReference(URI.create(eventSink.getAddress()));
    // leadContextHeader.setEventSink(eprReference);
    // return leadContextHeader;
    // }
    //
    // private static Object parseValue(WSComponentPort input, String valueString) {
    // String name = input.getName();
    // if (false) {
    // // Some user wants to pass empty strings, so this check is disabled.
    // if (valueString.length() == 0) {
    // throw new RuntimeException("Input parameter, " + name + ", cannot be empty");
    // }
    // }
    // QName type = input.getType();
    // Object value;
    // if (LEADTypes.isKnownType(type)) {
    // // TODO check the type.
    // value = valueString;
    // } else {
    // try {
    // value = XMLUtil.stringToXmlElement3(valueString);
    // } catch (RuntimeException e) {
    // throw e;
    // }
    // }
    // return value;
    // }
    //
    // /**
    // * @see edu.indiana.extreme.lead.workflow_tracking.client.Callback#deliverMessage(java.lang.String,
    // * edu.indiana.extreme.lead.workflow_tracking.client.NotificationType, org.apache.xmlbeans.XmlObject)
    // */
    // public void deliverMessage(String arg0, NotificationType arg1, XmlObject arg2) {
    // System.out.println(arg2.toString());
    //
    // }
}