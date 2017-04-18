package com.example.modelsimplification;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.modelsimplification.objects.LoadedObject;
import com.example.modelsimplification.objects.ObjectModel;
import com.example.modelsimplification.programs.LoadedObjectShaderProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created by Administrator on 2017/4/18.
 */

public class SceneRenderer implements GLSurfaceView.Renderer {

    private final Context mContext;

    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

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

        ObjectModel objectModel = new ObjectModel("C:\\Users\\Administrator\\Desktop\\dinosaur.2k" +
                ".obj");
        loadedObject = objectModel.toLoadedObject();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface
        glViewport(0, 0, width, height);

        Matrix.perspectiveM(projectionMatrix, 0, 45, (float) width / height, 1, 10);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 2f, 3f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
