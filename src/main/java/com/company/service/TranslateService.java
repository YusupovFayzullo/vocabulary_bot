package com.company.service;

import com.company.database.Database;
import com.company.domain.Translate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class TranslateService {
    public static Translate getTranslate(String text){
        try {
            URL url = new URL(Database.API_PATH_EN+text);

            URLConnection urlConnection = url.openConnection();

            try(BufferedReader reader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()))) {

                Gson gson = new Gson();

                Type type = new TypeToken<List<Translate>>() {
                }.getType();

                List<Translate> translate = gson.fromJson(reader, type);
                return translate.get(0);

            } catch (IOException e) {
                return null;
            }

        } catch (IOException e) {
            return null;
        }
    }
}
