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

import javax.swing.JList;
import javax.swing.SwingUtilities;
import java.io.File;

/**
 * @since 4/15/12 3:22 PM
 */
public class PreviewRenderRequest {
    private final JList list;
    private final int   index;
    private final File  file;

    public PreviewRenderRequest(final JList list, final int index, final File file) {
        this.list = list;
        this.index = index;
        this.file = file;
    }

    public boolean isStillValid() {
        return this.list.getFirstVisibleIndex() <= this.index && this.list.getLastVisibleIndex() >= this.index;
    }

    public JList getList() {
        return this.list;
    }

    public File getFile() {
        return this.file;
    }

    public void done() {
        final JList list = this.list;
        final int index = this.index;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                list.repaint(list.getCellBounds(index, index));
            }
        });
    }
}
