package com.example.modelsimplification.data;

import android.opengl.Matrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 使用可变长二维数组实现矩阵栈
 *
 * Created by Jiao Yue on 2017/4/24.
 */

public class MatrixStack {

    private float[][] mStack = new float[2][16];
    private int N = 0;          // 元素数量

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void push(float[] matrix) {
        if (N == mStack.length) {
            resize(2 * mStack.length);
        }
        mStack[N++] = matrix;
    }

    public float[] pop() {
        float[] matrix = mStack[--N];
        mStack[N] = null;
        if (N > 0 && N == mStack.length / 4) {
            resize(mStack.length / 2);
        }
        return matrix;
    }

    private void resize(int max) {
        float[][] temp = new float[max][16];
        for (int i = 0; i < N; i++) {
            temp[i] = mStack[i];
        }
        mStack = temp;
    }


    /**
     * 单元测试代码
     * @param args
     */
    public static final void main(String[] args) {
        try {
            MatrixStack stack = new MatrixStack();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String command = reader.readLine();
                if (command.equals("push")) {
                    float[] matrix = new float[16];
                    stack.push(matrix);
                    System.out.println("push:" + matrix);
                } else if (command.equals("pop")) {
                    System.out.println("pop:" + stack.pop());
                } else if (command.equals("exit")) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
