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

import java.awt.image.BufferedImage;

/**
 * Since: 3/7/12 10:11 PM
 */
public class TestImageUtil extends TestCase {
    public void testFormatImage() throws Exception {
        final int b = 0x0000FF;
        final int g = 0x00FF00;
        final int r = 0xFF0000;
        final int w = 0xFFFFFF;
        int[][] inputRGB = new int[][]{
                {b, b, b, b, b, b, b, b, b, b, g, g, g, g, g, g, g, g, g, g}, //row 0
                {b, b, b, b, b, b, b, b, b, b, g, g, g, g, g, g, g, g, g, g}, //row 1
                {b, b, b, b, b, b, b, b, b, b, g, g, g, g, g, g, g, g, g, g}, //row 2
                {b, b, b, b, b, b, b, b, b, b, g, g, g, g, g, g, g, g, g, g}, //row 3
                {b, b, b, b, b, b, b, b, b, b, g, g, g, g, g, g, g, g, g, g}, //row 4
                {b, b, b, b, b, b, b, b, b, b, g, g, g, g, g, g, g, g, g, g}, //row 5
                {r, r, r, r, r, r, r, r, r, r, w, w, w, w, w, w, w, w, w, w}, //row 6
                {r, r, r, r, r, r, r, r, r, r, w, w, w, w, w, w, w, w, w, w}, //row 7
                {r, r, r, r, r, r, r, r, r, r, w, w, w, w, w, w, w, w, w, w}, //row 8
                {r, r, r, r, r, r, r, r, r, r, w, w, w, w, w, w, w, w, w, w}, //row 9
                {r, r, r, r, r, r, r, r, r, r, w, w, w, w, w, w, w, w, w, w}, //row 10
                {r, r, r, r, r, r, r, r, r, r, w, w, w, w, w, w, w, w, w, w} //row 11
        };
        BufferedImage input = new BufferedImage(20, 12, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                input.setRGB(x, y, inputRGB[y][x]);
            }
        }

        ImageUtil util = new ImageUtil(new StubDesktopInfo());
        BufferedImage output = util.formatImage(input);
        assertEquals(10, output.getWidth());
        assertEquals(6, output.getHeight());

        //output is half as big and translated
        int[][] outputRGB = new int[][]{
                {b, g, g, g, g, g, b, b, b, b, b}, //row 1
                {b, g, g, g, g, g, b, b, b, b, b}, //row 2
                {r, w, w, w, w, w, r, r, r, r, r}, //row 3
                {r, w, w, w, w, w, r, r, r, r, r}, //row 4
                {r, w, w, w, w, w, r, r, r, r, r}, //row 5
                {b, g, g, g, g, g, b, b, b, b, b} //row 0
        };
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                assertEquals(dumpImage(output) + x + ", " + y, outputRGB[y][x], 0xFFFFFF & output.getRGB(x, y));
            }
        }
    }

    protected String dumpImage(final BufferedImage image) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                String hex = Integer.toHexString(image.getRGB(x, y));
                if (2 == hex.length()) {
                    sb.append("     ");
                } else if (4 == hex.length()) {
                    sb.append("   ");
                } else {
                    sb.append(" ");
                }
                sb.append(hex);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
