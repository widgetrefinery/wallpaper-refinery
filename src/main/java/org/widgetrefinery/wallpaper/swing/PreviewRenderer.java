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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @since 3/14/12 9:03 PM
 */
public class PreviewRenderer extends JComponent implements ListCellRenderer<File> {
    private final PreviewRenderQueue   renderQueue;
    private       PreviewRenderRequest renderRequest;
    private       boolean              isSelected;

    public PreviewRenderer(final PreviewRenderQueue renderQueue) {
        this.renderQueue = renderQueue;
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends File> list,
                                                  final File file,
                                                  final int index,
                                                  final boolean isSelected,
                                                  final boolean cellHasFocus) {
        this.renderRequest = new PreviewRenderRequest(list, index, file);
        this.isSelected = isSelected;
        return this;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        BufferedImage image = this.renderQueue.render(this.renderRequest);
        JList list = this.renderRequest.getList();
        if (null != image) {
            g.drawImage(image, 0, 0, list.getFixedCellWidth(), list.getFixedCellHeight(), null);
        }
        if (!this.isSelected) {
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, list.getFixedCellWidth(), list.getFixedCellHeight());
        }
    }

    @Override
    public String getToolTipText() {
        return this.renderRequest.getFile().getName();
    }

    @Override
    public void firePropertyChange(final String s, final boolean b, final boolean b1) {
    }

    @Override
    public void firePropertyChange(final String s, final int i, final int i1) {
    }

    @Override
    public void firePropertyChange(final String s, final char c, final char c1) {
    }

    @Override
    protected void firePropertyChange(final String s, final Object o, final Object o1) {
    }

    @Override
    public void firePropertyChange(final String s, final byte b, final byte b1) {
    }

    @Override
    public void firePropertyChange(final String s, final short i, final short i1) {
    }

    @Override
    public void firePropertyChange(final String s, final long l, final long l1) {
    }

    @Override
    public void firePropertyChange(final String s, final float v, final float v1) {
    }

    @Override
    public void firePropertyChange(final String s, final double v, final double v1) {
    }

    @Override
    public void invalidate() {
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public void repaint() {
    }

    @Override
    public void repaint(final long l, final int i, final int i1, final int i2, final int i3) {
    }

    @Override
    public void repaint(final Rectangle rectangle) {
    }

    @Override
    public void revalidate() {
    }

    @Override
    public void validate() {
    }
}
