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

package org.widgetrefinery.wallpaper.lang;

import org.widgetrefinery.util.lang.TranslatorKey;

/**
 * List of translator keys for the application.
 *
 * @see {@inheritDoc}
 * @since 4/14/12 11:07 AM
 */
public enum WallpaperTranslatorKey implements TranslatorKey {
    SAVE_ERROR_NO_INPUT("save.error.no_input"),
    SAVE_ERROR_SAME_INPUT_OUTPUT("save.error.same_input_output"),
    SAVE_ERROR_OUTPUT_EXISTS("save.error.output_exists"),
    SAVE_ERROR_OTHER("save.error.other");

    private final String key;

    private WallpaperTranslatorKey(final String key) {
        this.key = key;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.key;
    }
}
