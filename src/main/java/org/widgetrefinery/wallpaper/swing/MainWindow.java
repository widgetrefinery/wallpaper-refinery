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
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.event.EventListener;
import org.widgetrefinery.wallpaper.common.ImageUtil;
import org.widgetrefinery.wallpaper.os.OSSupport;
import org.widgetrefinery.wallpaper.os.OSUtil;
import org.widgetrefinery.wallpaper.swing.event.RetrySaveEvent;
import org.widgetrefinery.wallpaper.swing.event.SetOutputFileEvent;
import org.widgetrefinery.wallpaper.swing.event.SetWorkingDirectoryEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Since: 3/12/12 8:41 PM
 */
public class MainWindow extends JFrame implements EventListener<SetOutputFileEvent> {
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());

    private final EventBus eventBus;
    private final Model    model;

    public MainWindow() throws HeadlessException {
        this.eventBus = new EventBus();
        this.model = new Model();

        this.eventBus.add(SetWorkingDirectoryEvent.class, new EventListener<SetWorkingDirectoryEvent>() {
            @Override
            public void notify(final SetWorkingDirectoryEvent event) {
                setTitle(event.getValue());
            }
        });
        this.eventBus.add(SetOutputFileEvent.class, this);

        setTitle(this.model.getWorkingDirectory());
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        PreviewPanel previewPanel = new PreviewPanel(this.eventBus, this.model);
        ControlPanel controlPanel = new ControlPanel(this.eventBus, this.model);
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(previewPanel, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.WEST);
        add(contentPane);
    }

    protected void setTitle(File workingDirectory) {
        if (null != workingDirectory && workingDirectory.isFile()) {
            workingDirectory = workingDirectory.getParentFile();
        }
        setTitle("Wallpaper Refinery - " + workingDirectory);
    }

    @Override
    public void notify(final SetOutputFileEvent event) {
        File input = this.model.getInputFile();
        File output = this.model.getOutputFile();
        if (null != input && null != output) {
            if (input.equals(output)) {
                JOptionPane.showMessageDialog(this, "Input and output files cannot be the same.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean overwrite = output.exists();
                if (overwrite) {
                    int result = JOptionPane.showConfirmDialog(this, "Overwrite existing file?", output.toString(), JOptionPane.YES_NO_OPTION);
                    if (JOptionPane.NO_OPTION == result) {
                        this.eventBus.fireEvent(new RetrySaveEvent());
                        return;
                    }
                }
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    process(input, output, overwrite);
                } finally {
                    this.setCursor(null);
                }
            }
        }
    }

    protected void process(final File input, final File output, final boolean overwrite) {
        BufferedImage image = null;
        ImageUtil imageUtil = new ImageUtil();
        try {
            image = imageUtil.formatImage(input);
            imageUtil.saveImage(image, output, overwrite);
        } catch (Exception e) {
            String msg = "Failed to save image: " + e.getMessage();
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(StringUtil.format(msg, e));
            }
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }

        OSSupport osSupport = OSUtil.getOSSupport();
        if (null != image && null != osSupport) {
            try {
                if (this.model.isConfigOS()) {
                    osSupport.updateWallpaperSettings(output);
                }
                if (this.model.isRefreshOS()) {
                    osSupport.reloadWallpaperSettings();
                }
            } catch (Exception e) {
                String msg = "Failed to update the os: " + e.getMessage();
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(StringUtil.format(msg, e));
                }
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
