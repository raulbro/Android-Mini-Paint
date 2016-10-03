package paint.android_paint;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawView _drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._drawView = new DrawView(this);
        setContentView(_drawView);
        addContentView(_drawView._row, _drawView._params);
    }

    public class DrawView extends View{

        private final int _finalColor = Color.parseColor("#9FA9DF");
        private final int _finalWidthCircle = 14;
        private final int _finalWidthBrush = 12;

        private float _lastPosX, _lastPosY;

        private Paint _circlePaint;
        private Path _circlePath;
        private Path _brushPath;
        private Paint _brushPaint;

        private Canvas _canvas;
        private Bitmap _myBitMap;
        private Paint _bitMapPaint;

        private LinearLayout.LayoutParams _params;
        private LinearLayout _row;

        private Button _btnEraser;
        private Button _btnPencil;
        private Button _btnClear;

        public DrawView(Context context){
            super(context);
            _bitMapPaint = new Paint();
            createCircle();
            createBrush();
            createButtons(context);
            setBackgroundColor(Color.WHITE);
        }

        private void createCircle(){
            _circlePaint = new Paint();
            _circlePath = new Path();
            _circlePaint.setDither(true);
            _circlePaint.setAntiAlias(true);
            _circlePaint.setColor(_finalColor);
            _circlePaint.setStyle(Paint.Style.STROKE);
            _circlePaint.setStrokeWidth(_finalWidthCircle);
        }

        private void createBrush(){
            _brushPaint = new Paint();
            _brushPath = new Path();
            _brushPaint.setDither(true);
            _brushPaint.setAntiAlias(true);
            _brushPaint.setColor(_finalColor);
            _brushPaint.setStyle(Paint.Style.STROKE);
            _brushPaint.setStrokeWidth(_finalWidthBrush);
            _brushPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        private void createButtons(Context context){
            _params = new ActionMenuView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _row = new LinearLayout(context);
            _row.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

            createEraser(context);
            createPencil(context);
            createClear(context);

            _row.addView(_btnEraser);
            _row.addView(_btnPencil);
            _row.addView(_btnClear);
        }

        private void createPencil(Context context){
            _btnPencil = new Button(context);
            _btnPencil.setText("Pencil");
            _btnPencil.setWidth(200);

            _btnPencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    set_btnPencil();
                }
            });
        }

        private void createEraser(Context context){
            _btnEraser = new Button(context);
            _btnEraser.setText("Erase");
            _btnEraser.setWidth(200);

            _btnEraser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    set_btnEraser();
                }
            });
        }

        private void createClear(Context context){
            _btnClear = new Button(context);
            _btnClear.setText("Clear");
            _btnClear.setWidth(200);

            _btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    set_btnClear();
                }
            });
        }

        private void set_btnClear(){
            _brushPath.reset();
            _circlePath.reset();
            _canvas.drawColor(Color.WHITE);
            invalidate();
            set_btnPencil();
        }

        private void set_btnPencil(){
            _brushPaint.setStrokeWidth(_finalWidthBrush);
            _brushPaint.setColor(_finalColor);
        }

        private void set_btnEraser(){

            _brushPaint.setStrokeWidth(_finalWidthBrush + 20);
            _brushPaint.setColor(Color.WHITE);
        }

        private void drawCircle(Canvas canvas){
            Paint paint = new Paint();
            paint.setDither(true);
            paint.setAntiAlias(true);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(_finalColor);
            canvas.drawCircle(300, 200, 100, paint);
        }

        private void showMessage(String message){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        private void actionDown(float posX, float posY){
            _lastPosX = posX;
            _lastPosY = posY;
            _circlePath.reset();
            _brushPath.reset();
            _brushPath.moveTo(_lastPosX, _lastPosY);
        }

        private void actionMove(float posX, float posY){
            _circlePath.reset();

            _brushPath.quadTo(_lastPosX, _lastPosY, (posX + _lastPosX) / 2, (posY + _lastPosY) / 2);
            _lastPosX = posX;
            _lastPosY = posY;

            _circlePath.addCircle(posX, posY, 30, Path.Direction.CCW);
        }

        private void actionUp(){
            _circlePath.reset();
            _canvas.drawPath(_brushPath, _brushPaint);
            _brushPath.reset();
        }

        @Override
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            canvas.drawBitmap(_myBitMap, 0, 0, _bitMapPaint);
            canvas.drawPath(_brushPath, _brushPaint);
            canvas.drawPath(_circlePath, _circlePaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            //showMessage("Touch event");
            float posX = event.getX();
            float posY = event.getY();

            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    actionDown(posX, posY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    actionMove(posX, posY);
                    break;

                case MotionEvent.ACTION_UP:
                    actionUp();
                    break;
            }
            invalidate();
            return true;
        }

        @Override
        protected void onSizeChanged(int w, int h, int wold, int hold){
            super.onSizeChanged(w, h, wold, hold);
            _myBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            _canvas = new Canvas(_myBitMap);

        }
    }








}
