package com.thl.thl_advertlibrary.permissions;

import java.io.Serializable;
import java.util.List;


public abstract class RequestPermissionListener implements Serializable {
    public abstract void onSuccess();

    public void onFailed(List<String> grantedPermissions) {
    }
}