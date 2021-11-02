package com.github.yzl.cita.test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ContractFileUtil {

    private ContractFileUtil() {}

    public static String getFileContant(String fileName) {
        String path = System.getProperty("user.dir")  + "/src/test/resources/" + fileName;
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
