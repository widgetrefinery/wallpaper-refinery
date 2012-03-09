package org.widgetrefinery.wallpaper;/*
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

import org.widgetrefinery.util.StringUtil;
import org.widgetrefinery.util.clParser.Argument;
import org.widgetrefinery.util.clParser.BooleanArgumentType;
import org.widgetrefinery.util.clParser.CLParser;
import org.widgetrefinery.util.clParser.StringArgumentType;
import org.widgetrefinery.wallpaper.common.ImageUtil;
import org.widgetrefinery.wallpaper.os.OSSupport;
import org.widgetrefinery.wallpaper.os.OSUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Since: 2/20/12 9:12 PM
 */
public class Cli {
    public static void main(String[] args) {
        try {
            Cli cli = new Cli();
            cli.processCommandLine(args);
        } catch (Exception e) {
            if (null != System.getProperty("debug")) {
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
                                                      "Configure the OS to use the output image as the wallpaper. Only certain OSes\nare supported"),
                                         new Argument("f|force", new BooleanArgumentType(), "Override the output file if it exists."),
                                         new Argument("i|input", new StringArgumentType(), "Input image filename."),
                                         new Argument("o|output", new StringArgumentType(), "Output image filename."),
                                         new Argument("r|refresh",
                                                      new BooleanArgumentType(),
                                                      "Instruct the OS to reload the user settings. Only certain OSes\nare supported"));

        if (!clParser.hasArguments()) {
            System.err.println(clParser.getHelpMessage(Cli.class, null, "Reformats an image for use as a multi-monitor wallpaper."));
            System.exit(0);
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

        Boolean configure = clParser.getValue("configure");
        Boolean refresh = clParser.getValue("refresh");
        if (Boolean.TRUE == configure || Boolean.TRUE == refresh) {
            OSSupport osSupport = OSUtil.getOSSupport();
            if (null != osSupport) {
                if (Boolean.TRUE == configure) {
                    osSupport.updateWallpaperSettings(outputFile);
                }
                if (Boolean.TRUE == refresh) {
                    osSupport.reloadWallpaperSettings();
                }
            } else {
                System.err.println("Sorry, we do not support updating your OS.");
            }
        }
    }
}
