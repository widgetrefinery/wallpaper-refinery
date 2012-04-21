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

/**
 * @since 4/20/12 12:13 AM
 */
public class TestBasicCache extends TestCase {
    public void testCache() {
        //warm up the cache
        long twoHundredMegabytes = 200 * 1024 * 1024;
        StubCache<Integer, String> cache = new StubCache<Integer, String>(twoHundredMegabytes, twoHundredMegabytes);
        cache.put(3, "a");
        cache.put(2, "b");
        cache.put(1, "c");
        assertEquals(3, cache.size());

        //drop free memory below fixed threshold; the least recently used entry should be dropped by the call to put()
        cache.freeMemory = 10 * 1024 * 1024;
        cache.put(4, "d");
        assertEquals(3, cache.size());
        assertEquals(true, cache.containsKey(1));
        assertEquals(true, cache.containsKey(2));
        assertEquals(true, cache.containsKey(4));

        //increase max memory so that percentage free drops below threshold; the LRU entry should be dropped by the call to put()
        cache.maxMemory = 1001 * 1024 * 1024;
        cache.put(5, "e");
        assertEquals(3, cache.size());
        assertEquals(true, cache.containsKey(1));
        assertEquals(true, cache.containsKey(4));
        assertEquals(true, cache.containsKey(5));

        //decrease free memory and increase max memory; the LRU entry should be dropped
        cache.freeMemory = 10 * 1024 * 1024;
        cache.maxMemory = 1001 * 1024 * 1024;
        cache.get(4);
        assertEquals(2, cache.size());
        assertEquals(true, cache.containsKey(4));
        assertEquals(true, cache.containsKey(5));
    }

    protected static class StubCache<K, V> extends BasicCache<K, V> {
        private final long defaultFreeMemory;
        private final long defaultMaxMemory;
        private       long freeMemory;
        private       long maxMemory;

        public StubCache(final long defaultFreeMemory, final long defaultMaxMemory) {
            this.defaultFreeMemory = defaultFreeMemory;
            this.defaultMaxMemory = defaultMaxMemory;
            this.freeMemory = defaultFreeMemory;
            this.maxMemory = defaultMaxMemory;
        }

        @Override
        protected long getFreeMemory() {
            long freeMemory = this.freeMemory;
            this.freeMemory = this.defaultFreeMemory;
            return freeMemory;
        }

        @Override
        protected long getMaxMemory() {
            long maxMemory = this.maxMemory;
            this.maxMemory = this.defaultMaxMemory;
            return maxMemory;
        }
    }
}
