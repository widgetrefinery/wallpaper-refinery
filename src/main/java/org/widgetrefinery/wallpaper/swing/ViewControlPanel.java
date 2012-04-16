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
import org.widgetrefinery.wallpaper.common.Model;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @since 4/15/12 12:13 PM
 */
public class ViewControlPanel extends AbstractControlPanel {
    public ViewControlPanel(final EventBus eventBus, final Model model) {
        super(eventBus, model, "View");
    }

    @Override
    protected void populate(final EventBus eventBus, final Model model, final JFileChooser fileChooser) {
        JButton browse = new JButton("Browse...");
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                fileChooser.resetChoosableFileFilters();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image", "bmp", "gif", "jpg", "jpeg", "png"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = fileChooser.showOpenDialog(ViewControlPanel.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    File file = fileChooser.getSelectedFile();
                    model.setWorkingDirectory(file);
                }
            }
        });
        add(browse);
    }
}