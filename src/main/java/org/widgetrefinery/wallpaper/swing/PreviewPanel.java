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
import org.widgetrefinery.wallpaper.common.DesktopInfo;
import org.widgetrefinery.wallpaper.common.ImageUtil;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.event.ResizeFinishedEvent;
import org.widgetrefinery.wallpaper.event.ResizingEvent;
import org.widgetrefinery.wallpaper.event.SetThumbnailsPerRowEvent;
import org.widgetrefinery.wallpaper.event.SetWorkingDirectoryEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;

/**
 * @since 3/14/12 9:02 PM
 */
public class PreviewPanel extends JScrollPane {
    private final Model                        model;
    private final DefaultListModel<File>       listModel;
    private final JList<File>                  listWidget;
    private final PreviewListSelectionListener listener;

    public PreviewPanel(final EventBus eventBus, final Model model) {
        this.model = model;

        ImageUtil imageUtil = new ImageUtil(new DesktopInfo(200, -1));
        Rectangle bounds = imageUtil.getBounds();
        final PreviewRenderQueue renderQueue = new PreviewRenderQueue(imageUtil);
        renderQueue.start();

        this.listModel = new DefaultListModel<File>();
        this.listWidget = new JList<File>(this.listModel);
        this.listWidget.setBackground(Color.BLACK);
        this.listWidget.setCellRenderer(new PreviewRenderer(renderQueue));
        this.listWidget.setFixedCellHeight(bounds.height);
        this.listWidget.setFixedCellWidth(bounds.width);
        this.listWidget.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.listWidget.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.listWidget.setVisibleRowCount(-1);
        this.listener = new PreviewListSelectionListener(model, this.listWidget);
        this.listWidget.addListSelectionListener(this.listener);
        ClickDragScrollListener mouseListener = new ClickDragScrollListener(getViewport());
        this.listWidget.addMouseListener(mouseListener);
        this.listWidget.addMouseMotionListener(mouseListener);
        this.listWidget.addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                //trick the ToolTipManager into hiding the tooltip whenever the mouse is moved
                ToolTipManager.sharedInstance().mousePressed(e);
            }
        });

        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        String grabFocusAction = "grabFocus";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), grabFocusAction);
        actionMap.put(grabFocusAction, new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                PreviewPanel.this.listWidget.grabFocus();
            }
        });
        String zoomInAction = "zoomIn";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.SHIFT_MASK), zoomInAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), zoomInAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), zoomInAction);
        actionMap.put(zoomInAction, new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                model.setThumbnailsPerRow(model.getThumbnailsPerRow() - 1);
            }
        });
        String zoomOutAction = "zoomOut";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), zoomOutAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), zoomOutAction);
        actionMap.put(zoomOutAction, new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                model.setThumbnailsPerRow(model.getThumbnailsPerRow() + 1);
            }
        });

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setViewportView(this.listWidget);
        refresh();

        eventBus.add(SetWorkingDirectoryEvent.class, new EventListener<SetWorkingDirectoryEvent>() {
            @Override
            public void notify(final SetWorkingDirectoryEvent event) {
                refresh();
            }
        });
        eventBus.add(SetThumbnailsPerRowEvent.class, new EventListener<SetThumbnailsPerRowEvent>() {
            @Override
            public void notify(final SetThumbnailsPerRowEvent event) {
                resize(renderQueue, true);
            }
        });
        eventBus.add(ResizingEvent.class, new EventListener<ResizingEvent>() {
            @Override
            public void notify(final ResizingEvent event) {
                eventBus.remove(this);
                eventBus.add(ResizingEvent.class, new EventListener<ResizingEvent>() {
                    @Override
                    public void notify(final ResizingEvent event) {
                        resize(renderQueue, false);
                    }
                });
                resize(renderQueue, true);
            }
        });
        eventBus.add(ResizeFinishedEvent.class, new EventListener<ResizeFinishedEvent>() {
            @Override
            public void notify(final ResizeFinishedEvent event) {
                resize(renderQueue, true);
            }
        });
    }

    protected void refresh() {
        this.listener.setEnable(false);
        try {
            this.listModel.clear();
            File workingDirectory = this.model.getWorkingDirectory();
            File[] children = workingDirectory.listFiles(new ImageFileFilter());
            Integer selectedNdx = null;

            if (null != children && 0 < children.length) {
                this.listModel.ensureCapacity(children.length);
                File inputFile = this.model.getInputFile();
                for (File child : children) {
                    this.listModel.addElement(child);
                    if (child.equals(inputFile)) {
                        selectedNdx = this.listModel.size() - 1;
                    }
                }
            }

            if (null != selectedNdx) {
                this.listWidget.setSelectedIndex(selectedNdx);
                this.listWidget.ensureIndexIsVisible(selectedNdx);
            }
        } finally {
            this.listener.setEnable(true);
        }
    }

    protected void resize(final PreviewRenderQueue renderQueue, final boolean fullReconfigure) {
        int width = getViewport().getWidth() / this.model.getThumbnailsPerRow();
        ImageUtil imageUtil = new ImageUtil(new DesktopInfo(width, -1));
        Rectangle bounds = imageUtil.getBounds();
        this.listWidget.setFixedCellHeight(bounds.height);
        this.listWidget.setFixedCellWidth(bounds.width);
        if (fullReconfigure) {
            renderQueue.setImageUtil(imageUtil);
            this.listWidget.repaint();
        }
    }

    protected static class PreviewListSelectionListener implements ListSelectionListener {
        private final Model       model;
        private final JList<File> listWidget;
        private       boolean     enable;

        public PreviewListSelectionListener(final Model model, final JList<File> listWidget) {
            this.model = model;
            this.listWidget = listWidget;
            this.enable = true;
        }

        public void setEnable(final boolean enable) {
            this.enable = enable;
        }

        @Override
        public void valueChanged(final ListSelectionEvent event) {
            if (!event.getValueIsAdjusting() && this.enable) {
                File file = this.listWidget.getSelectedValue();
                this.model.setInputFile(file);
            }
        }
    }

    protected static class ImageFileFilter implements FileFilter {
        @Override
        public boolean accept(final File file) {
            boolean result = false;
            if (file.isFile() && file.canRead()) {
                String name = file.getName();
                int ndx = name.lastIndexOf('.');
                if (-1 < ndx) {
                    String extension = name.substring(ndx + 1).toLowerCase();
                    result = ImageIO.getImageReadersBySuffix(extension).hasNext();
                }
            }
            return result;
        }
    }

    protected static class ClickDragScrollListener extends MouseInputAdapter {
        private final JViewport viewport;
        private       Point     initialPosition;

        public ClickDragScrollListener(final JViewport viewport) {
            this.viewport = viewport;
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            if (MouseEvent.BUTTON3 == e.getButton()) {
                Point viewPosition = this.viewport.getViewPosition();
                Point mousePosition = this.viewport.getMousePosition(true);
                if (null != viewPosition && null != mousePosition) {
                    int x = viewPosition.x;
                    int y = viewPosition.y + mousePosition.y;
                    this.initialPosition = new Point(x, y);
                    this.viewport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            this.initialPosition = null;
            this.viewport.setCursor(null);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (null != this.initialPosition) {
                Point mousePosition = this.viewport.getMousePosition(true);
                if (null != mousePosition) {
                    int x = this.initialPosition.x;
                    int y = this.initialPosition.y - mousePosition.y;
                    if (0 > y) {
                        y = 0;
                    } else if (y > this.viewport.getView().getHeight() - this.viewport.getExtentSize().height) {
                        y = this.viewport.getView().getHeight() - this.viewport.getExtentSize().height;
                    }
                    this.viewport.setViewPosition(new Point(x, y));
                }
            }
        }
    }
}
