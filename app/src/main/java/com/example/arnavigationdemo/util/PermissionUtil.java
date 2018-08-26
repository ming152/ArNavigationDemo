package com.example.arnavigationdemo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2018/8/24.
 */

public class PermissionUtil {
    public static List<String> checkPermisson(Context context, String ... permissions ){
        //android 6.0以下不需要动态获取权限
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return null;
        }
        List<String> permissionLists = new ArrayList<>();
        if(permissions != null){
            for(String permission : permissions){
                if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    permissionLists.add(permission);
                }
            }
        }
        return permissionLists;
    }

    public static void requestPermissions(final Activity context, int requestCode, String ... permissions){
        for(String perssion : permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, perssion)){
                //当拒绝了授权后，为提升用户体验，可以以弹窗的方式引导用户到设置中去进行设置
                new AlertDialog.Builder(context)
                        .setMessage("需要开启权限才能使用此功能")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //引导用户到设置中去进行设置
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                return;
            }
        }
        ActivityCompat.requestPermissions(context ,permissions, requestCode);
    }
}
