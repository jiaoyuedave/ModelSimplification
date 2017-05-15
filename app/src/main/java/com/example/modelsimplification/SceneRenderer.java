package com.example.modelsimplification;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.modelsimplification.objects.GLObject;
import com.example.modelsimplification.objects.ObjectModel;
import com.example.modelsimplification.programs.LoadedObjectShaderProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Administrator on 2017/4/18.
 */

public class SceneRenderer implements GLSurfaceView.Renderer {

    public static final int DINOSAUR = 1;
    public static final int BUNNY = 2;
    public int model = 2;

    private final Context mContext;

    private LoadedObjectShaderProgram loProgram;

    private GLObject loadedObject;

    public SceneRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        loProgram = new LoadedObjectShaderProgram(mContext);

        try {
            if (model == DINOSAUR) {
                InputStream in = mContext.getAssets().open("dinosaur.2k.obj");
                Reader reader = new BufferedReader(new InputStreamReader(in));
                ObjectModel objectModel = new ObjectModel(reader);
//                objectModel.simplifiedTo(500);
                objectModel.simplifiedToRatio(0.1f);
//            loadedObject = objectModel.toIndexedGLObject();
                loadedObject = objectModel.toGLObject();
            } else if (model == BUNNY) {
                InputStream in = mContext.getAssets().open("bunny.obj");
                Reader reader = new BufferedReader(new InputStreamReader(in));
                ObjectModel objectModel = new ObjectModel(reader);
                objectModel.simplifiedToRatio(0.1f);
//                objectModel.simplifiedTo(500);
//            loadedObject = objectModel.toIndexedGLObject();
                loadedObject = objectModel.toGLObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (model == DINOSAUR) {
            loadedObject.bindProgram(loProgram);
            loadedObject.rotate(90, 0, 0, 1);
            loadedObject.rotate(90, 0, 1, 0);
            loadedObject.setColor(0.9f, 0.9f, 0.9f, 1f);
        } else if (model == BUNNY) {
            loadedObject.bindProgram(loProgram);
            loadedObject.translate(0, -5, 180);
            loadedObject.setColor(0.9f, 0.9f, 0.9f, 1f);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface
        glViewport(0, 0, width, height);

        GlobalState.setPerspectiveProjection(45, (float) width / height, 1, 100000);
        GlobalState.setCamera(0f, 0f, 200f, 0f, 0f, 0f, 0f, 1f, 0f);
        GlobalState.setLightDirection(-1, 0, -3);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        loadedObject.draw();
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        float[] v = new float[]{-deltaX / 16, deltaY / 16, 0};
        GlobalState.moveCamera(v);
    }

    public void handleScaleGesture(float scaleFactor) {
        float[] v = new float[]{0, 0, -5 * scaleFactor};
        GlobalState.moveCamera(v);
    }
}
