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
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.common.WallpaperTranslationKey;
import org.widgetrefinery.wallpaper.event.SetThumbnailsPerRowEvent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * @since 4/15/12 12:13 PM
 */
public class ViewControlPanel extends AbstractControlPanel {
    private final Container container;

    public ViewControlPanel(final EventBus eventBus, final Model model, final Container container) {
        super(eventBus, model, WallpaperTranslationKey.GUI_VIEW_TITLE);
        this.container = container;
    }

    @Override
    protected void populate(final EventBus eventBus, final Model model, final JFileChooser fileChooser) {
        JButton browse = new JButton(Translator.get(WallpaperTranslationKey.GUI_VIEW_BROWSE_LABEL));
        browse.setMnemonic(KeyEvent.VK_B);
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                fileChooser.resetChoosableFileFilters();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image", "bmp", "gif", "jpg", "jpeg", "png"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = fileChooser.showOpenDialog(ViewControlPanel.this.container);
                if (JFileChooser.APPROVE_OPTION == result) {
                    File file = fileChooser.getSelectedFile();
                    model.setWorkingDirectory(file);
                }
            }
        });
        browse.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(browse);

        add(createVerticalPadding());

        JPanel zoomPanel = new JPanel();
        BoxLayout zoomPanelLayout = new BoxLayout(zoomPanel, BoxLayout.X_AXIS);
        zoomPanel.setLayout(zoomPanelLayout);
        String zoomTooltip = StringUtil.swingWordWrap(WallpaperTranslationKey.GUI_VIEW_ZOOM_TOOLTIP,
                                                      WallpaperTranslationKey.CONFIG_GUI_WIDTH);
        zoomPanel.setToolTipText(zoomTooltip);
        zoomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(zoomPanel);

        int minZoom = model.getMinThumbnailsPerRow();
        int maxZoom = model.getMaxThumbnailsPerRow();
        Integer[] zoomLevels = new Integer[maxZoom - minZoom + 1];
        for (int ndx = 0; ndx < zoomLevels.length; ndx++) {
            zoomLevels[ndx] = minZoom + ndx;
        }
        final JComboBox<Integer> zoomInput = new JComboBox<Integer>(zoomLevels);
        zoomInput.setSelectedItem(model.getThumbnailsPerRow());
        zoomInput.setToolTipText(zoomPanel.getToolTipText());
        zoomInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                model.setThumbnailsPerRow((Integer) zoomInput.getSelectedItem());
            }
        });
        zoomInput.setMaximumSize(zoomInput.getPreferredSize());
        zoomPanel.add(zoomInput);

        zoomPanel.add(createHorizontalPadding());

        JLabel zoomLabel = new JLabel(Translator.get(WallpaperTranslationKey.GUI_VIEW_ZOOM_LABEL));
        zoomLabel.setDisplayedMnemonic(KeyEvent.VK_Z);
        zoomLabel.setLabelFor(zoomInput);
        zoomLabel.setToolTipText(zoomPanel.getToolTipText());
        zoomPanel.add(zoomLabel);

        eventBus.add(SetThumbnailsPerRowEvent.class, new EventListener<SetThumbnailsPerRowEvent>() {
            @Override
            public void notify(final SetThumbnailsPerRowEvent event) {
                zoomInput.setSelectedItem(event.getValue());
            }
        });
    }
}
