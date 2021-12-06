package com.thl.thl_advertlibrary.permissions;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * 水印选择
 *
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class PermissionDialogFragment extends DialogFragment {
    public static final String TAG = PermissionDialogFragment.class.getSimpleName();

    public static final String KEY_PERMISSION_ARRAY = "key_permission_array";
    public static final String KEY_LISTENER = "key_listener";

    String[] permissions;
    int requestCode;
    RequestPermissionListener listener;

    //Show dialog with provide text and text color
    public static PermissionDialogFragment show(FragmentManager fragmentManager,
                                                String[] permissions,
                                                RequestPermissionListener listener) {
        Bundle args = new Bundle();
        args.putStringArray(KEY_PERMISSION_ARRAY, permissions);
        args.putSerializable(KEY_LISTENER, listener);
        PermissionDialogFragment fragment = new PermissionDialogFragment();
        fragment.setArguments(args);
        fragment.show(fragmentManager, TAG);
        return fragment;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = new RelativeLayout(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(2, 2);
        rootView.setLayoutParams(params);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        permissions = getArguments().getStringArray(KEY_PERMISSION_ARRAY);
        requestCode = (int)Math.random()*20;
        listener = (RequestPermissionListener) getArguments().getSerializable(KEY_LISTENER);
        requestPermission();
    }

    protected void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, requestCode);
                    return;
                }
            }
        }
        listener.onSuccess();
        listener = null;
        dismissAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> grantedPermissions=null;
        if (this.requestCode == requestCode) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    if (grantedPermissions==null){
                        grantedPermissions=new ArrayList<>();
                    }
                    grantedPermissions.add(permission);
                }
            }
        }
        if (grantedPermissions==null||grantedPermissions.size()==0){
            listener.onSuccess();
        }else {
            listener.onFailed(grantedPermissions);
        }
        listener = null;
        dismissAllowingStateLoss();
    }
}
