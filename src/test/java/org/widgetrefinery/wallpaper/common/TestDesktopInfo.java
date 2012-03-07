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
        assertEquals(-4, desktopInfo.getBounds().x);
        assertEquals(-1, desktopInfo.getBounds().y);
        assertEquals(10, desktopInfo.getBounds().width);
        assertEquals(6, desktopInfo.getBounds().height);
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
}
