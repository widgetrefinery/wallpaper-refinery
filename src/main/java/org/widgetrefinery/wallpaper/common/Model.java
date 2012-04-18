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

import org.widgetrefinery.util.BadUserInputException;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.wallpaper.event.SetInputFileEvent;
import org.widgetrefinery.wallpaper.event.SetWorkingDirectoryEvent;
import org.widgetrefinery.wallpaper.os.OSSupport;
import org.widgetrefinery.wallpaper.os.OSUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Manages the settings used throughout the application.
 *
 * @since 4/11/12 9:57 PM
 */
public class Model {
    private final EventBus eventBus;
    private       File     workingDirectory;
    private       File     inputFile;
    private       File     outputFile;
    private       int      thumbnailsPerRow;
    private       boolean  configOS;
    private       boolean  refreshOS;

    /**
     * Creates a new instance with the working directory set to the current directory.
     *
     * @param eventBus event bus to fire events on
     */
    public Model(final EventBus eventBus) {
        this(eventBus, new File(System.getProperty("user.dir")));
    }

    /**
     * Creates a new instance with a specific working directory.
     *
     * @param eventBus         event bus to fire events on
     * @param workingDirectory initial working directory
     */
    public Model(final EventBus eventBus, final File workingDirectory) {
        this.eventBus = eventBus;
        setWorkingDirectory(workingDirectory);
        setThumbnailsPerRow(4);
    }

    /**
     * Get the working directory. The gui will search this directory for
     * images to preview.
     *
     * @return working directory
     */
    public File getWorkingDirectory() {
        return this.workingDirectory;
    }

    /**
     * Set the working directory to a new location. If the working directory is
     * a file, it will be used as the input file while the parent directory
     * will be used as the actual working directory. Otherwise the working
     * directory will be used as is and the input file will be set to null.
     *
     * @param workingDirectory new working directory
     */
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

    /**
     * Get the input file. This is the image that will be used to generate the
     * wallpaper.
     *
     * @return input file
     */
    public File getInputFile() {
        return this.inputFile;
    }

    /**
     * Set the input file to a new value.
     *
     * @param inputFile new input file
     */
    public void setInputFile(final File inputFile) {
        this.inputFile = inputFile;
        this.eventBus.fireEvent(new SetInputFileEvent(this.inputFile));
    }

    /**
     * Get the output file. This is the file that the resulting wallpaper will
     * be saved to.
     *
     * @return output file
     */
    public File getOutputFile() {
        return this.outputFile;
    }

    /**
     * Set the output file to a new value.
     *
     * @param outputFile new output file
     */
    public void setOutputFile(final File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Get the number of thumbnails to display per row.
     *
     * @return thumbnails per row
     */
    public int getThumbnailsPerRow() {
        return this.thumbnailsPerRow;
    }

    /**
     * Set the number of preview thumbnails to display per row.
     *
     * @param thumbnailsPerRow thumbnails per row
     */
    public void setThumbnailsPerRow(final int thumbnailsPerRow) {
        this.thumbnailsPerRow = thumbnailsPerRow;
    }

    /**
     * Determine if the application should configure the OS to use the output
     * file as the wallpaper. When set, {@link #process(boolean)} will call
     * {@link org.widgetrefinery.wallpaper.os.OSSupport#updateWallpaperSettings(java.io.File)}.
     *
     * @return true if the application should configure the OS
     */
    public boolean isConfigOS() {
        return this.configOS;
    }

    /**
     * Set whether the application should configure the OS.
     *
     * @param configOS boolean flag
     */
    public void setConfigOS(final boolean configOS) {
        this.configOS = configOS;
    }

    /**
     * Determine if the application should notify the OS to reload its
     * wallpaper. When set, {@link #process(boolean)} will call
     * {@link org.widgetrefinery.wallpaper.os.OSSupport#reloadWallpaperSettings()}.
     *
     * @return true if the application should notify the OS
     */
    public boolean isRefreshOS() {
        return this.refreshOS;
    }

    /**
     * Set whether the application should notify the OS to reload its
     * wallpaper.
     *
     * @param refreshOS boolean flag
     */
    public void setRefreshOS(final boolean refreshOS) {
        this.refreshOS = refreshOS;
    }

    /**
     * Generate a new wallpaper image and update the OS as configured.
     *
     * @param overwrite whether or not to overwrite the output file
     * @throws BadUserInputException if there is a problem with the user-supplied parameters
     * @throws IOException           if an IO error occurred
     * @throws InterruptedException  if the operation was interrupted
     */
    public void process(final boolean overwrite) throws BadUserInputException, IOException, InterruptedException {
        File input = getInputFile();
        if (null == input) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_NO_INPUT);
        }
        if (!input.exists()) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_INPUT_DOES_NOT_EXIST, input);
        }

        File output = getOutputFile();
        if (null == output) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_NO_OUTPUT);
        }
        if (input.equals(output)) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_SAME_INPUT_OUTPUT, output);
        }
        if (output.exists() && !overwrite) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_OUTPUT_EXISTS, output);
        }

        ImageUtil imageUtil = new ImageUtil();
        BufferedImage image = imageUtil.formatImage(input);
        if (null == image) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_BAD_INPUT, input);
        }
        imageUtil.saveImage(image, output);

        OSSupport osSupport = OSUtil.getOSSupport();
        if (null != osSupport) {
            if (isConfigOS()) {
                osSupport.updateWallpaperSettings(output);
            }
            if (isRefreshOS()) {
                osSupport.reloadWallpaperSettings();
            }
        }
    }
}
