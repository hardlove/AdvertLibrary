package com.thl.thl_advertlibrary.downloadhelper;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.thl.thl_advertlibrary.utils.Lg;
import com.thl.thl_advertlibrary.utils.ToastUtils;

//import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 系统下载
 *
 * @author ${dell}
 * @time 2019/9/6 16
 */
public class DownloadHelper {

    private Activity context;
    private String url;
    private String folderName;
    private String mds;
    private String mimeType;
    private String contentDisposition;

    private boolean showNotification;

    public DownloadHelper(Activity context) {
        this.context = context;
        folderName = Environment.DIRECTORY_DOWNLOADS;
    }

    /**
     * 开始下载
     *
     */
    public void start() {
        String filePath = Environment.getExternalStoragePublicDirectory(folderName) + File.separator + url.substring(url.lastIndexOf("/") + 1);
        File file = new File(filePath);
        if (file.exists() && checkFileMd5(filePath, mds)) {
            file.delete();
        }
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 表示下载进行中和下载完成的通知栏是否显示。默认只显示下载中通知。VISIBILITY_VISIBLE_NOTIFY_COMPLETED表示下载完成后显示通知栏提示。
        // VISIBILITY_HIDDEN表示不显示任何通知栏提示，这个需要在AndroidMainfest中添加权限android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
        if (showNotification) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        } else {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        if (!TextUtils.isEmpty(mimeType)){
            request.setMimeType(mimeType);
        }
        request.setTitle(file.getName());
        request.setDestinationInExternalPublicDir(folderName, file.getName());
        downloadManager.enqueue(request);
    }
    /**
     * 自研下载处理系统下载不能下载的部分问题
     */
    public void startDownloadByApp(View view) {
        new Thread(() -> {
            startDownloadFormHttp(view);
        }).start();
    }

    private void startDownloadFormHttp(View view) {
        String fileName = getRealNameAndsuffix();
//      https://blog.csdn.net/lifs419/article/details/102856135
        DownloadMgr.get().download(url, folderName, fileName, new DownloadMgr.OnDownloadListener() {
            @Override
            public void onDownloadStart(String fileName) {
                context.runOnUiThread(() -> {
                    ToastUtils.show(context.getApplicationContext(), "文件开始下载");
                });
            }

            @Override
            public void onDownloadSuccess(File file) {
                Lg.d("download onDownloadSuccess :" + file.getAbsolutePath());
                notifyMediaScanner(file.getParent());
                context.runOnUiThread(() -> {
//                    ToastUtils.show(context.getApplicationContext(), "文件 " + fileName + "下载成功");
                    ShowSnackbarAndJump(view, file);
                });
            }

            @Override
            public void onDownloading(int progress) {
//                Lg.d("download onDownloading :" + progress);
                context.runOnUiThread(() -> {

                });
            }

            @Override
            public void onDownloadFailed(Exception e) {
                e.printStackTrace();
                Lg.e("download onDownloadFailed :" + e.getMessage());
                context.runOnUiThread(() -> {
                    ToastUtils.show(context.getApplicationContext(), "文件下载失败:"+e.getMessage());
                });
            }

        });
    }

    @NotNull
    private String getRealNameAndsuffix() {
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Lg.d("download fileName :" + fileName + " ; mimeType :" + mimeType);
        fileName = checkSuffix(fileName, url);
        if (fileName.contains(".bin")) {
            if (url.lastIndexOf("?") != -1) {
                fileName = checkSuffixFromUrl(fileName, url);
            }
        }
        return fileName;
    }

