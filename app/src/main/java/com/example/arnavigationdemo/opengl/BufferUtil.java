package com.example.arnavigationdemo.opengl;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ming on 2018/8/13.
 */

public class BufferUtil {
    /*
       将浮点数组转换成字节缓冲区
     */
    public static ByteBuffer arr2ByteBuffer(float[] arr){
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length*4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        fbb.put(arr);
        ibb.position(0);
        return ibb;

    }
    /*
      将字节数组转换成字节缓冲区
    */
    public  static  ByteBuffer arr2ByteBuffer(byte[] arr){
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length);
        ibb.order(ByteOrder.nativeOrder());
        ibb.put(arr);
        ibb.position(0);
        return ibb;
    }
    /*
       将List转换成字节缓冲区
     */
    public static ByteBuffer list2ByteBuffer(List<Float> list){
        ByteBuffer ibb = ByteBuffer.allocateDirect(list.size()*4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        for (Float f: list){
            fbb.put(f);
        }
        ibb.position(0);
        return ibb;
    }
    /*
        将List转换成Float缓冲区
     */
    public static FloatBuffer list2FloatBuffer(List<Float> list){
        ByteBuffer ibb = ByteBuffer.allocateDirect(list.size()*4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        for (Float f: list){
            fbb.put(f);
        }
        fbb.position(0);
        return fbb;
    }
    /*
        将浮点数组转换成Float缓冲区
     */
    public static  FloatBuffer arr2FloatBuffer(float[] arr){
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length*4);
        ibb.order(ByteOrder.nativeOrder());
        FloatBuffer fbb = ibb.asFloatBuffer();
        for(Float f:arr){
            fbb.put(f);
        }
        fbb.position(0);
        return fbb;
    }
    /*
    *  将Int数组转换成int缓冲区
    * */
    public  static IntBuffer arr2IntBuffer(int[] arr){
        ByteBuffer ibb = ByteBuffer.allocateDirect(arr.length*4);
        ibb.order(ByteOrder.nativeOrder());
        IntBuffer ibb2 = ibb.asIntBuffer();
                for (Integer i:arr){
                    ibb2.put(i);
                }
        ibb2.position(0);
        return ibb2;
    }
    /*
       绘制球体
     */
    public static void DrawSphere(GL10 gl10,float r, int stack,int slice){
//        计算球体坐标
//        单位角度值
        float stackStep = (float)(Math.PI/stack);
//       水平圆递增的角度
        float sliceStep = (float)Math.PI/slice;
        float r0,r1,x0,x1,y0,y1,z0,z1;
        float alpha0 = 0,alpha1 = 0;
        float beta = 0;
        List<Float> coordsList = new ArrayList<>();
//        外层循环
        for (int i= 0;i<stack;i++){
            alpha0 = (float)(-Math.PI/2+(i*stackStep));
            alpha1 = (float)(-Math.PI/2+((i+1)*stackStep));
            y0 = (float)(r*(float) Math.sin(alpha0));
            r0 = (float)(r*(float) Math.cos(alpha0));
            y1 = (float)(r*(float) Math.sin(alpha1));
            r1 = (float)(r*(float) Math.cos(alpha1));
//            循环每一层圆
            for (int j =0;j<= (slice*2);j++){
                beta = j*sliceStep;
                x0 = (float)(r0*(float)Math.cos(beta));
                z0 = -(float)(r0*(float)Math.sin(beta));
                x1 = (float)(r1*(float)Math.cos(beta));
                z1 = -(float)(r1*(float)Math.sin(beta));
                coordsList.add(x0);
                coordsList.add(y0);
                coordsList.add(z0);
                coordsList.add(x1);
                coordsList.add(y1);
                coordsList.add(z1);
            }
        }
        FloatBuffer fbb = BufferUtil.list2FloatBuffer(coordsList);
        gl10.glVertexPointer(3, GL10.GL_FLOAT,0,fbb);
        gl10.glDrawArrays(GL10.GL_LINE_STRIP,0,coordsList.size()/3);
    }
    /*
        绘制圆环
     */
    public static void DrawRing(GL10 gl10,float Inner,float Outter,int count1,int count2){
          //        内环半径Inner
          //        环半径Outter
          //切片数count1,count2
        List<Float> coordsList = new ArrayList<>();
        float alphaStep = (float) (2 * Math.PI / count1) ;
        float alpha = 0 ;
        float x0,y0,z0,x1,y1,z1 ;
        float betaStep = (float) (2 * Math.PI) / count2;
        float beta = 0 ;
        for(int i = 0 ; i < count1 ; i ++){
            alpha = i * alphaStep ;
            for(int j = 0 ; j <= count2 ; j ++){
                beta = j * betaStep ;
                x0 = (float) Math.cos(alpha) * (Inner + Outter * (1 + (float)Math.cos(beta)));
                y0 = (float) Math.sin(alpha) * (Inner + Outter * (1 + (float)Math.cos(beta)));
                z0 = - Outter * (float)Math.sin(beta);

                x1 = (float)Math.cos(alpha + alphaStep) * (Inner + Outter * (1 + (float)Math.cos(beta)));
                y1 = (float)Math.sin(alpha + alphaStep) * (Inner + Outter * (1 + (float)Math.cos(beta)));
                z1 = - Outter * (float)Math.sin(beta);
                coordsList.add(x0);
                coordsList.add(y0);
                coordsList.add(z0);
                coordsList.add(x1);
                coordsList.add(y1);
                coordsList.add(z1);
            }
        }
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtil.list2FloatBuffer(coordsList));
        gl10.glDrawArrays(GL10.GL_LINE_STRIP, 0, coordsList.size() / 3);
    }
    /*
    * 绘制立方体
    * */
    public  static void DrawCude(GL10 gl10,float r){
        float[] coords = { -r, r, r, r, r, r, r, -r, r, -r, -r, r,

                -r, r, -r, r, r, -r, r, -r, -r, -r, -r, -r };
        byte [][] indices = { { 0, 1, 2, 3 }, { 0, 1, 5, 4 }, { 4, 5, 6, 7 },

                { 7, 6, 2, 3 }, { 0, 4, 7, 3 }, { 1, 5, 6, 2 } };

        float[][] colors = { { 1f, 0f, 0f, 0.6f }, { 0f, 1f, 0f, 0.6f }, { 0f, 0f, 1f, 0.6f }, { 0f, 1f, 1f, 0.6f },
                { 1f, 1f, 0f, 0.6f }, { 1f, 0f, 1f, 0.6f } };

        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtil.arr2FloatBuffer(coords));
        // 索引
        for (int i = 0; i < 6; i++) {
            gl10.glColor4f(colors[i][0], colors[i][1], colors[i][2], colors[i][3]);
            gl10.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, BufferUtil.arr2ByteBuffer(indices[i]));
        }
    }
    /*
    * 绘制圆
    * */
    public static void DrawCircle(GL10 gl10, float r,int slice){
        List<Float> coords = new ArrayList<>();
        float x0,y0,z0;
        float alpha =(float) (2*Math.PI)/slice;
        for (int i = 0;i< slice;i ++){
            x0 =  r*(float) Math.cos(i*alpha);
            y0 =  r*(float) Math.sin(i*alpha);
            z0 = 0;
            coords.add(x0);
            coords.add(y0);
            coords.add(z0);
        }
        gl10.glVertexPointer(3,GL10.GL_FLOAT,0,BufferUtil.list2ByteBuffer(coords));
        gl10.glDrawArrays(GL10.GL_TRIANGLE_FAN,0,coords.size()/3);
    }
    /*
    * 绘制点
    * */
    public static void DrawPoint(GL10 gl10, float Psize, float[] xyz){
        float[] coords ={xyz[0],xyz[1],xyz[2]};
        gl10.glPointSize(Psize);
        gl10.glVertexPointer(3,GL10.GL_FLOAT,0,BufferUtil.arr2ByteBuffer(coords));
        gl10.glDrawArrays(GL10.GL_POINTS,0,1);
    }
  /*
  * 绘制矩形
  * */
    public static void DrawRect(GL10 gl, float w, float h, float z) {
        float hw = w / 2;
        float hh = h / 2;
        float[] coords = { -hw, -hh, z, hw, -hh, z, -hw, hh, z, hw, hh, z, };
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, arr2FloatBuffer(coords));
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, coords.length / 3);
    }
    /*
    * 绘制正四面体
    * */
    public static void DrawTetrahedron(GL10 gl10,float prism){
//        定义正四面体的中心点坐标
//        定义正四面体的棱长
//        定义点坐标,分别是上顶点坐标，左前，右前，后
        float[] coords ={
                0f,prism*(float)Math.sqrt(6)/4,0,
                -prism/2,-prism*(float)Math.sqrt(6)/12,prism*(float)Math.sqrt(3)/12,
                prism/2,-prism*(float)Math.sqrt(6)/12,prism*(float)Math.sqrt(3)/12,
                0,-prism*(float)Math.sqrt(6)/12,-prism*(float)Math.sqrt(3)/6
        };
//        定义顶点索引位置
        byte[] indices = {
                0,1,2,2,0,3,3,0,1,1,2,3
        };
//       定义颜色数组
        float[] colors = {
                1f,0f,0f,1f,
                0f,1f,0f,1f,
                0f,0f,1f,1f,
                1f,1f,1f,1f
        };
        gl10.glColorPointer(4, GL10.GL_FLOAT,0,BufferUtil.arr2ByteBuffer(colors));
        gl10.glVertexPointer(3,GL10.GL_FLOAT,0, BufferUtil.arr2ByteBuffer(coords));
//        使用顶点索引方式绘制
        gl10.glDrawElements(GL10.GL_TRIANGLES,indices.length, GL10.GL_UNSIGNED_BYTE,BufferUtil.arr2ByteBuffer(indices));
    }
    /*
    * 绘制矩形2
    * */
    public static void DrawRect(GL10 gl, float[] vertex) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, arr2FloatBuffer(vertex));
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertex.length / 3);
    }
    /*
    * 加载纹理贴图
    * */

    public static void loadTexImage(GL10 gl, Bitmap bmp) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
        vbb.order(ByteOrder.nativeOrder());
        IntBuffer ibb = vbb.asIntBuffer();
        for (int y = 0; y < bmp.getHeight(); y++) {
            for (int x = 0; x < bmp.getWidth(); x++) {
                ibb.put(bmp.getPixel(x, y));
            }
        }
        ibb.position(0);
        gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.getWidth(), bmp.getHeight(), 0, GL10.GL_RGBA,
                GL10.GL_UNSIGNED_BYTE, ibb);
    }
}
