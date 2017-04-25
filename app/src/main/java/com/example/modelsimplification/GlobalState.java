package com.example.modelsimplification;

import android.opengl.Matrix;

import com.example.modelsimplification.data.Vector;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/4/24.
 */

public class GlobalState {

    private static float[] viewMatrix = new float[16];
    private static float[] projectionMatrix = new float[16];
    private static float[] viewProjectionMatrix = new float[16];
    private static float[] modelViewProjectionMatrix = new float[16];

    private static float[] lightLocation = new float[3];
    private static float[] lightDirection = new float[3];
    private static float[] cameraLocation = new float[3];
    private static float[] cameraCenter = new float[3];
    private static float[] cameraUp = new float[3];

    /**
     *  设置透视投影
     *
     * @param fovy y轴方向的视场角
     * @param aspect 宽高比
     * @param zNear 近平面的距离
     * @param zFar 远平面的距离
     */
    public static void setPerspectiveProjection(float fovy, float aspect, float zNear, float zFar) {
        Matrix.perspectiveM(projectionMatrix, 0, fovy, aspect, zNear, zFar);
    }

    /**
     * 设置摄像机
     *
     * @param cx 位置x
     * @param cy 位置y
     * @param cz 位置z
     * @param tx 中心朝向x
     * @param ty 中心朝向y
     * @param tz 中心朝向z
     * @param upx 头部方向x
     * @param upy 头部方向y
     * @param upz 头部方向z
     */
    public static void setCamera(float cx, float cy, float cz, float tx, float ty, float tz,
                                 float upx, float upy, float upz) {
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;

        cameraCenter[0] = tx;
        cameraCenter[1] = ty;
        cameraCenter[2] = tz;

        cameraUp[0] = upx;
        cameraUp[1] = upy;
        cameraUp[2] = upz;

        setCamera();
    }

    /**
     * 设置摄像机的位置
     *
     * @param loc 摄像机的位置
     */
    public static void setCameraLocation(float[] loc) {
        cameraLocation = loc;
        setCamera();
    }

    /**
     * 设置摄像机中心朝向
     *
     * @param center 摄像机的中心朝向
     */
    public static void setCameraCenter(float[] center) {
        cameraCenter = center;
        setCamera();
    }

    /**
     * 设置摄像机的头部朝向
     *
     * @param up 摄像机头部朝向
     */
    public static void setCameraUp(float[] up) {
        cameraUp = up;
        setCamera();
    }

    /**
     * 平移摄像机，摄像机中心及头部朝向不变
     *
     * @param vec 平移向量
     */
    public static void moveCamera(float[] vec) {
        cameraLocation[0] += vec[0];
        cameraLocation[1] += vec[1];
        cameraLocation[2] += vec[2];

        cameraCenter[0] += vec[0];
        cameraCenter[1] += vec[1];
        cameraCenter[2] += vec[2];

        setCamera();
    }

    public static float[] getFinalMatrix(float[] modelMatirx) {
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatirx, 0);
        return modelViewProjectionMatrix;
    }

    /**
     * 设置点光源位置，使用点光源时有效
     *
     * @param x 位置x
     * @param y 位置y
     * @param z 位置z
     */
    public static void setLightLocation(float x, float y, float z) {
        lightLocation[0] = x;
        lightLocation[1] = y;
        lightLocation[2] = z;
    }

    /**
     * 获取点光源位置，使用点光源时有效
     *
     * @return 点光源位置的拷贝
     */
    public static float[] getLightLocation() {
        return Arrays.copyOf(lightLocation, lightLocation.length);
    }

    /**
     * 设置平行光方向，使用平行光时有效
     *
     * @param x 方向x
     * @param y 方向y
     * @param z 方向z
     */
    public static void setLightDirection(float x, float y, float z) {
        lightDirection[0] = x;
        lightDirection[1] = y;
        lightDirection[2] = z;
    }

    /**
     * 获取平行光方向，使用平行光时有效
     *
     * @return 平行光方向的拷贝
     */
    public static float[] getLightDirection() {
        return Arrays.copyOf(lightDirection, lightDirection.length);
    }

    /**
     * 获取摄像机位置
     *
     * @return 摄像机位置的拷贝
     */
    public static float[] getCameraLocation() {
        return Arrays.copyOf(cameraLocation, cameraLocation.length);
    }

    public static float[] getCameraCenter() {
        return Arrays.copyOf(cameraCenter, cameraCenter.length);
    }

    private static void setCamera() {
        Matrix.setLookAtM(viewMatrix, 0,
                cameraLocation[0], cameraLocation[1], cameraLocation[2],
                cameraCenter[0], cameraCenter[1], cameraCenter[2],
                cameraUp[0], cameraUp[1], cameraUp[2]);
    }
}
