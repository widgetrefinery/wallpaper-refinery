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

package org.widgetrefinery.wallpaper;

import org.widgetrefinery.util.StringUtil;
import org.widgetrefinery.util.clParser.Argument;
import org.widgetrefinery.util.clParser.BooleanArgumentType;
import org.widgetrefinery.util.clParser.CLParser;
import org.widgetrefinery.util.clParser.StringArgumentType;
import org.widgetrefinery.wallpaper.common.ImageUtil;
import org.widgetrefinery.wallpaper.os.OSSupport;
import org.widgetrefinery.wallpaper.os.OSUtil;
import org.widgetrefinery.wallpaper.swing.MainWindow;

import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Since: 2/20/12 9:12 PM
 */
public class Cli {
    public static void main(String[] args) {
        boolean debugMode = null != System.getProperty("debug");

        if (debugMode) {
            Handler handler = new ConsoleHandler();
            handler.setLevel(Level.FINEST);
            Logger logger = Logger.getLogger("org.widgetrefinery");
            logger.setLevel(Level.FINEST);
            logger.addHandler(handler);
        }

        try {
            Cli cli = new Cli();
            cli.processCommandLine(args);
        } catch (Exception e) {
            if (debugMode) {
                e.printStackTrace(System.err);
            } else {
                System.err.println(e.getMessage());
            }
            System.exit(-1);
        }
    }

    protected void processCommandLine(final String[] args) throws IOException, InterruptedException {
        CLParser clParser = new CLParser(args,
                                         new Argument("c|configure",
                                                      new BooleanArgumentType(),
                                                      "Configure the OS to use the output image as the wallpaper. Only certain OS'es are supported"),
                                         new Argument("f|force",
                                                      new BooleanArgumentType(),
                                                      "Override the output file if it exists."),
                                         new Argument("h|help",
                                                      new BooleanArgumentType(),
                                                      "Displays this help message."),
                                         new Argument("i|input",
                                                      new StringArgumentType(),
                                                      "Input image filename."),
                                         new Argument("l|license",
                                                      new BooleanArgumentType(),
                                                      "Displays the GPLv3 license that this software is released under."),
                                         new Argument("o|output",
                                                      new StringArgumentType(),
                                                      "Output image filename."),
                                         new Argument("r|refresh",
                                                      new BooleanArgumentType(),
                                                      "Instruct the OS to reload the user settings. Only certain OS'es are supported"));

        if (Boolean.TRUE == clParser.getValue("help")) {
            System.err.println(clParser.getHelpMessage(Cli.class, null, "Reformats an image for use as a multi-monitor wallpaper."));
            System.exit(0);
        }
        if (Boolean.TRUE == clParser.getValue("license")) {
            clParser.displayLicense(System.out);
            System.exit(0);
        }
        if (!clParser.hasArguments()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                }
            });
            return;
        }

        String inputFilename = clParser.getValue("input");
        String outputFilename = clParser.getValue("output");
        if (StringUtil.isBlank(inputFilename)) {
            System.err.println("missing input filename");
            System.exit(-1);
        }
        if (StringUtil.isBlank(outputFilename)) {
            System.err.println("missing output filename");
            System.exit(-1);
        }
        File inputFile = new File(inputFilename);
        File outputFile = new File(outputFilename);

        ImageUtil imageUtil = new ImageUtil();
        BufferedImage img = imageUtil.formatImage(inputFile);
        imageUtil.saveImage(img, outputFile, Boolean.TRUE == clParser.getValue("force"));

        boolean configure = Boolean.TRUE == clParser.getValue("configure");
        boolean refresh = Boolean.TRUE == clParser.getValue("refresh");
        if (configure || refresh) {
            OSSupport osSupport = OSUtil.getOSSupport();
            if (null != osSupport) {
                if (configure) {
                    osSupport.updateWallpaperSettings(outputFile);
                }
                if (refresh) {
                    osSupport.reloadWallpaperSettings();
                }
            } else {
                System.err.println("Sorry, we do not support updating your OS.");
            }
        }
    }
}
