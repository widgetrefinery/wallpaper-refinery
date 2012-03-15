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

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.io.File;

/**
 * Since: 3/14/12 9:02 PM
 */
public class PreviewPanel extends JScrollPane {
    private final JPanel content;

    public PreviewPanel(final EventBus eventBus) {
        this.content = new JPanel();
        this.content.setLayout(new BoxLayout(this.content, BoxLayout.Y_AXIS));

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

    public void setPath(final File parent) {
        this.content.removeAll();
        File[] children = parent.listFiles();
        if (null != children) {
            for (File child : children) {
                if (child.isFile()) {
                    PreviewWidget previewWidget = PreviewWidget.create(child);
                    if (null != previewWidget) {
                        this.content.add(previewWidget);
                    }
                }
            }
        }
    }
}
