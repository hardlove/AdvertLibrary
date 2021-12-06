package com.thl.thl_advertlibrary.permissions;


import androidx.fragment.app.FragmentManager;

/**
 * @author dell
 * @date 2019/5/9
 * @time 14:51
 **/
public class PermissionHelper {

    public static void requestPermission(FragmentManager fragmentManager,
                                         RequestPermissionListener listener,
                                         String... permissions) {
        if (permissions==null||permissions.length == 0) {
            return;
        }
        PermissionDialogFragment.show(fragmentManager, permissions, listener);
    }

}
