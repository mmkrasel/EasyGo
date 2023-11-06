package rijve.shovon.easygo;

// campus main --> room 630

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.core.view.GestureDetectorCompat;

public class MyCanvas extends View {
    private Bitmap backgroundImage;
    private Canvas canvas;

    private HashMap<String, CircleCoordinates> coordinateObjectHashMap = new HashMap<>();
    private CircleCoordinates tempCircleCoordinateObject;
    private float zoomFactor = 1.0f;
    //    private float translateX = 0.0f;
//    private float translateY = 0.0f;
    private Paint paint,paint1,paint2,paint3,paint4;
    private Paint linePaint,linePaint1,linePaint2,linePaint3,linePaint4;
    float nodeX;
    float nodeY;
    float xCoordinate=0,yCoordinate=0;
    private float previousX;
    private int backgroundColor = Color.CYAN;
    private float previousY;
    private float translateX=0;
    private float translateY=0;
    private Paint textPaint,centerDotPaint,borderDotPaint;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private Path roadPath;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private float rotationDegrees = 0;
    private float mScaleFactor = 1.0f;

    private boolean mapChange=false,locChange=false;


    private ArrayList<CircleCoordinates> circleCoordinatesList = new ArrayList<>();
    private ArrayList<LineCoordinates> lineCoordinatesList = new ArrayList<>();

    private ArrayList<PathLineCordinates> pathLineCordinatesList = new ArrayList<>();

    public MyCanvas(Context context) {
        super(context);
        init();
    }

