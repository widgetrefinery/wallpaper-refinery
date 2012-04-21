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

import org.widgetrefinery.util.StringUtil;
import org.widgetrefinery.util.lang.TranslationKey;
import org.widgetrefinery.util.lang.Translator;

/**
 * @since 4/21/12 12:52 AM
 */
public class SwingUtil {
    public static String getToolTipText(final TranslationKey key) {
        String text = Translator.get(key);
        if (40 < text.length()) {
            text = StringUtil.wordWrap(text, 40);
            text = "<html>" + text.replace("\n", "<br>") + "</html>";
        }
        return text;
    }
}
