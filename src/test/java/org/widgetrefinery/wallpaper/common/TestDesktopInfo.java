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

import junit.framework.TestCase;

import java.awt.Rectangle;

/**
 * Since: 3/5/12 10:41 PM
 */
public class TestDesktopInfo extends TestCase {
    public void testDesktopInfo() {
        DesktopInfo desktopInfo = new StubDesktopInfo();
        Rectangle bounds = desktopInfo.getBounds();
        assertEquals(-4, bounds.x);
        assertEquals(-1, bounds.y);
        assertEquals(10, bounds.width);
        assertEquals(6, bounds.height);
        assertEquals(2, desktopInfo.getViewports().size());
        Rectangle viewport1 = desktopInfo.getViewports().get(0);
        assertEquals(0, viewport1.x);
        assertEquals(0, viewport1.y);
        assertEquals(6, viewport1.width);
        assertEquals(4, viewport1.height);
        Rectangle viewport2 = desktopInfo.getViewports().get(1);
        assertEquals(-4, viewport2.x);
        assertEquals(-1, viewport2.y);
        assertEquals(4, viewport2.width);
        assertEquals(6, viewport2.height);
    }

    public void testScale() {
        DesktopInfo verticallyBound = new StubDesktopInfo(200, 100);
        Rectangle bounds = verticallyBound.getBounds();
        assertEquals(-66, bounds.x);
        assertEquals(-16, bounds.y);
        assertEquals(166, bounds.width);
        assertEquals(100, bounds.height);
        Rectangle viewport1 = verticallyBound.getViewports().get(0);
        assertEquals(0, viewport1.x);
        assertEquals(0, viewport1.y);
        assertEquals(100, viewport1.width);
        assertEquals(66, viewport1.height);
        Rectangle viewport2 = verticallyBound.getViewports().get(1);
        assertEquals(-66, viewport2.x);
        assertEquals(-16, viewport2.y);
        assertEquals(66, viewport2.width);
        assertEquals(100, viewport2.height);

        DesktopInfo horizontallyBound = new StubDesktopInfo(100, 200);
        bounds = horizontallyBound.getBounds();
        assertEquals(-40, bounds.x);
        assertEquals(-10, bounds.y);
        assertEquals(100, bounds.width);
        assertEquals(60, bounds.height);
        viewport1 = horizontallyBound.getViewports().get(0);
        assertEquals(0, viewport1.x);
        assertEquals(0, viewport1.y);
        assertEquals(60, viewport1.width);
        assertEquals(40, viewport1.height);
        viewport2 = horizontallyBound.getViewports().get(1);
        assertEquals(-40, viewport2.x);
        assertEquals(-10, viewport2.y);
        assertEquals(40, viewport2.width);
        assertEquals(60, viewport2.height);
    }
}
