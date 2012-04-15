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
 * Base implementation of {@link org.widgetrefinery.wallpaper.os.OSSupport}
 * that provide utilities for interacting with the OS.
 *
 * @since 3/6/12 10:19 PM
 */
public abstract class AbstractOSSupport implements OSSupport {
    /**
     * Invokes the given command. There are no facilities for sending input or
     * reading output from the command so it should be a simple application
     * that is script-friendly. The command should exit with a return code
     * of zero in order to be considered successful.
     *
     * @param cmd the command to run plus any arguments
     * @throws IOException          if an IO error occurred
     * @throws InterruptedException if the operation was interrupted
     * @throws RuntimeException     if the return code does not match the expected value
     */
    protected void exec(final String... cmd) throws IOException, InterruptedException, RuntimeException {
        exec(0, cmd);
    }

    /**
     * Invokes the given command. There are no facilities for sending input or
     * reading output from the command so it should be a simple application
     * that is script-friendly.
     *
     * @param expectedStatus the expected return code from the command
     * @param cmd            the command to run plus any arguments
     * @throws IOException          if an IO error occurred
     * @throws InterruptedException if the operation was interrupted
     * @throws RuntimeException     if the return code does not match the expected value
     */
    protected void exec(final int expectedStatus, final String... cmd) throws IOException, InterruptedException, RuntimeException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);
        int status = p.waitFor();
        if (expectedStatus != status) {
            StringBuilder sb = new StringBuilder();
            sb.append("Command failed with status ").append(status).append(": ");
            for (String cmdArg : cmd) {
                sb.append(" '").append(cmdArg).append('\'');
            }
            throw new RuntimeException(sb.toString());
        }
    }
}
