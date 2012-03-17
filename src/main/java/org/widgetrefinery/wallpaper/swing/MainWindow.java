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

import org.widgetrefinery.util.event.DebugEventListener;
import org.widgetrefinery.util.event.Event;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.event.EventListener;
import org.widgetrefinery.wallpaper.swing.event.BrowseForFileEvent;
import org.widgetrefinery.wallpaper.swing.event.OpenFileEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.File;

/**
 * Since: 3/12/12 8:41 PM
 */
public class MainWindow extends JFrame {
    public MainWindow() throws HeadlessException {
        setTitle("Wallpaper Refinery");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        EventBus eventBus = new EventBus();
        eventBus.add(Event.class, new DebugEventListener());

        File currentDirectory = new File(System.getProperty("user.dir"));
        JFileChooser fileChooser = new JFileChooser(currentDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image", "bmp", "gif", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);
        registerBrowseEventListener(eventBus, fileChooser);

        PreviewPanel previewPanel = new PreviewPanel(eventBus);
        ControlPanel controlPanel = new ControlPanel(eventBus);
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(previewPanel, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.WEST);
        add(contentPane);
    }

    protected void registerBrowseEventListener(final EventBus eventBus, final JFileChooser fileChooser) {
        eventBus.add(BrowseForFileEvent.class, new EventListener<BrowseForFileEvent>() {
            @Override
            public void notify(final BrowseForFileEvent event) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = fileChooser.showOpenDialog(MainWindow.this);
                if (JFileChooser.APPROVE_OPTION == result) {
                    File file = fileChooser.getSelectedFile();
                    eventBus.fireEvent(new OpenFileEvent(file));
                }
            }
        });
    }
}
