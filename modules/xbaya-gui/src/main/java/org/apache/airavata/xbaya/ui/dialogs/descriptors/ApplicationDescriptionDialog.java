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

package org.apache.airavata.xbaya.ui.dialogs.descriptors;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.apache.airavata.common.registry.api.exception.RegistryException;
import org.apache.airavata.common.utils.SwingUtil;
import org.apache.airavata.commons.gfac.type.ApplicationDeploymentDescription;
import org.apache.airavata.commons.gfac.type.HostDescription;
import org.apache.airavata.commons.gfac.type.ServiceDescription;
import org.apache.airavata.registry.api.AiravataRegistry;
import org.apache.airavata.schemas.gfac.ApplicationDeploymentDescriptionType;
import org.apache.airavata.schemas.gfac.GlobusHostType;
import org.apache.airavata.schemas.gfac.GramApplicationDeploymentType;
import org.apache.airavata.xbaya.XBayaEngine;
import org.apache.airavata.xbaya.ui.widgets.GridPanel;
import org.apache.airavata.xbaya.ui.widgets.XBayaLabel;
import org.apache.airavata.xbaya.ui.widgets.XBayaLinkButton;
import org.apache.airavata.xbaya.ui.widgets.XBayaTextField;
import org.apache.xmlbeans.XmlException;

public class ApplicationDescriptionDialog extends JDialog implements ActionListener {
    /**
	 * 
	 */
    private static final long serialVersionUID = -2745085755585610025L;
    private XBayaTextField txtExecPath;
    private XBayaTextField txtAppName;
    private XBayaTextField txtTempDir;

    private AiravataRegistry registry;
    private ApplicationDeploymentDescription shellApplicationDescription;
    private JLabel lblError;
    private boolean applcationDescCreated = false;
    private JButton okButton;

    private String serviceName;
    private String hostName;
    private JComboBox cmbServiceName;
    private JComboBox cmbHostName;

