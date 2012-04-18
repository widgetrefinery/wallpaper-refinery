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

package org.widgetrefinery.wallpaper.event;

import org.widgetrefinery.util.event.Event;

/**
 * Event indicating the user has just resized the window. This event can be
 * fired many times in a brief period, such as when the user drags a resize
 * handle. Therefore listeners should be quick to keep the UI responsive. More
 * expensive work should be attached to the {@link ResizeFinishedEvent}.
 *
 * @see ResizeFinishedEvent
 * @see org.widgetrefinery.util.event.EventBus
 * @since 4/17/12 9:45 PM
 */
public class ResizingEvent implements Event {
}
