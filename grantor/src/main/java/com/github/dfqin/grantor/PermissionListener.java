package com.github.dfqin.grantor;

import android.support.annotation.NonNull;

/**
 * Created by dfqin on 2017/1/20.
 */

public interface PermissionListener {

    /**
     * 通过授权
     * @param permissions
     */
    void permissionGranted(@NonNull String[] permissions);

    /**
     * 拒绝授权
     * @param permissions
     */
    void permissionDenied(@NonNull String[] permissions);
}