    @NotNull
    private String checkSuffix(String fileName, String url) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        Lg.d("download origin_suffix :" + suffix);
        if (!MimeTypeMap.getSingleton().hasExtension(suffix)) {
            suffix = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            Lg.d("download mime_suffix :" + suffix);
            if (!TextUtils.isEmpty(suffix)) {
                fileName = fileName.substring(0,fileName.lastIndexOf("."));
                fileName += "." + suffix;
            }
        } else {
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
            Lg.d("download suffix_mime :" + mime);
        }
        return fileName;
    }

    @NotNull
    private String checkSuffixFromUrl(String fileName, String url) {
        String subUrl = url.substring(0, url.lastIndexOf("?"));
        String suffix = subUrl.substring(subUrl.lastIndexOf(".") + 1);
        if (MimeTypeMap.getSingleton().hasExtension(suffix)) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
            Lg.d("download ,checkSuffixFromUrl , suffix :" + suffix);
            Lg.d("download ,checkSuffixFromUrl , mimeType :" + mimeType);
            if (!TextUtils.isEmpty(suffix)) {
                fileName = fileName.substring(0,fileName.lastIndexOf("."));
                fileName += "." + suffix;
            }

        } else {

        }
        Lg.d("download ,checkSuffixFromUrl , fileName :" + fileName);
        return fileName;
    }

    private void ShowSnackbarAndJump(View view, File file) {
        Snackbar sb = Snackbar.make(view, "文件已保存到目录 /手机/Download/", Snackbar.LENGTH_INDEFINITE); // LENGTH_INDEFINITE

        sb.setAction("立即查看", v -> {
//               android.os.FileUriExposedException: file:///storage/emulated/0/Download/qrnDNeD7uuyxv1T7S8GJxcMH9FJm59mg.pptx exposed beyond app through Intent.getData()
//            openAssignFolder(file);
//            tryOpenDocuments(file);
            tryOpenFileWithInstalledApk(file);
        });
        sb.setActionTextColor(Color.GREEN);
        sb.show();
    }

    private void tryOpenFileWithInstalledApk(File file){
        if (null == file || !file.exists()) {
            ToastUtils.show(context.getApplicationContext(), "文件不存在");
            return;
        }

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = DownloadProvider.getUriForFile(context, context.getPackageName() + ".download.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, type);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.show(context.getApplicationContext(), "文件打开失败，请检查是否安装相应APP");
            e.printStackTrace();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param file
     */
    private String getMIMEType(File file) {
        String type="*/*";
        String fName = file.getName();
//获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名 */
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
//在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * 跳转到文件支持的界面
     *
     * @param file
     */
    private void openAssignFolder(File file) {
        if (null == file || !file.exists()) {
            ToastUtils.show(context.getApplicationContext(), "文件不存在");
            return;
        }
        File DownloadDir = file.getParentFile();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
//        intent.setType(mimeType);//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        https://blog.csdn.net/bzlj2912009596/article/details/80994628
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = DownloadProvider.getUriForFile(context, context.getPackageName() + ".download.fileprovider", DownloadDir);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(DownloadDir);
        }
//        intent.setDataAndType(uri, "file/*");
//        intent.setDataAndType(uri, mimeType);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtils.show(context.getApplicationContext(), "文件打开异常");
            e.printStackTrace();
        }
    }



    private void tryOpenDocuments(File file){
        if (null == file || !file.exists()) {
            ToastUtils.show(context.getApplicationContext(), "文件不存在");
            return;
        }
        File DownloadDir = file.getParentFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            context.startActivity(intent);
        }
//        context.startActivityForResult(intent, 1);
    }

    /**
     * 修改snackbar背景颜色 以及title字体颜色
     *
     * @param snackbar
     * @param messageColor
     * @param backgroundColor
     */
