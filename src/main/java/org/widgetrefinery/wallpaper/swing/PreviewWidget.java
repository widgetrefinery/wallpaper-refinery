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

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Since: 3/14/12 9:03 PM
 */
public class PreviewWidget extends JLabel {
    public PreviewWidget(final File file) {
        setText(file.getName());
    }

    public static PreviewWidget create(final File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (null != image) {
                return new PreviewWidget(file);
            }
        } catch (Exception e) {
            //not an image
        }
        return null;
    }
}
