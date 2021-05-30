/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.DYWriterException;
import com.osiris.dyml.utils.UtilsDYModule;
import com.osiris.dyml.utils.UtilsTimeStopper;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Responsible for parsing and writing the provided modules to file.
 */
class DYWriter {

    public void parse(DreamYaml yaml, boolean overwrite, boolean reset) throws DYWriterException, IOException {
        UtilsTimeStopper timer = new UtilsTimeStopper();
        timer.start();
        if (yaml.isDebugEnabled()) {
            System.out.println();
            System.out.println("Started writing yaml file: " + yaml.getFile().getName() + " at " + new Date());
        }

        File file = yaml.getFile();
        if (file == null) throw new DYWriterException("File is null! Make sure to load it at least once!");
        if (!file.exists()) throw new DYWriterException("File '" + file.getName() + "' doesn't exist!");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file), 32768); // TODO compare speed with def buffer
        writer.write(""); // Clear old content

        if (reset) return;

        List<DYModule> modulesToSave = new ArrayList<>();
        if (overwrite) {
            modulesToSave = yaml.getAllInEdit();
            if (modulesToSave.isEmpty())
                throw new DYWriterException("Failed to write modules to file: There are no modules in the 'inEditModules list' for file '" + file.getName() + "' ! Nothing to write!");
        } else {
            modulesToSave = new UtilsDYModule().createUnifiedList(yaml.getAllInEdit(), yaml.getAllLoaded());
            if (modulesToSave.isEmpty())
                throw new DYWriterException("Failed to write modules to file: There are no modules in the list for file '" + file.getName() + "' ! Nothing to write!");
        }


        DYModule lastModule = new DYModule(); // Create an empty module as start point
        for (DYModule m :
                modulesToSave) {
            parseModule(writer, m, lastModule);
            lastModule = m;
        }

        timer.stop();
        if (yaml.isDebugEnabled()) {
            System.out.println();
            System.out.println("Finished writing of " + yaml.getFile().getName() + " at " + new Date());
            System.out.println("Operation took " + timer.getFormattedMillis() + "ms or " + timer.getFormattedSeconds() + "s");
        }
    }

    /**
     * Writes an in-memory {@link DYModule} object to file.
     *
     * @param writer       the writer to use.
     * @param module       the current module to write.
     * @param beforeModule the last already written module.
     */
    private void parseModule(BufferedWriter writer,
                             DYModule module,
                             DYModule beforeModule) throws IOException {
        int keysSize = module.getKeys().size();
        int beforeKeysSize = beforeModule.getKeys().size();
        String currentKey; // The current key of the current module
        String currentBeforeKey; // The current key of the before module
        for (int i = 0; i < keysSize; i++) { // Go through each key of the module

            // Get current modules key and beforeModules key.
            // It may happen that the beforeModule has less keys, or no keys at all,
            // so deal with that:
            currentKey = module.getKeyByIndex(i);
            if (i < beforeKeysSize && !beforeModule.getKeys().isEmpty())
                currentBeforeKey = beforeModule.getKeyByIndex(i);
            else
                currentBeforeKey = "";

            // Only write this key, if its not equal to the currentBeforeKey
            // or... the other part is hard to explain.
            if (!currentKey.equals(currentBeforeKey) || (i != 0 && !module.getKeyByIndex(i - 1).equals(beforeModule.getKeyByIndex(i - 1)))) {

                String spaces = "";
                for (int j = 0; j < i; j++) { // The current keys index/position in the list defines how much spaces are needed.
                    spaces = spaces + "  ";
                }

                if (module.getComments() != null && i == (keysSize - 1)) // Only write comments to the last key in the list
                    for (String comment :
                            module.getComments()) {
                        // Adds support for Strings containing \n to split up comments
                        BufferedReader bufReader = new BufferedReader(new StringReader(comment));
                        String commentLine = null;
                        boolean isMultiline = false;
                        while ((commentLine = bufReader.readLine()) != null) {
                            isMultiline = true;
                            writer.write(spaces + "# " + commentLine);
                            writer.newLine();
                            writer.flush();
                        }

                        if (!isMultiline) {
                            writer.write(spaces + "# " + comment);
                            writer.newLine();
                            writer.flush();
                        }
                    }

                writer.write(spaces + currentKey + ": ");

                if (module.getValues() != null && i == (keysSize - 1)) { // Only write values to the last key in the list
                    if (!module.getValues().isEmpty()) { // Write values if they exist, else write defaults, else write nothing
                        if (module.getValues().size() == 1) { // Even if we only got one DYModule, it written as a list
                            DYValue value = module.getValue();
                            if (value != null) { // Only write if its not null
                                if (value.asString() != null) writer.write(value.asString());
                                if (value.hasComment())
                                    writer.write(" # " + value.getComment()); // Append side comment to value
                            }

                            writer.newLine();
                            writer.flush();
                        } else { // This means we got multiple values, aka a list
                            writer.newLine();
                            for (int j = 0; j < module.getValues().size(); j++) {
                                DYValue value = module.getValueByIndex(j);
                                if (value != null) {
                                    writer.write(spaces + "  - ");
                                    if (value.asString() != null) writer.write(value.asString()); // Append the value
                                    if (value.hasComment())
                                        writer.write(" # " + value.getComment()); // Append side comment to value
                                }

                                writer.newLine();
                                writer.flush();
                            }
                        }
                    } else if (module.isWriteDefaultWhenValuesListIsEmptyEnabled()) {
                        if (module.getDefaultValues() != null && !module.getDefaultValues().isEmpty()) {
                            if (module.getDefaultValues().size() == 1) {
                                DYValue defValue = module.getDefaultValue();
                                if (defValue != null) {
                                    if (defValue.asString() != null) writer.write(defValue.asString());
                                    if (defValue.hasComment())
                                        writer.write(" # " + defValue.getComment()); // Append side comment to value
                                }

                                writer.newLine();
                                writer.flush();
                            } else {
                                writer.newLine();
                                for (int j = 0; j < module.getDefaultValues().size(); j++) {
                                    DYValue value = module.getDefaultValueByIndex(j);
                                    if (value != null) {
                                        writer.write(spaces + "  - ");
                                        if (value.asString() != null)
                                            writer.write(value.asString()); // Append the value
                                        if (value.hasComment())
                                            writer.write(" # " + value.getComment()); // Append side comment to value
                                    }

                                    writer.newLine();
                                    writer.flush();
                                }
                            }
                        } else {
                            writer.newLine();
                            writer.flush();
                        }
                    }

                } else {
                    writer.newLine();
                    writer.flush();
                }
            }
        }
    }
}
