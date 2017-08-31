### update 2017-07-31  
fix bug on XiaoMi device

### update 2017-08-08
update dialog style

### update 2017-08-29
set minSdkVersion to 14

### Grantor
An Android permission grant util which is concise and easy to use. Normally you need to request permission in an Activity or Fragment and get the result by inheriting its method:
```
 public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        /* callback - no nothing */
    }
```
If you request permissions in other Class(for example in a widget), things will become complicated. Grantor handle permission in alone Activity and simplify the work, when user deny permission,  by default it's can show a dialog to explain why you need the permission, of course you can config it not to show the explaining dialog.
### How to use
* 1 add to module's dependencies.
```
dependencies {
      compile 'com.github.dfqin:grantor:2.3.1'
}
```
* 2 use in your code.
```
PermissionsUtil.requestPermission(Activity activity, PermissionListener listener, String[] permissions);
```
* 3 some demo.

```
private void requestCemera() {
        if (PermissionsUtil.hasPermission(this, Manifest.permission.CAMERA)) {
            Toast.makeText(this, "访问摄像头", Toast.LENGTH_LONG).show();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    Toast.makeText(MainActivity.this, "用户授予访问摄像头权限", Toast.LENGTH_LONG).show();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(MainActivity.this, "用户拒绝了访问摄像头", Toast.LENGTH_LONG).show();
                }
            }, new String[]{Manifest.permission.CAMERA});
        }
    }

private void requestContact() {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "我就是想看下你的通讯录", "不让看", "打开权限");

        if (PermissionsUtil.hasPermission(this, Manifest.permission.READ_CONTACTS)) {
            try {
                JSONArray arr = getContactInfo(this);
                Toast.makeText(this, arr.toString(), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    Toast.makeText(MainActivity.this, "用户授予读取通讯录权限", Toast.LENGTH_LONG).show();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(MainActivity.this, "用户拒绝了读取通讯录权限", Toast.LENGTH_LONG).show();
                }
            }, new String[]{Manifest.permission.READ_CONTACTS}, true, tip);
        }
    }

private void requestSms() {
        if (PermissionsUtil.hasPermission(this, Manifest.permission.READ_SMS)) {
            Toast.makeText(this, "读取消息", Toast.LENGTH_LONG).show();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    Toast.makeText(MainActivity.this, "用户授予读取消息权限", Toast.LENGTH_LONG).show();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Toast.makeText(MainActivity.this, "用户拒绝了读取消息权限", Toast.LENGTH_LONG).show();
                }
            }, new String[]{Manifest.permission.READ_SMS}, false, null);
        }
    }

```
* 4 the demo image.

![默认](https://github.com/dfqin/PermissionGrantor/blob/master/grant1.gif)


![授权拒绝时自定义Dialog](https://github.com/dfqin/PermissionGrantor/blob/master/grant2.gif)


![授权拒绝时不显示Dialog](https://github.com/dfqin/PermissionGrantor/blob/master/grant3.gif)

### License
[The MIT License (MIT)](http://opensource.org/licenses/MIT)
Copyright (c) 2017, dfqin