    private XBayaEngine engine;
	private JButton btnHostAdvanceOptions;
	private boolean newDescritor;
	private ApplicationDeploymentDescription originalDeploymentDescription;
	private String originalHost; 
	private String originalService;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ApplicationDescriptionDialog dialog = new ApplicationDescriptionDialog(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApplicationDescriptionDialog(XBayaEngine engine) {
    	this(engine,true,null,null,null);
    }
    /**
     * Create the dialog.
     */
    public ApplicationDescriptionDialog(XBayaEngine engine, boolean newDescritor, ApplicationDeploymentDescription originalDeploymentDescription, String originalHost, String originalService) {
    	setNewDescritor(newDescritor);
    	setOriginalDeploymentDescription(originalDeploymentDescription);
    	setOriginalHost(originalHost);
    	setOriginalService(originalService);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent arg0) {
                if (isNewDescritor()) {
					String baseName = "Application";
					int i = 1;
					String defaultName = baseName + i;
					try {
						List<ApplicationDeploymentDescription> applicationDeploymentDescriptions = getRegistry()
								.searchDeploymentDescription(getServiceName(),
										getHostName());
						while (true) {
							boolean notFound = true;
							for (ApplicationDeploymentDescription deploymentDescription : applicationDeploymentDescriptions) {
								if (deploymentDescription.getType()
										.getApplicationName().getStringValue()
										.equals(defaultName)) {
									notFound = false;
									break;
								}
							}
							if (notFound) {
								break;
							}
							defaultName = baseName + (++i);
						}
					} catch (Exception e) {
					}
					txtAppName.setText(defaultName);
					setApplicationName(txtAppName.getText());
				}
            }
        });
        this.engine=engine;
        setRegistry(engine.getConfiguration().getJcrComponentRegistry().getRegistry());
        iniGUI();
    }

    public void open() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    protected ApplicationDescriptionDialog getDialog() {
        return this;
    }

    private void iniGUI() {
        if (isNewDescritor()) {
			setTitle("New Deployment Description");
		}else{
			setTitle("Update Deployment Description: "+getOriginalDeploymentDescription().getType().getApplicationName().getStringValue());
		}
		setBounds(100, 100, 600, 620);
        setModal(true);
        setLocationRelativeTo(null);
        GridPanel buttonPane = new GridPanel();
//        getContentPane().setLayout(new BorderLayout());
        {
        	
//            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//            getContentPane().add(buttonPane, BorderLayout.SOUTH);

            lblError = new JLabel("");
            lblError.setForeground(Color.RED);
            buttonPane.add(lblError);
            if (!isNewDescritor()){
            	JButton resetButton = new JButton("Reset");
                resetButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        loadData();
                    }
                });
                buttonPane.add(resetButton);
            }
            {
                okButton = new JButton("Save");
                if (!isNewDescritor()){
                	okButton.setText("Update");
                }
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveApplicationDescription();
                        close();
                    }
                });
                okButton.setEnabled(false);
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setApplicationDescCreated(false);
                        close();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        {
//            JPanel panel = new JPanel();
//            getContentPane().add(panel, BorderLayout.CENTER);
        	GridPanel execPath=new GridPanel();
            txtExecPath = new XBayaTextField();
            txtExecPath.getTextField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    setExecutablePath(txtExecPath.getText());
                }
            });
            txtExecPath.getTextField().addFocusListener(new FocusAdapter() {
            	@Override
            	public void focusLost(FocusEvent e) {
            		super.focusLost(e);
            		updateTempDirWithExecPath(txtExecPath.getText());
            	}
			});
            txtExecPath.setColumns(10);
            JButton execBrowse=new JButton("Browse...");
            execBrowse.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser c = new JFileChooser();
					int rVal = c.showOpenDialog(null);
					if (rVal == JFileChooser.APPROVE_OPTION) {
						txtExecPath.setText(c.getSelectedFile().toString());
						setExecutablePath(txtExecPath.getText());
					}
				}
            });
            execPath.add(txtExecPath);
            execPath.add(execBrowse);
            execPath.layout(1, 2, 0, 0);
            txtAppName = new XBayaTextField();
            txtAppName.getTextField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent arg0) {
                    setApplicationName(txtAppName.getText());
                }
            });
            txtAppName.setColumns(10);
            XBayaLabel lblApplicationName = new XBayaLabel("Application name",txtAppName);
            XBayaLabel lblExecutablePath = new XBayaLabel("Executable path",txtExecPath);
        	GridPanel tmpDirPath=new GridPanel();

            txtTempDir = new XBayaTextField();
            txtTempDir.getTextField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    setTempDir(txtTempDir.getText());
                }
            });
            txtTempDir.setColumns(10);
            JButton tmpDirBrowse=new JButton("Browse...");
            tmpDirBrowse.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser c = new JFileChooser();
					c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int rVal = c.showOpenDialog(null);
					if (rVal == JFileChooser.APPROVE_OPTION) {
						txtTempDir.setText(c.getSelectedFile().toString());
						setTempDir(txtTempDir.getText());
					}
				}
            });
            tmpDirPath.add(txtTempDir);
            tmpDirPath.add(tmpDirBrowse);
            tmpDirPath.layout(1, 2, 0, 0);

            XBayaLabel lblTemporaryDirectory = new XBayaLabel("Temporary directory",txtTempDir);

            JButton btnAdvance = new JButton("Advanced options...");
            btnAdvance.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        ApplicationDescriptionAdvancedOptionDialog serviceDescriptionDialog = new ApplicationDescriptionAdvancedOptionDialog(
                                getRegistry(), getShellApplicationDescription());
                        serviceDescriptionDialog.open();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
                    }
                }
            });

            XBayaLinkButton lnkNewService = new XBayaLinkButton("New button");
            lnkNewService.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    try {
                        ServiceDescriptionDialog serviceDescriptionDialog = new ServiceDescriptionDialog(getRegistry());
                        serviceDescriptionDialog.open();
                        if (serviceDescriptionDialog.isServiceCreated()) {
                            loadServiceDescriptions();
                            cmbServiceName.setSelectedItem(serviceDescriptionDialog.getServiceName());
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
                    }
                }
            });
            lnkNewService.setText("Create new service...");
            lnkNewService.setHorizontalAlignment(SwingConstants.TRAILING);

            cmbServiceName = new JComboBox();
            cmbServiceName.addActionListener(this);
