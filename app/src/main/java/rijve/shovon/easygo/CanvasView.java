package rijve.shovon.easygo;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class CanvasView extends View {
    private Paint paint,centerDotPaint,borderDotPaint;
    private float xCoordinate = 0;
    private float yCoordinate = 0;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        centerDotPaint = new Paint();
        centerDotPaint.setColor(Color.BLUE);
        centerDotPaint.setStyle(Paint.Style.FILL);

        borderDotPaint = new Paint();
        borderDotPaint.setColor(Color.BLUE);
        borderDotPaint.setStyle(Paint.Style.FILL);
        borderDotPaint.setAlpha(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;

        // Translate the canvas to place (0, 0) in the middle
        canvas.translate(centerX, centerY);

        // Draw the rectangle
        int rectWidth = 200; // Adjust the size of the rectangle as needed
        int rectHeight = 200;
        paint.setColor(Color.WHITE);
        canvas.drawRect(-rectWidth / 2, -rectHeight / 2, rectWidth / 2, rectHeight / 2, paint);

        canvas.drawCircle(xCoordinate*30,yCoordinate*30*(-1),15,centerDotPaint);
        canvas.drawCircle(xCoordinate*30,yCoordinate*30*(-1),40,borderDotPaint);
    }

    public void setCoordinates(float x, float y) {
        xCoordinate = x;
        yCoordinate = y;
        invalidate(); // Redraw the canvas with the new dot
    }
}
