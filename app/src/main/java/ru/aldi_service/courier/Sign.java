package ru.aldi_service.courier;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Sign extends AppCompatActivity implements View.OnClickListener {

    Paint mPaint;
    Button btnOk,btnCancel,btnClear;
    LinearLayout linLayout;
    MyView signView;
    ByteArrayOutputStream signOS;
    //    ViewGroup.LayoutParams lp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        signView  = new MyView(this);
//        signView = myView;
        linLayout=(LinearLayout) findViewById(R.id.linLayout);
        linLayout.addView(signView);

        // Buttons
        btnOk = (Button) findViewById(R.id.button);
        btnClear = (Button) findViewById(R.id.button2);
        btnCancel = (Button) findViewById(R.id.button3);
        btnOk.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        // Paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);

    }
    @Override
    public void onClick(View v) {
        // по id определеяем кнопку, вызвавшую этот обработчик
        switch (v.getId()) {
            case R.id.button:
                // кнопка ОК
                Log.d("Button", "Ok");
//                tvOut.setText("Нажата кнопка ОК");
                signOS=signView.write();
                Log.d ("Button-Ok","Size = "+String.valueOf(signOS.size()));
                break;
            case R.id.button2:
                // кнопка Clear
                Log.d("Button", "Clear");
                signView.clear();
//                tvOut.setText("Нажата кнопка Cancel");
                break;
            case R.id.button3:
                // кнопка Cancel
                Log.d ("Button","Cancel");
                finish();
//                tvOut.setText("Нажата кнопка Cancel");
                break;
        }
    }
    public class MyView extends View {

        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;

        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint   mBitmapPaint;
        private int wSign,hSign;
        public MyView(Context c) {
            super(c);

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mBitmap);
            //        mCanvas.setBitmap(mBitmap);
            wSign=w;
            hSign=h;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            //canvas.drawLine(mX, mY, Mx1, My1, mPaint);
            //canvas.drawLine(mX, mY, x, y, mPaint);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);

        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 1;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    //   Mx1=(int) event.getX();
                    //  My1= (int) event.getY();
                    invalidate();
                    break;
            }
            return true;
        }
        public void clear() {
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mBitmap = Bitmap.createBitmap(wSign, hSign, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mBitmap);
            mCanvas.setBitmap(mBitmap);
            invalidate();
        }
        public ByteArrayOutputStream write() {
//            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "savedBitmap.png");
            ByteArrayOutputStream result=null;
//            FileOutputStream fos = null;
            try {
                result=new ByteArrayOutputStream();
//                fos = new FileOutputStream(file);
                Log.d("Bitmap","Size = "+String.valueOf(mBitmap.getByteCount()));
//                mBitmap.copyPixelsFromBuffer();
                mBitmap.compress(Bitmap.CompressFormat.PNG,100,result);
//                mBitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
//                fos.close();
                result.close();
//                Log.d("Compress", "Size = " + String.valueOf(result.size()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}

