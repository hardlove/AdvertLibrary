package com.thl.thl_advertlibrary.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Browser;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thl.thl_advertlibrary.R;
import com.thl.thl_advertlibrary.downloadhelper.DownloadHelper;
import com.thl.thl_advertlibrary.downloadhelper.DownloadProvider;
import com.thl.thl_advertlibrary.network.bean.UrlInterceptModel;
import com.thl.thl_advertlibrary.permissions.PermissionHelper;
import com.thl.thl_advertlibrary.permissions.RequestPermissionListener;
import com.thl.thl_advertlibrary.utils.Fhad_DeviceUtil;
import com.thl.thl_advertlibrary.utils.Fhad_PackageUtil;
import com.thl.thl_advertlibrary.utils.Lg;
import com.thl.thl_advertlibrary.utils.WebH5Init;
import com.thl.thl_advertlibrary.utils.WebUrlFilter;

import org.litepal.LitePal;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


/**
 * @author dell
 * @date 2019/2/21
 * @time 16:33
 **/
public class Fhad_WebPageActivity extends AppCompatActivity {

    public static final String TAG_TITLE = "html_title";
    public static final String PAY_SITE = "pay_site";

    public WebView mWebView;
    public TextView titleView;
    private ProgressBar progressBar;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    private Activity mContext;

    private final int PHOTO_REQUEST = 0x01;
    private Uri imageUri;
    private String mTitle = null;
    private String mWebSite= null;
    private LinearLayout rlToolbar;

    public static void openActivity(Context context, String url, String title) {
        Intent it = new Intent(context, Fhad_WebPageActivity.class);
        it.setData(TextUtils.isEmpty(url) ? null : Uri.parse(url));
        it.putExtra(Fhad_WebPageActivity.TAG_TITLE, title);
    }
    public static void openActivity(Context context, String url, String title,String paySite) {
        Intent it = new Intent(context, Fhad_WebPageActivity.class);
        it.setData(TextUtils.isEmpty(url) ? null : Uri.parse(url));
        it.putExtra(Fhad_WebPageActivity.TAG_TITLE, title);
        it.putExtra(Fhad_WebPageActivity.PAY_SITE, paySite);
        context.startActivity(it);
    }

    public static void openUrlByBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(TextUtils.isEmpty(url) ? null : Uri.parse(url));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fhad_activity_web);
        mContext = this;

        initView();
        initParams(getIntent());
    }

    public boolean interceptUrl(String url) {
        List<UrlInterceptModel> list = LitePal.findAll(UrlInterceptModel.class);
        UrlInterceptModel interceptModel = null;
        if (list != null) {
            for (UrlInterceptModel model : list) {
                if (model.getFragment_type() == 0 && url.startsWith(model.getUrl_fragment())) {
                    interceptModel = model;
                } else if (model.getFragment_type() == 1 && url.contains(model.getUrl_fragment())) {
                    interceptModel = model;
                }
            }
        }
        if (interceptModel != null) {
            if (interceptModel.getAfter_operation() == 1) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception ignored) {
                    ignored.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(Fhad_WebPageActivity.this);
                    builder.setTitle("温馨提示");
                    builder.setMessage("请安装" + interceptModel.getApplication_name() + "应用后重试！")
                            .setCancelable(true)
                            .setPositiveButton("知道了", null);
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }
            return true;
        }

        return false;
    }

    public void initView() {
        findViewById(R.id.fhad_title_back).setOnClickListener(v -> finish());
        rlToolbar = findViewById(R.id.rl_toolbar);
        titleView = findViewById(R.id.fhad_title_title);
        mWebView = findViewById(R.id.fhad_wv_show);
        progressBar = findViewById(R.id.fhad_progress_bar);

        WebSettings mWebSettings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为，
        }
        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//缩放操作
        mWebSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        mWebSettings.setSupportMultipleWindows(false); //设置同一个界面,不支持多窗口
