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

package org.widgetrefinery.wallpaper.common;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Since: 3/5/12 10:19 PM
 */
public class DesktopInfo {
    private final List<Rectangle> viewports;
    private final Rectangle       bounds;

    public DesktopInfo() {
        this.viewports = computeViewports();
        Rectangle bounds = new Rectangle();
        for (Rectangle viewport : this.viewports) {
            bounds.add(viewport);
        }
        this.bounds = bounds;
    }

    public DesktopInfo(final int maxWidth, final int maxHeight) {
        this();
        Rectangle bounds = getBounds();
        int width = bounds.width;
        int height = bounds.height;
        double scale;
        if (((double) width) / height >= ((double) maxWidth) / maxHeight) {
            scale = ((double) maxWidth) / width;
        } else {
            scale = ((double) maxHeight) / height;
        }
        scale(bounds, scale);
        for (Rectangle viewport : getViewports()) {
            scale(viewport, scale);
        }
    }

    protected List<Rectangle> computeViewports() {
        List<Rectangle> viewports = new ArrayList<Rectangle>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            for (GraphicsConfiguration gc : gd.getConfigurations()) {
                viewports.add(gc.getBounds());
            }
        }
        return Collections.unmodifiableList(viewports);
    }

    protected void scale(final Rectangle rect, final double scale) {
        rect.x = (int) (rect.x * scale);
        rect.y = (int) (rect.y * scale);
        rect.width = (int) (rect.width * scale);
        rect.height = (int) (rect.height * scale);
    }

    public List<Rectangle> getViewports() {
        return this.viewports;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }
}
