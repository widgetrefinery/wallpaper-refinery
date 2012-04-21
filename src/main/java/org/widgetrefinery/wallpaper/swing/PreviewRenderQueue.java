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

import org.widgetrefinery.wallpaper.common.BasicCache;
import org.widgetrefinery.wallpaper.common.ImageUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @since 4/15/12 3:18 PM
 */
public class PreviewRenderQueue extends Thread {
    private static final Logger logger = Logger.getLogger(PreviewRenderQueue.class.getName());

    private final BlockingQueue<PreviewRenderRequest> queue;
    private final Map<File, CacheRecord>              cache;
    private       ImageUtil                           imageUtil;

    public PreviewRenderQueue(final ImageUtil imageUtil) {
        this.queue = new LinkedBlockingQueue<PreviewRenderRequest>();
        this.cache = Collections.synchronizedMap(new BasicCache<File, CacheRecord>());
        setImageUtil(imageUtil);
    }

    public BufferedImage render(final PreviewRenderRequest request) {
        BufferedImage cachedImage;
        CacheRecord record = this.cache.get(request.getFile());
        if (null != record) {
            cachedImage = record.getImage();
            if (record.isStale(this.imageUtil)) {
                enqueue(request);
            }
        } else {
            cachedImage = renderPlaceholderImage(this.imageUtil.getBounds(), request.getFile(), Color.WHITE, Color.BLACK);
            enqueue(request);
        }
        return cachedImage;
    }

    protected void enqueue(final PreviewRenderRequest request) {
        try {
            this.queue.put(request);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "interrupted while trying to enqueue " + request, e);
        }
    }

    public void setImageUtil(final ImageUtil imageUtil) {
        this.imageUtil = imageUtil;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            while (true) {
                render(this.queue.take(), this.imageUtil);
            }
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "interrupted while waiting on queue", e);
        }
    }

    protected void render(final PreviewRenderRequest request, final ImageUtil imageUtil) {
        File inputFile = request.getFile();
        CacheRecord record = this.cache.get(inputFile);
        if (request.isStillValid() && (null == record || record.isStale(imageUtil))) {
            BufferedImage image = null;
            if (null == record || !record.isPlaceholder()) {
                try {
                    image = imageUtil.previewImage(inputFile);
                    if (null == image) {
                        logger.log(Level.WARNING, "failed to render image " + inputFile);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "failed to render image " + inputFile, e);
                }
            }
            boolean isPlaceholder = null == image;
            if (isPlaceholder) {
                image = renderPlaceholderImage(imageUtil.getBounds(), inputFile, Color.BLACK, Color.RED);
            }
            this.cache.put(inputFile, new CacheRecord(imageUtil, image, isPlaceholder));
            request.done();
        }
    }

    protected BufferedImage renderPlaceholderImage(final Rectangle bounds, final File file, final Color bg, final Color fg) {
        BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(bg);
        g2d.fillRect(0, 0, bounds.width, bounds.height);
        g2d.setColor(fg);
        g2d.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
        Font font = g2d.getFont();
        FontRenderContext frc = g2d.getFontRenderContext();
        String text = file.getName();
        Rectangle2D textBounds = font.getStringBounds(text, frc);
        if (textBounds.getWidth() + 10 > bounds.width) {
            Rectangle2D ellipsisBounds = font.getStringBounds("...", frc);
            int ndx = text.length() - 3;
            for (; ndx > 0 && textBounds.getWidth() + ellipsisBounds.getWidth() + 10 > bounds.width; ndx--) {
                textBounds = font.getStringBounds(text, 0, ndx, frc);
            }
            text = text.substring(0, ndx) + "...";
            textBounds = font.getStringBounds(text, frc);
        }
        g2d.drawString(text, (bounds.width - (int) textBounds.getWidth()) / 2, (int) ((bounds.height - textBounds.getHeight()) / 2 - textBounds.getY()));
        g2d.dispose();
        return image;
    }

    protected static class CacheRecord {
        private final ImageUtil     imageUtil;
        private final BufferedImage image;
        private final boolean       isPlaceholder;

        public CacheRecord(final ImageUtil imageUtil, final BufferedImage image, final boolean isPlaceholder) {
            this.imageUtil = imageUtil;
            this.image = image;
            this.isPlaceholder = isPlaceholder;
        }

        public boolean isStale(final ImageUtil imageUtil) {
            return imageUtil != this.imageUtil;
        }

        public BufferedImage getImage() {
            return this.image;
        }

        public boolean isPlaceholder() {
            return this.isPlaceholder;
        }
    }
}
