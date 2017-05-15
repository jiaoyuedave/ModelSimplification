package com.example.modelsimplification.util;

/**
 * Created by lenovo on 2017/4/26.
 */

public class MatrixHelper {

    /**
     * 将两个4X4的矩阵相加，结果保存在resMat中
     * @param resMat
     * @param lhsMat
     * @param rhsMat
     */
    public static void add(float[] resMat, float[] lhsMat, float[] rhsMat) {
        for (int i = 0; i < 16; i++) {
            resMat[i] = lhsMat[i] + rhsMat[i];
        }
    }

    /**
     * 将4X4矩阵与常数相乘，结果保存在resMat 中
     * @param resMat
     * @param mat
     * @param k
     */
    public static void multiplyK(float[] resMat, float[] mat, float k) {
        for (int i = 0; i < 16; i++) {
            resMat[i] = mat[i] * k;
        }
    }

    /**
     * 点乘两个4维向量
     * @param vec1
     * @param vec2
     * @return
     */
    public static float dotProduct(float[] vec1, float[] vec2) {
        float res = 0;
        for (int i = 0; i < 4; i++) {
            res += vec1[i] * vec2[i];
        }
        return res;
    }
}
