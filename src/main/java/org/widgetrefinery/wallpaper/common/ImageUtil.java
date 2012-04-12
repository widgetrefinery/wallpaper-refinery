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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Since: 2/20/12 5:24 PM
 */
public class ImageUtil {
    private final DesktopInfo   info;
    private       BufferedImage mask;

    public ImageUtil() {
        this(new DesktopInfo());
    }

    public ImageUtil(final DesktopInfo info) {
        this.info = info;
    }

    public Rectangle getBounds() {
        return this.info.getBounds();
    }

    protected List<Rectangle> getViewports() {
        return this.info.getViewports();
    }

    protected BufferedImage getMask() {
        if (null == this.mask) {
            Rectangle bounds = getBounds();
            BufferedImage mask = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2d = mask.createGraphics();
            g2d.setBackground(new Color(0, 0, 0, 0)); //transparent black
            g2d.setColor(new Color(0, 0, 0, 128)); //semi-transparent black
            g2d.fillRect(0, 0, bounds.width, bounds.height);
            for (Rectangle viewport : getViewports()) {
                g2d.clearRect(viewport.x - bounds.x, viewport.y - bounds.y, viewport.width, viewport.height);
            }
            g2d.dispose();
            this.mask = mask;
        }
        return this.mask;
    }

    public BufferedImage formatImage(final File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        return formatImage(img);
    }

    public BufferedImage formatImage(final BufferedImage img) {
        BufferedImage resized = resize(img);
        return translate(resized);
    }

    public BufferedImage previewImage(final File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        return previewImage(img);
    }

    public BufferedImage previewImage(final BufferedImage img) {
        BufferedImage resized = resize(img);
        return mask(resized);
    }

    protected BufferedImage resize(final BufferedImage img) {
        BufferedImage result = img;
        Rectangle bounds = getBounds();
        if (img.getWidth() != bounds.width || img.getHeight() != bounds.height) {
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

    protected BufferedImage mask(final BufferedImage img) {
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(getMask(), 0, 0, img.getWidth(), img.getHeight(), null);
        g2d.dispose();
        return img;
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
