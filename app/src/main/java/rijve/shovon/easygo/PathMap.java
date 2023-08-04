package rijve.shovon.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class PathMap extends AppCompatActivity {
    private String sourceNode,destinationNode;
    private MyCanvas myCanvas;
    boolean isCompleted1=false,isCompleted2=false;
    private Button btnZoomIn;
    private Button btnZoomOut,nextBtn,previousBtn;
    private RequestQueue requestQueue;
    private HashMap<String, String> coordinateValue = new HashMap<>();
    private String Path="";
    private ArrayList<String> nodeList = new ArrayList<>();
    private ArrayList<String> edgeList = new ArrayList<>();
    private int floor_index=0;
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_map);
        Intent intent = getIntent();
        sourceNode = intent.getStringExtra("sourceNode");
        destinationNode = intent.getStringExtra("destinationNode");


        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        myCanvas = findViewById(R.id.myCanvas);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);

        sp = getSharedPreferences("nodeInfo",MODE_PRIVATE);
        spEditor = sp.edit();

        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvas.zoomIn();
            }
        });

        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCanvas.zoomOut();
            }
        });



        nextBtn.setOnClickListener(v -> {
            String nodeInfo="",edgeInfo="";
            if(floor_index+1<nodeList.size() || floor_index+1<edgeList.size()){
                if(floor_index+1<nodeList.size()){
                    nodeInfo = nodeList.get(floor_index+1);
                }
                if(floor_index+1<edgeList.size()){
                    edgeInfo = edgeList.get(floor_index+1);
                }
                floor_index++;
                if(!nodeInfo.isEmpty() || !edgeInfo.isEmpty()){
                    myCanvas.clearCanvas();
                    myCanvas.setNodeData(nodeInfo,edgeInfo);
                    nodeInfo="";
                    edgeInfo="";
                }
            }

        });
        previousBtn.setOnClickListener(v -> {
            String nodeInfo="",edgeInfo="";
            if(floor_index>0){
                if(nodeList.size()>0) nodeInfo = nodeList.get(floor_index-1);
                if(edgeList.size()>0){
                    edgeInfo = edgeList.get(floor_index-1);
                }
                floor_index--;
                if(!nodeInfo.isEmpty() || !edgeInfo.isEmpty()){
                    myCanvas.clearCanvas();
                    myCanvas.setNodeData(nodeInfo,edgeInfo);
                    nodeInfo="";
                    edgeInfo="";
                }
            }
        });

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        //System.out.println(sourceNode+"   "+destinationNode);
        //Path="";
        //Path = "campus main->reception->common 001A->central lobby->common 003A->room 102->common 004A->common 005A->lift 002A->stairs 003A->common 009A->room 113->room 114->room 115->stairs 005A->common 010A->toilet 003A->lecture gallery ->wifi zone->common 011A->lift 004A->lift 005F->CSE department->common 014F->room 647->room 648";

        //new PathMap.FetchShortestPathDataTask().execute();
        new PathMap.FetchNodeDataTaskCoordinates().execute();
        //new PathMap.FetchShortestPathDataTaskTest().execute();
        //System.out.println(Path);
        //designMap();



    }


    private void designMap(){
        System.out.println("path: "+Path);
        if(Path.isEmpty()){
            //No Path..
        }
        else{
            String[] nodesNames = Path.split("->");

            String nodeInfo="",edgeInfo="";
            String prev_tempCoordinate="";
            float prev_floor=-1;
            int track=0;
            for(String name: nodesNames){

                String tempCoordinate = sp.getString(name,"");
                String[] tempCoordinates = tempCoordinate.split("_");
                //System.out.println(name);
                //if(tempCoordinate.isEmpty()) System.out.println(name+" FOund");
                float tempX = Float.parseFloat(tempCoordinates[0])*100*-1;
                float tempY = Float.parseFloat(tempCoordinates[1])*100;
                float tempZ = Float.parseFloat(tempCoordinates[2]);
                if(prev_floor!=-1 && tempZ!=prev_floor){
                    //System.out.println(nodeInfo);
                    //System.out.println(edgeInfo);
                    nodeList.add(nodeInfo);
                    edgeList.add(edgeInfo);
                    track=0;
                    nodeInfo="";
                    edgeInfo="";
                }
                tempCoordinate = tempX+"_"+tempY+"_"+tempZ;
                if(!tempCoordinate.isEmpty()){
                    if(nodeInfo.isEmpty()) nodeInfo+=name+"_"+tempCoordinate;
                    else nodeInfo+="___"+name+"_"+tempCoordinate;
                }
                if(track>0){
                    if(edgeInfo.isEmpty()) edgeInfo+= name+"_"+tempCoordinate+"_"+prev_tempCoordinate;
                    else edgeInfo+= "___"+name+"_"+tempCoordinate+"_"+prev_tempCoordinate;
                }
                track++;
                prev_tempCoordinate = name+"_"+tempCoordinate;
                prev_floor=tempZ;

            }
            if(!nodeInfo.isEmpty()) nodeList.add(nodeInfo);
            if(!edgeInfo.isEmpty()) edgeList.add(edgeInfo);
            for(String nodes:nodeList){
                //System.out.println(nodes);
            }
            for(String nodes:edgeList){
                //System.out.println(nodes);
            }
            if(nodeList.size()>0 || edgeList.size()>0){
                nodeInfo = nodeList.get(0);
                if(edgeList.size()>0) edgeInfo=edgeList.get(0);
                else edgeInfo="";
                floor_index=0;
                myCanvas.setNodeData(nodeInfo,edgeInfo);
            }
            myCanvas.setNodeData(nodeInfo,edgeInfo);
        }

    }








    private class FetchShortestPathDataTask extends AsyncTask<Void, Void, String> {
        private static final String API_URL = "https://lgorithmbd.com/php_rest_app/api/shortpath/read.php";

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
                        String nodeName1 = nodeObject.getString("from_node");
                        String nodeName2 = nodeObject.getString("to_node");
                        String tempPath   = nodeObject.getString("path");
                        String distance = nodeObject.getString("distance");
                        //System.out.println(nodeName1+"->"+nodeName2);
                        if(nodeName1.equals(sourceNode) && nodeName2.equals(destinationNode) && !distance.equals("-1")){
                            Path = tempPath;
                            //System.out.println(Path);
                            break;
                        }
                        //if(nodeName1.equals("campus")) continue;
//                        if(nodeName1.equals("lift 004A") || nodeName2.equals("lift 004A") || nodeName1.equals("lift 005F") || nodeName2.equals("lift 005F")){
//                            System.out.println(nodeName1+" -> "+nodeName2+" = "+nodeDistance);
//                        }

                    }

                    if(!Path.isEmpty()) designMap();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PathMap.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchNodeDataTaskCoordinates extends AsyncTask<Void, Void, String> {
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
                        String tempNodeName = nodeObject.getString("node_number");
                        String tempXAxis  = nodeObject.getString("node_x");
                        String tempYAxis = nodeObject.getString("node_y");
                        String tempZAxis  = nodeObject.getString("node_z");

                        //if(tempZAxis.equals("1")) System.out.println(tempNodeName);
                        //System.out.println(isNodeExist);

                        String temp = sp.getString(tempNodeName,"");
                        if(temp.isEmpty()){
                            //System.out.println("Previous Existed Node Not Found");
                            spEditor.putString(tempNodeName,tempXAxis+"_"+tempYAxis+"_"+tempZAxis);
                            spEditor.apply();
                        }
                        //System.out.println(tempNodeName);
                    }
                    new PathMap.FetchShortestPathDataTaskTest().execute();




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PathMap.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class FetchShortestPathDataTaskTest extends AsyncTask<Void, Void, String> {
        private static final String API_URL = "https://lgorithmbd.com/php_rest_app/api/shortpath/read_single.php";

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = null;

            try {
                // Create the URL object with the 'from_node' and 'to_node' parameters
                //String tempSourceNode = sourceNode; // Replace with the actual source node
                //String tempDestinationNode = destinationNode; // Replace with the actual destination node
                URL url = new URL(API_URL + "?from_node=" + URLEncoder.encode(sourceNode, "UTF-8")
                        + "&to_node=" + URLEncoder.encode(destinationNode, "UTF-8"));

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

                    // Check if 'from_node' and 'to_node' are provided in the response
                    if (response.has("from_node") && response.has("to_node")) {
                        String nodeName1 = response.getString("from_node");
                        String nodeName2 = response.getString("to_node");
                        String tempPath = response.getString("path");
                        String distance = response.getString("distance");

                        // Check if the data is valid and not 'INF'
                        System.out.println("FOUND: "+tempPath);
                        Toast.makeText(PathMap.this, tempPath, Toast.LENGTH_SHORT).show();
                        if(!tempPath.isEmpty()) {
                            Path = tempPath;
                            designMap();
                        }
                    } else {
                        // 'from_node' or 'to_node' is missing in the response
                        Toast.makeText(PathMap.this, "Invalid API response.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PathMap.this, "Error parsing API response.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PathMap.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }


}