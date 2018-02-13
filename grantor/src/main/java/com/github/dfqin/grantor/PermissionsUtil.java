package com.github.dfqin.grantor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by dfqin on 2017/1/20.
 */

public class PermissionsUtil {

    public static final String TAG = "PermissionGrantor";
    private static HashMap<String, PermissionListener> listenerMap = new HashMap();

    /**
     * 申请授权，当用户拒绝时，会显示默认一个默认的Dialog提示用户
     * @param context
     * @param listener
     * @param permission 要申请的权限
     */
    public static void requestPermission(Context context, PermissionListener listener, String... permission) {
        requestPermission(context, listener, permission, true, null);
    }

    /**
     *  申请授权，当用户拒绝时，可以设置是否显示Dialog提示用户，也可以设置提示用户的文本内容
     * @param context
     * @param listener
     * @param permission 需要申请授权的权限
     * @param showTip 当用户拒绝授权时，是否显示提示
     * @param tip 当用户拒绝时要显示Dialog设置
     */
    public static void requestPermission(@NonNull Context context, @NonNull PermissionListener listener
            , @NonNull String[] permission, boolean showTip, @Nullable TipInfo tip) {
        
        if (listener == null) {
            Log.e(TAG, "listener is null");
            return;
        }

        if (PermissionsUtil.hasPermission(context, permission)) {
            listener.permissionGranted(permission);
        } else {
            if (Build.VERSION.SDK_INT < 23) {
                listener.permissionDenied(permission);
            } else {
                String key = String.valueOf(System.currentTimeMillis());
                listenerMap.put(key, listener);
                Intent intent = new Intent(context, PermissionActivity.class);
                intent.putExtra("permission", permission);
                intent.putExtra("key", key);
                intent.putExtra("showTip", showTip);
                intent.putExtra("tip", tip);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        }
    }


    /**
     * 判断权限是否授权
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {

        if (permissions.length == 0) {
            return false;
        }

        for (String per : permissions ) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if ( result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断一组授权结果是否为授权通过
     * @param grantResult
     * @return
     */
    public static boolean isGranted(@NonNull int... grantResult) {

        if (grantResult.length == 0) {
            return false;
        }

        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 跳转到当前应用对应的设置页面
     * @param context
     */
    public static void gotoSetting(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /**
     *
     * @param key
     * @return
     */
    static PermissionListener fetchListener(String key) {
        return listenerMap.remove(key);
    }


    public static class TipInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        String title;
        String content;
        String cancel;  //取消按钮文本
        String ensure;  //确定按钮文本

        public TipInfo ( @Nullable String title,  @Nullable String content,  @Nullable String cancel,  @Nullable String ensure) {
            this.title = title;
            this.content = content;
            this.cancel = cancel;
            this.ensure = ensure;
        }
    }
}
