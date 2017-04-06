package com.github.dfqin.grantor;

import android.support.annotation.NonNull;

/**
 * Created by dfqin on 2017/1/20.
 */

public interface PermissionListener {

    /**
     * 通过授权
     * @param permission
     */
    void permissionGranted(@NonNull String[] permission);

    /**
     * 拒绝授权
     * @param permission
     */
    void permissionDenied(@NonNull String[] permission);
}