    public void clearCanvas() {

        circleCoordinatesList.clear();
        lineCoordinatesList.clear();
        backgroundImage = null;
        System.out.println(circleCoordinatesList.size());
        System.out.println(lineCoordinatesList.size());
        invalidate();
    }





    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    private void init() {

        paint4 = new Paint();
        paint4.setColor(Color.GREEN);
        paint4.setStyle(Paint.Style.FILL);
        paint4.setStrokeWidth(40f);

        centerDotPaint = new Paint();
        centerDotPaint.setColor(Color.BLUE);
        centerDotPaint.setStyle(Paint.Style.FILL);

        borderDotPaint = new Paint();
        borderDotPaint.setColor(Color.BLUE);
        borderDotPaint.setStyle(Paint.Style.FILL);
        borderDotPaint.setAlpha(50);

        paint1 = new Paint();
        paint1.setColor(Color.BLUE);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setStrokeWidth(40f);

        paint2 = new Paint();
        paint2.setColor(Color.YELLOW);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(40f);

        paint3 = new Paint();
        paint3.setColor(Color.GRAY);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setStrokeWidth(40f);

        linePaint4 = new Paint();

        linePaint4.setColor(Color.BLUE);
        linePaint4.setStrokeWidth(150f);

        // Set the border line width

        linePaint1 = new Paint();
        linePaint1.setColor(Color.YELLOW);
        linePaint1.setStrokeWidth(50f);

        linePaint2 = new Paint();
        linePaint2.setColor(Color.GREEN);
        linePaint2.setStrokeWidth(50f);

        linePaint3 = new Paint();
        linePaint3.setColor(Color.RED);
        linePaint3.setStrokeWidth(50f);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                translateX -= distanceX / zoomFactor;
                translateY -= distanceY / zoomFactor;
                invalidate();
                return true;
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
                invalidate();
                return true;
            }
        });

    }

    public void setBackgroundImage(Bitmap bitmap) {
        backgroundImage = bitmap;
        canvas = new Canvas(backgroundImage);
    }

    public Canvas getCanvas() {
        return canvas;
    }



    public void setNodeData(String nodeDataString , String nodeEdgeString) {
        ArrayList<CircleCoordinates> circleCoordinatesList = new ArrayList<>();
        ArrayList<LineCoordinates> lineCoordinatesArrayList = new ArrayList<>();
        String[] nodeDataArray = nodeDataString.split("___");
        String[] edgeDataArray = nodeEdgeString.split("___");
        mapChange=true;

        for (String nodeData : nodeDataArray) {
            String[] coordinates = nodeData.split("_");

            String nodeName = coordinates[0];
            float nodeX = Float.parseFloat(coordinates[1]);
            float nodeY = Float.parseFloat(coordinates[2]);
            float nodeZ = Float.parseFloat(coordinates[3]);


            // Add the CircleCoordinates object to the ArrayList
            tempCircleCoordinateObject = new CircleCoordinates(nodeX, nodeY,nodeZ,nodeName);
            this.circleCoordinatesList.add(tempCircleCoordinateObject);

        }


        for(String edgeInfo: edgeDataArray){
            String[] edgeDetails = edgeInfo.split("_");

            String nodeName1 = edgeDetails[0];
            float nodeX1 = Float.parseFloat(edgeDetails[1]);
            float nodeY1 =Float.parseFloat(edgeDetails[2]);
            float nodeZ1 = Float.parseFloat(edgeDetails[3]);
            String nodeName2 =edgeDetails[4];
            float nodeX2 =Float.parseFloat(edgeDetails[5]);
            float nodeY2 = Float.parseFloat(edgeDetails[6]);
            float nodeZ2 = Float.parseFloat(edgeDetails[7]);


            this.lineCoordinatesList.add(new LineCoordinates(nodeX1, nodeY1,nodeZ1,nodeX2, nodeY2,nodeZ2));
            System.out.println(nodeName1+" -> "+nodeName2);

        }

        //this.lineCoordinatesList = lineCoordinatesArrayList;
        //this.circleCoordinatesList = circleCoordinatesList;
        invalidate();
    }

    public void setPathNodeData(String nodeDataString , String nodeEdgeString) {
        ArrayList<CircleCoordinates> circleCoordinatesList = new ArrayList<>();
        ArrayList<PathLineCordinates> pathLineCordinatesList = new ArrayList<>();
        String[] nodeDataArray = nodeDataString.split("___");
        String[] edgeDataArray = nodeEdgeString.split("___");
        mapChange=true;

        for (String nodeData : nodeDataArray) {
            String[] coordinates = nodeData.split("_");

            String nodeName = coordinates[0];
            float nodeX = Float.parseFloat(coordinates[1]);
            float nodeY = Float.parseFloat(coordinates[2]);
            float nodeZ = Float.parseFloat(coordinates[3]);


            // Add the CircleCoordinates object to the ArrayList
            tempCircleCoordinateObject = new CircleCoordinates(nodeX, nodeY,nodeZ,nodeName);
            this.circleCoordinatesList.add(tempCircleCoordinateObject);

        }


        for(String edgeInfo: edgeDataArray){
            String[] edgeDetails = edgeInfo.split("_");

            String nodeName1 = edgeDetails[0];
            float nodeX1 = Float.parseFloat(edgeDetails[1]);
            float nodeY1 =Float.parseFloat(edgeDetails[2]);
            float nodeZ1 = Float.parseFloat(edgeDetails[3]);
            String nodeName2 =edgeDetails[4];
            float nodeX2 =Float.parseFloat(edgeDetails[5]);
            float nodeY2 = Float.parseFloat(edgeDetails[6]);
            float nodeZ2 = Float.parseFloat(edgeDetails[7]);


            this.pathLineCordinatesList.add(new PathLineCordinates(nodeX1, nodeY1,nodeZ1,nodeX2, nodeY2,nodeZ2));
            System.out.println(nodeName1+" -> "+nodeName2);

        }

        //this.lineCoordinatesList = lineCoordinatesArrayList;
        //this.circleCoordinatesList = circleCoordinatesList;
        invalidate();
    }

    public void zoomIn() {
        zoomFactor += 0.1f; // Adjust the increment as per your preference
        invalidate();
    }

    public void zoomOut() {
        zoomFactor -= 0.1f; // Adjust the decrement as per your preference
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Apply zoom and translation to the canvas
        canvas.scale(zoomFactor, zoomFactor);
        canvas.translate(translateX, translateY);

        nodeX = canvas.getWidth() / 2f;
        nodeY = canvas.getHeight() / 2f;
        canvas.scale(scaleFactor, scaleFactor, nodeX, nodeY);




        // Draw the background image
        if (backgroundImage != null) {
            canvas.drawBitmap(backgroundImage, 0, 0, null);
        }

        // Define a dash effect
//        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5,5}, 0);



        // Draw lines based on LineCoordinates
        //if(mapChange){
            if (lineCoordinatesList != null) {
                for (LineCoordinates lineCoordinates : lineCoordinatesList) {

                    float startX = getWidth() - lineCoordinates.getStartX() * zoomFactor;
                    float startY = getHeight() - lineCoordinates.getStartY() * zoomFactor;
                    float endX = getWidth() - lineCoordinates.getEndX() * zoomFactor;
                    float endY = getHeight() - lineCoordinates.getEndY() * zoomFactor;

                    Paint linePaint;
                    if (lineCoordinates.getStartZ() == 1) linePaint = linePaint1;
                    else if (lineCoordinates.getStartZ() == 2) linePaint = linePaint2;
                    else if (lineCoordinates.getStartZ() == 5) linePaint = linePaint3;
                    else linePaint = linePaint4;

                    Path path = new Path();
                    path.moveTo(startX, startY);
                    path.lineTo(endX, endY);

                    String whiteColor = "#ffffff";
                    int customColor = Color.parseColor(whiteColor);
                    linePaint.setColor(customColor); // Set color for the path
                    linePaint.setStrokeWidth(100f * zoomFactor);
                    linePaint.setStyle(Paint.Style.STROKE);


                    Paint borderPaint = new Paint();
                    String grayColor = "#dadce0"; // #78909c
                    int customGrayColor = Color.parseColor(grayColor);
                    borderPaint.setColor(customGrayColor); // Set the border color
                    borderPaint.setStrokeWidth(108f * zoomFactor);
                    borderPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path, borderPaint);


                    canvas.drawPath(path, linePaint);



                    // Reset the path effect to draw subsequent lines normally
                    linePaint.setPathEffect(null);
                }
            }
        // For path line
        if (pathLineCordinatesList != null) {
            for (PathLineCordinates pathLineCordinates : pathLineCordinatesList) {

                float startX = getWidth() - pathLineCordinates.getStartX() * zoomFactor;
                float startY = getHeight() - pathLineCordinates.getStartY() * zoomFactor;
                float endX = getWidth() - pathLineCordinates.getEndX() * zoomFactor;
                float endY = getHeight() - pathLineCordinates.getEndY() * zoomFactor;

                Paint linePaint;
                linePaint = new Paint();
                String pathColor = "";
                if (pathLineCordinates.getStartZ() == 1) pathColor = "#54aeff";
                else if (pathLineCordinates.getStartZ() == 2) pathColor = "#98e4ff";
                else if (pathLineCordinates.getStartZ() == 3) pathColor = "#d0bfff";
                else if (pathLineCordinates.getStartZ() == 4) pathColor = "#ffcf96";
                else if (pathLineCordinates.getStartZ() == 5) pathColor = "#64ccc5";
                else pathColor = "#8e8ffa";

                Path path = new Path();
                path.moveTo(startX, startY);
                path.lineTo(endX, endY);


                int customColor = Color.parseColor(pathColor);
                linePaint.setColor(customColor); // Set color for the path
                linePaint.setStrokeWidth(100f * zoomFactor);
                linePaint.setStyle(Paint.Style.STROKE);


                canvas.drawPath(path, linePaint);



                // Reset the path effect to draw subsequent lines normally
                linePaint.setPathEffect(null);
            }
        }

            // Draw decorative circles

            // Draw circles based on CircleCoordinates
            if (circleCoordinatesList != null) {
                for (CircleCoordinates circleCoordinates : circleCoordinatesList) {
//                nodeX = circleCoordinates.getX();
//                nodeY = circleCoordinates.getY();

                    // Adjust the coordinates based on the current zoom factor
                    nodeX = circleCoordinates.getX() * zoomFactor;
                    nodeY = circleCoordinates.getY() * zoomFactor;


                    String nodeName = circleCoordinates.getNodeName();
                    Paint circlePaint;
                    if (circleCoordinates.getZ() == 1) circlePaint = paint1;
                    else if (circleCoordinates.getZ() == 2) circlePaint = paint2;
                    else if (circleCoordinates.getZ() == 5) circlePaint = paint3;
                    else circlePaint = paint4;

                    canvas.drawCircle(getWidth() - nodeX, getHeight() - nodeY, 50 * zoomFactor, circlePaint);

                    float textHeight = textPaint.descent() - textPaint.ascent();
                    float textOffset = (textHeight / 2) - textPaint.descent();


                    String grayColor = "#78909c"; // #78909c
                    int customGrayColor = Color.parseColor(grayColor);
                    Typeface boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
                    textPaint.setTextSize(50f * zoomFactor);
                    textPaint.setTypeface(boldTypeface);
                    textPaint.setColor(customGrayColor);


                    canvas.drawText(String.valueOf(nodeName), getWidth() - nodeX, (getHeight() - nodeY) - textOffset, textPaint);

                }
            }
            mapChange=false;
        //}
        //if(locChange){
            canvas.drawCircle(getWidth()-xCoordinate,getHeight()-yCoordinate,40,centerDotPaint);
            canvas.drawCircle(getWidth()-xCoordinate,getHeight()-yCoordinate,100,borderDotPaint);
            locChange=false;
        //}

    }

    public void drawCircleOnCanvas(float x, float y) {
        // Adjust the radius and color as per your preference
        float radius = 20f;
        int color = Color.RED;

        // Calculate the adjusted coordinates based on zoom and translation
        float adjustedX = translateX + (x * zoomFactor);
        float adjustedY = translateY + (y * zoomFactor);

        // Create a new Paint object for the circle
        Paint circlePaint = new Paint();
        circlePaint.setColor(color);
        circlePaint.setStrokeWidth(10);
        circlePaint.setStyle(Paint.Style.STROKE);

        // Draw the circle on the canvas
        canvas.drawCircle(adjustedX, adjustedY, radius, circlePaint);

        // Invalidate the view to trigger a redraw
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);

        int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                previousX = event.getX();
                previousY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - previousX;
                float dy = event.getY() - previousY;
                translateX += dx / mScaleFactor;
                translateY += dy / mScaleFactor;
                invalidate();
                previousX = event.getX();
                previousY = event.getY();
                break;


            case MotionEvent.ACTION_CANCEL:
                 /*dx = event.getX() - previousX;
                 dy = event.getY() - previousY;
                translateX += dx / mScaleFactor;
                translateY += dy / mScaleFactor;
                invalidate();
                previousX = event.getX();
                previousY = event.getY();*/
                dx = event.getX() - previousX;
                dy = event.getY() - previousY;
                translateX=dx / mScaleFactor;
                translateY=dy / mScaleFactor;
                break;

        }

        return true;
    }

    public void setCoordinates(float x, float y) {
        xCoordinate = x;
        yCoordinate = y;
        locChange=true;
        invalidate(); // Redraw the canvas with the new dot
    }



}