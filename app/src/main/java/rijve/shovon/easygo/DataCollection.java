package rijve.shovon.easygo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Random;

// Need to import the data and Create object of that data.........
public class DataCollection extends AppCompatActivity{
    private Button btnSet,btnStart,btnShowMap;
    private Dialog dialog_nodeInfo;
    private String data="";
    private HighPassFilter highPassFilter;
    private MovingAverageFilter movingAverageFilter;
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;
    private MyBroadcastReceiver accelerometer_receiver , gyroscope_receiver,magnetometer_receiver;

    private SensorManager sensorManager;
    private Calculate calculate;
    private Intent ServiceIntent;
    private AccelerometerInfo accelerometerInfo;
    private MagnetometerInfo magnetometerInfo;
    private GyroscopeInfo gyroscopeInfo;
    private Sensor accelerometerSensor,gyroscopeSensor,magnetometerSensor;
    private SensorService sensorService;

    private float[] gravityValues = new float[3]; // needs for filtering..
    private float[] gyroscopeValues = new float[3];
    private float[] magnetometerValues= new float[3];
    private float[] accelerometerValues = new float[3];

    //@Extra
    private String previous_node_name="First Node",current_nodeName="";
    private float previous_node_x=0,previous_node_y = 0, previous_node_z=1;
    private String pre_nodeId;
    private double pre_X;
    private double pre_Y;
    private double pre_Z;
    //Extra
    private ListView mListView;
    private NodeListAdapter adapter;
    private ArrayList<NodeData> nodeList;
    private boolean isReceiverRegistered = false;
    //extra

    // dialog
    private TextView sourceNodeName, sourceNodeInfo;
    private EditText adjacentNodeName, adjacentNodeInfo;

