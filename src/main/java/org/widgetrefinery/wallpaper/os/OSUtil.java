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

/**
 * Utility class for returning an appropriate
 * {@link org.widgetrefinery.wallpaper.os.OSSupport} implementation for the
 * current OS.
 *
 * @since 3/6/12 10:15 PM
 */
public class OSUtil {
    /**
     * Returns an appropriate {@link org.widgetrefinery.wallpaper.os.OSSupport}
     * implementation for the current OS or null if the current OS is not
     * supported.
     *
     * @return an {@link org.widgetrefinery.wallpaper.os.OSSupport} implementation
     */
    public static OSSupport getOSSupport() {
        String os = System.getProperty("os.name");
        if ("Windows XP".equals(os)) {
            return new WinXPSupport();
        }
        return null;
    }
}
