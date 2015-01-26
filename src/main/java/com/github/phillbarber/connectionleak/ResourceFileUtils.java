package com.github.phillbarber.connectionleak;

import com.google.common.io.Resources;

import java.io.File;
import java.net.URISyntaxException;

public class ResourceFileUtils {

    public static File getFileFromClassPath(String file)  {
        try {
            return new File(Resources.getResource(file).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
