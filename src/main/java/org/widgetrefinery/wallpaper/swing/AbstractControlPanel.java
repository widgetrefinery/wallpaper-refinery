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

import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.lang.TranslationKey;
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.wallpaper.common.Model;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * @since 3/12/12 10:58 PM
 */
public abstract class AbstractControlPanel extends JPanel {
    protected AbstractControlPanel(final EventBus eventBus, final Model model, final TranslationKey title) {
        JFileChooser fileChooser = new JFileChooser(model.getWorkingDirectory());

        Border border = BorderFactory.createTitledBorder(Translator.get(title));
        setBorder(border);
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        populate(eventBus, model, fileChooser);
    }

    protected abstract void populate(EventBus eventBus, Model model, JFileChooser fileChooser);
}
