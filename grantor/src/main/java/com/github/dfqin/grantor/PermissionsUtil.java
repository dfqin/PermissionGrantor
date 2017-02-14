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
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by dfqin on 2017/1/20.
 */

public class PermissionsUtil {


    public static HashMap<String, PermissionListener> listenerMap = new HashMap();

    /**
     * 申请授权，当用户拒绝时，会显示默认一个默认的Dialog提示用户
     * @param activity
     * @param listener
     * @param permissions 要申请的权限
     */
    public static void requestPermission(Activity activity, PermissionListener listener, String[] permissions) {
        requestPermission(activity, listener, permissions, true, null);
    }

    /**
     *  申请授权，当用户拒绝时，可以设置是否显示Dialog提示用户，也可以设置提示用户的文本内容
     * @param activity
     * @param listener
     * @param permissions 需要申请授权的权限
     * @param showTip 当用户拒绝授权时，是否显示提示
     * @param tip 当用户拒绝时要显示Dialog设置
     */
    public static void requestPermission(@NonNull Activity activity, @NonNull PermissionListener listener, @NonNull String[] permissions, boolean showTip, @Nullable TipInfo tip) {

        if (Build.VERSION.SDK_INT < 23) {
            listener.permissionDenied(permissions);
            Log.e("Tag", "API level : " + Build.VERSION.SDK_INT + "不需要申请动态权限!");
            return;
        }

        String key = String.valueOf(System.currentTimeMillis());
        if (listener == null) {
            return;
        }

        listenerMap.put(key, listener);
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra("permission", permissions);
        intent.putExtra("key", key);
        intent.putExtra("showTip", showTip);
        intent.putExtra("tip", tip);

        activity.startActivity(intent);
    }

    /**
     *  判断是否所以权限都已经授权
     * @param context
     * @param permissions
     * @return
     */
    public static boolean allPermissionGranted(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static boolean allPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否授权
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 跳转到当前应用对应的设置页面
     */
    public static void gotoSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    static PermissionListener fetchListener(String key) {
        return listenerMap.remove(key);
    }


    static public class TipInfo implements Serializable {

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
