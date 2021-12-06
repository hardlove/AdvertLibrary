package com.thl.thl_advertlibrary.downloadhelper;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.thl.thl_advertlibrary.utils.Lg;
import com.thl.thl_advertlibrary.utils.SSLSocketClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: Lenovo
 * created on: 2020/4/21 11:24
 * description:
 */
public class DownloadMgr {

    private static DownloadMgr downloadMgr;
    private final OkHttpClient okHttpClient;

    public static DownloadMgr get() {
        if (downloadMgr == null) {
            downloadMgr = new DownloadMgr();
        }
        return downloadMgr;
    }

    private DownloadMgr() {
//        okHttpClient = new OkHttpClient();
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60,TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .build();
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final String fileName, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        listener.onDownloadStart(fileName);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String downloadDir = checkDownloadDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file;
                    if (TextUtils.isEmpty(fileName)) {
                        file = new File(downloadDir, getNameFromUrl(url));
                    } else {
                        file = new File(downloadDir, fileName);
                    }
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed(e);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        listener.onDownloadFailed(e);
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        listener.onDownloadFailed(e);
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String checkDownloadDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = Environment.getExternalStoragePublicDirectory(saveDir);
        if (!downloadFile.exists()) {
            downloadFile.mkdirs();
        }
        String downloadDir = downloadFile.getAbsolutePath();
        Lg.d("download,dir :" + downloadDir);
        return downloadDir;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {

        void onDownloadStart(String fileName);
        /**
         * 下载成功
         */
        void onDownloadSuccess(File file);

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed(Exception e);
    }
}