//    public static void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) {
//        View view = snackbar.getView();//获取Snackbar的view
//        if (view != null) {
//            view.setBackgroundColor(backgroundColor);//修改view的背景色
//            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);//获取Snackbar的message控件，修改字体颜色
//        }
//    }


    private void notifyMediaScanner(String dirName) {
        Lg.d("download ,notifyMediaScanner dir path :" + dirName);
        MediaScannerConnection.scanFile(context, new String[]{dirName}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Lg.d("download main path :" + path);
            }
        });
    }


    /**
     * 调用系统的下载管理器下载，android 10 版本的下载有点问题。
     */
    public void startDownloadBySystem() {
//        trygetRealName(url);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Lg.d("download fileName :" + fileName);
        String filePath = Environment.getExternalStoragePublicDirectory(folderName) + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists() && checkFileMd5(filePath, mds)) {
            Lg.d("delete filePath :" + filePath);
            file.delete();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 表示下载进行中和下载完成的通知栏是否显示。默认只显示下载中通知。VISIBILITY_VISIBLE_NOTIFY_COMPLETED表示下载完成后显示通知栏提示。
        // VISIBILITY_HIDDEN表示不显示任何通知栏提示，这个需要在AndroidMainfest中添加权限android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
        if (showNotification) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        } else {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();

        if (!TextUtils.isEmpty(mimeType)) {
            mimeType = "application/msword";
            request.setMimeType(mimeType);
        }
        Lg.d("download mimeType :" + mimeType);
        Lg.d("download fileName :" + file.getName());
        Lg.d("download fileName :" + fileName);
        // 设置下载文件保存的路径和文件名
        request.setTitle(fileName);
        request.setDescription("正在下载：" + fileName);
//        request.setDestinationInExternalPublicDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),fileName);
        request.setDestinationInExternalPublicDir(folderName, fileName);
        DownloadManager downloadManager = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);
        Lg.d("download downloadId :" + downloadId);
    }

    public DownloadHelper setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
        return this;
    }

    public DownloadHelper setUrl(String url) {
        this.url = url;
        return this;
    }

    public DownloadHelper setFolderName(String folderName) {
        this.folderName = folderName;
        return this;
    }

    public DownloadHelper setMds(String mds) {
        this.mds = mds;
        return this;
    }

    public DownloadHelper setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public DownloadHelper setShowNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    public void trygetRealName(final String url) {
        new Thread(() -> {
            getFileName(url);
        }).start();
    }

    public String getFileName(String url) {
        String fileName = null;
        if (!TextUtils.isEmpty(url)) {
            try {
                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                Request request = new Request.Builder()
                        .url(url)//请求接口。如果需要传参拼接到接口后面。
                        .build();//创建Request 对象
                Response response = client.newCall(request).execute();//得到Response 对象

                Lg.d("headers :" + response.headers());
                HttpUrl realUrl = response.request().url();
                Lg.d("real:" + realUrl);
                if (realUrl != null) {
                    String temp = realUrl.toString();
                    fileName = temp.substring(temp.lastIndexOf("/") + 1);

                }
            } catch (IOException e) {
                e.printStackTrace();
                Lg.e("Get File Name:error : " + e);
            }
        }
        Lg.d("fileName--->" + fileName);
        return fileName;
    }

    private boolean checkFileMd5(String path, String md5) {
        if (TextUtils.isEmpty(md5)) {
            return true;
        }
        MessageDigest digest;
        FileInputStream in;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(path);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
            BigInteger bigInt = new BigInteger(1, digest.digest());
            String result = bigInt.toString(16);
            if (TextUtils.equals(md5, result)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private final String[][] MIME_MapTable={
            //{后缀名， MIME类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",    "image/bmp"},
            {".c",  "text/plain"},
            {".class",  "application/octet-stream"},
            {".conf",   "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls",    "application/vnd.ms-excel"},
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",   "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h",  "text/plain"},
            {".htm",    "text/html"},
            {".html",   "text/html"},
            {".jar",    "application/java-archive"},
            {".java",   "text/plain"},
            {".jpeg",   "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",   "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",   "video/mp4"},
            {".mpga",   "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop",   "text/plain"},
            {".rc", "text/plain"},
            {".rmvb",   "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh", "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",  "application/x-compress"},
            {".zip",    "application/x-zip-compressed"},
            {"",        "*/*"}
    };
}
