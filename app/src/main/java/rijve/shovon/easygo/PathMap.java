package rijve.shovon.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.concurrent.TimeUnit;

public class PathMap extends AppCompatActivity {
    private Button markButton,btnStart,btnStop;
    public double setFloor =1.0;
    private float xAxis=0,yAxis=0,zAxis=0;
    private HashMap<String,String> selectedNodesHashMap = new HashMap<>();
    private String nodedatastring = "",edgeDataString="";
    private boolean isDataCollectionCompleted=false;
    private AccelerometerInfo accelerometerInfo;
    private MagnetometerInfo magnetometerInfo;
    private GyroscopeInfo gyroscopeInfo;
    private long previousWalkedTime = System.currentTimeMillis();
    private static long minimum_step_per_second = 400;
    private Intent ServiceIntent;
    private float[] gyroscopeValues = new float[3];
    private float[] magnetometerValues= new float[3];
    private float[] accelerometerValues = new float[3];
    private SensorService sensorService;
    private boolean isReceiverRegistered = false;
    private MyBroadcastReceiver accelerometer_receiver , gyroscope_receiver,magnetometer_receiver;
    private String sourceNode,destinationNode;
    private MyCanvas myCanvas;
    private CanvasView loc;
    boolean isCompleted1=false,isCompleted2=false;
    private Button btnZoomIn;
    private Button btnZoomOut,nextBtn,previousBtn,btnIncrease;
    private RequestQueue requestQueue;
    private HashMap<String, String> coordinateValue = new HashMap<>();
    private String Path="";
    private ArrayList<String> nodeList = new ArrayList<>();
    private ArrayList<String> edgeList = new ArrayList<>();
    private int floor_index=0;
    private float a=0,b=0;
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
        //loc = findViewById(R.id.locCanvas);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        btnIncrease = findViewById(R.id.increase);


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
                    myCanvas.setPathNodeData(nodeInfo,edgeInfo);
                    String[] floorQuery = nodeInfo.split("___");
                    String[] currentFloor = floorQuery[0].split("_");
                    //Toast.makeText(PathMap.this, "Current Floor: "+currentFloor[3], Toast.LENGTH_SHORT).show();
                    setFloor = Float.parseFloat(currentFloor[3]);
                    nodedatastring="";
                    edgeDataString="";
                    new FetchNodeDataTask().execute();
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
                    myCanvas.setPathNodeData(nodeInfo,edgeInfo);
                    String[] floorQuery = nodeInfo.split("___");
                    String[] currentFloor = floorQuery[0].split("_");
                    //Toast.makeText(PathMap.this, "Current Floor: "+currentFloor[3], Toast.LENGTH_SHORT).show();
                    setFloor = Float.parseFloat(currentFloor[3]);
                    nodedatastring="";
                    edgeDataString="";
                    new FetchNodeDataTask().execute();
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
        new FetchNodeDataTask().execute();
        new PathMap.FetchNodeDataTaskCoordinates().execute();