//            cmbServiceName.setRenderer(new DefaultListCellRenderer());
            cmbHostName = new JComboBox();
            cmbHostName.addActionListener(this);

            XBayaLabel lblHostName = new XBayaLabel("Host",cmbHostName);

            XBayaLinkButton lnkNewHost = new XBayaLinkButton("New button");
            lnkNewHost.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        HostDescriptionDialog hostDescriptionDialog = new HostDescriptionDialog(engine);
                        hostDescriptionDialog.open();

                        if (hostDescriptionDialog.isHostCreated()) {
                            loadHostDescriptions();
                            cmbHostName.setSelectedItem(hostDescriptionDialog.getHostLocation());
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
                    }
                }
            });
            lnkNewHost.setText("Create new host...");
            lnkNewHost.setHorizontalAlignment(SwingConstants.TRAILING);

            XBayaLabel lblService = new XBayaLabel("Service",cmbServiceName);

            JLabel lblBindThisDeployment = new JLabel("Bind this deployment description to:");
            lblBindThisDeployment.setFont(new Font("Tahoma", Font.BOLD, 11));

            btnHostAdvanceOptions=new JButton("Gram Configuration...");
            btnHostAdvanceOptions.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						ApplicationDescriptionHostAdvancedOptionDialog hostAdvancedOptionsDialog = new ApplicationDescriptionHostAdvancedOptionDialog(getRegistry(),getShellApplicationDescription());
                        hostAdvancedOptionsDialog.open();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, e1.getLocalizedMessage());
                    }
				}
			});
            
            GridPanel hostPanel=new GridPanel();
            hostPanel.add(cmbHostName);
            hostPanel.add(btnHostAdvanceOptions);
            
            SwingUtil.layoutToGrid(hostPanel.getSwingComponent(), 1, 2, 0, 0);
            GridPanel infoPanel0 = new GridPanel();

            infoPanel0.add(lblApplicationName);
            infoPanel0.add(txtAppName);
            
            GridPanel infoPanel1 = new GridPanel();

            
            infoPanel1.add(lblExecutablePath);
            infoPanel1.add(execPath);
            infoPanel1.add(lblTemporaryDirectory);
            infoPanel1.add(tmpDirPath);
//            infoPanel1.add(new JLabel());
//            infoPanel1.add(btnAdvance);
            
            GridPanel infoPanel2 = new GridPanel();
