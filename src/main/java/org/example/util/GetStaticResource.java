package org.example.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GetStaticResource {
    public byte[] getHtmlInfo(String fileName) throws IOException {
        return Files.readAllBytes(new File("./webapp/" + fileName + ".html").toPath());
    }
}
