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

import junit.framework.TestCase;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

/**
 * Since: 3/5/12 10:41 PM
 */
public class TestDesktopInfo extends TestCase {
    private static final boolean overrideComputeViewports = true;

    public void testDesktopInfo() {
        DesktopInfo desktopInfo = new StubDesktopInfo();
        assertEquals(-100, desktopInfo.getBounds().x);
        assertEquals(-50, desktopInfo.getBounds().y);
        assertEquals(300, desktopInfo.getBounds().width);
        assertEquals(200, desktopInfo.getBounds().height);
        assertEquals(2, desktopInfo.getViewports().size());
        Rectangle viewport1 = desktopInfo.getViewports().get(0);
        assertEquals(0, viewport1.x);
        assertEquals(0, viewport1.y);
        assertEquals(200, viewport1.width);
        assertEquals(100, viewport1.height);
        Rectangle viewport2 = desktopInfo.getViewports().get(1);
        assertEquals(-100, viewport2.x);
        assertEquals(-50, viewport2.y);
        assertEquals(100, viewport2.width);
        assertEquals(200, viewport2.height);
    }

    protected static class StubDesktopInfo extends DesktopInfo {
        @Override
        protected List<Rectangle> computeViewports() {
            if (overrideComputeViewports) {
                return Arrays.asList(new Rectangle(0, 0, 200, 100), new Rectangle(-100, -50, 100, 200));
            }
            return super.computeViewports();
        }
    }
}
