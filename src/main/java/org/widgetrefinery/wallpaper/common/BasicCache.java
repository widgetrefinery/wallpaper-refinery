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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple in-memory cache for holding frequently used data. The cache will
 * continue to grow until the amount of free memory drops below one of two
 * thresholds. The cache will purge items based on a least-recently-used policy
 * until the JVM memory drops below both thresholds.
 * <p/>
 * The first threshold specifies a fixed amount of free memory, in bytes. The
 * default threshold is 20 megabytes. This means the cache will purge items
 * until the JVM has at least 20 megabytes of free memory.
 * <p/>
 * The second threshold specifies a percentage amount of free memory. The
 * default threshold is 20%. This means the cache will purge items until the
 * JVM has at least 20% free memory. The ratio is computed between the amount
 * of free memory and the maximum memory the JVM can grow up to (-Xmx on the
 * command line).
 * <p/>
 * The cache is based on {@link java.util.LinkedHashMap}. This means you will
 * need to wrap it with {@link java.util.Collections#synchronizedMap(java.util.Map)}
 * if you plan to use it with multiple threads. Unlike LinkedHashMap, the call
 * to {@link #get(Object)} will also cause the cache to check the memory
 * thresholds and resize as appropriate.
 *
 * @see java.lang.Runtime
 * @see java.util.LinkedHashMap
 * @since 4/20/12 12:00 AM
 */
public class BasicCache<K, V> extends LinkedHashMap<K, V> {
    private static final Logger logger                       = Logger.getLogger(BasicCache.class.getName());
    private static final int    DEFAULT_FIXED_THRESHOLD      = 20 * 1024 * 1024; //20 megabytes
    private static final int    DEFAULT_PERCENTAGE_THRESHOLD = 20;
    private static final int    INITIAL_CAPACITY             = 100;
    private static final float  LOAD_FACTOR                  = 0.75F; //default LinkedHashMap load factor according to jdk javadoc

    private final int fixedThreshold;
    private final int percentageThreshold;

    /**
     * Creates a new instance using the default memory thresholds. The default
     * fixed threshold is 20 megabytes. The default percentage threshold is
     * 20%.
     */
    public BasicCache() {
        this(DEFAULT_FIXED_THRESHOLD, DEFAULT_PERCENTAGE_THRESHOLD);
    }

    /**
     * Creates a new instance using the given memory thresholds.
     *
     * @param fixedThreshold      fixed memory threshold, in bytes
     * @param percentageThreshold percentage free memory threshold
     */
    public BasicCache(final int fixedThreshold, final int percentageThreshold) {
        super(INITIAL_CAPACITY, LOAD_FACTOR, true);
        this.fixedThreshold = fixedThreshold;
        this.percentageThreshold = percentageThreshold;
    }

    /**
     * Returns the value for the given key, or null if the key is not defined.
     * Note, calling this method will cause the cache to resize as appropriate.
     *
     * @param key the key to look up
     * @return the value associated with the given key, or null
     */
    @Override
    public V get(final Object key) {
        V value = super.get(key);
        removeEldestEntry(null);
        logger.fine("cache " + (null != value ? "hit" : "miss") + " on " + key);
        return value;
    }

    /**
     * This will remove items based on a least-recently-used policy to stay
     * within the given memory thresholds.
     *
     * @param eldest least recently accessed entry in the map
     * @return always false
     */
    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        Iterator<K> itr = keySet().iterator();
        int count = 0;
        long freeMemory = getFreeMemory();
        long maxMemory = getMaxMemory();
        while (itr.hasNext() && (freeMemory < this.fixedThreshold || freeMemory * 100 / maxMemory < this.percentageThreshold)) {
            itr.next();
            itr.remove();
            count++;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(MessageFormat.format("free: {0}, max: {1}, fixed: {2}, %: {3}",
                                                 freeMemory,
                                                 maxMemory,
                                                 this.fixedThreshold,
                                                 this.percentageThreshold));
            }
            freeMemory = getFreeMemory();
            maxMemory = getMaxMemory();
        }
        if (0 < count) {
            logger.fine("purged " + count + " items");
        }
        return false;
    }

    /**
     * Computes the JVM free memory, in bytes. This is provided so that unit
     * tests can alter how free memory is computed.
     *
     * @return JVM free memory in bytes
     */
    protected long getFreeMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.freeMemory() + runtime.maxMemory() - runtime.totalMemory();
    }

    /**
     * Computes the maximum JVM memory, in bytes. This is provided so that unit
     * tests can alter how maximum memory is computed.
     *
     * @return JVM maximum memory in bytes
     */
    protected long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
}
