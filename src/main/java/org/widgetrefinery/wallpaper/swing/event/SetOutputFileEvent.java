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

package org.widgetrefinery.wallpaper.swing.event;

import org.widgetrefinery.util.event.Event;

import java.io.File;

/**
 * Since: 4/11/12 10:06 PM
 */
public class SetOutputFileEvent implements Event {
    private final File outputFile;

    public SetOutputFileEvent(final File outputFile) {
        this.outputFile = outputFile;
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getOutputFile();
    }
}
