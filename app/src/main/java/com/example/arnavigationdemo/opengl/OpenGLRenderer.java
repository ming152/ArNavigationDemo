package com.example.arnavigationdemo.opengl;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import com.baidu.mapapi.search.core.RouteStep;
import com.example.arnavigationdemo.opengl.mesh.Group;
import com.example.arnavigationdemo.opengl.mesh.Mesh;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ming on 2018/8/13.
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    private final Group root;

    //    围绕X轴旋转的角度
    public float xrotate = 0f;
    //    围绕Y轴旋转的角度
    public float yrotate = 0f;
    //    围绕Z轴旋转的角度
    public float zrotate = 0f;

    public OpenGLRenderer() {
        // Initialize our root.
        Group group = new Group();
        root = group;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition
     * .khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background color to black ( rgba ).
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);
        // Depth buffer setup.
        gl.glClearDepthf(1.0f);
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // The type of depth testing to do.
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // Really nice perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
     * khronos.opengles.GL10)
     */
    public void onDrawFrame(GL10 gl) {
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //        模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // Replace the current matrix with the identity matrix
        gl.glLoadIdentity();
        // Translates 4 units into the screen.
        GLU.gluLookAt(gl,0,0,10,0,2,0,0,1,0);
        gl.glRotatef(xrotate,1,0,0);
        gl.glRotatef(yrotate,0,1,0);
        gl.glRotatef(zrotate,0,0,1);
        // Draw our scene.
        root.draw(gl);
//        gl.glTranslatef(0, 0, -4);

        Log.d("======", "onDrawFrame");
    }

    /*
         * (non-Javadoc)
         *
         * @see
         * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
         * .khronos.opengles.GL10, int, int)
         */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Sets the current view port to the new size.
        gl.glViewport(0, 0, width, height);
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        gl.glLoadIdentity();
        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
                1000.0f);
        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        gl.glLoadIdentity();
    }

    /**
     * Adds a mesh to the root.
     *
     * @param mesh
     *            the mesh to add.
     */
    public void addMesh(Mesh mesh) {
        root.add(mesh);
    }

    public void clearMesh(){
        root.clear();
    }

}

