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

package org.widgetrefinery.wallpaper.os;

import java.io.IOException;

/**
 * Since: 3/6/12 10:19 PM
 */
public abstract class AbstractOSSupport implements OSSupport {
    protected void exec(final String... cmd) throws IOException, InterruptedException, RuntimeException {
        exec(0, cmd);
    }

    protected void exec(final int expectedStaus, final String... cmd) throws IOException, InterruptedException, RuntimeException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);
        int status = p.waitFor();
        if (expectedStaus != status) {
            StringBuilder sb = new StringBuilder();
            sb.append("Command failed with status ").append(status).append(": ");
            for (String cmdArg : cmd) {
                sb.append(" '").append(cmdArg).append('\'');
            }
            throw new RuntimeException(sb.toString());
        }
    }
}
