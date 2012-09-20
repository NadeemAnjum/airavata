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

package org.apache.airavata.xbaya.appwrapper;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.apache.airavata.commons.gfac.type.ApplicationDeploymentDescription;
import org.apache.airavata.registry.api.Registry;
import org.apache.airavata.schemas.gfac.ApplicationDeploymentDescriptionType;
import org.apache.airavata.schemas.gfac.JobTypeType;
import org.apache.airavata.xbaya.gui.XBayaComboBox;

public class ApplicationDescriptionAdvancedOptionDialog extends JDialog {
    private static final long serialVersionUID = 3920479739097405014L;
    private JTextField txtInputDir;
    private JTextField txtWorkingDir;
    private JTextField txtOutputDir;
    private JTextField txtSTDIN;
    private JTextField txtSTDOUT;
    private JTextField txtSTDERR;
    private XBayaComboBox jobType;
    private JTextField projectAccountNumber;
    private JTextField projectAccountDescription;
    private JTextField queueType;
    private JTextField queueName;
    private JTextField rslParameterName;
    private JTextField rslParameterValue;
    private JTable tblEnv;
    private ApplicationDeploymentDescription shellApplicationDescription;
    private DefaultTableModel defaultTableModel;
    private boolean tableModelChanging = false;
    private JButton btnDeleteVariable;
    private JButton okButton;
    private Registry registry;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ApplicationDescriptionAdvancedOptionDialog dialog = new ApplicationDescriptionAdvancedOptionDialog(null,
                    null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public ApplicationDescriptionAdvancedOptionDialog(Registry registry, ApplicationDeploymentDescription descriptor) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent arg0) {
                loadApplicationDescriptionAdvancedOptions();
            }
        });
        setRegistry(registry);
        setShellApplicationDescription(descriptor);
        initGUI();
    }

    public void open() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    protected ApplicationDescriptionAdvancedOptionDialog getDialog() {
        return this;
    }

    public void close() {
        getDialog().setVisible(false);
    }

    private void initGUI() {
        setTitle("Application Description Advance Options");
        setModal(true);
        setBounds(100, 100, 654, 417);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                okButton = new JButton("Update");
                okButton.setActionCommand("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveApplicationDescriptionAdvancedOptions();
                        close();
                    }
                });
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        close();
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
        {
            JPanel panel = new JPanel();
            getContentPane().add(panel, BorderLayout.CENTER);
            JLabel lblWorkingDirectory = new JLabel("Working Directory");
            JLabel lbljobType = new JLabel("Job Type");
            JLabel lblProjectAccountNumber = new JLabel("Project Account Number");
            JLabel lblProjectAccountDescription = new JLabel("Project Account Description");
            JLabel lblQueueType = new JLabel("Queue Type");
            JLabel lblQueueName = new JLabel("Queue Name");
            JLabel lblRSLParameterName = new JLabel("RSL Parameter Name");
            JLabel lslRSLParameterValue = new JLabel("RSL Parameter Value");
            JLabel lblInputDirectory = new JLabel("Input directory");

            txtInputDir = new JTextField();
            txtInputDir.setColumns(10);
            txtWorkingDir = new JTextField();
            txtWorkingDir.setColumns(10);
            JLabel lblLocations = new JLabel("Locations");
            lblLocations.setFont(new Font("Tahoma", Font.BOLD, 11));

            txtOutputDir = new JTextField();
            txtOutputDir.setColumns(10);

            JLabel lblOutputDirectory = new JLabel("Output directory");

            JSeparator separator = new JSeparator();

            JLabel label = new JLabel("Program data");
            label.setFont(new Font("Tahoma", Font.BOLD, 11));

            JLabel lblStdin = new JLabel("STDIN");
            lblStdin.setHorizontalAlignment(SwingConstants.TRAILING);

            txtSTDIN = new JTextField();
            txtSTDIN.setColumns(10);

            JLabel lblStdout = new JLabel("STDOUT");
            lblStdout.setHorizontalAlignment(SwingConstants.TRAILING);

            txtSTDOUT = new JTextField();
            txtSTDOUT.setColumns(10);

            JLabel lblStderr = new JLabel("STDERR");
            lblStderr.setHorizontalAlignment(SwingConstants.TRAILING);

            txtSTDERR = new JTextField();
            txtSTDERR.setColumns(10);

            JLabel other = new JLabel("Other");
            other.setFont(new Font("Tahoma", Font.BOLD, 11));
            lblStdin.setHorizontalAlignment(SwingConstants.TRAILING);

            projectAccountNumber = new JTextField();
            projectAccountNumber.setColumns(10);

            projectAccountDescription = new JTextField();
            projectAccountDescription.setColumns(10);

            queueName = new JTextField();
            queueName.setColumns(10);

            queueType = new JTextField();
            queueType.setColumns(10);

            rslParameterName = new JTextField();
            rslParameterName.setColumns(10);

            rslParameterValue = new JTextField();
            rslParameterValue.setColumns(10);

            this.jobType = new XBayaComboBox(new DefaultComboBoxModel(new String[]
                    {JobTypeType.OPEN_MP.toString(),JobTypeType.MPI.toString(),JobTypeType.SERIAL.toString()}));
            this.jobType.setEditable(false);

            JSeparator separator_1 = new JSeparator();
            separator_1.setOrientation(SwingConstants.VERTICAL);

            JLabel lblEnvironmentalVariables = new JLabel("Environmental Variables");
            lblEnvironmentalVariables.setFont(new Font("Tahoma", Font.BOLD, 11));

            JScrollPane scrollPane = new JScrollPane();

            btnDeleteVariable = new JButton("Delete variable");
            btnDeleteVariable.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteSelectedRows();
                }
            });
            btnDeleteVariable.setEnabled(false);
            GroupLayout gl_panel = new GroupLayout(panel);
            gl_panel.setHorizontalGroup(gl_panel
                    .createParallelGroup(Alignment.LEADING)
                    .addGroup(
                            gl_panel.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(
                                            gl_panel.createParallelGroup(Alignment.LEADING)
                                                    .addGroup(
                                                            gl_panel.createSequentialGroup()
                                                                    .addComponent(lblLocations,
                                                                            GroupLayout.DEFAULT_SIZE, 190,
                                                                            Short.MAX_VALUE).addGap(135))
                                                    .addGroup(
                                                            gl_panel.createSequentialGroup()
                                                                    .addGroup(
                                                                            gl_panel.createParallelGroup(
                                                                                    Alignment.TRAILING)
                                                                                    .addGroup(
                                                                                            gl_panel.createSequentialGroup()
                                                                                                    .addComponent(
                                                                                                            lblStderr,
                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                            80,
                                                                                                            GroupLayout.PREFERRED_SIZE)
                                                                                                    .addGap(18)
                                                                                                    .addComponent(
                                                                                                            txtSTDERR,
                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                            181,
                                                                                                            GroupLayout.PREFERRED_SIZE))
                                                                                    .addGroup(
                                                                                            gl_panel.createSequentialGroup()
                                                                                                    .addComponent(
                                                                                                            lblStdout,
                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                            72,
                                                                                                            GroupLayout.PREFERRED_SIZE)
                                                                                                    .addGap(18)
                                                                                                    .addComponent(
                                                                                                            txtSTDOUT,
                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                            181,
                                                                                                            GroupLayout.PREFERRED_SIZE))
                                                                                    .addGroup(
                                                                                            gl_panel.createSequentialGroup()
                                                                                                    .addComponent(
                                                                                                            lblStdin,
                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                            86,
                                                                                                            GroupLayout.PREFERRED_SIZE)
                                                                                                    .addGap(18)
                                                                                                    .addComponent(
                                                                                                            txtSTDIN,
                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                            181,
                                                                                                            GroupLayout.PREFERRED_SIZE)))
                                                                    .addPreferredGap(ComponentPlacement.UNRELATED))
                                                    .addComponent(separator, GroupLayout.PREFERRED_SIZE, 293,
                                                            GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(
                                                            gl_panel.createSequentialGroup()
                                                                    .addGroup(
                                                                            gl_panel.createParallelGroup(
                                                                                    Alignment.TRAILING)
                                                                                    .addGroup(
                                                                                            gl_panel.createSequentialGroup()
                                                                                                    .addComponent(
                                                                                                            lblWorkingDirectory)
                                                                                                    .addGap(18))
                                                                                    .addGroup(
                                                                                            gl_panel.createSequentialGroup()
                                                                                                    .addComponent(
                                                                                                            lblInputDirectory)
                                                                                                    .addGap(17))
                                                                                    .addGroup(
                                                                                            gl_panel.createSequentialGroup()
                                                                                                    .addComponent(
                                                                                                            lblOutputDirectory)
                                                                                                    .addGap(18)))
                                                                    .addGroup(
                                                                            gl_panel.createParallelGroup(
                                                                                    Alignment.LEADING, false)
                                                                                    .addComponent(txtOutputDir)
                                                                                    .addComponent(txtInputDir)
                                                                                    .addComponent(txtWorkingDir,
                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                            179,
                                                                                            GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(label, GroupLayout.PREFERRED_SIZE, 227,
                                                            GroupLayout.PREFERRED_SIZE))
                                    .addGap(2)
                                    .addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                            GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.UNRELATED)
                                    .addGroup(
                                            gl_panel.createParallelGroup(Alignment.TRAILING)
                                                    .addGroup(
                                                            gl_panel.createParallelGroup(Alignment.LEADING)
                                                                    .addComponent(lblEnvironmentalVariables)
                                                                    .addGroup(
                                                                            gl_panel.createSequentialGroup()
                                                                                    .addGap(10)
                                                                                    .addComponent(scrollPane,
                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                            258,
                                                                                            GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(btnDeleteVariable)).addContainerGap()));
            gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
                    gl_panel.createSequentialGroup()
                            .addGroup(
                                    gl_panel.createParallelGroup(Alignment.LEADING)
                                            .addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 212,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addGroup(
                                                    gl_panel.createSequentialGroup()
                                                            .addComponent(lblLocations)
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addGroup(
                                                                    gl_panel.createParallelGroup(Alignment.BASELINE)
                                                                            .addComponent(lblWorkingDirectory)
                                                                            .addComponent(txtWorkingDir,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addGroup(
                                                                    gl_panel.createParallelGroup(Alignment.BASELINE)
                                                                            .addComponent(lblInputDirectory)
                                                                            .addComponent(txtInputDir,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addGroup(
                                                                    gl_panel.createParallelGroup(Alignment.BASELINE)
                                                                            .addComponent(txtOutputDir,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(lblOutputDirectory))
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addComponent(separator, GroupLayout.PREFERRED_SIZE, 2,
                                                                    GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addComponent(label)
                                                            .addGap(3)
                                                            .addGroup(
                                                                    gl_panel.createParallelGroup(Alignment.BASELINE)
                                                                            .addComponent(lblStdin)
                                                                            .addComponent(txtSTDIN,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE))
                                                            .addGap(9)
                                                            .addGroup(
                                                                    gl_panel.createParallelGroup(Alignment.BASELINE)
                                                                            .addComponent(lblStdout)
                                                                            .addComponent(txtSTDOUT,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE))
                                                            .addGap(9)
                                                            .addGroup(
                                                                    gl_panel.createParallelGroup(Alignment.BASELINE)
                                                                            .addComponent(lblStderr)
                                                                            .addComponent(txtSTDERR,
                                                                                    GroupLayout.PREFERRED_SIZE,
                                                                                    GroupLayout.DEFAULT_SIZE,
                                                                                    GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(
                                                    gl_panel.createSequentialGroup()
                                                            .addComponent(lblEnvironmentalVariables)
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 153,
                                                                    GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(ComponentPlacement.RELATED)
                                                            .addComponent(btnDeleteVariable)))
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

            tblEnv = new JTable();
            tblEnv.setFillsViewportHeight(true);
            scrollPane.setViewportView(tblEnv);
            tblEnv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            defaultTableModel = new DefaultTableModel(new Object[][] { { null, null }, }, new String[] { "Name",
                    "Value" }) {
                Class[] columnTypes = new Class[] { String.class, String.class };

                public Class getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }
            };
            tblEnv.setModel(defaultTableModel);
            defaultTableModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent arg0) {
                    if (!tableModelChanging) {
                        addNewRowIfLastIsNotEmpty();
                    }
                }

            });
            tblEnv.getColumnModel().getColumn(0).setPreferredWidth(67);
            tblEnv.getColumnModel().getColumn(1).setPreferredWidth(158);
            ListSelectionModel selectionModel = tblEnv.getSelectionModel();
            selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            selectionModel.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    btnDeleteVariable.setEnabled(tblEnv.getSelectedRows().length > 0);
                }

            });
            gl_panel.setAutoCreateGaps(true);
            gl_panel.setAutoCreateContainerGaps(true);
            panel.setLayout(gl_panel);
        }
        setResizable(false);
        getRootPane().setDefaultButton(okButton);
    }

    private void deleteSelectedRows() {
        // TODO confirm deletion of selected rows
        int selectedRow = tblEnv.getSelectedRow();
        while (selectedRow >= 0) {
            defaultTableModel.removeRow(selectedRow);
            selectedRow = tblEnv.getSelectedRow();
        }
        addNewRowIfLastIsNotEmpty();
    }

    public ApplicationDeploymentDescription getApplicationDescription() {
        return shellApplicationDescription;
    }

    public ApplicationDeploymentDescriptionType getShellApplicationDescriptionType() {
        return (ApplicationDeploymentDescriptionType)shellApplicationDescription.getType();
    }
    
    public void setShellApplicationDescription(ApplicationDeploymentDescription shellApplicationDescription) {
        this.shellApplicationDescription = shellApplicationDescription;
    }

    private void addNewRowIfLastIsNotEmpty() {
        Object varName = null;
        if (defaultTableModel.getRowCount() > 0) {
            varName = defaultTableModel.getValueAt(defaultTableModel.getRowCount() - 1, 0);
        }
        if (defaultTableModel.getRowCount() == 0 || (varName != null && !varName.equals(""))) {
            defaultTableModel.addRow(new Object[] { null, null });
        }
    }

    private void saveApplicationDescriptionAdvancedOptions() {
    	getShellApplicationDescriptionType().setStaticWorkingDirectory(txtWorkingDir.getText());
    	getShellApplicationDescriptionType().setInputDataDirectory(txtInputDir.getText());
    	getShellApplicationDescriptionType().setOutputDataDirectory(txtOutputDir.getText());
    	getShellApplicationDescriptionType().setStandardInput(txtSTDIN.getText());
    	getShellApplicationDescriptionType().setStandardOutput(txtSTDOUT.getText());
    	getShellApplicationDescriptionType().setStandardError(txtSTDERR.getText());

//    	getShellApplicationDescriptionType().setEnv(Apll.Factory.newInstance().getEnv());
//        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
//            String varName = (String) defaultTableModel.getValueAt(i, 0);
//            if (varName != null && !varName.equals("")) {
//                String varValue = (String) defaultTableModel.getValueAt(i, 1);
//                getShellApplicationDescriptionType().getEnv().addNewEntry().setKey(varName);
//                getShellApplicationDescriptionType().getEnv().addNewEntry().setValue(varValue);
//            }
//        }
    }

    private void loadApplicationDescriptionAdvancedOptions() {
        txtWorkingDir.setText(getShellApplicationDescriptionType().getScratchWorkingDirectory());
        txtInputDir.setText(getShellApplicationDescriptionType().getInputDataDirectory());
        txtOutputDir.setText(getShellApplicationDescriptionType().getOutputDataDirectory());
        txtSTDIN.setText(getShellApplicationDescriptionType().getStandardInput());
        txtSTDOUT.setText(getShellApplicationDescriptionType().getStandardOutput());
        txtSTDERR.setText(getShellApplicationDescriptionType().getStandardError());
        tableModelChanging = true;
//      todo handle other parameters previous sent in the Entry Now they are defined
//        ShellApplicationDeploymentType.Env.Entry[] entry = getShellApplicationDescriptionType().getEnv().getEntryArray();
//
//        Map<String, String> env = null;
//        for (int i = 0; i < entry.length; i++) {
//            String key = getShellApplicationDescriptionType().getEnv().getEntryArray(i).getKey();
//            String value = getShellApplicationDescriptionType().getEnv().getEntryArray(i).getValue();
//            env.put(key, value);
//        }

//        while (defaultTableModel.getRowCount() > 0) {
//            defaultTableModel.removeRow(0);
//        }
//        if (env != null) {
//            for (String varName : env.keySet()) {
//                defaultTableModel.addRow(new String[] { varName, env.get(varName) });
//            }
//        }
        addNewRowIfLastIsNotEmpty();
        tableModelChanging = false;
    }

    private String getNotNullValue(String value) {
        return value == null ? "" : value;
    }

    // private void setError(String errorMessage){
    // if (errorMessage==null || errorMessage.trim().equals("")){
    // lblError.setText("");
    // }else{
    // lblError.setText(errorMessage.trim());
    // }
    // }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    // private void updateDialogStatus(){
    // String message=null;
    // try {
    // validateDialog();
    // } catch (Exception e) {
    // message=e.getLocalizedMessage();
    // }
    // okButton.setEnabled(message==null);
    // setError(message);
    // }
    //
    // private void validateDialog() throws Exception{
    // if (getApplicationName()==null || getApplicationName().trim().equals("")){
    // throw new Exception("Name of the application cannot be empty!!!");
    // }
    //
    // List<ApplicationDeploymentDescription> deploymentDescriptions=null;
    // try {
    // deploymentDescriptions = getJCRComponentRegistry().getRegistry().searchDeploymentDescription(getServiceName(),
    // getHostName(), Pattern.quote(getApplicationName()));
    // } catch (PathNotFoundException e) {
    // //what we want
    // } catch (Exception e){
    // throw e;
    // }
    // if (deploymentDescriptions.size()>0){
    // throw new Exception("Application descriptor with the given name already exists!!!");
    // }
    //
    // if (getExecutablePath()==null || getExecutablePath().trim().equals("")){
    // throw new Exception("Executable path cannot be empty!!!");
    // }
    //
    // if (getTempDir()==null || getTempDir().trim().equals("")){
    // throw new Exception("Temporary directory location cannot be empty!!!");
    // }
    //
    // if (getServiceName()==null || getServiceName().trim().equals("")){
    // throw new Exception("Please select/create service to bind to this deployment description");
    // }
    //
    // if (getHostName()==null || getHostName().trim().equals("")){
    // throw new Exception("Please select/create host to bind to this deployment description");
    // }
    //
    // }

}