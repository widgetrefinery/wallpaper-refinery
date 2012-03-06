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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Since: 2/20/12 5:24 PM
 */
public class ImageUtil {
    private final int x;
    private final int y;
    private final int w;
    private final int h;

    public ImageUtil() {
        Rectangle dim = computeScreenDimension();
        this.x = Double.valueOf(dim.getX()).intValue();
        this.y = Double.valueOf(dim.getY()).intValue();
        this.w = Double.valueOf(dim.getWidth()).intValue();
        this.h = Double.valueOf(dim.getHeight()).intValue();
    }

    protected Rectangle computeScreenDimension() {
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            for (GraphicsConfiguration gc : gd.getConfigurations()) {
                virtualBounds = virtualBounds.union(gc.getBounds());
            }
        }
        return virtualBounds;
    }

    protected BufferedImage formatImage(final File file) throws IOException {
        if (!file.exists()) {
            throw new IllegalArgumentException("no such source file (" + file + ')');
        }

        BufferedImage img = ImageIO.read(file);

        if (img.getWidth() != this.w || img.getHeight() != this.h) {
            BufferedImage resized = new BufferedImage(this.w, this.h, img.getType());
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(img, 0, 0, this.w, this.h, null);
            g2d.dispose();
            img = resized;
        }

        if (0 != this.x || 0 != this.y) {
            BufferedImage tiled = new BufferedImage(this.w, this.h, img.getType());
            Graphics2D g2d = tiled.createGraphics();
            g2d.drawImage(img, this.x, this.y, null);
            if (0 != this.x) {
                g2d.drawImage(img, this.x + this.w, this.y, null);
            }
            if (0 != this.y) {
                g2d.drawImage(img, this.x, this.y + this.h, null);
            }
            if (0 != this.x && 0 != this.y) {
                g2d.drawImage(img, this.x + this.w, this.y + this.h, null);
            }
            g2d.dispose();
            img = tiled;
        }

        return img;
    }

    protected void saveImage(final RenderedImage img, final File file) throws IOException {
        String filename = file.toString();

        int ndx = filename.lastIndexOf('.');
        if (-1 == ndx) {
            throw new IllegalArgumentException("target file does not contain an extension (" + file + ')');
        }
        String ext = filename.substring(ndx + 1);

        if (file.exists()) {
            throw new IllegalArgumentException("target file already exists (" + file + ')');
        }

        boolean result = ImageIO.write(img, ext, file);
        if (!result) {
            throw new IllegalArgumentException("unsupported image type (" + ext + ')');
        }
    }
}
