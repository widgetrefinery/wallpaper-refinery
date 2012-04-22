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
import org.widgetrefinery.util.cl.CLParser;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.util.lang.UtilTranslationKey;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.common.WallpaperTranslationKey;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @since 4/21/12 3:33 PM
 */
public class AboutControlPanel extends AbstractControlPanel {
    private final Container container;

    public AboutControlPanel(final EventBus eventBus, final Model model, final Container container) {
        super(eventBus, model, WallpaperTranslationKey.GUI_ABOUT_TITLE);
        this.container = container;
    }

    @Override
    protected void populate(final EventBus eventBus, final Model model, final JFileChooser fileChooser) {
        JButton help = new JButton(Translator.get(WallpaperTranslationKey.GUI_ABOUT_HELP_LABEL));
        help.setMnemonic(KeyEvent.VK_H);
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(AboutControlPanel.this.container,
                                              Translator.get(WallpaperTranslationKey.GUI_ABOUT_HELP_MSG),
                                              Translator.get(WallpaperTranslationKey.CL_OPT_VERSION_MSG),
                                              JOptionPane.PLAIN_MESSAGE);
            }
        });
        help.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(help);

        add(createVerticalPadding());

        JButton license = new JButton(Translator.get(WallpaperTranslationKey.GUI_ABOUT_LICENSE_LABEL));
        license.setMnemonic(KeyEvent.VK_L);
        license.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                String msg;
                try {
                    msg = StringUtil.loadResource(CLParser.LICENSE_ABSTRACT_RESOURCE);
                    if (null == msg) {
                        msg = Translator.get(UtilTranslationKey.CL_ERROR_MISSING_LICENSE);
                    }
                } catch (IOException e) {
                    msg = Translator.get(UtilTranslationKey.CL_ERROR_MISSING_LICENSE);
                }
                JOptionPane.showMessageDialog(AboutControlPanel.this.container,
                                              msg,
                                              Translator.get(WallpaperTranslationKey.CL_OPT_VERSION_MSG),
                                              JOptionPane.PLAIN_MESSAGE);
            }
        });
        license.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(license);
    }
}
