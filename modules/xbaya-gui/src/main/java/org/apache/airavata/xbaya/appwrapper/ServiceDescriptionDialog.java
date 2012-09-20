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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.jcr.PathNotFoundException;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.airavata.commons.gfac.type.ServiceDescription;
import org.apache.airavata.registry.api.Registry;
import org.apache.airavata.registry.api.exception.RegistryException;
import org.apache.airavata.schemas.gfac.DataType;
import org.apache.airavata.schemas.gfac.InputParameterType;
import org.apache.airavata.schemas.gfac.OutputParameterType;
import org.apache.airavata.schemas.gfac.ParameterType;
import org.apache.airavata.schemas.gfac.ServiceDescriptionType;

public class ServiceDescriptionDialog extends JDialog {

    private static final long serialVersionUID = 2705760838264284423L;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblServiceName;
    private JTextField txtServiceName;
    private JTable tblParameters;
    private boolean serviceCreated = false;
    private JLabel lblError;
    private ServiceDescription serviceDescription;
    private JButton okButton;
    private JButton btnDeleteParameter;
    private DefaultTableModel defaultTableModel;
    private Registry registry;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ServiceDescriptionDialog dialog = new ServiceDescriptionDialog(null);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public ServiceDescriptionDialog(Registry registry) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent arg0) {
                String baseName = "Service";
                int i = 1;
                String defaultName = baseName + i;
                try {
                    while (getRegistry().getServiceDescription(defaultName) != null) {
                        defaultName = baseName + (++i);
                    }
                } catch (Exception e) {
                }
                txtServiceName.setText(defaultName);
                setServiceName(txtServiceName.getText());
            }
        });
        setRegistry(registry);
        initGUI();

    }

    public void open() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    protected ServiceDescriptionDialog getDialog() {
        return this;
    }

    private void initGUI() {
        setTitle("New Service Description");
        setBounds(100, 100, 463, 459);
        setModal(true);
        setLocationRelativeTo(null);
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(5);
        borderLayout.setHgap(5);
        getContentPane().setLayout(borderLayout);
        contentPanel.setBorder(null);
        getContentPane().add(contentPanel, BorderLayout.EAST);
        {
            lblServiceName = new JLabel("Service name");
        }

        txtServiceName = new JTextField();
        txtServiceName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setServiceName(txtServiceName.getText());
            }
        });
        txtServiceName.setColumns(10);

        JSeparator separator = new JSeparator();

        JLabel lblInputParameters = new JLabel("Service Parameters");
        lblInputParameters.setFont(new Font("Tahoma", Font.BOLD, 11));

        JScrollPane scrollPane = new JScrollPane();

        btnDeleteParameter = new JButton("Delete parameter");
        btnDeleteParameter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                deleteSelectedRows();
            }
        });
        btnDeleteParameter.setEnabled(false);
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel
                .setHorizontalGroup(gl_contentPanel
                        .createParallelGroup(Alignment.TRAILING)
                        .addGroup(
                                gl_contentPanel
                                        .createSequentialGroup()
                                        .addContainerGap(212, Short.MAX_VALUE)
                                        .addGroup(
                                                gl_contentPanel
                                                        .createParallelGroup(Alignment.LEADING)
                                                        .addGroup(
                                                                gl_contentPanel
                                                                        .createSequentialGroup()
                                                                        .addComponent(separator,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addContainerGap())
                                                        .addGroup(
                                                                gl_contentPanel
                                                                        .createParallelGroup(Alignment.TRAILING, false)
                                                                        .addGroup(
                                                                                gl_contentPanel
                                                                                        .createSequentialGroup()
                                                                                        .addGroup(
                                                                                                gl_contentPanel
                                                                                                        .createParallelGroup(
                                                                                                                Alignment.TRAILING)
                                                                                                        .addComponent(
                                                                                                                scrollPane,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                380,
                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(
                                                                                                                btnDeleteParameter))
                                                                                        .addGap(27))
                                                                        .addGroup(
                                                                                gl_contentPanel
                                                                                        .createSequentialGroup()
                                                                                        .addGroup(
                                                                                                gl_contentPanel
                                                                                                        .createParallelGroup(
                                                                                                                Alignment.LEADING)
                                                                                                        .addComponent(
                                                                                                                lblInputParameters)
                                                                                                        .addGroup(
                                                                                                                gl_contentPanel
                                                                                                                        .createSequentialGroup()
                                                                                                                        .addComponent(
                                                                                                                                lblServiceName)
                                                                                                                        .addGap(18)
                                                                                                                        .addComponent(
                                                                                                                                txtServiceName,
                                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                                309,
                                                                                                                                GroupLayout.PREFERRED_SIZE)))
                                                                                        .addGap(30))))));
        gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_contentPanel
                        .createSequentialGroup()
                        .addContainerGap(42, Short.MAX_VALUE)
                        .addGroup(
                                gl_contentPanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblServiceName)
                                        .addComponent(txtServiceName, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE).addGap(12).addComponent(lblInputParameters)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnDeleteParameter).addGap(78)));

        tblParameters = new JTable();
        tblParameters.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tblParameters.setFillsViewportHeight(true);
        defaultTableModel = new DefaultTableModel(new Object[][] { { null, null, null, null }, }, new String[] { "I/O",
                "Parameter Name", "Type", "Description" });
        tblParameters.setModel(defaultTableModel);
        defaultTableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent arg0) {
                int selectedRow = tblParameters.getSelectedRow();
                if (selectedRow != -1) {
                    Object parameterIOType = defaultTableModel.getValueAt(selectedRow, 0);
                    Object parameterDataType = defaultTableModel.getValueAt(selectedRow, 2);
                    if (parameterIOType == null || parameterIOType.equals("")) {
                        defaultTableModel.setValueAt(getIOStringList()[0], selectedRow, 0);
                    }
                    if (parameterDataType == null || parameterDataType.equals("")) {
                        defaultTableModel.setValueAt(getDataTypes()[0], selectedRow, 2);
                    }
                }
                addNewRowIfLastIsNotEmpty();
            }

        });
        TableColumn ioColumn = tblParameters.getColumnModel().getColumn(0);
        String[] ioStringList = getIOStringList();
        ioColumn.setCellEditor(new StringArrayComboBoxEditor(ioStringList));

        TableColumn datatypeColumn = tblParameters.getColumnModel().getColumn(2);
        String[] dataTypeStringList = getDataTypes();
        datatypeColumn.setCellEditor(new StringArrayComboBoxEditor(dataTypeStringList));

        TableColumn parameterNameCol = tblParameters.getColumnModel().getColumn(1);
        parameterNameCol.setPreferredWidth(190);
        scrollPane.setViewportView(tblParameters);
        ListSelectionModel selectionModel = tblParameters.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                btnDeleteParameter.setEnabled(tblParameters.getSelectedRows().length > 0);
            }

        });

        gl_contentPanel.setAutoCreateContainerGaps(true);
        gl_contentPanel.setAutoCreateGaps(true);
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            GridBagLayout gbl_buttonPane = new GridBagLayout();
            gbl_buttonPane.columnWidths = new int[] { 307, 136, 0 };
            gbl_buttonPane.rowHeights = new int[] { 33, 0 };
            gbl_buttonPane.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
            gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
            buttonPane.setLayout(gbl_buttonPane);

            lblError = new JLabel("");
            lblError.setForeground(Color.RED);
            GridBagConstraints gbc_lblError = new GridBagConstraints();
            gbc_lblError.insets = new Insets(0, 0, 0, 5);
            gbc_lblError.gridx = 0;
            gbc_lblError.gridy = 0;
            buttonPane.add(lblError, gbc_lblError);
            JPanel panel = new JPanel();
            GridBagConstraints gbc_panel = new GridBagConstraints();
            gbc_panel.anchor = GridBagConstraints.NORTHWEST;
            gbc_panel.gridx = 1;
            gbc_panel.gridy = 0;
            buttonPane.add(panel, gbc_panel);
            {
                okButton = new JButton("Save");
                okButton.setEnabled(false);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveServiceDescription();
                        close();
                    }
                });
                panel.add(okButton);
                okButton.setActionCommand("OK");
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setServiceCreated(false);
                        close();
                    }
                });
                panel.add(cancelButton);
                cancelButton.setActionCommand("Cancel");
            }
        }
        setResizable(false);
        getRootPane().setDefaultButton(okButton);
    }

    private String[] getIOStringList() {
        String[] ioStringList = new String[] { "Input", "Output" };
        return ioStringList;
    }

    private String[] getDataTypes() {
        String[] type = new String[DataType.Enum.table.lastInt()];
        for (int i = 1; i <= DataType.Enum.table.lastInt(); i++) {
            type[i - 1] = DataType.Enum.forInt(i).toString();
        }
        return type;
    }

    public boolean isServiceCreated() {
        return serviceCreated;
    }

    public void setServiceCreated(boolean serviceCreated) {
        this.serviceCreated = serviceCreated;
    }

    public ServiceDescription getServiceDescription() {
        if (serviceDescription == null) {
            serviceDescription = new ServiceDescription();
        }
        return serviceDescription;
    }

    public ServiceDescriptionType getServiceDescriptionType() {
        return getServiceDescription().getType();
    }

    public String getServiceName() {
        return getServiceDescription().getType().getName();
    }

    public void setServiceName(String serviceName) {
        getServiceDescription().getType().setName(serviceName);
        updateDialogStatus();
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
        if (getServiceName() == null || getServiceName().trim().equals("")) {
            throw new Exception("Name of the service cannot be empty!!!");
        }

        ServiceDescription serviceDescription2 = null;
        try {
            serviceDescription2 = getRegistry().getServiceDescription(Pattern.quote(getServiceName()));
        } catch (RegistryException e) {
            if (e.getCause() instanceof PathNotFoundException) {
                // non-existant name. just want we want
            } else {
                throw e;
            }
        }
        if (serviceDescription2 != null) {
            throw new Exception("Service descriptor with the given name already exists!!!");
        }
    }

    public void saveServiceDescription() {
        List<InputParameterType> inputParameters = new ArrayList<InputParameterType>();
        List<OutputParameterType> outputParameters = new ArrayList<OutputParameterType>();

        for (int i = 0; i < defaultTableModel.getRowCount(); i++) {
            String parameterName = (String) defaultTableModel.getValueAt(i, 1);
            String paramType = (String) defaultTableModel.getValueAt(i, 2);
            String parameterDescription = (String) defaultTableModel.getValueAt(i, 3);
            if (parameterName != null && !parameterName.trim().equals("")) {
                // todo how to handle Enum
                if (getIOStringList()[0].equals(defaultTableModel.getValueAt(i, 0))) {
                    InputParameterType parameter = InputParameterType.Factory.newInstance();
                    parameter.setParameterName(parameterName);
                    parameter.setParameterDescription(parameterDescription);
                    ParameterType parameterType = parameter.addNewParameterType();
                    parameterType.setType(DataType.Enum.forString(paramType));
                    parameterType.setName(paramType);
                    inputParameters.add(parameter);

                } else {
                    OutputParameterType parameter = OutputParameterType.Factory.newInstance();
                    parameter.setParameterName(parameterName);
                    parameter.setParameterDescription(parameterDescription);
                    ParameterType parameterType = parameter.addNewParameterType();
                    parameterType.setType(DataType.Enum.forString(paramType));
                    parameterType.setName(paramType);
                    outputParameters.add(parameter);
                }
            }
        }
        getServiceDescriptionType().setInputParametersArray(inputParameters.toArray(new InputParameterType[] {}));
        getServiceDescriptionType().setOutputParametersArray(outputParameters.toArray(new OutputParameterType[] {}));

        getRegistry().saveServiceDescription(getServiceDescription());
        setServiceCreated(true);
    }

    public void close() {
        getDialog().setVisible(false);
    }

    private void setError(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().equals("")) {
            lblError.setText("");
        } else {
            lblError.setText(errorMessage.trim());
        }
    }

    private void deleteSelectedRows() {
        // TODO confirm deletion of selected rows
        int selectedRow = tblParameters.getSelectedRow();
        while (selectedRow >= 0) {
            defaultTableModel.removeRow(selectedRow);
            selectedRow = tblParameters.getSelectedRow();
        }
        addNewRowIfLastIsNotEmpty();
    }

    private void addNewRowIfLastIsNotEmpty() {
        Object parameterName = defaultTableModel.getValueAt(defaultTableModel.getRowCount() - 1, 1);
        if (parameterName != null && !parameterName.equals("")) {
            defaultTableModel.addRow(new Object[] { null, null, null, null });
        }
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    private class StringArrayComboBoxEditor extends DefaultCellEditor {
        private static final long serialVersionUID = -304464739219209395L;

        public StringArrayComboBoxEditor(Object[] items) {
            super(new JComboBox(items));
        }
    }
}