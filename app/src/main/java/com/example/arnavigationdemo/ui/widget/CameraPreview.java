package com.example.arnavigationdemo.ui.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.arnavigationdemo.util.DisplayUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by ming on 2018/8/24.
 */

public class CameraPreview {
    private static final String TAG = CameraPreview.class.getSimpleName();
    private SurfaceView surfaceView;
    private Camera camera;

    private SurfaceHolder.Callback cameraPreviewcallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            startPreview();
            Log.i(TAG, "surfaceCreated" + Thread.currentThread().getName());
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.i(TAG, "surfaceChanged");
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.i(TAG, "surfaceDestoryed");
            if (camera != null) {
                camera.stopPreview();
                camera.release();//释放相机资源
                camera = null;
            }
        }
    };

    public CameraPreview(Context context){
        /**
         * 设置相机预览SurfaceView
         * */
        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(cameraPreviewcallback);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public SurfaceView getPreview(){
        return surfaceView;
    }

    /**
     * 相机预览功能函数
     */
    private void startPreview() {
        camera = Camera.open();
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();//获取所有支持的camera尺寸
            Log.d(TAG,"optionSize : mSurfaceView " + surfaceView.getWidth()+" * " + surfaceView.getHeight());
            Camera.Size optionSize = DisplayUtil.getOptimalPreviewSize(sizeList, surfaceView.getHeight(), surfaceView.getWidth());//获取一个最为适配的camera.size
            Log.d(TAG,"optionSize : "+optionSize.width+" * "+optionSize.height);
            parameters.setPreviewSize(optionSize.width,optionSize.height);//把camera.size赋值到parameters
            camera.setParameters(parameters);
            //通过SurfaceView显示预览
            camera.setPreviewDisplay(surfaceView.getHolder());
            camera.setDisplayOrientation(90);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
