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

package org.widgetrefinery.wallpaper.os;

import java.io.File;
import java.io.IOException;

/**
 * Provides OS-specific support for performing wallpaper-related operations.
 *
 * @since 3/6/12 10:17 PM
 */
public interface OSSupport {
    /**
     * Configures the OS to use the given file as the wallpaper. This usually
     * means writing something out to the OS registry or other configuration
     * file.
     *
     * @param imgFile file to use as the wallpaper
     * @throws IOException          if an IO error occurred
     * @throws InterruptedException if the operation was interrupted
     * @throws RuntimeException     if the return code does not match the expected value
     */
    public void updateWallpaperSettings(File imgFile) throws IOException, InterruptedException, RuntimeException;

    /**
     * Notify the OS to reload its wallpaper. The OS might not refresh the
     * wallpaper after performing {@link #updateWallpaperSettings(java.io.File)}.
     * This wil notify the OS to explicitly reload its wallpaper.
     *
     * @throws IOException          if an IO error occurred
     * @throws InterruptedException if the operation was interrupted
     * @throws RuntimeException     if the return code does not match the expected value
     */
    public void reloadWallpaperSettings() throws IOException, InterruptedException, RuntimeException;
}
