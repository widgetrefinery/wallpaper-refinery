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
 * An implementation of {@link org.widgetrefinery.wallpaper.os.OSSupport}
 * providing support for Windows XP.
 *
 * @since 3/6/12 10:26 PM
 */
public class WinXPSupport extends AbstractOSSupport {
    /**
     * Updates the Windows registry to use the new file as the wallpaper. Note
     * that Windows only supports bitmap images.
     *
     * @param imgFile file to use as the wallpaper
     * @throws IOException          {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     * @throws RuntimeException     {@inheritDoc}
     */
    @Override
    public void updateWallpaperSettings(final File imgFile) throws IOException, InterruptedException, RuntimeException {
        exec("reg", "add", "HKCU\\Control Panel\\Desktop", "/V", "Wallpaper", "/T", "REG_SZ", "/F", "/D", imgFile.getAbsolutePath());
        exec("reg", "add", "HKCU\\Control Panel\\Desktop", "/V", "WallpaperStyle", "/T", "REG_SZ", "/F", "/D", "0"); //0: center, 2: stretch
        exec("reg", "add", "HKCU\\Control Panel\\Desktop", "/V", "TileWallpaper", "/T", "REG_SZ", "/F", "/D", "1"); //0: center, 1: tile
    }

    /**
     * Invokes the user32.dll library to ask Windows to reload its registry
     * settings.
     *
     * @throws IOException          {@inheritDoc}
     * @throws InterruptedException {@inheritDoc}
     * @throws RuntimeException     {@inheritDoc}
     */
    @Override
    public void reloadWallpaperSettings() throws IOException, InterruptedException, RuntimeException {
        exec("rundll32", "user32.dll,", "UpdatePerUserSystemParameters");
    }
}
