package org.example.util;

public class StringUtil {
    public String getRequestStartLine(String requestInfo) {
        return requestInfo.split(System.lineSeparator(), 2)[0].split("\\s+", 3)[1];
    }
}
