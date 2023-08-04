package rijve.shovon.easygo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

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
    private float previousX;
    private int backgroundColor = Color.CYAN;
    private float previousY;
    private float translateX;
    private float translateY;
    private Paint textPaint;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private Path roadPath;


    private ArrayList<CircleCoordinates> circleCoordinatesList = new ArrayList<>();
    private ArrayList<LineCoordinates> lineCoordinatesList = new ArrayList<>();

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
        linePaint4.setStrokeWidth(50f);

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
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
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

    public Bitmap getBitmap() {
        return backgroundImage;
    }


    public void zoomIn() {
        zoomFactor += 0.1f; // Adjust the increment as per your preference
        invalidate();
    }

    public void zoomOut() {
        zoomFactor -= 0.1f; // Adjust the decrement as per your preference
        invalidate();
    }

    @Override
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
        // nodeName1
        // Draw circles based on CircleCoordinates

        if(lineCoordinatesList!=null){
            for(LineCoordinates lineCoordinates: lineCoordinatesList){

                float startX = getWidth()-lineCoordinates.getStartX();
                float startY =getHeight() - lineCoordinates.getStartY();
                float endX = getWidth()-lineCoordinates.getEndX();
                float endY = getHeight() -lineCoordinates.getEndY();

                if(lineCoordinates.getStartZ()==1) linePaint = linePaint1;
                else if(lineCoordinates.getStartZ()==2) linePaint = linePaint2;
                else if(lineCoordinates.getStartZ()==5) linePaint = linePaint3;
                else linePaint = linePaint4;
                canvas.drawLine(startX, startY,endX, endY, linePaint);
            }
        }

        //System.out.println(circleCoordinatesList.size());
        if (circleCoordinatesList != null) {
            for (CircleCoordinates circleCoordinates : circleCoordinatesList) {
                nodeX = circleCoordinates.getX();
                nodeY = circleCoordinates.getY();

                String nodeName = circleCoordinates.getNodeName();
                if(circleCoordinates.getZ()==1) paint = paint1;
                else if(circleCoordinates.getZ()==2) paint = paint2;
                else if(circleCoordinates.getZ()==5) paint = paint3;
                else paint = paint4;
                canvas.drawCircle(getWidth() - nodeX, getHeight() - nodeY, 30, paint);
                float textHeight = textPaint.descent() - textPaint.ascent();
                float textOffset = (textHeight / 2) - textPaint.descent();
                //nodeX *= -1;
                canvas.drawText(String.valueOf(nodeName), getWidth() - nodeX, (getHeight() - nodeY) - textOffset, textPaint);
            }
        }



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
        scaleGestureDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = x;
                previousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;
                translateX += dx;
                translateY += dy;
                invalidate();
                break;
        }

        previousX = x;
        previousY = y;

        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            // Limit the scale factor to a certain range (optional)
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            return true;
        }
    }

}