        //new PathMap.FetchShortestPathDataTaskTest().execute();
        //System.out.println(Path);
        //designMap();


//        btnIncrease.setOnClickListener(v -> {
//            myCanvas.setCoordinates(a*100*(-1), b*100);
//            a++;
//            b++;
//        });


    }

    private void startTracking(){

        sensorService = new SensorService();
        accelerometerInfo = new AccelerometerInfo(3);
        magnetometerInfo = new MagnetometerInfo(20);
        gyroscopeInfo = new GyroscopeInfo(3);
        registerSensorReceiver();
        ServiceIntent = new Intent(this, SensorService.class);
        startService(ServiceIntent);
    }


    private void designMap(){
       // System.out.println("path: "+Path);
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
                //System.out.println(tempCoordinate);
                String[] tempCoordinates = tempCoordinate.split("_");
                //System.out.println(name);
                //if(tempCoordinate.isEmpty()) System.out.println(name+" FOund");

                float tempX = Float.parseFloat(tempCoordinates[0])*50*-1;
                float tempY = Float.parseFloat(tempCoordinates[1])*50;
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
                myCanvas.setPathNodeData(nodeInfo,edgeInfo);
            }
            myCanvas.setPathNodeData(nodeInfo,edgeInfo);
        }
        String tempCoordinate = sp.getString(sourceNode,"");
        //System.out.println(tempCoordinate);
        String[] tempCoordinates = tempCoordinate.split("_");
        xAxis = Float.parseFloat(tempCoordinates[0]);
        yAxis=Float.parseFloat(tempCoordinates[1]);
        zAxis = Float.parseFloat(tempCoordinates[2]);
        myCanvas.setCoordinates(xAxis*50*(-1),yAxis*50);
        startTracking();

    }

    protected void onPause() {
        super.onPause();
        if(isReceiverRegistered){
            unregisterReceiver(accelerometer_receiver);
            unregisterReceiver(magnetometer_receiver);
            unregisterReceiver(gyroscope_receiver);
            stopService(ServiceIntent);
        }
        isReceiverRegistered=false;
    }

    public boolean hasWalked(){
        float accelerometerMagnitude = accelerometerInfo.getMagnitude();
        float  gyroscopeMagnitude = gyroscopeInfo.getMagnitude();
        long currentTime = System.currentTimeMillis();
        //System.out.println(accelerometerMagnitude+"    "+gyroscopeMagnitude);
        if(accelerometerMagnitude>=.8f && gyroscopeMagnitude<1f && (currentTime-previousWalkedTime)>minimum_step_per_second){
            previousWalkedTime = currentTime;
            return true;
        }
        return false;
    }

    private void calcCoordinate() {
        float x=0,y=0,z=zAxis;
        float theta=0;
        float walkingDistance = .50f;
        float direction = magnetometerInfo.getCurrentDegree();
        if(direction>0 && direction<90){
            theta = 90-direction;
            x = ((float)Math.cos(Math.toRadians((double)theta))*walkingDistance)+xAxis;
            y = ((float)Math.sin(Math.toRadians((double)theta))*walkingDistance)+yAxis;
        }
        else if(direction>90 && direction<180){
            theta = 180-direction;
            x = ((float)Math.sin(Math.toRadians((double)theta))*walkingDistance)+xAxis;
            y = yAxis- ((float)Math.cos(Math.toRadians((double)theta))*walkingDistance);
        }
        else if(direction>180 && direction<270){
            theta = 270-direction;
            x = xAxis-((float)Math.cos(Math.toRadians((double)theta))*walkingDistance);
            y = yAxis- ((float)Math.sin(Math.toRadians((double)theta))*walkingDistance);
        }
        else if(direction>270 && direction<360){
            theta  = 360-direction;
            x = xAxis- ((float)Math.sin(Math.toRadians((double)theta))*walkingDistance);
            y = yAxis+ ((float)Math.cos(Math.toRadians((double)theta))*walkingDistance);
        }
        else if(direction==0){
            x = xAxis;
            y = yAxis+walkingDistance;
        }
        else if(direction==90){
            x = xAxis+walkingDistance;
            y = yAxis;
        }
        else if(direction==180){
            x = xAxis;
            y = yAxis-walkingDistance;
        }
        else if(direction==270){
            x = xAxis-walkingDistance;
            y = yAxis;
        }
        xAxis = x;
        yAxis =y;
        zAxis = z;
        //System.out.println(x+"   "+y);
        //System.out.println(direction);
        myCanvas.setCoordinates(xAxis*50*(-1), yAxis*50);
    }
    private void processAccelerometerData(float[] val){
        //send for calculation..

        accelerometerValues = val.clone();
        accelerometerInfo.setAccelerometerValues(accelerometerValues);
        //System.out.println("ACC: "+ accelerometerInfo.getMagnitude());
        if(hasWalked() && !magnetometerInfo.isDirectionChanging()){
            //calculate and update the coordinate..
            //System.out.println("OK");
            calcCoordinate();
        }
        //else System.out.println("Not OK");
        //calculate.hasWalked(accelerometerInfo.getMagnitude(),gyroscopeInfo.getMagnitude());
        //distanceShow.setText(String.valueOf(calculate.getWalkingDistance()));


    }
    private void processMagnetometerData(float[] val){
        magnetometerValues = val.clone();
        magnetometerInfo.setMagnetometerLiveTracking(magnetometerValues,accelerometerValues);
        //System.out.println("MAG: "+ magnetometerInfo.getDirection());
//        if(!calculate.isDirectionOk(magnetometerInfo.isDirectionOk(),gyroscopeInfo.getMagnitude())){
//            //@Give warning of change of direction and restart again....
//            Toast.makeText(DataCollection.this, "DO NOT CHANGE DIRECTION... Start Again!!!", Toast.LENGTH_SHORT).show();
//        }
    }
    private void processGyroscopeData(float[] val){
        gyroscopeValues = val.clone();
        gyroscopeInfo.setGyroscope_value(gyroscopeValues);
        //System.out.println("GYR: "+ gyroscopeInfo.getMagnitude());
    }
    private void registerSensorReceiver(){
        isReceiverRegistered=true;
        accelerometer_receiver = new MyBroadcastReceiver();
        IntentFilter accelerometer_receiver_filter = new IntentFilter(SensorService.ACTION_ACCELEROMETER_DATA);
        registerReceiver(accelerometer_receiver, accelerometer_receiver_filter);
        magnetometer_receiver = new MyBroadcastReceiver();
        IntentFilter magnetometer_receiver_filter = new IntentFilter(SensorService.ACTION_MAGNETOMETER_DATA);
        registerReceiver(accelerometer_receiver, magnetometer_receiver_filter);
        gyroscope_receiver = new MyBroadcastReceiver();
        IntentFilter gyroscope_receiver_filter = new IntentFilter(SensorService.ACTION_GYROSCOPE_DATA);
        registerReceiver(gyroscope_receiver,gyroscope_receiver_filter);
    }
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SensorService.ACTION_ACCELEROMETER_DATA)) {
                float[] floatArray = intent.getFloatArrayExtra(SensorService.EXTRA_ACCELEROMETER_DATA);
                processAccelerometerData(floatArray);
                //System.out.println(floatArray[0]+" acc ");
            }

            else if (intent.getAction().equals(SensorService.ACTION_MAGNETOMETER_DATA)) {
                float[] floatArray = intent.getFloatArrayExtra(SensorService.EXTRA_MAGNETOMETER_DATA);
                processMagnetometerData(floatArray);
                //System.out.println(floatArray[0]+" MAG ");
            }

            else if (intent.getAction().equals(SensorService.ACTION_GYROSCOPE_DATA)) {
                float[] floatArray = intent.getFloatArrayExtra(SensorService.EXTRA_GYROSCOPE_DATA);
                processGyroscopeData(floatArray);
                //System.out.println(floatArray[0]+" GYR ");
            }

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

    private class FetchNodeEdgeDataTask extends AsyncTask<Void, Void, String> {
        private static final String API_URL = "https://lgorithmbd.com/php_rest_app/api/edgeinfo/read.php";

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
                        String nodeName1 = nodeObject.getString("node1");
                        String nodeName2 = nodeObject.getString("node2");
                        String nodeDistance   = nodeObject.getString("distance");
                        //if(nodeName1.equals("campus")) continue;
                        if(selectedNodesHashMap.containsKey(nodeName1) && selectedNodesHashMap.containsKey(nodeName2)){
                            if(edgeDataString.isEmpty()){
                                edgeDataString += selectedNodesHashMap.get(nodeName1)+"_"+selectedNodesHashMap.get(nodeName2);
                                //edgeDataString += nodeName1+"_"+nodeName2 + "_" + nodeDistance;
                            }
                            else{
                                edgeDataString += "___"+selectedNodesHashMap.get(nodeName1)+"_"+selectedNodesHashMap.get(nodeName2);
                                //edgeDataString += "___"+nodeName1+"_"+nodeName2 + "_" + nodeDistance;
                            }
                        }

                    }
                    //Toast.makeText(PathMap.this, "Data fetch data" + nodedatastring, Toast.LENGTH_SHORT).show();
                    //System.out.println(edgeDataString);
                    // Create circles on canvas using the node data
                    //need to remove it later
                    isDataCollectionCompleted=true;

                    //System.out.println("Edge Data Fetch Successfully");
                    //String fakeData="campus main_0_0_1___reception_0_10_1___A_0_15_1___central lobby_0_20_1___B_0_30_1___Room102_0_50_1___Moshjid_0_60_1___C_0_65_1___Lift1_-5_65_1___Room103_-10_67_1___Room105_-15_67_1___toilet1_-20_67_1___Room107_-30_67_1___Room110_-40_67_1___Room104_-10_63_1___Room106_-15_63_1___Room108_-20_63_1___Room109_-30_63_1___Room111_-40_63_1___Stairs1_-10_15_1___Toilet2_-15_15_1___Auditorium_-40_15_1___FUB Entry_-50_15_1";
                    //String fakeDataEdge = "campus main_0_0_1_reception_0_10_1_10@reception_0_10_1_A_0_15_1_5@A_0_15_1_central lobby_0_20_1_5@A_0_15_1_Stairs1_-10_15_1_10@central lobby_0_20_1_B_0_30_1_10@B_0_30_1_Room102_0_50_1_20@Room102_0_50_1_Moshjid_0_60_1_10@Moshjid_0_60_1_C_0_65_1_5@C_0_65_1_Lift1_-5_65_1_5@Lift1_-5_65_1_Room103_-10_67_1_7@Lift1_-5_65_1_Room104_-10_63_1_7@Room103_-10_67_1_Room105_-15_67_1_5@Room105_-15_67_1_toilet1_-20_67_1_5@toilet1_-20_67_1_Room107_-30_67_1_10@Room107_-30_67_1_Room110_-40_67_1_10@Room104_-10_63_1_Room106_-15_63_1_5@Room106_-15_63_1_Room108_-20_63_1_5@Room108_-20_63_1_Room109_-30_63_1_10@Room109_-30_63_1_Room111_-40_63_1_10@Stairs1_-10_15_1_Toilet2_-15_15_1_5@Toilet2_-15_15_1_Auditorium_-40_15_1_25@Auditorium_-40_15_1_FUB Entry_-50_15_1_10";
                    myCanvas.setNodeData(nodedatastring,edgeDataString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PathMap.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class FetchNodeDataTask extends AsyncTask<Void, Void, String> {
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
                        String id = nodeObject.getString("id");
                        String nodeName = nodeObject.getString("node_number");
                        double nodeX = nodeObject.getDouble("node_x")*50*-1;
                        double nodeY = nodeObject.getDouble("node_y")*50;
                        double nodeZ = nodeObject.getDouble("node_z");

                        if(nodeZ==setFloor){
                            //System.out.println(nodeName);
                            if(nodedatastring.isEmpty()){
                                nodedatastring += nodeName+"_"+nodeX + "_" + nodeY + "_" + nodeZ;
                            }
                            else{
                                nodedatastring += "___"+nodeName+"_"+nodeX + "_" + nodeY + "_" + nodeZ;
                            }
                            selectedNodesHashMap.put(nodeName,nodeName+"_"+nodeX + "_" + nodeY + "_" + nodeZ);
                        }

                    }
                    //Toast.makeText(Map_node.this, "Data fetch data" + nodedatastring, Toast.LENGTH_SHORT).show();
                    System.out.println(nodedatastring);
                    new PathMap.FetchNodeEdgeDataTask().execute();
                    // Create circles on canvas using the node data
                    //need to remove it later

//                    String fakeData="campus main_0_0_1___reception_0_10_1___A_0_15_1___central lobby_0_20_1___B_0_30_1___Room102_0_50_1___Moshjid_0_60_1___C_0_65_1___Lift1_-5_65_1___Room103_-10_67_1___Room105_-15_67_1___toilet1_-20_67_1___Room107_-30_67_1___Room110_-40_67_1___Room104_-10_63_1___Room106_-15_63_1___Room108_-20_63_1___Room109_-30_63_1___Room111_-40_63_1___Stairs1_-10_15_1___Toilet2_-15_15_1___Auditorium_-40_15_1___FUB Entry_-50_15_1";
//                    String fakeDataEdge = "campus main_0_0_1_reception_0_10_1_10@reception_0_10_1_A_0_15_1_5@A_0_15_1_central lobby_0_20_1_5@A_0_15_1_Stairs1_-10_15_1_10@central lobby_0_20_1_B_0_30_1_10@B_0_30_1_Room102_0_50_1_20@Room102_0_50_1_Moshjid_0_60_1_10@Moshjid_0_60_1_C_0_65_1_5@C_0_65_1_Lift1_-5_65_1_5@Lift1_-5_65_1_Room103_-10_67_1_7@Lift1_-5_65_1_Room104_-10_63_1_7@Room103_-10_67_1_Room105_-15_67_1_5@Room105_-15_67_1_toilet1_-20_67_1_5@toilet1_-20_67_1_Room107_-30_67_1_10@Room107_-30_67_1_Room110_-40_67_1_10@Room104_-10_63_1_Room106_-15_63_1_5@Room106_-15_63_1_Room108_-20_63_1_5@Room108_-20_63_1_Room109_-30_63_1_10@Room109_-30_63_1_Room111_-40_63_1_10@Stairs1_-10_15_1_Toilet2_-15_15_1_5@Toilet2_-15_15_1_Auditorium_-40_15_1_25@Auditorium_-40_15_1_FUB Entry_-50_15_1_10";
//                    myCanvas.setNodeData(fakeData,fakeDataEdge);

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
                        //System.out.println("FOUND: "+tempPath);
                        //Toast.makeText(PathMap.this, tempPath, Toast.LENGTH_SHORT).show();
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