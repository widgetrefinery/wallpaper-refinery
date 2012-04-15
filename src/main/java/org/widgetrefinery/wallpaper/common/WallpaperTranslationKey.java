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

import org.widgetrefinery.util.lang.TranslationKey;

/**
 * List of translation keys for the application.
 *
 * @see org.widgetrefinery.util.lang.Translator
 * @since 4/14/12 11:07 AM
 */
public enum WallpaperTranslationKey implements TranslationKey {
    PROCESS_ERROR_NO_INPUT("process.error.no_input"),
    PROCESS_ERROR_INPUT_DOES_NOT_EXIST("process.error.input_does_not_exist"),
    PROCESS_ERROR_BAD_INPUT("process.error.bad_input"),
    PROCESS_ERROR_NO_OUTPUT("process.error.no_output", true),
    PROCESS_ERROR_OUTPUT_EXISTS("process.error.output_exists"),
    PROCESS_ERROR_BAD_OUTPUT_NAME("process.error.bad_output_name", true),
    PROCESS_ERROR_SAME_INPUT_OUTPUT("process.error.same_input_output", true),
    PROCESS_ERROR_OTHER("process.error.other");

    private final String  key;
    private final boolean retryGuiSave;

    private WallpaperTranslationKey(final String key) {
        this(key, false);
    }

    private WallpaperTranslationKey(final String key, final boolean retryGuiSave) {
        this.key = key;
        this.retryGuiSave = retryGuiSave;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public boolean isRetryGuiSave() {
        return this.retryGuiSave;
    }
}
