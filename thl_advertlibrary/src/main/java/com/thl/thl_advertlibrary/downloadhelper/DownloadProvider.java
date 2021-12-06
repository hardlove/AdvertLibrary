package com.thl.thl_advertlibrary.downloadhelper;

import android.content.Context;

import androidx.core.content.FileProvider;

/**
 * @author ${dell}
 * @time 2019/9/10 15
 */
public class DownloadProvider extends FileProvider {


    public static String getAuthor(Context mContext) {

        return mContext.getPackageName() + ".downloadhelper.fileprovider";
    }


}
