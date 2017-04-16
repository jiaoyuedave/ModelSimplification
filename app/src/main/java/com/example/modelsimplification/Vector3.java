package com.example.modelsimplification;

/**
 * Created by Administrator on 2017/4/14.
 */

public class Vector3 {

    public float x;
    public float y;
    public float z;

    public Vector3() {};

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float dotProduct(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }
}
