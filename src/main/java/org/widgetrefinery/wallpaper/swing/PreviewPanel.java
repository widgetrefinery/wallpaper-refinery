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
import org.widgetrefinery.wallpaper.common.DesktopInfo;
import org.widgetrefinery.wallpaper.common.ImageUtil;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.event.SetWorkingDirectoryEvent;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.File;
import java.io.FileFilter;

/**
 * Since: 3/14/12 9:02 PM
 */
public class PreviewPanel extends JScrollPane {
    private final Model                        model;
    private final DefaultListModel<File>       listModel;
    private final JList<File>                  listWidget;
    private final PreviewListSelectionListener listener;

    public PreviewPanel(final EventBus eventBus, final Model model) {
        this.model = model;

        this.listModel = new DefaultListModel<File>();
        this.listWidget = new JList<File>(this.listModel);
        this.listWidget.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listWidget.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.listWidget.setVisibleRowCount(-1);
        int previewSize = model.getPreviewSize();
        this.listWidget.setCellRenderer(new PreviewRenderer(new ImageUtil(new DesktopInfo(previewSize, previewSize))));
        this.listener = new PreviewListSelectionListener(model, this.listWidget);
        this.listWidget.addListSelectionListener(this.listener);

        setViewportView(this.listWidget);
        refresh();

        eventBus.add(SetWorkingDirectoryEvent.class, new EventListener<SetWorkingDirectoryEvent>() {
            @Override
            public void notify(final SetWorkingDirectoryEvent event) {
                refresh();
            }
        });
    }

    public void refresh() {
        this.listener.setEnable(false);
        try {
            this.listModel.clear();
            File workingDirectory = this.model.getWorkingDirectory();
            File[] children = workingDirectory.listFiles(new ImageFileFilter());
            Integer selectedNdx = null;

            if (null != children && 0 < children.length) {
                this.listModel.ensureCapacity(children.length);
                File inputFile = this.model.getInputFile();
                for (File child : children) {
                    this.listModel.addElement(child);
                    if (child.equals(inputFile)) {
                        selectedNdx = this.listModel.size() - 1;
                    }
                }
            }

            if (null != selectedNdx) {
                this.listWidget.setSelectedIndex(selectedNdx);
                this.listWidget.ensureIndexIsVisible(selectedNdx);
            }
        } finally {
            this.listener.setEnable(true);
        }
    }

    protected static class PreviewListSelectionListener implements ListSelectionListener {
        private final Model       model;
        private final JList<File> listWidget;
        private       boolean     enable;

        public PreviewListSelectionListener(final Model model, final JList<File> listWidget) {
            this.model = model;
            this.listWidget = listWidget;
            this.enable = true;
        }

        public void setEnable(final boolean enable) {
            this.enable = enable;
        }

        @Override
        public void valueChanged(final ListSelectionEvent event) {
            if (!event.getValueIsAdjusting() && this.enable) {
                File file = this.listWidget.getSelectedValue();
                this.model.setInputFile(file);
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
