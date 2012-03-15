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
import org.widgetrefinery.wallpaper.swing.event.BrowseForFileEvent;
import org.widgetrefinery.wallpaper.swing.event.SetConfigureOSEvent;
import org.widgetrefinery.wallpaper.swing.event.SetRefreshOSEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Since: 3/12/12 10:58 PM
 */
public class ControlPanel extends JPanel {
    public ControlPanel(final EventBus eventBus) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createOpenPanel(eventBus));
        add(createSavePanel(eventBus));
    }

    protected JPanel createOpenPanel(final EventBus eventBus) {
        JButton browse = new JButton("Browse...");
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                eventBus.fireEvent(new BrowseForFileEvent());
            }
        });

        JPanel panel = createPanel("View");
        panel.add(browse);
        return panel;
    }

    protected JPanel createSavePanel(final EventBus eventBus) {
        final JCheckBox configureOS = new JCheckBox("Configure OS");
        configureOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                eventBus.fireEvent(new SetConfigureOSEvent(configureOS.isSelected()));
            }
        });

        final JCheckBox refreshOS = new JCheckBox("Refresh OS");
        refreshOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                eventBus.fireEvent(new SetRefreshOSEvent(refreshOS.isSelected()));
            }
        });

        JPanel panel = createPanel("Save");
        panel.add(configureOS);
        panel.add(refreshOS);
        return panel;
    }

    protected JPanel createPanel(final String title) {
        JPanel panel = new JPanel();
        Border border = BorderFactory.createTitledBorder(title);
        panel.setBorder(border);
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        return panel;
    }
}
