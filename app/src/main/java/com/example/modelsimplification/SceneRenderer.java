package com.example.modelsimplification;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.modelsimplification.objects.IndexedGLObject;
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

    private final Context mContext;

    private LoadedObjectShaderProgram loProgram;

    private IndexedGLObject indexedGLObject;

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
            InputStream in = mContext.getAssets().open("dinosaur.2k.obj");
            Reader reader = new BufferedReader(new InputStreamReader(in));
            ObjectModel objectModel = new ObjectModel(reader);
            objectModel.simplifiedTo(2000);
            indexedGLObject = objectModel.toIndexedGLObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        indexedGLObject.bindProgram(loProgram);
        indexedGLObject.rotate(90, 0, 0, 1);
        indexedGLObject.rotate(90, 0, 1, 0);
        indexedGLObject.setColor(0.9f, 0.9f, 0.9f, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface
        glViewport(0, 0, width, height);

        GlobalState.setPerspectiveProjection(45, (float) width / height, 1, 1000);
        GlobalState.setCamera(0f, 0f, 200f, 0f, 0f, 0f, 0f, 1f, 0f);
        GlobalState.setLightDirection(-1, 0, -3);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        indexedGLObject.draw();
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
