package com.example.tinyweather;

import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by MH on 2017-08-26.
 */

public class JsonResourceReader {
    private String jsonString;
    private static final String TAG = JsonResourceReader.class.getSimpleName();

    /**
     * Read from a resources file and create a {@link JsonResourceReader} object that will allow the creation of other
     * objects from this resource.
     *
     * @param resources An application {@link Resources} object.
     * @param id The id for the resource to load, typically held in the raw/ folder.
     */
    public JsonResourceReader(Resources resources, int id) {
        InputStream resourceReader = resources.openRawResource(id);
        Writer writer = new StringWriter();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceReader, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unhandled exception while using JsonResourceReader", e);
        } finally {
            try {
                resourceReader.close();
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while using JsonResourceReader", e);
            }
        }

        jsonString = writer.toString();
    }

    public String getJsonString() {
        return jsonString;
    }

    /**
     * Build an object from the specified JSON resource using Gson.
     * @param type The type of the object to build.
     * @return An object of type T, with member fields populated using Gson.
     */
    public <T> T constructUsingGson(Class<T> type) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(jsonString, type);
    }
}