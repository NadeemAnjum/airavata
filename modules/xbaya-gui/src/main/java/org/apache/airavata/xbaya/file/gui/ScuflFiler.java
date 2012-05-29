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

package org.apache.airavata.xbaya.file.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.airavata.common.utils.IOUtil;
import org.apache.airavata.xbaya.XBayaConstants;
import org.apache.airavata.xbaya.XBayaEngine;
import org.apache.airavata.xbaya.file.XBayaPathConstants;
import org.apache.airavata.xbaya.graph.GraphException;
import org.apache.airavata.xbaya.gui.ErrorMessages;
import org.apache.airavata.xbaya.scufl.script.ScuflScript;
import org.apache.airavata.xbaya.wf.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScuflFiler {

    private static Logger logger = LoggerFactory.getLogger(ScuflFiler.class);

    private XBayaEngine engine;

    private JFileChooser scuflFileChooser;

    private final FileFilter scuflFileFilter = new FileFilter() {
        @Override
        public String getDescription() {
            return "Scufl Scripts";
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String name = file.getName();
            if (name.endsWith(XBayaConstants.SCUFL_SCRIPT_SUFFIX)) {
                return true;
            }
            return false;
        }
    };

    /**
     * Constructs a ScuflFiler.
     * 
     * @param engine
     */
    public ScuflFiler(XBayaEngine engine) {
        this.engine = engine;
        this.scuflFileChooser = new JFileChooser(XBayaPathConstants.SCUFL_SCRIPT_DIRECTORY);
        this.scuflFileChooser.addChoosableFileFilter(this.scuflFileFilter);
    }

    /**
     * Exports a Scufl script to the local file
     */
    public void exportScuflScript() {
        Workflow workflow = this.engine.getWorkflow();
        ScuflScript script = new ScuflScript(workflow, this.engine.getConfiguration());

        // Check if there is any errors in the workflow first.
        ArrayList<String> warnings = new ArrayList<String>();
        if (!script.validate(warnings)) {
            StringBuilder buf = new StringBuilder();
            for (String warning : warnings) {
                buf.append("- ");
                buf.append(warning);
                buf.append("\n");
            }
            this.engine.getErrorWindow().warning(buf.toString());
            return;
        }
        int returnVal = this.scuflFileChooser.showSaveDialog(this.engine.getGUI().getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = this.scuflFileChooser.getSelectedFile();
            logger.info(file.getPath());

            // Put ".py" at the end of the file name
            String path = file.getPath();
            if (!path.endsWith(XBayaConstants.SCUFL_SCRIPT_SUFFIX)) {
                file = new File(path + XBayaConstants.SCUFL_SCRIPT_SUFFIX);
            }

            try {
                // Create the script.
                script.create();
                // Write to a file
                IOUtil.writeToFile(script.getScript(), file);
            } catch (IOException e) {
                this.engine.getErrorWindow().error(ErrorMessages.WRITE_FILE_ERROR, e);
            } catch (GraphException e) {
                this.engine.getErrorWindow().error(ErrorMessages.GRAPH_FORMAT_ERROR, e);
            } catch (RuntimeException e) {
                this.engine.getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
            } catch (Error e) {
                this.engine.getErrorWindow().error(ErrorMessages.UNEXPECTED_ERROR, e);
            }
        }

    }
}