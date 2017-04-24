package com.example.modelsimplification.objects;

import android.opengl.Matrix;

import com.example.modelsimplification.data.MatrixStack;
import com.example.modelsimplification.programs.ShaderProgram;

/**
 * Created by Administrator on 2017/4/24.
 */

abstract public class GLObject {

    private float[] MMatrix;

    private MatrixStack matrixStack = new MatrixStack();

    protected GLObject() {
        MMatrix = new float[16];
        Matrix.setRotateM(MMatrix, 0, 0, 1, 0, 0);
    }

    /**
     * 重置物体的位置
     */
    public void resetState() {
        Matrix.setRotateM(MMatrix, 0, 0, 1, 0, 0);
    }

    /**
     * 将物体沿x、y、z 轴平移
     *
     * @param x 沿x轴平移的距离
     * @param y 沿y轴平移的距离
     * @param z 沿z轴平移的距离
     */
    public void translate(float x, float y, float z) {
        Matrix.translateM(MMatrix, 0, x, y, z);
    }

    /**
     * 将物体沿x、y、z 轴旋转angle 角度
     *
     * @param angle 旋转的角度
     * @param x x轴分量
     * @param y y轴分量
     * @param z z轴分量
     */
    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(MMatrix, 0, angle, x, y, z);
    }

    /**
     * 将物体沿x、y、z 轴进行缩放
     *
     * @param x x轴缩放因子
     * @param y y轴缩放因子
     * @param z z轴缩放因子
     */
    public void scale(float x, float y, float z) {
        Matrix.scaleM(MMatrix, 0, x, y, z);
    }

    /**
     * 入栈保存物体的位置状态，与popState() 方法结合，用于重新绘制物体的位置
     */
    public void pushState() {
        matrixStack.push(MMatrix);
    }

    /**
     * 将物体的位置状态出栈，与pushState() 方法结合， 用于重新绘制物体的位置
     */
    public void popState() {
        MMatrix = matrixStack.pop();
    }

    /**
     * 获取物体的模型矩阵
     *
     * @return 模型矩阵
     */
    public float[] getMMatrix() {
        return MMatrix;
    }
}
