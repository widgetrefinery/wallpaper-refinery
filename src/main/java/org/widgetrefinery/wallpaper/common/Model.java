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

package org.widgetrefinery.wallpaper.common;

import org.widgetrefinery.util.StringUtil;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.wallpaper.event.SetInputFileEvent;
import org.widgetrefinery.wallpaper.event.SetWorkingDirectoryEvent;
import org.widgetrefinery.wallpaper.os.OSSupport;
import org.widgetrefinery.wallpaper.os.OSUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Since: 4/11/12 9:57 PM
 */
public class Model {
    private static final Logger logger = Logger.getLogger(Model.class.getName());

    private final EventBus eventBus;
    private       File     workingDirectory;
    private       File     inputFile;
    private       File     outputFile;
    private       int      previewSize;
    private       boolean  configOS;
    private       boolean  refreshOS;

    public Model(final EventBus eventBus) {
        this(eventBus, new File(System.getProperty("user.dir")));
    }

    public Model(final EventBus eventBus, final File workingDirectory) {
        this.eventBus = eventBus;
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
        this.eventBus.fireEvent(new SetWorkingDirectoryEvent(this.workingDirectory));
    }

    public File getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(final File inputFile) {
        this.inputFile = inputFile;
        this.eventBus.fireEvent(new SetInputFileEvent(this.inputFile));
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

    public Error process(final boolean overwrite) {
        Error error = null;
        File input = getInputFile();
        File output = getOutputFile();
        if (null == input || null == output) {
            error = Error.NO_INPUT;
        } else if (input.equals(output)) {
            error = Error.SAME_INPUT_OUTPUT;
        } else if (output.exists() && !overwrite) {
            error = Error.OUTPUT_EXISTS;
        } else {
            try {
                ImageUtil imageUtil = new ImageUtil();
                BufferedImage image = imageUtil.formatImage(input);
                imageUtil.saveImage(image, output, overwrite);
                OSSupport osSupport = OSUtil.getOSSupport();
                if (null != osSupport) {
                    if (isConfigOS()) {
                        osSupport.updateWallpaperSettings(output);
                    }
                    if (isRefreshOS()) {
                        osSupport.reloadWallpaperSettings();
                    }
                }
            } catch (Exception e) {
                error = Error.OTHER;
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(StringUtil.format("failed to process image", e));
                }
            }
        }
        return error;
    }

    public static enum Error {
        NO_INPUT, SAME_INPUT_OUTPUT, OUTPUT_EXISTS, OTHER
    }
}
