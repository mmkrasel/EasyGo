package com.easyGo.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SearchLocation extends AppCompatActivity {
//    private EditText fromNodes,toNodes;
private AutoCompleteTextView fromNodes,toNodes;
    private TextView btnSearchPath;
    private String nodeDataString = "";
    private String data[] = {};
    List<String> list = new ArrayList<String>(
            Arrays.asList(data));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        fromNodes = findViewById(R.id.sourceNodeName1);
        toNodes = findViewById(R.id.sourceNodeName2);
        btnSearchPath = findViewById(R.id.saveBtn);

        new FetchNodeNameDataTask().execute();
        btnSearchPath.setOnClickListener(v -> {
            String sourceNode = fromNodes.getText().toString();
            String  destinationNode = toNodes.getText().toString();
            Intent intent = new Intent(this, PathMap.class);
            intent.putExtra("sourceNode", sourceNode);
            intent.putExtra("destinationNode",destinationNode);
            startActivity(intent);
        });

    }

    class FetchNodeNameDataTask extends AsyncTask<Void, Void, String> {
        private static final String API_URL = "https://lgorithmbd.com/php_rest_app/api/nodeinfo/read.php";

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                // Create the URL object
                URL url = new URL(API_URL);

                // Create the HTTP connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Connect to the API
                urlConnection.connect();

                // Read the response
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    result = builder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Close the connections and readers
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Parse the JSON response
                    JSONObject response = new JSONObject(result);
                    JSONArray data = response.getJSONArray("data");

                    // Iterate over the JSON array and add nodes to the nodeList
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject nodeObject = data.getJSONObject(i);
                        String nodeName = nodeObject.getString("node_number");
                        if(nodeDataString.isEmpty()){
                            nodeDataString += nodeName;
                        }
                        else{
                            nodeDataString += "_"+nodeName;
                        }
                    }
                    Toast.makeText(SearchLocation.this, "Data fetch data", Toast.LENGTH_SHORT).show();

                    setNodeNameData(nodeDataString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(SearchLocation.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setNodeNameData(String nodeDataString) {
        String[] nodeDataArray = nodeDataString.split("_");
        for (String nodeData : nodeDataArray) {
            list.add(nodeData);
        }
        data = list.toArray(data);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SearchLocation.this, android.R.layout.simple_list_item_1, data);
        fromNodes.setAdapter(arrayAdapter);
        toNodes.setAdapter(arrayAdapter);
    }
}