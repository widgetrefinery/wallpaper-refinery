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

import org.widgetrefinery.util.BadUserInputException;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.event.EventListener;
import org.widgetrefinery.util.lang.TranslationKey;
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.common.WallpaperTranslationKey;
import org.widgetrefinery.wallpaper.event.SetInputFileEvent;
import org.widgetrefinery.wallpaper.os.OSSupport;
import org.widgetrefinery.wallpaper.os.OSUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Since: 3/12/12 10:58 PM
 */
public class ControlPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ControlPanel.class.getName());

    private final Model model;

    public ControlPanel(final EventBus eventBus, final Model model) {
        this.model = model;
        JFileChooser fileChooser = new JFileChooser(model.getWorkingDirectory());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createOpenPanel(model, fileChooser));
        add(createSavePanel(eventBus, model, fileChooser));
    }

    protected JPanel createOpenPanel(final Model model, final JFileChooser fileChooser) {
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
                }
            }
        });

        JPanel panel = createPanel("View");
        panel.add(browse);
        return panel;
    }

    protected JPanel createSavePanel(final EventBus eventBus, final Model model, final JFileChooser fileChooser) {
        OSSupport osSupport = OSUtil.getOSSupport();

        JCheckBox configureOS = new JCheckBox("Configure OS");
        configureOS.setEnabled(null != osSupport);
        configureOS.setSelected(model.isConfigOS());
        configureOS.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent itemEvent) {
                model.setConfigOS(itemEvent.getStateChange() == ItemEvent.SELECTED);
            }
        });

        JCheckBox refreshOS = new JCheckBox("Refresh OS");
        refreshOS.setEnabled(null != osSupport);
        refreshOS.setSelected(model.isRefreshOS());
        refreshOS.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent itemEvent) {
                model.setRefreshOS(itemEvent.getStateChange() == ItemEvent.SELECTED);
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
                    setOutputFile(fileChooser);
                    if (doSave()) {
                        save.doClick();
                    }
                }
            }
        });

        eventBus.add(SetInputFileEvent.class, new EventListener<SetInputFileEvent>() {
            @Override
            public void notify(final SetInputFileEvent event) {
                save.setEnabled(null != event.getValue());
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

    protected void setOutputFile(final JFileChooser fileChooser) {
        File file = fileChooser.getSelectedFile();
        if (!file.getName().contains(".")) {
            FileFilter fileFilter = fileChooser.getFileFilter();
            if (null != fileFilter && fileFilter instanceof FileNameExtensionFilter) {
                FileNameExtensionFilter fnef = (FileNameExtensionFilter) fileFilter;
                String[] extensions = fnef.getExtensions();
                if (null != extensions && 0 < extensions.length) {
                    file = new File(file.getParentFile(), file.getName() + "." + extensions[0]);
                }
            }
        }
        this.model.setOutputFile(file);
    }

    protected boolean doSave() {
        boolean retry;
        try {
            retry = doSave(false);
        } catch (BadUserInputException e) {
            int result = JOptionPane.showConfirmDialog(this, "Overwrite existing file?", this.model.getOutputFile().toString(), JOptionPane.YES_NO_OPTION);
            retry = JOptionPane.NO_OPTION == result || doSave(true);
        }
        return retry;
    }

    protected boolean doSave(final boolean overwrite) {
        boolean retry = false;
        try {
            process(overwrite);
        } catch (BadUserInputException e) {
            if (!overwrite && e.getKey() == WallpaperTranslationKey.PROCESS_ERROR_OUTPUT_EXISTS) {
                throw e;
            } else {
                TranslationKey key = e.getKey();
                retry = key instanceof WallpaperTranslationKey && ((WallpaperTranslationKey) key).isRetryGuiSave();
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            String msg = Translator.get(WallpaperTranslationKey.PROCESS_ERROR_OTHER);
            logger.log(Level.WARNING, msg, e);
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return retry;
    }

    protected void process(final boolean overwrite) throws IOException, InterruptedException {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            this.model.process(overwrite);
        } finally {
            setCursor(null);
        }
    }
}
