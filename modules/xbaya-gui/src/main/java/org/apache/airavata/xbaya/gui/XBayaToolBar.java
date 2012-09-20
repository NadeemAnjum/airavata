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

package org.apache.airavata.xbaya.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import org.apache.airavata.common.utils.SwingUtil;
import org.apache.airavata.xbaya.XBayaEngine;
import org.apache.airavata.xbaya.XBayaExecutionState;
import org.apache.airavata.xbaya.graph.GraphException;
import org.apache.airavata.xbaya.wf.Workflow;

public class XBayaToolBar implements XBayaComponent {

    private XBayaEngine engine;

    private JToolBar toolbar;

    private JButton play;

    private JButton step;

    private JButton stop;

    /**
     * IMAGES_STOP_JPEG
     */
    public static final String IMAGES_STOP_JPEG = "stop.jpeg";
    /**
     * IMAGES_PAUSE_JPEG
     */
    public static final String IMAGES_PAUSE_JPEG = "pause.jpeg";
    /**
     * IMAGES_PLAY_JPEG
     */
    public static final String IMAGES_PLAY_JPEG = "play.jpeg";
    /**
     * IMAGES_STEP_JPEG
     */
    private static final String IMAGES_STEP_JPEG = "step.gif";

    private AbstractAction playAction;

    private AbstractAction stepAction;

    private AbstractAction stopAction;

    private ImageIcon PLAY_ICON;

    private ImageIcon PAUSE_ICON;

    /**
     * Creates a toolbar.
     * 
     * @param client
     */
    public XBayaToolBar(XBayaEngine client) {
        this.engine = client;
        init();
    }

    /**
     * Returns the toolbar.
     * 
     * @return The toolbar
     */
    public JComponent getSwingComponent() {
        return this.toolbar;
    }

    private void init() {

        this.toolbar = new JToolBar();
        this.toolbar.setFloatable(false);
        Border border = BorderFactory.createEtchedBorder();
        this.toolbar.setBorder(border);

        JButton addNodeButton = new JButton("Add Node");
        addNodeButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent event) {
                try {
                    XBayaToolBar.this.engine.getGUI().addNode();
                } catch (RuntimeException e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                } catch (Error e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }
            }
        });

        JButton removeNodeButton = new JButton("Remove Node");
        removeNodeButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent event) {
                try {
                    XBayaToolBar.this.engine.getGUI().getGraphCanvas().removeSelectedNode();
                } catch (GraphException e) {
                    // Should not happen
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                } catch (RuntimeException e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                } catch (Error e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }
            }
        });

        JButton connectEdgeButton = new JButton("Connect/Disconnect");
        connectEdgeButton.addActionListener(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent event) {
                try {
                    XBayaToolBar.this.engine.getGUI().getGraphCanvas().addOrRemoveEdge();
                } catch (RuntimeException e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                } catch (Error e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }
            }
        });

        this.play = new JButton();
        PAUSE_ICON = SwingUtil.createImageIcon(IMAGES_PAUSE_JPEG);
        PLAY_ICON = SwingUtil.createImageIcon(IMAGES_PLAY_JPEG);
        this.playAction = new AbstractAction(null, PAUSE_ICON) {
            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e1) {
                try {
                    Workflow workflow = engine.getWorkflow();
                    XBayaExecutionState executionState = workflow.getExecutionState();
                    if (executionState == XBayaExecutionState.RUNNING || executionState == XBayaExecutionState.STEP) {
                        workflow.setExecutionState(XBayaExecutionState.PAUSED);
                        play.setIcon(PLAY_ICON);
                    } else if (executionState == XBayaExecutionState.PAUSED) {
                        workflow.setExecutionState(XBayaExecutionState.RUNNING);
                        play.setIcon(PAUSE_ICON);
                    } else {
                        throw new IllegalStateException("Unknown state :" + executionState);
                    }
                } catch (RuntimeException e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                } catch (Error e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }

            }
        };
        this.play.setAction(this.playAction);

        this.step = new JButton();
        this.stepAction = new AbstractAction(null, SwingUtil.createImageIcon(IMAGES_STEP_JPEG)) {
            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e2) {
                try {
                    if (engine.getWorkflow().getExecutionState() == XBayaExecutionState.PAUSED) {
                        engine.getWorkflow().setExecutionState(XBayaExecutionState.STEP);
                    } else {
                        throw new IllegalStateException("Unknown state :" + engine.getWorkflow().getExecutionState());
                    }
                } catch (RuntimeException e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                } catch (Error e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }

            }
        };
        this.step.setAction(stepAction);

        this.stop = new JButton();
        this.stopAction = new AbstractAction(null, SwingUtil.createImageIcon(IMAGES_STOP_JPEG)) {
            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e1) {
                try {
                    if (engine.getWorkflow().getExecutionState() != XBayaExecutionState.NONE
                            || engine.getWorkflow().getExecutionState() != XBayaExecutionState.STOPPED) {
                        engine.getWorkflow().setExecutionState(XBayaExecutionState.STOPPED);
                    } else {
                        throw new IllegalStateException("Unknown state :" + engine.getWorkflow().getExecutionState());
                    }
                } catch (RuntimeException e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                } catch (Error e) {
                    XBayaToolBar.this.engine.getGUI().getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
                }

            }
        };
        this.stop.setAction(stopAction);

        this.toolbar.add(addNodeButton);
        this.toolbar.add(removeNodeButton);
        this.toolbar.addSeparator();
        this.toolbar.add(connectEdgeButton);
    }

    public void addDynamicExecutionTools() {
        this.toolbar.add(this.play);
        this.toolbar.add(this.step);
        this.toolbar.add(this.stop);
        this.toolbar.repaint();
    }

    public void removeDynamicExecutionTools() {
        this.toolbar.remove(this.play);
        this.toolbar.remove(this.step);
        this.toolbar.remove(this.stop);
        this.toolbar.repaint();
    }

    /**
     * Returns the playAction.
     * 
     * @return The playAction
     */
    public AbstractAction getPlayAction() {
        return this.playAction;
    }

    /**
     * Returns the stepAction.
     * 
     * @return The stepAction
     */
    public AbstractAction getStepAction() {
        return this.stepAction;
    }

    /**
     * Returns the stopAction.
     * 
     * @return The stopAction
     */
    public AbstractAction getStopAction() {
        return this.stopAction;
    }

}