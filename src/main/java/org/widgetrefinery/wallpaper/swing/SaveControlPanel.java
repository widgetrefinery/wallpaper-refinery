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
import org.widgetrefinery.util.StringUtil;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.event.EventListener;
import org.widgetrefinery.util.lang.TranslationKey;
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.common.WallpaperTranslationKey;
import org.widgetrefinery.wallpaper.event.SetInputFileEvent;
import org.widgetrefinery.wallpaper.os.OSUtil;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @since 4/15/12 12:17 PM
 */
public class SaveControlPanel extends AbstractControlPanel {
    private static final Logger logger = Logger.getLogger(SaveControlPanel.class.getName());

    private final Container container;

    public SaveControlPanel(final EventBus eventBus, final Model model, final Container container) {
        super(eventBus, model, WallpaperTranslationKey.GUI_SAVE_TITLE);
        this.container = container;
    }

    @Override
    protected void populate(final EventBus eventBus, final Model model, final JFileChooser fileChooser) {
        if (null != OSUtil.getOSSupport()) {
            JCheckBox configureOS = new JCheckBox(Translator.get(WallpaperTranslationKey.GUI_SAVE_CONFIG_OS_LABEL));
            configureOS.setMnemonic(KeyEvent.VK_C);
            configureOS.setSelected(model.isConfigOS());
            String configureOSTooltip = StringUtil.swingWordWrap(WallpaperTranslationKey.CL_OPT_CONFIGURE_DESC,
                                                                 WallpaperTranslationKey.CONFIG_GUI_WIDTH);
            configureOS.setToolTipText(configureOSTooltip);
            configureOS.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(final ItemEvent itemEvent) {
                    model.setConfigOS(itemEvent.getStateChange() == ItemEvent.SELECTED);
                }
            });
            add(configureOS);

            JCheckBox refreshOS = new JCheckBox(Translator.get(WallpaperTranslationKey.GUI_SAVE_REFRESH_OS_LABEL));
            refreshOS.setMnemonic(KeyEvent.VK_R);
            refreshOS.setSelected(model.isRefreshOS());
            String refreshOSTooltip = StringUtil.swingWordWrap(WallpaperTranslationKey.CL_OPT_REFRESH_DESC,
                                                               WallpaperTranslationKey.CONFIG_GUI_WIDTH);
            refreshOS.setToolTipText(refreshOSTooltip);
            refreshOS.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(final ItemEvent itemEvent) {
                    model.setRefreshOS(itemEvent.getStateChange() == ItemEvent.SELECTED);
                }
            });
            add(refreshOS);
        }

        final JButton save = new JButton(Translator.get(WallpaperTranslationKey.GUI_SAVE_SAVE_LABEL));
        save.setEnabled(null != model.getInputFile());
        save.setMnemonic(KeyEvent.VK_S);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                fileChooser.resetChoosableFileFilters();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Bitmap", "bmp"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF", "gif"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Jpeg", "jpg", "jpeg"));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG", "png"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showSaveDialog(SaveControlPanel.this.container);
                if (JFileChooser.APPROVE_OPTION == result) {
                    setOutputFile(fileChooser, model);
                    if (doSave(model)) {
                        save.doClick();
                    }
                }
            }
        });
        add(save);

        eventBus.add(SetInputFileEvent.class, new EventListener<SetInputFileEvent>() {
            @Override
            public void notify(final SetInputFileEvent event) {
                save.setEnabled(null != event.getValue());
            }
        });
    }

    protected void setOutputFile(final JFileChooser fileChooser, final Model model) {
        File file = fileChooser.getSelectedFile();
        if (!file.getName().contains(".")) {
            FileFilter fileFilter = fileChooser.getFileFilter();
            if (null != fileFilter && fileFilter instanceof FileNameExtensionFilter) {
                FileNameExtensionFilter extensionFilter = (FileNameExtensionFilter) fileFilter;
                String[] extensions = extensionFilter.getExtensions();
                if (null != extensions && 0 < extensions.length) {
                    file = new File(file.getParentFile(), file.getName() + "." + extensions[0]);
                }
            }
        }
        model.setOutputFile(file);
    }

    protected boolean doSave(final Model model) {
        boolean retry;
        try {
            retry = doSave(model, false);
        } catch (BadUserInputException e) {
            int result = JOptionPane.showConfirmDialog(this.container,
                                                       Translator.get(WallpaperTranslationKey.GUI_SAVE_CONFIRM_OVERWRITE_MESSAGE),
                                                       model.getOutputFile().toString(),
                                                       JOptionPane.YES_NO_OPTION);
            retry = JOptionPane.NO_OPTION == result || doSave(model, true);
        }
        return retry;
    }

    protected boolean doSave(final Model model, final boolean overwrite) {
        boolean retry = false;
        try {
            process(model, overwrite);
        } catch (BadUserInputException e) {
            if (!overwrite && e.getKey() == WallpaperTranslationKey.PROCESS_ERROR_OUTPUT_EXISTS) {
                throw e;
            } else {
                TranslationKey key = e.getKey();
                retry = key instanceof WallpaperTranslationKey && ((WallpaperTranslationKey) key).isRetryGuiSave();
                JOptionPane.showMessageDialog(this.container,
                                              e.getMessage(),
                                              Translator.get(WallpaperTranslationKey.GUI_SAVE_ERROR_DIALOG_TITLE),
                                              JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            String msg = Translator.get(WallpaperTranslationKey.PROCESS_ERROR_OTHER);
            logger.log(Level.WARNING, msg, e);
            JOptionPane.showMessageDialog(this.container,
                                          msg,
                                          Translator.get(WallpaperTranslationKey.GUI_SAVE_ERROR_DIALOG_TITLE),
                                          JOptionPane.ERROR_MESSAGE);
        }
        return retry;
    }

    protected void process(final Model model, final boolean overwrite) throws IOException, InterruptedException {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            model.process(overwrite);
        } finally {
            setCursor(null);
        }
    }
}
