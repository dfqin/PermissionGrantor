package com.github.dfqin.grantor;

/**
 * Created by dfqin on 2017/1/22.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.io.Serializable;

public class PermissionActivity extends Activity {


    private static final int PERMISSION_REQUEST_CODE = 64;
    private boolean isRequireCheck;

    private String[] pers;
    private String key;
    private boolean showTip;
    private PermissionsUtil.TipInfo tipInfo;

    private final String defaultTitle = "帮助";
    private final String defaultContent = "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
    private final String defaultCancel = "取消";
    private final String defaultEnsure = "设置";

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra("permission")) {
            finish();
            return;
        }

        isRequireCheck = true;
        pers = getIntent().getStringArrayExtra("permission");
        key = getIntent().getStringExtra("key");
        showTip = getIntent().getBooleanExtra("showTip", true);
        Serializable ser = getIntent().getSerializableExtra("tip");

        if (ser == null) {
            tipInfo = new PermissionsUtil.TipInfo(defaultTitle, defaultContent, defaultCancel, defaultEnsure);
        } else {
            tipInfo = (PermissionsUtil.TipInfo)ser;
        }

    }

    @Override protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            if (PermissionsUtil.allPermissionGranted(this, pers)) {
                allPermissionsGranted();
            } else {
                requestPermissions(pers); // 请求权限,回调时会触发onResume
                isRequireCheck = false;
            }
        } else {
            isRequireCheck = true;
        }
    }

    // 请求权限兼容低版本
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    private void allPermissionsGranted() {
        PermissionListener listener = PermissionsUtil.fetchListener(key);
        if (listener != null) {
            listener.permissionGranted(pers);
        }
        finish();
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && PermissionsUtil.allPermissionsGranted(grantResults)) {
            allPermissionsGranted();
        } else if (showTip){
            showMissingPermissionDialog();
        } else { //不需要提示用户
            denyPermit();
        }
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);

        builder.setTitle(TextUtils.isEmpty(tipInfo.title) ? defaultTitle : tipInfo.title);
        builder.setMessage(TextUtils.isEmpty(tipInfo.content) ? defaultContent : tipInfo.content);

        builder.setNegativeButton(TextUtils.isEmpty(tipInfo.cancel) ? defaultCancel : tipInfo.cancel, new DialogInterface.OnClickListener(){
            @Override public void onClick(DialogInterface dialog, int which) {
                denyPermit();
            }
        });

        builder.setPositiveButton(TextUtils.isEmpty(tipInfo.ensure) ? defaultEnsure : tipInfo.ensure, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.setCancelable(false);
        builder.show();

    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }


    private void denyPermit() {
        PermissionListener listener = PermissionsUtil.fetchListener(key);
        if (listener != null) {
            listener.permissionDenied(pers);
        }
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

}

