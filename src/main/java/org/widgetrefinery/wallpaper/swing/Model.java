/*
 * Copyright (C) 2012  Widget Refinery
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.widgetrefinery.wallpaper.swing;

import java.io.File;

/**
 * Since: 4/11/12 9:57 PM
 */
public class Model {
    private File    workingDirectory;
    private File    inputFile;
    private File    outputFile;
    private int     previewSize;
    private boolean configOS;
    private boolean refreshOS;

    public Model() {
        this(new File(System.getProperty("user.dir")));
    }

    public Model(final File workingDirectory) {
        setWorkingDirectory(workingDirectory);
        setPreviewSize(200);
    }

    public File getWorkingDirectory() {
        return this.workingDirectory;
    }

    public void setWorkingDirectory(final File workingDirectory) {
        if (null != workingDirectory && workingDirectory.isFile()) {
            this.workingDirectory = workingDirectory.getParentFile();
            setInputFile(workingDirectory);
        } else {
            this.workingDirectory = workingDirectory;
            setInputFile(null);
        }
    }

    public File getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(final File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    public void setOutputFile(final File outputFile) {
        this.outputFile = outputFile;
    }

    public int getPreviewSize() {
        return this.previewSize;
    }

    public void setPreviewSize(final int previewSize) {
        this.previewSize = previewSize;
    }

    public boolean isConfigOS() {
        return this.configOS;
    }

    public void setConfigOS(final boolean configOS) {
        this.configOS = configOS;
    }

    public boolean isRefreshOS() {
        return this.refreshOS;
    }

    public void setRefreshOS(final boolean refreshOS) {
        this.refreshOS = refreshOS;
    }
}
