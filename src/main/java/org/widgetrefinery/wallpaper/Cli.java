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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Since: 2/20/12 9:12 PM
 */
public class Cli {
    public static void main(String[] args) {
        try {
            CLParser clParser = new CLParser(args,
                                             new Argument("d|debug", new BooleanArgumentType(), "Display debugging info"),
                                             new Argument("f|force", new BooleanArgumentType(), "Override the output file if it exists."),
                                             new Argument("i|input", new StringArgumentType(), "Input image filename."),
                                             new Argument("o|output", new StringArgumentType(), "Output image filename."),
                                             new Argument("r|registry",
                                                          new BooleanArgumentType(),
                                                          "Update the Windows registry to use the output image as the wallpaper."));

            if (!clParser.hasArguments()) {
                System.err.println(clParser.getHelpMessage(Cli.class,
                                                           null,
                                                           "Reformats an image suitable for use as a multi-monitor wallpaper\n" +
                                                           "according to your current monitor configuration."));
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
            log(clParser, "loading " + inputFile);
            BufferedImage img = imageUtil.formatImage(inputFile);
            log(clParser, "writing " + outputFile);
            if (Boolean.TRUE == clParser.getValue("force") && outputFile.exists()) {
                log(clParser, "deleting " + outputFile);
                outputFile.delete();
            }
            imageUtil.saveImage(img, outputFile);

            if (Boolean.TRUE != clParser.getValue("registry")) {
                log(clParser, "updating registry");
                exec("reg", "add", "HKCU\\Control Panel\\Desktop", "/V", "Wallpaper", "/T", "REG_SZ", "/F", "/D", outputFile.getAbsolutePath());
                exec("reg", "add", "HKCU\\Control Panel\\Desktop", "/V", "WallpaperStyle", "/T", "REG_SZ", "/F", "/D", "0"); //0: center, 2: stretch
                exec("reg", "add", "HKCU\\Control Panel\\Desktop", "/V", "TileWallpaper", "/T", "REG_SZ", "/F", "/D", "1"); //0: center, 1: tile
            }
            log(clParser, "refreshing settings");
            exec("rundll32", "user32.dll,", "UpdatePerUserSystemParameters");
            log(clParser, "done");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    protected static void log(final CLParser clParser, final String msg) {
        if (Boolean.TRUE == clParser.getValue("verbose")) {
            System.err.println(msg);
        }
    }

    protected static void exec(final String... cmd) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);
        int result = p.waitFor();
        if (0 != result) {
            StringBuilder sb = new StringBuilder();
            String delimit = "";
            for (String cmdArg : cmd) {
                sb.append(delimit).append('"').append(cmdArg).append('"');
                delimit = " ";
            }
            throw new RuntimeException("command returned with status " + result + ": " + sb.toString());
        }
    }
}
