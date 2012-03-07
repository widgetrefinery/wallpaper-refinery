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

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Since: 2/20/12 5:24 PM
 */
public class ImageUtil {
    private final DesktopInfo info;

    public ImageUtil() {
        this(new DesktopInfo());
    }

    public ImageUtil(final DesktopInfo info) {
        this.info = info;
    }

    protected Rectangle getBounds() {
        return this.info.getBounds();
    }

    public BufferedImage formatImage(final File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        return formatImage(img);
    }

    public BufferedImage formatImage(final BufferedImage img) {
        BufferedImage resized = resize(img);
        return translate(resized);
    }

    protected BufferedImage resize(final BufferedImage img) {
        BufferedImage result = img;
        Rectangle bounds = getBounds();
        if (img.getWidth() != bounds.x || img.getHeight() != bounds.y) {
            result = new BufferedImage(bounds.width, bounds.height, img.getType());
            Graphics2D g2d = result.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(img, 0, 0, bounds.width, bounds.height, null);
            g2d.dispose();
        }
        return result;
    }

    protected BufferedImage translate(final BufferedImage img) {
        BufferedImage result = img;
        Rectangle bounds = getBounds();
        if (0 != bounds.x || 0 != bounds.y) {
            result = new BufferedImage(bounds.width, bounds.height, img.getType());
            Graphics2D g2d = result.createGraphics();
            g2d.drawImage(img, bounds.x, bounds.y, null);
            if (0 != bounds.x) {
                g2d.drawImage(img, bounds.x + bounds.width, bounds.y, null);
            }
            if (0 != bounds.y) {
                g2d.drawImage(img, bounds.x, bounds.y + bounds.height, null);
            }
            if (0 != bounds.x && 0 != bounds.y) {
                g2d.drawImage(img, bounds.x + bounds.width, bounds.y + bounds.height, null);
            }
            g2d.dispose();
        }
        return result;
    }

    public void saveImage(final RenderedImage img, final File file, final boolean overrideExisting) throws IllegalArgumentException, IOException {
        String filename = file.getName();
        int ndx = filename.lastIndexOf('.');
        if (-1 == ndx) {
            throw new IllegalArgumentException("Output filename does not contain an extension (" + file + ')');
        }
        String ext = filename.substring(ndx + 1);

        if (file.exists() && !overrideExisting) {
            throw new IllegalArgumentException("Output filename already exists (" + file.getAbsolutePath() + ')');
        }

        boolean result = ImageIO.write(img, ext, file);
        if (!result) {
            throw new IllegalArgumentException("Unsupported image type (" + ext + ')');
        }
    }
}
