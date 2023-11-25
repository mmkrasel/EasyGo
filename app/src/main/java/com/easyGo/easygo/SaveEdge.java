package com.easyGo.easygo;


import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveEdge extends AsyncTask<JSONObject, Void, String> {
    private static final String CREATE_URL = "https://lgorithmbd.com/php_rest_app/api/edgeinfo/create.php";

    @Override
    protected String doInBackground(JSONObject... params) {
        // Send a POST request to create.php
        try {
            // Create the URL object
            URL url = new URL(CREATE_URL);

            // Create the HTTP connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Write the JSON payload to the request body
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(params[0].toString());
            writer.flush();

            // Get the response
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Close the connections and readers
            writer.close();
            reader.close();
            urlConnection.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the response
        if (result != null) {
            // Process the result
            // ...
        } else {
            // Handle the error
            // ...
        }
    }
}
