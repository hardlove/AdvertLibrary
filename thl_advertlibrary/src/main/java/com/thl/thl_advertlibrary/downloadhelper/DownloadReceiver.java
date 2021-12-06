package com.thl.thl_advertlibrary.downloadhelper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        File file = getFileByDownloadId(manager, id);
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            //在广播中取出下载任务的id
            if (file != null && file.exists()) {
                if (!install(context, file)) {//非安卓安装包格式
                    scanFile(context, file);
                }
            }
        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
            long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            //点击通知栏取消下载
            manager.remove(ids);
            Toast.makeText(context, "已经取消下载", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 通过下载id获取文件
     *
     * @param manager
     * @param downloadId
     * @return
     */
    private File getFileByDownloadId(DownloadManager manager, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        //在广播中取出下载任务的id
        query.setFilterById(downloadId);
        Cursor c = manager.query(query);
        if (c.moveToFirst()) {
            //获取文件下载路径
            String filePath = "";
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                int fileUriIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String fileUri = c.getString(fileUriIdx);
                if (fileUri != null) {
                    filePath = Uri.parse(fileUri).getPath();
                }
            } else {
                int fileNameIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                filePath = c.getString(fileNameIdx);
            }
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * 安装apk
     *
     * @param context
     * @param mSaveFile
     */
    private boolean install(Context context, File mSaveFile) {
        String apkMimeType = "application/vnd.android.package-archive";
        String fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(mSaveFile.getAbsolutePath()));//获取文件的mimeType
        if (!TextUtils.equals(fileMimeType, apkMimeType)) {//非apk文件
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        //判断版本是否是 7.0 及 7.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = DownloadProvider.getUriForFile(context, DownloadProvider.getAuthor(context), mSaveFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(mSaveFile);
        }
        intent.setDataAndType(apkUri, apkMimeType);
        context.startActivity(intent);
        return true;
    }


    /**
     * @param context 上下文
     * @param file    文件
     */
    private static void scanFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);
        }
    }

}