    private TextView previous_nodeTextview,distanceShow,floorTextview;
    private Button saveBtn,btncreateNewNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);

        //btnStart will have 2 texts..(start & stop)
        //btncreateNewNode = findViewById(R.id.CreateNode);
        btnStart = findViewById(R.id.startBtn);
        btnSet = findViewById(R.id.setBtn);
        btnShowMap = findViewById(R.id.showMap);
        distanceShow = findViewById(R.id.distance_show);
        floorTextview = findViewById(R.id.floor);
        previous_nodeTextview  = findViewById(R.id.previous_nodeName);
        previous_nodeTextview.setText(previous_node_name);
        
        sensorService = new SensorService();


        sp = getSharedPreferences("nodeInfo",MODE_PRIVATE);
        spEditor = sp.edit();

        //Fetch all the data...
        new FetchNodeDetailsTask().execute();

        btnShowMap.setOnClickListener(v -> {
            Intent I = new Intent(DataCollection.this, Map_node.class);
            startActivity(I);
        });

        btnStart.setOnClickListener(v -> {

            // @ First need to check if Current Node is set or not.. If Current Node is set continue else Do nothing//
            // @ Get the source Node..
            // @ If (text==start) start the sensor otherwise proceed to save in the database and also toggle the text everytime it's pressed..
            String btnStartTxt = btnStart.getText().toString();

            if(previous_node_name.isEmpty() || floorTextview.getText().toString().isEmpty()){
                Toast.makeText(DataCollection.this, "Need to set a Node first", Toast.LENGTH_SHORT).show();
            }
            else if(btnStartTxt.equals("START")){
                // proceed to start sensor
                accelerometerInfo = new AccelerometerInfo(3);
                magnetometerInfo = new MagnetometerInfo(5);
                gyroscopeInfo = new GyroscopeInfo(3);
                calculate = new Calculate(0.8f,1f);


                registerSensorReceiver();
                ServiceIntent = new Intent(this, SensorService.class);
                startService(ServiceIntent);

                //@ change button colour to red..
                btnStart.setText("STOP");
                btnStart.setTextColor(Color.RED);

            }
            else{
                //stopService;
                previous_node_z  = Float.parseFloat(floorTextview.getText().toString());
                stopService(ServiceIntent);
                float details[] = calculate.calculateData(magnetometerInfo.getDirection(),previous_node_x,previous_node_y,previous_node_z,previous_node_name);
                Toast.makeText(DataCollection.this, "X: " + details[0]+" Y: "+details[1]+" Z "+details[2]+" Dir: "+magnetometerInfo.getDirection(), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertDialog_nodeInfo = new
                        AlertDialog.Builder(this);

                View view = getLayoutInflater().inflate(R.layout.node_info_dialog, null);
                sourceNodeName = view.findViewById(R.id.sourceNodeName);
                sourceNodeInfo = view.findViewById(R.id.sourceNodeInfo);
                adjacentNodeName = view.findViewById(R.id.adjacentNodeName);
                adjacentNodeInfo = view.findViewById(R.id.adjacentNodeInfo);
                saveBtn = view.findViewById(R.id.saveBtn);

                alertDialog_nodeInfo.setView(view);
                AlertDialog dialog_nodeInfo = alertDialog_nodeInfo.create();

                dialog_nodeInfo.show();
                sourceNodeName.setText(previous_node_name);
                // sourceNodeInfo.setText();

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        current_nodeName =  adjacentNodeName.getText().toString();


                        //String nodeInfo = current_nodeName+"---"+details[0]+"---"+details[1]+"---"+details[2];
                        //String edgeInfo = previous_node_name+"___"+current_nodeName+"___"+calculate.getWalkingDistance();


                        String isNodeExist = sp.getString(current_nodeName,"");
                        if(isNodeExist.isEmpty()){
                            System.out.println("Previous Existed Node");
                            spEditor.putString(current_nodeName,"Found");
                            //createNode(current_nodeName, details[0], details[1], details[2]);
                        }
                        else{
                            System.out.println("Previous Existed Node Not Found");
                        }

                        //createEdge(current_nodeName, previous_node_name, calculate.getWalkingDistance());



                        // updating the previous Node value..
                        previous_node_x = details[0];
                        previous_node_y = details[1];
                        previous_node_z = details[2];
                        previous_node_name = current_nodeName;
                        previous_nodeTextview.setText(previous_node_name);
                        floorTextview.setText(Float.toString(previous_node_z));

                        //System.out.println(nodeInfo);
                        //System.out.println(edgeInfo);

                        dialog_nodeInfo.dismiss();
                        distanceShow.setText("0");
                    }
                });






                btnStart.setText("START");
                btnStart.setTextColor(Color.GREEN);
                //@ proceed to save in the database...
            }

        });

        btnSet.setOnClickListener(v -> {
            // x y z , id
            // @ Set the start from button can choose from the already saved node ..
            // @ if there is no saved node create a node first..

            AlertDialog.Builder alertDialog = new
                    AlertDialog.Builder(this);

            View rowList = getLayoutInflater().inflate(R.layout.row, null);

            mListView = rowList.findViewById(R.id.listViewnew);


            nodeList = new ArrayList<>();

            adapter = new NodeListAdapter(this, R.layout.list_view, nodeList);
            mListView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
            alertDialog.setView(rowList);
            AlertDialog dialog = alertDialog.create();
            dialog.show();

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    pre_nodeId=nodeList.get(i).getID();
                    pre_X=nodeList.get(i).getX();
                    pre_Y=nodeList.get(i).getY();
                    pre_Z=nodeList.get(i).getZ();
                    previous_node_x = (float)pre_X;
                    previous_node_y = (float)pre_Y;
                    previous_node_z = (float)pre_Z;
                    floorTextview.setText(Float.toString(previous_node_z));
                    previous_node_name = pre_nodeId;
                    previous_nodeTextview.setText(previous_node_name);
                    Toast.makeText(DataCollection.this, "X: " + previous_node_x+" Z: "+previous_node_z, Toast.LENGTH_SHORT).show();

                    dialog.dismiss();

                }
            });

            if (isNetworkAvailable()) {
                FetchNodeDataTask fetchNodeDataTask = new FetchNodeDataTask();
                fetchNodeDataTask.execute();
            } else {
                Toast.makeText(this, "No internet connection available", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Start the SensorService
    }

    @Override
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
    void stopSensors(){
        // @ Stop  the sensor service from stop button or on pause..
        stopService(ServiceIntent);
    }

    private void createNode(String nodeName , float x , float y , float z){

        JSONObject postData = new JSONObject();
        try {
            postData.put("node_number", nodeName);
            postData.put("node_x", Double.valueOf(x));
            postData.put("node_y", Double.valueOf(y));
            postData.put("node_z", Double.valueOf(z));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SaveDataTask().execute(postData);

    }

    private void createEdge(String nodeName1 ,String nodeName2 , float distance ){
        JSONObject postData = new JSONObject();
        try {
            postData.put("node1", nodeName1);
            postData.put("node2", nodeName2);
            postData.put("distance", Double.valueOf(distance));
            //postData.put("node_z", Double.valueOf(z));

        } catch (JSONException e) {
            e.printStackTrace();        }
        new SaveEdge().execute(postData);

    }

    private void processAccelerometerData(float[] val){
        //send for calculation..
        if(magnetometerInfo.isDirectionOk()){
            accelerometerValues = val.clone();
            accelerometerInfo.setAccelerometerValues(accelerometerValues);
            calculate.hasWalked(accelerometerInfo.getMagnitude(),gyroscopeInfo.getMagnitude());
            distanceShow.setText(String.valueOf(calculate.getWalkingDistance()));
        }

    }
    private void processMagnetometerData(float[] val){
        magnetometerValues = val.clone();
        magnetometerInfo.setMagnetometerReading(magnetometerValues,accelerometerValues);
        if(!calculate.isDirectionOk(magnetometerInfo.isDirectionOk(),gyroscopeInfo.getMagnitude())){
            //@Give warning of change of direction and restart again....
            Toast.makeText(DataCollection.this, "DO NOT CHANGE DIRECTION... Start Again!!!", Toast.LENGTH_SHORT).show();
        }
    }
    private void processGyroscopeData(float[] val){
        gyroscopeValues = val.clone();
        gyroscopeInfo.setGyroscope_value(gyroscopeValues);
    }


    // Receiving Sensor Data..
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


    boolean checkSensor(){
        //@ check all 3sensors
        // sensor count==3 ->true
        // sensor count==2(acc and mag) ->give warning about not accurate data & true
        //  @ else false
        return true;
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
                        String nodeNumber = nodeObject.getString("node_number");
                        double nodeX = nodeObject.getDouble("node_x");
                        double nodeY = nodeObject.getDouble("node_y");
                        double nodeZ = nodeObject.getDouble("node_z");
                        //data += nodeNumber+"-"+nodeX+"-"+nodeY+"-"+nodeZ+"---";
                        String nodeExist = sp.getString(nodeNumber,"");
                        if(nodeExist.isEmpty()){
                            spEditor.putString(nodeNumber,"Found");
                        }
                        NodeData nodeData = new NodeData(id, nodeNumber, nodeX, nodeY, nodeZ);
                        nodeList.add(nodeData);
                    }

                    // Notify the adapter of the data change
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(DataCollection.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private class FetchNodeDetailsTask extends AsyncTask<Void, Void, String> {
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
                        String nodeNumber = nodeObject.getString("node_number");
                        double nodeX = nodeObject.getDouble("node_x");
                        double nodeY = nodeObject.getDouble("node_y");
                        double nodeZ = nodeObject.getDouble("node_z");
                        String dt = nodeX+"-"+nodeY+"-"+nodeZ;
                        String nodeExist = sp.getString(nodeNumber,"");
                        if(nodeExist.isEmpty()){
                            spEditor.putString(nodeNumber,dt);
                        }
                    }

                    // Notify the adapter of the data change
                    //adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(DataCollection.this, "Failed to fetch node data", Toast.LENGTH_SHORT).show();
            }
        }
    }

}