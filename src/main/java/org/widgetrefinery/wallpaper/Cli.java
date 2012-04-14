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
import org.widgetrefinery.util.clParser.*;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.lang.WallpaperTranslatorKey;
import org.widgetrefinery.wallpaper.swing.MainWindow;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Since: 2/20/12 9:12 PM
 */
public class Cli extends AbstractCli {
    public static void main(String[] args) {
        new Cli().start(args);
    }

    @Override
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
        if (clParser.hasArguments()) {
            doTui(clParser);
        } else {
            doGui(clParser);
        }
    }

    protected void doTui(final CLParser clParser) throws IOException {
        if (Boolean.TRUE == clParser.getValue("help")) {
            System.err.println(clParser.getHelpMessage(Cli.class));
            System.exit(0);
        }
        if (Boolean.TRUE == clParser.getValue("license")) {
            clParser.displayLicense(System.out);
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

        EventBus eventBus = new EventBus();
        Model model = new Model(eventBus);
        model.setInputFile(new File(inputFilename));
        model.setOutputFile(new File(outputFilename));
        model.setConfigOS(Boolean.TRUE == clParser.getValue("configure"));
        model.setRefreshOS(Boolean.TRUE == clParser.getValue("refresh"));
        Model.Error error = model.process(Boolean.TRUE == clParser.getValue("force"));
        if (Model.Error.NO_INPUT == error) {
            System.err.println(Translator.get(WallpaperTranslatorKey.SAVE_ERROR_NO_INPUT));
        } else if (Model.Error.SAME_INPUT_OUTPUT == error) {
            System.err.println(Translator.get(WallpaperTranslatorKey.SAVE_ERROR_SAME_INPUT_OUTPUT));
        } else if (Model.Error.OUTPUT_EXISTS == error) {
            System.err.println(Translator.get(WallpaperTranslatorKey.SAVE_ERROR_OUTPUT_EXISTS, outputFilename));
        } else if (Model.Error.OTHER == error) {
            System.err.println(Translator.get(WallpaperTranslatorKey.SAVE_ERROR_OTHER));
        }
    }

    protected void doGui(final CLParser clParser) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EventBus eventBus = new EventBus();
                Model model;
                List<String> leftovers = clParser.getLeftovers();
                if (!leftovers.isEmpty()) {
                    File workingDirectory = new File(leftovers.get(0));
                    model = new Model(eventBus, workingDirectory);
                } else {
                    model = new Model(eventBus);
                }
                MainWindow mainWindow = new MainWindow(eventBus, model);
                mainWindow.setVisible(true);
            }
        });
    }
}