//            infoPanel2.add(new JSeparator());
            infoPanel2.add(lblBindThisDeployment);
            
            GridPanel infoPanel3 = new GridPanel();

            infoPanel3.add(lblService);
            infoPanel3.add(cmbServiceName);
            infoPanel3.add(new JLabel());
            infoPanel3.add(lnkNewService);
            infoPanel3.add(lblHostName);
            infoPanel3.add(hostPanel);
            infoPanel3.add(new JLabel());
            infoPanel3.add(lnkNewHost);
            
            GridPanel infoPanel4=new GridPanel();
            infoPanel4.add(new JLabel());
            infoPanel4.add(btnAdvance);
            infoPanel4.layout(1, 2, 0, 0);
            
            SwingUtil.layoutToGrid(infoPanel0.getSwingComponent(), 1, 2, SwingUtil.WEIGHT_NONE, 1);

            SwingUtil.layoutToGrid(infoPanel1.getSwingComponent(), 2, 2, SwingUtil.WEIGHT_NONE, 1);
            SwingUtil.layoutToGrid(infoPanel2.getSwingComponent(), 1, 1, SwingUtil.WEIGHT_NONE, 0);
            SwingUtil.layoutToGrid(infoPanel3.getSwingComponent(), 4, 2, SwingUtil.WEIGHT_NONE, 1);

            GridPanel infoPanel = new GridPanel();
            infoPanel.add(infoPanel0);
            infoPanel.add(new JSeparator());
            infoPanel.add(infoPanel1);
            infoPanel.add(new JSeparator());
            infoPanel.add(infoPanel2);
            infoPanel.add(infoPanel3);
            infoPanel.add(new JSeparator());
            infoPanel.add(infoPanel4);
            
            SwingUtil.layoutToGrid(infoPanel.getSwingComponent(), 8, 1, SwingUtil.WEIGHT_NONE, 0);
            SwingUtil.layoutToGrid(buttonPane.getSwingComponent(), 1, buttonPane.getContentPanel().getComponentCount(),SwingUtil.WEIGHT_NONE,0);
            getContentPane().add(infoPanel.getSwingComponent());
            getContentPane().add(buttonPane.getSwingComponent());
            
            buttonPane.getSwingComponent().setBorder(BorderFactory.createEtchedBorder());
            infoPanel.getSwingComponent().setBorder(BorderFactory.createEtchedBorder());

            SwingUtil.layoutToGrid(getContentPane(), 2, 1, -1, 0);
            loadServiceDescriptions();
            loadHostDescriptions();
        }
        setResizable(true);
        getRootPane().setDefaultButton(okButton);
        if (!isNewDescritor()){
        	loadData();
        }
    }

    private void loadServiceDescriptions() {
        cmbServiceName.removeAllItems();
        setServiceName(null);
        try {
            List<ServiceDescription> serviceDescriptions = getRegistry().searchServiceDescription("");
            for (ServiceDescription serviceDescription : serviceDescriptions) {
                cmbServiceName.addItem(serviceDescription.getType().getName());
            }
        } catch (Exception e) {
            setError(e.getLocalizedMessage());
        }
        updateServiceName();
    }

    private void loadData(){
    	txtAppName.setText(getOriginalDeploymentDescription().getType().getApplicationName().getStringValue());
    	setApplicationName(txtAppName.getText());
    	txtExecPath.setText(getOriginalDeploymentDescription().getType().getExecutableLocation());
    	setExecutablePath(txtExecPath.getText());
    	txtTempDir.setText(getOriginalDeploymentDescription().getType().getScratchWorkingDirectory());
    	setTempDir(txtTempDir.getText());

    	cmbHostName.setSelectedItem(getOriginalHost());
    	setHostName(cmbHostName.getSelectedItem().toString());
    	cmbServiceName.setSelectedItem(getOriginalService());
    	setServiceName(cmbServiceName.getSelectedItem().toString());
    	txtAppName.setEditable(isNewDescritor());
    }
    
    private void loadHostDescriptions() {
        cmbHostName.removeAllItems();
        setHostName(null);
        try {
            List<HostDescription> hostDescriptions = getRegistry().searchHostDescription(".*");
            for (HostDescription hostDescription : hostDescriptions) {
                if (hostDescription.getType().getHostName() == null) {
                    cmbHostName.addItem(hostDescription.getType().getHostName());
                } else {
                    cmbHostName.addItem(hostDescription.getType().getHostName());
                }
            }
        } catch (Exception e) {
            setError(e.getLocalizedMessage());
        }
        updateHostName();
    }

    public ApplicationDeploymentDescription getShellApplicationDescription() {
        if(shellApplicationDescription == null){
            if (isNewDescritor()) {
				shellApplicationDescription = new ApplicationDeploymentDescription();
			}else{
				try {
					shellApplicationDescription=ApplicationDeploymentDescription.fromXML(getOriginalDeploymentDescription().toXML());
				} catch (XmlException e) {
					//shouldn't happen (hopefully)
				}
			}
        }
        return shellApplicationDescription;
    }

    public ApplicationDeploymentDescriptionType getApplicationDescriptionType() {
    	return getShellApplicationDescription().getType();
    }
    
    public String getApplicationName() {
        return getApplicationDescriptionType().getApplicationName().getStringValue();
    }

    public void setApplicationName(String applicationName) {
//        ApplicationDeploymentDescriptionType.ApplicationName applicationName1 = getApplicationDescriptionType().addNewApplicationName();
//        applicationName1.setStringValue(applicationName);
    	if (getApplicationDescriptionType().getApplicationName()==null){
    		getApplicationDescriptionType().addNewApplicationName();
    	}
    	getApplicationDescriptionType().getApplicationName().setStringValue(applicationName);
        updateDialogStatus();
    }

    public String getExecutablePath() {
        return getApplicationDescriptionType().getExecutableLocation();
    }

    public void setExecutablePath(String executablePath) {
    	getApplicationDescriptionType().setExecutableLocation(executablePath);
    	updateTempDirWithExecPath(executablePath);
        updateDialogStatus();
    }

	private void updateTempDirWithExecPath(String executablePath) {
		if (!executablePath.trim().equals("") && (!txtExecPath.getSwingComponent().isFocusOwner()) && 
				(getApplicationDescriptionType().getScratchWorkingDirectory()==null || getApplicationDescriptionType().getScratchWorkingDirectory().trim().equalsIgnoreCase(""))){
    		String temp_location = "workflow_runs";
			String tempDir = new File(new File(executablePath).getParentFile(),temp_location).toString();
			txtTempDir.setText(tempDir);
    		txtTempDir.getSwingComponent().setSelectionStart(tempDir.length()-temp_location.length());
    		txtTempDir.getSwingComponent().setSelectionEnd(tempDir.length());
    		setTempDir(txtTempDir.getText());
    		txtTempDir.getSwingComponent().requestFocus();
    	}
	}

    public String getTempDir() {
        return getApplicationDescriptionType().getScratchWorkingDirectory();
    }

    public void setTempDir(String tempDir) {
    	getApplicationDescriptionType().setScratchWorkingDirectory(tempDir);
        updateDialogStatus();
    }

    public void close() {
        getDialog().setVisible(false);
    }

    public void saveApplicationDescription() {
        try {
			getRegistry().saveDeploymentDescription(getServiceName(), getHostName(), getShellApplicationDescription());
			if (!isNewDescritor() && (!getServiceName().equals(getOriginalService()) || !getHostName().equals(getOriginalHost()))) {
				try {
					getRegistry().deleteDeploymentDescription(getOriginalService(),
							getOriginalHost(),getOriginalDeploymentDescription().getType()
							.getApplicationName().getStringValue());
				} catch (RegistryException e) {
					engine.getGUI().getErrorWindow().error(e);
				}
			}
			setApplicationDescCreated(true);
		} catch (RegistryException e) {
			engine.getGUI().getErrorWindow().error(e);
		}
    }

    public boolean isApplicationDescCreated() {
        return applcationDescCreated;
    }

    public void setApplicationDescCreated(boolean applicationDescCreated) {
        this.applcationDescCreated = applicationDescCreated;
    }

    private void setError(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().equals("")) {
            lblError.setText("");
        } else {
            lblError.setText(errorMessage.trim());
        }
    }

    private void updateDialogStatus() {
        String message = null;
        try {
            validateDialog();
        } catch (Exception e) {
            message = e.getLocalizedMessage();
        }
        okButton.setEnabled(message == null);
        setError(message);
    }

    private void validateDialog() throws Exception {
        if (getApplicationName() == null || getApplicationName().trim().equals("")) {
            throw new Exception("Name of the application cannot be empty!!!");
        }

        List<ApplicationDeploymentDescription> deploymentDescriptions = null;
        try {
            deploymentDescriptions = getRegistry().searchDeploymentDescription(getServiceName(), getHostName(),
                    Pattern.quote(getApplicationName()));
        } catch (RegistryException e) {
            throw e;
        }
        if (deploymentDescriptions.size() > 0 && (isNewDescritor() || (!getServiceName().equals(getOriginalService()) || !getHostName().equals(getOriginalHost())))) {
            throw new Exception("Application name already exists for the selected service & host!!!");
        }

        if (getExecutablePath() == null || getExecutablePath().trim().equals("")) {
            throw new Exception("Executable path cannot be empty!!!");
        }

        if (getTempDir() == null || getTempDir().trim().equals("")) {
            throw new Exception("Temporary directory location cannot be empty!!!");
        }

        if (getServiceName() == null || getServiceName().trim().equals("")) {
            throw new Exception("Please select/create service to bind to this deployment description");
        }

        if (getHostName() == null || getHostName().trim().equals("")) {
            throw new Exception("Please select/create host to bind to this deployment description");
        }

    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
        updateDialogStatus();
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
        HostDescription hostDescription;
		try {
			hostDescription = registry.getHostDescription(hostName);
			if (hostDescription.getType() instanceof GlobusHostType){
	        	getShellApplicationDescription().getType().changeType(GramApplicationDeploymentType.type);
	        }else{
	        	getShellApplicationDescription().getType().changeType(ApplicationDeploymentDescriptionType.type);
	        }
			btnHostAdvanceOptions.setVisible(hostDescription.getType() instanceof GlobusHostType);
		} catch (RegistryException e) {
			//not there - ouch
		}
        
        updateDialogStatus();
    }

    private void updateServiceName() {
        if (cmbServiceName.getSelectedItem() != null) {
            setServiceName(cmbServiceName.getSelectedItem().toString());
        }
    }

    private void updateHostName() {
        if (cmbHostName.getSelectedItem() != null) {
            setHostName(cmbHostName.getSelectedItem().toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmbServiceName) {
            updateServiceName();
        }
        if (e.getSource() == cmbHostName) {
            updateHostName();
        }
        if (e.getSource() == txtAppName) {
            setApplicationName(txtAppName.getText());
        }
        if (e.getSource() == txtExecPath) {
            setExecutablePath(txtExecPath.getText());
        }
        if (e.getSource() == txtTempDir) {
            setTempDir(txtTempDir.getText());
        }
    }

    public AiravataRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(AiravataRegistry registry) {
        this.registry = registry;
    }

	public boolean isNewDescritor() {
		return newDescritor;
	}

	public void setNewDescritor(boolean newDescritor) {
		this.newDescritor = newDescritor;
	}

	public ApplicationDeploymentDescription getOriginalDeploymentDescription() {
		return originalDeploymentDescription;
	}

	public void setOriginalDeploymentDescription(
			ApplicationDeploymentDescription originalDeploymentDescription) {
		this.originalDeploymentDescription = originalDeploymentDescription;
	}

	public String getOriginalService() {
		return originalService;
	}

	public void setOriginalService(String originalService) {
		this.originalService = originalService;
	}

	public String getOriginalHost() {
		return originalHost;
	}

	public void setOriginalHost(String originalHost) {
		this.originalHost = originalHost;
	}

}