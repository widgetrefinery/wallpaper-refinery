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

import org.widgetrefinery.util.BadUserInputException;

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
 * Utility class for reading and writing images. It uses
 * {@link org.widgetrefinery.wallpaper.common.DesktopInfo} to format images
 * suitable for use as a multi-head wallpaper.
 *
 * @see javax.imageio.ImageIO
 * @since 2/20/12 5:24 PM
 */
public class ImageUtil {
    private final DesktopInfo   info;
    private       BufferedImage mask;

    /**
     * Creates a new instance that targets the current monitor configuration.
     */
    public ImageUtil() {
        this(new DesktopInfo());
    }

    /**
     * Creates a new instance that targets the given configuration.
     *
     * @param info target configuration
     */
    public ImageUtil(final DesktopInfo info) {
        this.info = info;
    }

    /**
     * Returns the value from {@link org.widgetrefinery.wallpaper.common.DesktopInfo#getBounds()}.
     *
     * @return value from {@link org.widgetrefinery.wallpaper.common.DesktopInfo#getBounds()}
     */
    public Rectangle getBounds() {
        return this.info.getBounds();
    }

    /**
     * Returns the value from {@link org.widgetrefinery.wallpaper.common.DesktopInfo#getMonitors()}.
     *
     * @return value from {@link org.widgetrefinery.wallpaper.common.DesktopInfo#getMonitors()}
     */
    protected List<Rectangle> getMonitors() {
        return this.info.getMonitors();
    }

    /**
     * Returns an image with the non-visible portions semitransparent. When
     * painted over an actual image, it dims the off screen areas.
     *
     * @return image masking off screen areas
     */
    protected BufferedImage getMask() {
        if (null == this.mask) {
            Rectangle bounds = getBounds();
            BufferedImage mask = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2d = mask.createGraphics();
            g2d.setBackground(new Color(0, 0, 0, 0)); //transparent black
            g2d.setColor(new Color(0, 0, 0, 128)); //semi-transparent black
            g2d.fillRect(0, 0, bounds.width, bounds.height);
            for (Rectangle monitor : getMonitors()) {
                g2d.clearRect(monitor.x - bounds.x, monitor.y - bounds.y, monitor.width, monitor.height);
            }
            g2d.dispose();
            this.mask = mask;
        }
        return this.mask;
    }

    /**
     * Reads an image into memory and formats it for use as a multi-head
     * wallpaper. If the image type is not supported then null is returned.
     *
     * @param file image file to load and format
     * @return formatted image or null
     * @throws IOException if an error occurred loading the image
     */
    public BufferedImage formatImage(final File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        return null != img ? formatImage(img) : null;
    }

    /**
     * Formats the given image for use as a multi-head wallpaper.
     *
     * @param img image to format
     * @return formatted image
     */
    public BufferedImage formatImage(final BufferedImage img) {
        BufferedImage resized = resize(img);
        return translate(resized);
    }

    /**
     * Reads an image into memory and formats it for use as a multi-head
     * wallpaper. The translation step is skipped and a mask is added to
     * highlight which portions are visible. If the image type is not supported
     * then null is returned.
     *
     * @param file image file to load and format
     * @return formatted preview image or null
     * @throws IOException if an error occurred loading the image
     */
    public BufferedImage previewImage(final File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        return null != img ? previewImage(img) : null;
    }

    /**
     * Formats the given image for use as a multi-head wallpaper. The
     * translation step is skipped and a mask is added to highlight which
     * portions are visible.
     *
     * @param img image to format
     * @return formatted preview image
     */
    public BufferedImage previewImage(final BufferedImage img) {
        BufferedImage resized = resize(img);
        return mask(resized);
    }

    /**
     * Resize the given image so that it fits within the screen bounds.
     *
     * @param img image to resize
     * @return resized image
     */
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

    /**
     * Translates the given image so that the upper-left corner of the image
     * aligns with the upper-left corner of the screen bounds.
     *
     * @param img image to translate
     * @return translated image
     */
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

    /**
     * Applies the visibility mask to the given image.
     *
     * @param img image to mask
     * @return masked image
     */
    protected BufferedImage mask(final BufferedImage img) {
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(getMask(), 0, 0, img.getWidth(), img.getHeight(), null);
        g2d.dispose();
        return img;
    }

    /**
     * Saves the given image to the given file.
     *
     * @param img  image to save
     * @param file filename to save the image to
     * @throws BadUserInputException if there is a problem with the output filename
     * @throws IOException           if an error occurred writing the image
     */
    public void saveImage(final RenderedImage img, final File file) throws BadUserInputException, IOException {
        String filename = file.getName();
        int ndx = filename.lastIndexOf('.');
        if (-1 == ndx) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_BAD_OUTPUT_NAME, filename);
        }
        String ext = filename.substring(ndx + 1);

        boolean result = ImageIO.write(img, ext, file);
        if (!result) {
            throw new BadUserInputException(WallpaperTranslationKey.PROCESS_ERROR_BAD_OUTPUT_NAME, filename);
        }
    }
}
