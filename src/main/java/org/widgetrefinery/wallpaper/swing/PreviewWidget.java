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
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Since: 3/14/12 9:03 PM
 */
public class PreviewWidget implements Icon {
    private final File          file;
    private final BufferedImage image;

    public PreviewWidget(final File file) throws IOException {
        this.file = file;
        this.image = ImageIO.read(file);
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        g.drawImage(this.image, x, y, 200, 200, null);
    }

    @Override
    public int getIconWidth() {
        return 200;
    }

    @Override
    public int getIconHeight() {
        return 200;
    }
}
