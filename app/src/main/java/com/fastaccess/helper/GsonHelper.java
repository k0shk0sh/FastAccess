package com.fastaccess.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kosh on 29 May 2016, 5:09 AM
 */

public class GsonHelper {

    public static <T> T getObject(String json, Class<T> clazz) {
        return gson().fromJson(json, clazz);
    }

    public static <T> List<T> getList(String json, Class<T[]> clazz) {
        return Arrays.asList(gson().fromJson(json, clazz));
    }

    public static Gson gson() {
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }
}
