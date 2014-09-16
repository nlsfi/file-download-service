package fi.nls.fileservice.util;

import java.text.DecimalFormat;

public class Formatter {

    public static String formatLength(Long length) {
        if (length <= 0)
            return "0";
        final String[] units = new String[] { "t", "Kt", "Mt", "Gt", "Tt" };
        int digitGroups = (int) (Math.log10(length) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(length
                / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    public static String[] getPathComponents(String path) {
        int path2fileSeparator = path.lastIndexOf("/");
        if (path2fileSeparator > 0) {
            String parentPath = path.substring(0, path2fileSeparator);
            String name = path.substring(path2fileSeparator + 1, path.length());
            return new String[] { parentPath, name };
        }
        return new String[] { path };
    }
}
