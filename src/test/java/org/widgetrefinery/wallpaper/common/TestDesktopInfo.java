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
 * @since 3/5/12 10:41 PM
 */
public class TestDesktopInfo extends TestCase {
    public void testDesktopInfo() {
        DesktopInfo desktopInfo = new StubDesktopInfo();
        Rectangle bounds = desktopInfo.getBounds();
        assertEquals(-4, bounds.x);
        assertEquals(-1, bounds.y);
        assertEquals(10, bounds.width);
        assertEquals(6, bounds.height);
        assertEquals(2, desktopInfo.getMonitors().size());
        Rectangle monitor1 = desktopInfo.getMonitors().get(0);
        assertEquals(0, monitor1.x);
        assertEquals(0, monitor1.y);
        assertEquals(6, monitor1.width);
        assertEquals(4, monitor1.height);
        Rectangle monitor2 = desktopInfo.getMonitors().get(1);
        assertEquals(-4, monitor2.x);
        assertEquals(-1, monitor2.y);
        assertEquals(4, monitor2.width);
        assertEquals(6, monitor2.height);
    }

    public void testScale() {
        DesktopInfo verticallyBound = new StubDesktopInfo(200, 100);
        Rectangle bounds = verticallyBound.getBounds();
        assertEquals(-66, bounds.x);
        assertEquals(-16, bounds.y);
        assertEquals(166, bounds.width);
        assertEquals(100, bounds.height);
        Rectangle monitor1 = verticallyBound.getMonitors().get(0);
        assertEquals(0, monitor1.x);
        assertEquals(0, monitor1.y);
        assertEquals(100, monitor1.width);
        assertEquals(66, monitor1.height);
        Rectangle monitor2 = verticallyBound.getMonitors().get(1);
        assertEquals(-66, monitor2.x);
        assertEquals(-16, monitor2.y);
        assertEquals(66, monitor2.width);
        assertEquals(100, monitor2.height);

        DesktopInfo horizontallyBound = new StubDesktopInfo(100, 200);
        bounds = horizontallyBound.getBounds();
        assertEquals(-40, bounds.x);
        assertEquals(-10, bounds.y);
        assertEquals(100, bounds.width);
        assertEquals(60, bounds.height);
        monitor1 = horizontallyBound.getMonitors().get(0);
        assertEquals(0, monitor1.x);
        assertEquals(0, monitor1.y);
        assertEquals(60, monitor1.width);
        assertEquals(40, monitor1.height);
        monitor2 = horizontallyBound.getMonitors().get(1);
        assertEquals(-40, monitor2.x);
        assertEquals(-10, monitor2.y);
        assertEquals(40, monitor2.width);
        assertEquals(60, monitor2.height);
    }
}
