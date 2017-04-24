package com.example.modelsimplification;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.modelsimplification.objects.LoadedObject;
import com.example.modelsimplification.objects.ObjectModel;
import com.example.modelsimplification.programs.LoadedObjectShaderProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created by Administrator on 2017/4/18.
 */

public class SceneRenderer implements GLSurfaceView.Renderer {

    private final Context mContext;

    private LoadedObjectShaderProgram loProgram;

    private LoadedObject loadedObject;

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
            loadedObject = objectModel.toLoadedObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadedObject.rotate(90, 0, 0, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface
        glViewport(0, 0, width, height);

        GlobalState.setPerspectiveProjection(45, (float) width / height, 100, 300);
        GlobalState.setCamera(0f, 0f, 200f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        loProgram.useProgram();
        loProgram.setUniforms(GlobalState.getFinalMatrix(loadedObject.getMMatrix()), loadedObject
                .getMMatrix(), new float[]{0, 0, -1}, new float[]{0, 0, 200}, 0.9f, 0.9f, 0.9f);
        loadedObject.bindData(loProgram);
        loadedObject.draw();
    }
}
