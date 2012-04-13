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

import org.widgetrefinery.wallpaper.common.ImageUtil;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Since: 3/14/12 9:03 PM
 */
public class PreviewRenderer implements ListCellRenderer<File> {
    private final ImageUtil                imageUtil;
    private final Map<File, PreviewWidget> cache;

    public PreviewRenderer(final ImageUtil imageUtil) {
        this.imageUtil = imageUtil;
        this.cache = new HashMap<File, PreviewWidget>();
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends File> jList,
                                                  final File file,
                                                  final int index,
                                                  final boolean isSelected,
                                                  final boolean cellHasFocus) {
        PreviewWidget previewWidget = this.cache.get(file);
        if (null == previewWidget) {
            previewWidget = new PreviewWidget(this.imageUtil, file);
            this.cache.put(file, previewWidget);
        }
        previewWidget.setSelected(isSelected);
        return previewWidget;
    }

    protected static class PreviewWidget extends JPanel {
        private final ImageUtil     imageUtil;
        private final File          file;
        private       BufferedImage image;
        private       long          loadTimestamp;
        private       boolean       selected;

        public PreviewWidget(final ImageUtil imageUtil, final File file) {
            this.imageUtil = imageUtil;
            this.file = file;
            Rectangle bounds = imageUtil.getBounds();
            setPreferredSize(new Dimension(bounds.width, bounds.height));
        }

        protected BufferedImage getImage() {
            if (null == this.image || this.loadTimestamp < this.file.lastModified()) {
                try {
                    this.image = this.imageUtil.previewImage(this.file);
                } catch (Exception e) {
                    Rectangle bounds = this.imageUtil.getBounds();
                    this.image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_3BYTE_BGR);
                    Graphics2D g2d = this.image.createGraphics();
                    g2d.setColor(Color.RED);
                    g2d.drawLine(0, 0, bounds.width, bounds.height);
                    g2d.drawLine(0, bounds.height, bounds.width, 0);
                    g2d.dispose();
                }
                this.loadTimestamp = System.currentTimeMillis();
            }
            return this.image;
        }

        public void setSelected(final boolean selected) {
            this.selected = selected;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            BufferedImage image = getImage();
            g.drawImage(image, 0, 0, null);
            if (!this.selected) {
                g.setColor(new Color(0, 0, 0, 64));
                g.fillRect(0, 0, image.getWidth(), image.getHeight());
            }
        }
    }
}
