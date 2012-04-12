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
import org.widgetrefinery.util.event.EventListener;
import org.widgetrefinery.wallpaper.os.OSSupport;
import org.widgetrefinery.wallpaper.os.OSUtil;
import org.widgetrefinery.wallpaper.swing.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Since: 3/12/12 10:58 PM
 */
public class ControlPanel extends JPanel {
    public ControlPanel(final EventBus eventBus, final Model model) {
        JFileChooser fileChooser = new JFileChooser(model.getWorkingDirectory());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createOpenPanel(eventBus, model, fileChooser));
        add(createSavePanel(eventBus, model, fileChooser));
    }

    protected JPanel createOpenPanel(final EventBus eventBus, final Model model, final JFileChooser fileChooser) {
        JButton browse = new JButton("Browse...");
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                fileChooser.resetChoosableFileFilters();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image", "bmp", "gif", "jpg", "jpeg", "png"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = fileChooser.showOpenDialog(ControlPanel.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    File file = fileChooser.getSelectedFile();
                    model.setWorkingDirectory(file);
                    eventBus.fireEvent(new SetWorkingDirectoryEvent(file));
                }
            }
        });

        JPanel panel = createPanel("View");
        panel.add(browse);
        return panel;
    }

    protected JPanel createSavePanel(final EventBus eventBus, final Model model, final JFileChooser fileChooser) {
        OSSupport osSupport = OSUtil.getOSSupport();

        final JCheckBox configureOS = new JCheckBox("Configure OS");
        configureOS.setEnabled(null != osSupport);
        configureOS.setSelected(model.isConfigOS());
        configureOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                boolean selected = configureOS.isSelected();
                model.setConfigOS(selected);
                eventBus.fireEvent(new SetConfigureOSEvent(selected));
            }
        });

        final JCheckBox refreshOS = new JCheckBox("Refresh OS");
        refreshOS.setEnabled(null != osSupport);
        refreshOS.setSelected(model.isRefreshOS());
        refreshOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                boolean selected = refreshOS.isSelected();
                model.setRefreshOS(selected);
                eventBus.fireEvent(new SetRefreshOSEvent(selected));
            }
        });

        final JButton save = new JButton("Save...");
        save.setEnabled(null != model.getInputFile());
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                fileChooser.resetChoosableFileFilters();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Bitmap", "bmp"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF", "gif"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Jpeg", "jpg", "jpeg"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG", "png"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showSaveDialog(ControlPanel.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    File file = fileChooser.getSelectedFile();
                    model.setOutputFile(file);
                    eventBus.fireEvent(new SetOutputFileEvent(file));
                }
            }
        });

        eventBus.add(SetWorkingDirectoryEvent.class, new EventListener<SetWorkingDirectoryEvent>() {
            @Override
            public void notify(final SetWorkingDirectoryEvent event) {
                save.setEnabled(null != model.getInputFile());
            }
        });
        eventBus.add(SetInputFileEvent.class, new EventListener<SetInputFileEvent>() {
            @Override
            public void notify(final SetInputFileEvent event) {
                save.setEnabled(null != event.getInputFile());
            }
        });

        JPanel panel = createPanel("Save");
        panel.add(configureOS);
        panel.add(refreshOS);
        panel.add(save);
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