//        渲染相关
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新的窗口
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染等级
//其他细节操作
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        mWebSettings.setAllowContentAccess(false); //设置可以访问文件
        mWebSettings.setAllowFileAccess(true); //设置可以访问文件
        mWebSettings.setDomStorageEnabled(true);  //        支持H5的本地存储
        mWebSettings.setJavaScriptEnabled(true);//  在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可。若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）。在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        mWebSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setBlockNetworkImage(false);//        解除阻止图片网络数据

        //   禁止左右滑动
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setHorizontalScrollBarEnabled(false);
//  防止滑动空白处理
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(chromeClient);
        /**
         * 调用系统下载
         */
        mWebView.setDownloadListener((url, userAgent, contentDisposition,mimetype, contentLength) -> 
//                PermissionHelper.requestPermission(Fhad_WebPageActivity.this.getSupportFragmentManager(),
//                new RequestPermissionListener() {
//                    @Override
//                    public void onSuccess() {
//                        new DownloadHelper(Fhad_WebPageActivity.this)
//                                .setUrl(url)
//                                .setMimeType(mimetype)
//                                .setShowNotification(true)
//                                .start();
//                    }
//
//                }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE));
        {
            PermissionHelper.requestPermission(Fhad_WebPageActivity.this.getSupportFragmentManager(), new RequestPermissionListener() {
                @Override
                public void onSuccess() {
                    try {
                        new DownloadHelper(Fhad_WebPageActivity.this)
                                .setUrl(url)
                                .setMimeType(mimetype)
                                .setContentDisposition(contentDisposition)
                                .setShowNotification(true)
                                .startDownloadByApp(progressBar);
                    } catch (Exception e) {
//                        Lg.e("download,error:" + e.getMessage());
                    }
                }

            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Lg.d("shouldOverrideUrlLoading :---" + url);
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    } catch (Exception e) {
                        new AlertDialog.Builder(Fhad_WebPageActivity.this)
                                .setMessage("未检测到支付宝客户端，请安装后重试。")
                                .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                        startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                                    }
                                }).setNegativeButton("取消", null).show();
                    }
                    return true;
                }
                if (!(url.startsWith("http") || url.startsWith("https"))) {
                    return true;
                }

                if (url.contains("https://wx.tenpay.com")) {
                    Map<String, String> extraHeaders = new HashMap<>();
                    String referer = mWebSite;
                    Lg.d("shouldOverrideUrlLoading，tenpay,website :---" + referer);
                    extraHeaders.put("Referer", referer);
                    view.loadUrl(url, extraHeaders);
//                    referer = url;
                    return true;
                }
                return interceptUrl(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                Lg.d("onPageStarted :---" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                Lg.d("onPageFinished :---" + url);

                injectJavaScirptMethod(view, url);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (errorCode == -10 && (Uri.parse(failingUrl).getScheme().equalsIgnoreCase("taobao"))) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(failingUrl));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                        startActivity(intent);
                    } catch (ActivityNotFoundException ignored) {
                    }
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
    }
    private void injectJavaScirptMethod(WebView view, String url) {
        if (WebUrlFilter.get().isShouldPayUrl(url)) {
            String mUserId = Fhad_DeviceUtil.getDeviceId(this);
            if (!TextUtils.isEmpty(WebH5Init.mChannel) && !TextUtils.isEmpty(mUserId)) {
                injectJsUserInfo(view, "getUserInfoFromAndroid", WebH5Init.mPkg, mUserId, WebH5Init.mChannel);
            }
        }
    }

    private void injectJsUserInfo(WebView view, String methodName, String packageName, String userId, String channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.evaluateJavascript("javascript:" + methodName + "('" + packageName + "','" + userId + "','" + channel + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Lg.e("onReceiveValue :---" + value);
                }
            });
        } else {
            view.loadUrl("javascript:" + methodName + "('" + userId + "','" + channel + "')");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initParams(getIntent());
    }

    public void initParams(Intent intent) {
        mTitle = intent.getStringExtra(TAG_TITLE);
        mWebSite = intent.getStringExtra(PAY_SITE);
        if (TextUtils.isEmpty(mWebSite)) {
            rlToolbar.setVisibility(View.VISIBLE);
        } else {
            rlToolbar.setVisibility(View.GONE);
        }
        titleView.setText(mTitle);
        if (intent.getData() == null) {
            finish();
        } else {
            mWebView.loadUrl(intent.getData().toString());
        }
    }

    private WebChromeClient chromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, final String title) {
            if (Patterns.WEB_URL.matcher(title).matches()) {
                return;
            }
            titleView.setText(title);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            final boolean remember = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(Fhad_WebPageActivity.this);
            builder.setTitle("位置信息");
            builder.setMessage(origin + "允许获取您的地理位置信息吗？")
                    .setCancelable(true)
                    .setPositiveButton("允许", (dialog, id) -> callback.invoke(origin, true, remember))
                    .setNegativeButton("不允许", (dialog, id) -> callback.invoke(origin, false, remember));
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;


            PermissionHelper.requestPermission(Fhad_WebPageActivity.this.getSupportFragmentManager(),
                    new RequestPermissionListener() {
                        @Override
                        public void onSuccess() {
                            takePhoto();
                        }
                    }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE);

            return true;

        }


    };


    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);

            mWebView.destroy();
            mWebView = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        finish();
    }


    /**
     * 拍照或选择相册
     */
    private void takePhoto() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("选择");
        alertDialog.setOnCancelListener(new ReOnCancelListener());
        alertDialog.setItems(new CharSequence[]{"相机", "相册"},
                (dialog, which) -> {
                    if (which == 0) {
                        File fileUri = new File(Environment.getExternalStorageDirectory().getPath()
                                + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = DownloadProvider.getUriForFile(mContext,
                                    DownloadProvider.getAuthor(mContext), fileUri);//通过FileProvider创建一个content类型的Uri
                        } else {
                            imageUri = Uri.fromFile(fileUri);
                        }
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, PHOTO_REQUEST);
                    } else {
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");
                        mContext.startActivityForResult(Intent.createChooser(i, "File Browser"),
                                PHOTO_REQUEST);
                    }
                });
        alertDialog.show();
    }


    /**
     * 点击取消的回调
     */
    private class ReOnCancelListener implements DialogInterface.OnCancelListener {

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL.onReceiveValue(null);
                mUploadCallbackAboveL = null;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != PHOTO_REQUEST || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
    }

}
