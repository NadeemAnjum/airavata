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

package org.apache.airavata.xbaya.menues.run;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.airavata.xbaya.XBayaEngine;
import org.apache.airavata.xbaya.event.Event;
import org.apache.airavata.xbaya.event.Event.Type;
import org.apache.airavata.xbaya.event.EventListener;
import org.apache.airavata.xbaya.experiment.gui.WorkflowInterpreterLaunchWindow;
import org.apache.airavata.xbaya.graph.dynamic.gui.DynamicWorkflowRunnerWindow;
import org.apache.airavata.xbaya.gridchem.gui.GridChemRunnerWindow;
import org.apache.airavata.xbaya.gui.ErrorMessages;
import org.apache.airavata.xbaya.jython.gui.JythonRunnerWindow;
import org.apache.airavata.xbaya.monitor.Monitor;
import org.apache.airavata.xbaya.monitor.MonitorConfiguration;
import org.apache.airavata.xbaya.monitor.gui.MonitorConfigurationWindow;
import org.apache.airavata.xbaya.monitor.gui.MonitorStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunMenuItem  implements EventListener{

    private XBayaEngine engine;

    private JMenu runMenu;
    
    private JMenuItem launchDynamicWorkflowItem;
    
    private JMenuItem runJythonWorkflowItem;

    private JMenuItem launchGridChemWorkflowItem;

    private JMenuItem launchXBayaInterpreterItem;

    private JMenuItem launchAndSaveInGridChemWorkflowItem;

    private JMenuItem configMonitorItem;

    private JMenuItem resumeMonitoringItem;

    private JMenuItem pauseMonitoringItem;

    private JMenuItem resetMonitoringItem;

    private static final Logger logger = LoggerFactory.getLogger(RunMenuItem.class);

    /**
     * Constructs a WorkflowMenu.
     * 
     * @param engine
     */
    public RunMenuItem(XBayaEngine engine) {
        this.engine = engine;
        createWorkflowMenu();
        Monitor monitor = this.engine.getMonitor();
        monitor.addEventListener(this);
        monitor.getConfiguration().addEventListener(this);
    }

    /**
     * @return The workflow menu.
     */
    public JMenu getMenu() {
        return this.runMenu;
    }

    /**
     * Creates workflow menu.
     */
    private void createWorkflowMenu() {
        this.launchDynamicWorkflowItem = createLaunchDynamicWorkflowItem();
        this.runJythonWorkflowItem = createRunJythonWorkflowItem();
        this.launchGridChemWorkflowItem = createLaunchGridChemWorkflowItem();
        createLaunchXBayaInterpreterItem();
        createLaunchAndSaveGridChemWorkflowItem();
        this.configMonitorItem = createConfigMonitoring();
        this.resumeMonitoringItem = createResumeMonitoring();
        this.pauseMonitoringItem = createPauseMonitoring();
        this.resetMonitoringItem = createStopMonitoring();
        
        runMenu = new JMenu("Run");
        runMenu.setMnemonic(KeyEvent.VK_R);

        runMenu.add(launchDynamicWorkflowItem);
        runMenu.add(runJythonWorkflowItem);
        runMenu.add(launchXBayaInterpreterItem);

        runMenu.addSeparator();
        runMenu.add(launchGridChemWorkflowItem);
        runMenu.add(launchAndSaveInGridChemWorkflowItem);

        runMenu.addSeparator();
        
        runMenu.add(this.resumeMonitoringItem);
        runMenu.add(this.pauseMonitoringItem);
        runMenu.add(this.resetMonitoringItem);
        runMenu.add(this.configMonitorItem);
        
    }
    
    private JMenuItem createConfigMonitoring() {
        JMenuItem item = new JMenuItem("Configure Monitoring...");
        item.setMnemonic(KeyEvent.VK_C);
        item.addActionListener(new AbstractAction() {
            private MonitorConfigurationWindow window;

            public void actionPerformed(ActionEvent e) {
                if (this.window == null) {
                    this.window = new MonitorConfigurationWindow(engine);
                }
                this.window.show();
            }
        });
        return item;
    }

    private JMenuItem createResumeMonitoring() {
        JMenuItem item = new JMenuItem("Resume monitoring");
        item.setMnemonic(KeyEvent.VK_S);
        item.addActionListener(new AbstractAction() {
            private MonitorStarter starter;

            public void actionPerformed(ActionEvent event) {
                if (this.starter == null) {
                    this.starter = new MonitorStarter(engine);
                }
                this.starter.start();
            }
        });
        boolean valid = this.engine.getMonitor().getConfiguration().isValid();
        item.setVisible(valid);
        return item;
    }

    private JMenuItem createPauseMonitoring() {
        JMenuItem item = new JMenuItem("Pause monitoring");
        item.setMnemonic(KeyEvent.VK_T);
        item.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                try {
                    engine.getMonitor().asynchronousStop();
                } catch (RuntimeException e) {
                    engine.getErrorWindow().error(ErrorMessages.MONITOR_ERROR, e);
                } catch (Error e) {
                    engine.getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }
            }
        });
        item.setVisible(false);
        return item;
    }

    private JMenuItem createStopMonitoring() {
        JMenuItem item = new JMenuItem("Reset monitoring");
        item.setMnemonic(KeyEvent.VK_R);
        item.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                try {
                    engine.getMonitor().reset();
                } catch (RuntimeException e) {
                    engine.getErrorWindow().error(ErrorMessages.MONITOR_ERROR, e);
                } catch (Error e) {
                    engine.getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }
            }
        });
        item.setVisible(false);
        return item;
    }

    
    private JMenuItem createRunJythonWorkflowItem() {
        JMenuItem menuItem = new JMenuItem("Run workflow as Jython...");
        menuItem.setMnemonic(KeyEvent.VK_J);
        menuItem.addActionListener(new AbstractAction() {
            private JythonRunnerWindow window;

            public void actionPerformed(ActionEvent event) {
                if (this.window == null) {
                    this.window = new JythonRunnerWindow(engine);
                }
                this.window.show();
            }
        });
        return menuItem;
    }

    private JMenuItem createLaunchDynamicWorkflowItem() {
        JMenuItem menuItem = new JMenuItem("Run workflow...");
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addActionListener(new AbstractAction() {
            private DynamicWorkflowRunnerWindow window;

            public void actionPerformed(ActionEvent event) {
                if (this.window == null) {
                    this.window = new DynamicWorkflowRunnerWindow(engine);
                }
                this.window.show();
            }
        });
        return menuItem;
    }
    

    private JMenuItem createLaunchGridChemWorkflowItem() {
        JMenuItem menuItem = new JMenuItem("Run as GridChem Workflow...");
        menuItem.addActionListener(new AbstractAction() {
            private GridChemRunnerWindow window;

            public void actionPerformed(ActionEvent event) {
                if (this.window == null) {
                    this.window = new GridChemRunnerWindow(engine);
                }
                this.window.show();
            }
        });
        return menuItem;
    }
    
    private void createLaunchXBayaInterpreterItem() {
        this.launchXBayaInterpreterItem = new JMenuItem("Run on Interpreter Server...");
        launchXBayaInterpreterItem.addActionListener(new AbstractAction() {
            private WorkflowInterpreterLaunchWindow window;

            public void actionPerformed(ActionEvent e) {
                if (this.window == null) {
                    this.window = new WorkflowInterpreterLaunchWindow(engine);
                }
                try {
                    this.window.show();
                } catch (Exception e1) {
                    engine.getErrorWindow().error(e1);
                }

            }
        });
    }

    /**
	 * 
	 */
    private void createLaunchAndSaveGridChemWorkflowItem() {
        launchAndSaveInGridChemWorkflowItem = new JMenuItem("Run & Register in GridChem...	");
        // TODO Add the following operations
        // First Call OGCE-GridChem-Bridge Service to register an experiment
        // Set lead context header with all the required notifier context
        // call launch workflow

    }
    
    /**
     * @see org.apache.airavata.xbaya.event.EventListener#eventReceived(org.apache.airavata.xbaya.event.Event)
     */
    public void eventReceived(Event event) {
        Type type = event.getType();
        if (type.equals(Event.Type.MONITOR_CONFIGURATION_CHANGED)) {
            MonitorConfiguration configuration = this.engine.getMonitor().getConfiguration();
            boolean valid = configuration.isValid();
            resumeMonitoringItem.setVisible(valid);
            pauseMonitoringItem.setVisible(false);
            resetMonitoringItem.setVisible(false);
        } else if (type.equals(Event.Type.MONITOR_STARTED)) {
            resumeMonitoringItem.setVisible(false);
            pauseMonitoringItem.setVisible(true);
            resetMonitoringItem.setVisible(true);
        } else if (type.equals(Event.Type.MONITOR_STOPED)) {
            resumeMonitoringItem.setVisible(true);
            pauseMonitoringItem.setVisible(false);
            resetMonitoringItem.setVisible(false);
        }
    }
}