package com.example.arnavigationdemo.opengl.mesh;

import com.example.arnavigationdemo.opengl.BufferUtil;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 17111862 on 2018/8/15.
 */

public class Dot extends Mesh {
    private float radius;
    private int slice;
    public Dot(float r, int slice){
        this.radius = r;
        this.slice = slice;
    }
    /**
    * 绘制圆
    * */
    @Override
    public void draw(GL10 gl10){
        super.draw(gl10);
        List<Float> coords = new ArrayList<>();
        float x0,y0,z0;
        float alpha =(float) (2*Math.PI)/slice;
        for (int i = 0;i< slice;i ++){
            x0 =  radius * (float) Math.cos(i*alpha);
            y0 =  radius * (float) Math.sin(i*alpha);
            z0 = 0;
            coords.add(x0);
            coords.add(y0);
            coords.add(z0);
        }
        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl10.glVertexPointer(3,GL10.GL_FLOAT,0, BufferUtil.list2ByteBuffer(coords));
        gl10.glDrawArrays(GL10.GL_TRIANGLE_FAN,0,coords.size()/3);
        // Disable the vertices buffer.
        gl10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
