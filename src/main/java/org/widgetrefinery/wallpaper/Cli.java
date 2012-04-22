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

import org.widgetrefinery.util.BadUserInputException;
import org.widgetrefinery.util.cl.*;
import org.widgetrefinery.util.event.EventBus;
import org.widgetrefinery.util.lang.Translator;
import org.widgetrefinery.wallpaper.common.Model;
import org.widgetrefinery.wallpaper.common.WallpaperTranslationKey;
import org.widgetrefinery.wallpaper.swing.MainWindow;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads the wallpaper-refinery application. It supports both text and
 * graphical UIs.
 *
 * @since 2/20/12 9:12 PM
 */
public class Cli extends AbstractCli {
    private static final Logger logger = Logger.getLogger(Cli.class.getName());

    public static void main(String[] args) {
        new Cli().start(args);
    }

    @Override
    protected void processCommandLine(final String[] args) throws IOException {
        CLParser clParser = new CLParser(args,
                                         new Argument("c|configure",
                                                      new BooleanArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_CONFIGURE_DESC)),
                                         new Argument("f|force",
                                                      new BooleanArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_FORCE_DESC)),
                                         new Argument("h|help",
                                                      new BooleanArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_HELP_DESC)),
                                         new Argument("i|input",
                                                      new StringArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_INPUT_DESC)),
                                         new Argument("l|license",
                                                      new BooleanArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_LICENSE_ABSTRACT_DESC)),
                                         new Argument("L|full-license",
                                                      new BooleanArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_LICENSE_DESC)),
                                         new Argument("o|output",
                                                      new StringArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_OUTPUT_DESC)),
                                         new Argument("r|refresh",
                                                      new BooleanArgumentType(),
                                                      Translator.get(WallpaperTranslationKey.CL_OPT_REFRESH_DESC)));
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
            clParser.getLicenseAbstract(System.out);
            System.exit(0);
        }
        if (Boolean.TRUE == clParser.getValue("full-license")) {
            clParser.getLicense(System.out);
            System.exit(0);
        }

        EventBus eventBus = new EventBus();
        Model model = new Model(eventBus);
        model.setInputFile(new File(clParser.<String>getValue("input")));
        model.setOutputFile(new File(clParser.<String>getValue("output")));
        model.setConfigOS(Boolean.TRUE == clParser.getValue("configure"));
        model.setRefreshOS(Boolean.TRUE == clParser.getValue("refresh"));
        try {
            model.process(Boolean.TRUE == clParser.getValue("force"));
        } catch (BadUserInputException e) {
            throw e;
        } catch (Exception e) {
            String msg = Translator.get(WallpaperTranslationKey.PROCESS_ERROR_OTHER);
            logger.log(Level.FINE, msg, e);
            System.err.println(msg);
            System.exit(-1);
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
