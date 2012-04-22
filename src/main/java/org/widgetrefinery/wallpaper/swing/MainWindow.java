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
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.common.WallpaperTranslationKey;
import org.widgetrefinery.wallpaper.event.ResizeFinishedEvent;
import org.widgetrefinery.wallpaper.event.ResizingEvent;
import org.widgetrefinery.wallpaper.event.SetWorkingDirectoryEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @since 3/12/12 8:41 PM
 */
public class MainWindow extends JFrame {
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());

    public MainWindow(final EventBus eventBus, final Model model) throws HeadlessException {
        eventBus.add(SetWorkingDirectoryEvent.class, new EventListener<SetWorkingDirectoryEvent>() {
            @Override
            public void notify(final SetWorkingDirectoryEvent event) {
                setTitle(event.getValue());
            }
        });
        addComponentListener(new ResizeListener(eventBus));

        setTitle(model.getWorkingDirectory());
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();
        BoxLayout controlPanelLayout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
        controlPanel.setLayout(controlPanelLayout);
        controlPanel.add(new ViewControlPanel(eventBus, model));
        controlPanel.add(new SaveControlPanel(eventBus, model));
        controlPanel.add(Box.createVerticalGlue());
        controlPanel.add(new AboutControlPanel(eventBus, model));
        int maxWidth = 0;
        for (Component c : controlPanel.getComponents()) {
            maxWidth = Math.max(maxWidth, c.getPreferredSize().width);
            ((JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        for (Component c : controlPanel.getComponents()) {
            if (!(c instanceof Box.Filler)) {
                c.setMaximumSize(new Dimension(maxWidth, c.getPreferredSize().height));
            }
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(controlPanel, BorderLayout.WEST);
        contentPane.add(new PreviewPanel(eventBus, model), BorderLayout.CENTER);
        add(contentPane);

        if (logger.isLoggable(Level.FINER)) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(final KeyEvent e) {
                    logger.finer(MessageFormat.format("code: {0}, ex-code: {1}, mod: {2}, ex-mod: {3}",
                                                      e.getKeyCode(),
                                                      e.getExtendedKeyCode(),
                                                      e.getModifiers(),
                                                      e.getModifiersEx()));
                    return false;
                }
            });
        }
    }

    protected void setTitle(File workingDirectory) {
        setTitle(Translator.get(WallpaperTranslationKey.GUI_TITLE, workingDirectory));
    }

    protected static class ResizeListener extends ComponentAdapter {
        private final EventBus       eventBus;
        private final Lock           lock;
        private       ActionListener listener;

        public ResizeListener(final EventBus eventBus) {
            this.eventBus = eventBus;
            this.lock = new ReentrantLock();
        }

        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            this.lock.lock();
            try {
                this.eventBus.fireEvent(new ResizingEvent());
                ActionListener listener = new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent actionEvent) {
                        ResizeListener.this.lock.lock();
                        if (this == ResizeListener.this.listener) {
                            ResizeListener.this.eventBus.fireEvent(new ResizeFinishedEvent());
                        }
                        ResizeListener.this.lock.unlock();
                    }
                };
                this.listener = listener;
                Timer timer = new Timer(1000, listener);
                timer.setRepeats(false);
                timer.start();
            } finally {
                this.lock.unlock();
            }
        }
    }
}
