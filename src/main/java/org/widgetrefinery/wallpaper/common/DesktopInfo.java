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
 * Utility class for discovering the current monitor layout.
 *
 * @since 3/5/12 10:19 PM
 */
public class DesktopInfo {
    private final List<Rectangle> monitors;
    private final Rectangle       bounds;

    /**
     * Creates a new instance that describes the current monitor layout.
     */
    public DesktopInfo() {
        this.monitors = computeMonitors();
        Rectangle bounds = new Rectangle();
        for (Rectangle monitor : this.monitors) {
            bounds.add(monitor);
        }
        this.bounds = bounds;
    }

    /**
     * Creates a new instance that describes the current monitor layout scaled
     * to within the given limits.
     *
     * @param maxWidth  maximum horizontal size
     * @param maxHeight maximum vertical size
     */
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
        for (Rectangle monitor : getMonitors()) {
            scale(monitor, scale);
        }
    }

    /**
     * Computes the current monitor layout. Each rectangle returned represent
     * the dimensions for one monitor.
     *
     * @return list of monitor dimensions
     */
    protected List<Rectangle> computeMonitors() {
        List<Rectangle> monitors = new ArrayList<Rectangle>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            for (GraphicsConfiguration gc : gd.getConfigurations()) {
                monitors.add(gc.getBounds());
            }
        }
        return Collections.unmodifiableList(monitors);
    }

    /**
     * Scales the given rectangle by the given amount. A scale of 1.0 will
     * produce no change.
     *
     * @param rect  rectangle to scale
     * @param scale amount to scale
     */
    protected void scale(final Rectangle rect, final double scale) {
        rect.x = (int) (rect.x * scale);
        rect.y = (int) (rect.y * scale);
        rect.width = (int) (rect.width * scale);
        rect.height = (int) (rect.height * scale);
    }

    /**
     * Returns the dimensions for all monitors.
     *
     * @return list of monitor dimensions
     */
    public List<Rectangle> getMonitors() {
        return this.monitors;
    }

    /**
     * Returns the smallest dimension that all monitors fit inside.
     *
     * @return screen bounds
     */
    public Rectangle getBounds() {
        return this.bounds;
    }
}
