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

import org.widgetrefinery.wallpaper.common.ImageUtil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
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
        this.cache = Collections.synchronizedMap(new Cache());
        setImageUtil(imageUtil);
    }

    public BufferedImage render(final PreviewRenderRequest request) {
        BufferedImage cachedImage = null;
        CacheRecord record = this.cache.get(request.getFile());
        if (null != record) {
            cachedImage = record.getImage();
            if (record.isStale(this.imageUtil)) {
                enqueue(request);
            }
        } else {
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
            BufferedImage image;
            try {
                image = imageUtil.previewImage(inputFile);
                if (null == image) {
                    logger.log(Level.WARNING, "failed to render image " + inputFile);
                    image = renderErrorImage(imageUtil.getBounds());
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "failed to render image " + inputFile, e);
                image = renderErrorImage(imageUtil.getBounds());
            }
            this.cache.put(inputFile, new CacheRecord(imageUtil, image));
            request.done();
        }
    }

    protected BufferedImage renderErrorImage(final Rectangle bounds) {
        BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        g2d.drawLine(0, 0, bounds.width, bounds.height);
        g2d.drawLine(0, bounds.height, bounds.width, 0);
        g2d.dispose();
        return image;
    }

    protected static class Cache extends LinkedHashMap<File, CacheRecord> {
        private static final int   CACHE_SIZE  = 256;
        private static final float LOAD_FACTOR = 0.75F; //default LinkedHashMap load factor according to jdk javadoc

        private final int size;

        public Cache() {
            super(CACHE_SIZE + 1, LOAD_FACTOR, true);
            this.size = CACHE_SIZE;
        }

        @Override
        protected boolean removeEldestEntry(final Map.Entry<File, CacheRecord> eldest) {
            return size() > this.size;
        }
    }

    protected static class CacheRecord {
        private final ImageUtil     imageUtil;
        private final BufferedImage image;

        public CacheRecord(final ImageUtil imageUtil, final BufferedImage image) {
            this.imageUtil = imageUtil;
            this.image = image;
        }

        public boolean isStale(final ImageUtil imageUtil) {
            return imageUtil != this.imageUtil;
        }

        public BufferedImage getImage() {
            return this.image;
        }
    }
}
