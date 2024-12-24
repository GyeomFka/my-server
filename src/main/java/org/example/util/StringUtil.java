package org.example.util;

public class StringUtil {
    public static String getRequestStartLine(String requestInfo) {
        return requestInfo.split(System.lineSeparator(), 2)[0].split("\\s+", 3)[1];
    }

    public static boolean isValidQueryString(String requestUrl) {
        return requestUrl.contains("?")
                && requestUrl.indexOf("?") < requestUrl.length() - 1;
    }
}
