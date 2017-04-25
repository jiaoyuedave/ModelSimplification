package com.example.modelsimplification;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;    // if the GLSufaceView is valid

    private ScaleGestureDetector scaleGestureDetector;

    private long timeScaleEnd;
    private long timeMoveStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        // check support for OpenGL ES 2.0
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context
                .ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        // if the program runs in emulator, use the check code below
/*        final boolean supportsEs2 = configurationInfo.reqGlEsVersion > 0x20000
                || (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));*/

        // config rendering surface
        final SceneRenderer sceneRenderer = new SceneRenderer(this);
        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer
            glSurfaceView.setRenderer(sceneRenderer);
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector
                .OnScaleGestureListener() {
            float scaleFactor;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor = detector.getScaleFactor();
                if (detector.getCurrentSpan() - detector.getPreviousSpan() < 0) {
                    scaleFactor = -scaleFactor;
                }

//                glSurfaceView.queueEvent(new Runnable() {
//                    @Override
//                    public void run() {
//                        sceneRenderer.handleScaleGesture(scaleFactor);
//                    }
//                });
//                Log.d(TAG, "onScale: " + scaleFactor);
                sceneRenderer.handleScaleGesture(scaleFactor);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                timeScaleEnd = System.currentTimeMillis();
            }
        });

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            float previousX, previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event == null) {
                    return false;
                }
                scaleGestureDetector.onTouchEvent(event);

                int pointerCount = event.getPointerCount();

                timeMoveStart = System.currentTimeMillis();

                if (pointerCount == 1 && !scaleGestureDetector.isInProgress() && timeMoveStart -
                        timeScaleEnd > 1000) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;
                        previousX = event.getX();
                        previousY = event.getY();

//                        glSurfaceView.queueEvent(new Runnable() {
//                            @Override
//                            public void run() {
//                                sceneRenderer.handleTouchDrag(deltaX, deltaY);
//                            }
//                        });
                        sceneRenderer.handleTouchDrag(deltaX, deltaY);
                    }
                }
                return true;
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}
