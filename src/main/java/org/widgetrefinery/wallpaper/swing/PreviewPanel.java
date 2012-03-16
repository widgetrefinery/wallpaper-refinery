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

import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.event.EventListener;
import org.widgetrefinery.wallpaper.swing.event.OpenFileEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Since: 3/14/12 9:02 PM
 */
public class PreviewPanel extends JScrollPane {
    private static final Logger logger = Logger.getLogger(PreviewPanel.class.getName());

    private final DefaultListModel<Icon> list;

    public PreviewPanel(final EventBus eventBus) {
        this.list = new DefaultListModel<Icon>();

        JList<Icon> content = new JList<Icon>(this.list);
        content.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        content.setVisibleRowCount(-1);
        setViewportView(content);

        registerOpenFileEventListener(eventBus);
    }

    protected void registerOpenFileEventListener(final EventBus eventBus) {
        eventBus.add(new EventListener<OpenFileEvent>() {
            @Override
            public void notify(final OpenFileEvent event) {
                setPath(event.getFile());
            }
        });
    }

    public void setPath(final File file) {
        this.list.clear();
        File parent = file.isDirectory() ? file : file.getParentFile();
        File[] children = parent.listFiles(new ImageFileFilter());
        if (null != children && 0 < children.length) {
            this.list.ensureCapacity(children.length);
            for (File child : children) {
                try {
                    this.list.addElement(new PreviewWidget(child));
                } catch (IOException e) {
                    logger.fine(e.getMessage());
                }
            }
        }
    }

    protected static class ImageFileFilter implements FileFilter {
        @Override
        public boolean accept(final File file) {
            boolean result = false;
            if (file.isFile() && file.canRead()) {
                String name = file.getName();
                int ndx = name.lastIndexOf('.');
                if (-1 < ndx) {
                    String extension = name.substring(ndx + 1).toLowerCase();
                    result = ImageIO.getImageReadersBySuffix(extension).hasNext();
                }
            }
            return result;
        }
    }
}
