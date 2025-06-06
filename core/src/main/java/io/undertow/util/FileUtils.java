/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.undertow.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Stuart Douglas
 */
public class FileUtils {

    private FileUtils() {

    }

    public static String readFile(Class<?> testClass, String fileName) {
        final URL res = testClass.getResource(fileName);
        return readFile(res);
    }

    public static String readFile(URL url) {
        try {
            return readFile(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFile(InputStream file, String charset) {
        try {
            Charset charSet = charset != null ? Charset.forName(charset) : StandardCharsets.UTF_8;
            return readFile(file, charSet);
        } catch (UnsupportedCharsetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the {@link InputStream file} and converting it to {@link String} using UTF-8 encoding.
     */
    public static String readFile(InputStream file) {
        return readFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Reads the {@link InputStream file} and converting it to {@link String} using <code>charSet</code> encoding.
     */
    public static String readFile(InputStream file, Charset charSet) {
        try (BufferedInputStream stream = new BufferedInputStream(file)) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int read;
            while ((read = stream.read(buff)) != -1) {
                result.write(buff, 0, read);
            }
            return result.toString(charSet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecursive(final Path directory) throws IOException {
        if(!Files.isDirectory(directory)) {
            return;
        }
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    // ignored
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir);
                } catch (IOException e) {
                    // ignored
                }
                return FileVisitResult.CONTINUE;
            }

        });
    }
}
