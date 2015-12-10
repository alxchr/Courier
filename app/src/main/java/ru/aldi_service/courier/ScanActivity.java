package ru.aldi_service.courier;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.ArrayList;

/**
 * Created by alx on 10.12.15.
 */


public class ScanActivity extends Activity {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    static {
        System.loadLibrary("iconv");
    }

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private FrameLayout preview;
    private TextView scanText;
    private ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private String lastScannedCode;
    private Image codeImage;
    private int mode;
    private ArrayList<String> resArray;
    private EditText scanned;
    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
//            Log.d("CameraTestActivity", "onPreviewFrame data length = " + (data != null ? data.length : 0));
            codeImage.setData(data);
            int result = scanner.scanImage(codeImage);
            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    lastScannedCode = sym.getData();
                    if (lastScannedCode != null) {
                        scanText.setText(getString(R.string.scan_result_label) + lastScannedCode);
                        boolean d = false;
                        for (String code : resArray) {
                            if (code.equals(lastScannedCode)) {
                                d = true;
                            }
                        }
                        if (!d) {
                            Log.d("Scan", "Last = " + lastScannedCode);
                            if (resArray.size() > 0) {
                                scanned.setText(scanned.getText() + ", ");
                            }
                            resArray.add(lastScannedCode);
                            scanned.setText(scanned.getText() + lastScannedCode);
                            barcodeScanned = true;
                            if (mode > 0 && resArray.size() >= mode) {
                                Intent intent = new Intent();
                                intent.putExtra("result", resArray);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                }
            }
            camera.addCallbackBuffer(data);
        }
    };
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonReset:
                    scanned.setText("");
                    resArray.clear();
                    break;
                case R.id.buttonStop:
                    Intent intent = new Intent();
                    intent.putExtra("result", resArray);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };
    private Button buttonStop, buttonReset;
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing && mCamera != null) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };
    // Mimic continuous auto-focusing
    final Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            //
        }
        return c;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scan_layout);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();

        preview = (FrameLayout) findViewById(R.id.cameraPreview);


        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        scanText = (TextView) findViewById(R.id.scanText);
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 1);
        scanned = (EditText) findViewById(R.id.scannedText);
        buttonReset = (Button) findViewById(R.id.buttonReset);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonReset.setOnClickListener(ocl);
        buttonStop.setOnClickListener(ocl);
        resArray = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeCamera();
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void resumeCamera() {
        scanText.setText(getString(R.string.scan_process_label));
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        preview.removeAllViews();
        preview.addView(mPreview);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            codeImage = new Image(size.width, size.height, "Y800");
            previewing = true;
            mPreview.refreshDrawableState();
        }
    }
}
