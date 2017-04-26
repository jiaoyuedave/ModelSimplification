package com.example.modelsimplification.util;

/**
 * Created by lenovo on 2017/4/26.
 */

public class MatrixHelper {

    /**
     * 点乘两个4维向量
     *
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
