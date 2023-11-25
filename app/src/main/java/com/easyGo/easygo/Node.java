package com.easyGo.easygo;

public class Node {
    private String nodeInfo;
    private String nodeName;
    private float axisX;
    private float axisY;
    private float axisZ;
    private int nodeId;
    private static int nextNodeId = 1;//must be unique..

    public Node(){
        //this.nodeId = nextNodeId++; Get the id from the
        nodeName = "";
        nodeInfo = "";
    }

    public void setAxis(float x , float y , float z){
            axisX = x;
            axisZ = z;
            axisY = y;
    }
}
