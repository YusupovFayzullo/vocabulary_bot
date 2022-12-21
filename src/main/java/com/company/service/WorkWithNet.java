package com.company.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class WorkWithNet {
    public static File getAudio(String audioPath) {
        try {
            URL url = new URL(audioPath);

            URLConnection urlConnection = url.openConnection();

            return (File) urlConnection.getContent();

        } catch (IOException e) {
            return null;
        }
    }
}
