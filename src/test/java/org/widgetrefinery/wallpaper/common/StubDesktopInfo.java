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

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

/**
 * Since: 3/6/12 7:47 PM
 */
public class StubDesktopInfo extends DesktopInfo {
    private static final boolean overrideComputeViewports = true;

    @Override
    protected List<Rectangle> computeViewports() {
        if (overrideComputeViewports) {
            return Arrays.asList(new Rectangle(0, 0, 6, 4), new Rectangle(-4, -1, 4, 6));
        }
        return super.computeViewports();
    }
}
