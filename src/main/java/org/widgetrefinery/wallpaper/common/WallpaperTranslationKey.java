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

package org.widgetrefinery.wallpaper.common;

import org.widgetrefinery.util.lang.TranslationKey;

/**
 * List of translation keys for the application.
 *
 * @see org.widgetrefinery.util.lang.Translator
 * @since 4/14/12 11:07 AM
 */
public enum WallpaperTranslationKey implements TranslationKey {
    CL_OPT_CONFIGURE_DESC("cl.option.configure.description"),
    CL_OPT_FORCE_DESC("cl.option.force.description"),
    CL_OPT_HELP_DESC("cl.option.help.description"),
    CL_OPT_INPUT_DESC("cl.option.input.description"),
    CL_OPT_LICENSE_ABSTRACT_DESC("cl.option.license_abstract.description"),
    CL_OPT_LICENSE_DESC("cl.option.license.description"),
    CL_OPT_OUTPUT_DESC("cl.option.output.description"),
    CL_OPT_REFRESH_DESC("cl.option.refresh.description"),
    CL_OPT_VERSION_DESC("cl.option.version.description"),
    CL_OPT_VERSION_MSG("cl.option.version.msg"),

    GUI_TITLE("gui.title"),
    GUI_VIEW_TITLE("gui.view.title"),
    GUI_VIEW_BROWSE_LABEL("gui.view.browse.label"),
    GUI_VIEW_ZOOM_LABEL("gui.view.zoom.label"),
    GUI_VIEW_ZOOM_TOOLTIP("gui.view.zoom.tooltip"),
    GUI_SAVE_TITLE("gui.save.title"),
    GUI_SAVE_CONFIG_OS_LABEL("gui.save.configOS.label"),
    GUI_SAVE_REFRESH_OS_LABEL("gui.save.refreshOS.label"),
    GUI_SAVE_SAVE_LABEL("gui.save.save.label"),
    GUI_SAVE_CONFIRM_OVERWRITE_MESSAGE("gui.save.confirmOverwrite.message"),
    GUI_SAVE_ERROR_DIALOG_TITLE("gui.save.errorDialog.title"),
    GUI_ABOUT_TITLE("gui.about.title"),
    GUI_ABOUT_HELP_LABEL("gui.about.help.label"),
    GUI_ABOUT_HELP_MSG("gui.about.help.msg"),
    GUI_ABOUT_LICENSE_LABEL("gui.about.license.label"),

    PROCESS_ERROR_NO_INPUT("process.error.no_input"),
    PROCESS_ERROR_INPUT_DOES_NOT_EXIST("process.error.input_does_not_exist"),
    PROCESS_ERROR_BAD_INPUT("process.error.bad_input"),
    PROCESS_ERROR_NO_OUTPUT("process.error.no_output", true),
    PROCESS_ERROR_OUTPUT_EXISTS("process.error.output_exists"),
    PROCESS_ERROR_BAD_OUTPUT_NAME("process.error.bad_output_name", true),
    PROCESS_ERROR_SAME_INPUT_OUTPUT("process.error.same_input_output", true),
    PROCESS_ERROR_OTHER("process.error.other"),

    CONFIG_GUI_WIDTH("config.gui.width");

    private final String  key;
    private final boolean retryGuiSave;

    private WallpaperTranslationKey(final String key) {
        this(key, false);
    }

    private WallpaperTranslationKey(final String key, final boolean retryGuiSave) {
        this.key = key;
        this.retryGuiSave = retryGuiSave;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public boolean isRetryGuiSave() {
        return this.retryGuiSave;
    }
}
