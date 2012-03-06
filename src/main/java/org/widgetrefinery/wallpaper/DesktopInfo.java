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

package org.widgetrefinery.wallpaper;

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

    public List<Rectangle> getViewports() {
        return this.viewports;
    }

    public Rectangle getBounds() {
        return this.bounds;
    }
}
