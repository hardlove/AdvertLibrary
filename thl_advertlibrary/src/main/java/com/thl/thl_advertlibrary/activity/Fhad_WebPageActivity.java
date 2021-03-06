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
                    builder.setTitle("????????????");
                    builder.setMessage("?????????" + interceptModel.getApplication_name() + "??????????????????")
                            .setCancelable(true)
                            .setPositiveButton("?????????", null);
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
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//???????????????????????????????????????????????????????????????????????????WebView????????????
        }
        //????????????????????????????????????
        mWebSettings.setUseWideViewPort(true); //????????????????????????webview?????????
        mWebSettings.setLoadWithOverviewMode(true); // ????????????????????????
//????????????
        mWebSettings.setSupportZoom(false); //????????????????????????true??????????????????????????????
        mWebSettings.setBuiltInZoomControls(false); //????????????????????????????????????false?????????WebView????????????
        mWebSettings.setDisplayZoomControls(false); //???????????????????????????
        mWebSettings.setSupportMultipleWindows(false); //?????????????????????,??????????????????
//        ????????????
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//????????????js??????????????????
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//??????????????????
//??????????????????
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //??????webview?????????
        mWebSettings.setAllowContentAccess(false); //????????????????????????
        mWebSettings.setAllowFileAccess(true); //????????????????????????
        mWebSettings.setDomStorageEnabled(true);  //        ??????H5???????????????
        mWebSettings.setJavaScriptEnabled(true);//  ??? onStop ??? onResume ???????????? setJavaScriptEnabled() ???????????? false ??? true ????????????????????? html ??????JS ???????????????????????????????????????????????????CPU?????????????????? onStop ??? onResume ???????????? setJavaScriptEnabled() ???????????? false ??? true ??????
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //????????????JS???????????????
        mWebSettings.setLoadsImagesAutomatically(true); //????????????????????????
        mWebSettings.setDefaultTextEncodingName("utf-8");//??????????????????
        mWebSettings.setBlockNetworkImage(false);//        ??????????????????????????????

        //   ??????????????????
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setHorizontalScrollBarEnabled(false);
//  ????????????????????????
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(chromeClient);
        /**
         * ??????????????????
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
                                .setMessage("??????????????????????????????????????????????????????")
                                .setPositiveButton("????????????", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                        startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                                    }
                                }).setNegativeButton("??????", null).show();
                    }
                    return true;
                }
                if (!(url.startsWith("http") || url.startsWith("https"))) {
                    return true;
                }

                if (url.contains("https://wx.tenpay.com")) {
                    Map<String, String> extraHeaders = new HashMap<>();
                    String referer = mWebSite;
                    Lg.d("shouldOverrideUrlLoading???tenpay,website :---" + referer);
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
            builder.setTitle("????????????");
            builder.setMessage(origin + "??????????????????????????????????????????")
                    .setCancelable(true)
                    .setPositiveButton("??????", (dialog, id) -> callback.invoke(origin, true, remember))
                    .setNegativeButton("?????????", (dialog, id) -> callback.invoke(origin, false, remember));
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
     * ?????????????????????
     */
    private void takePhoto() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("??????");
        alertDialog.setOnCancelListener(new ReOnCancelListener());
        alertDialog.setItems(new CharSequence[]{"??????", "??????"},
                (dialog, which) -> {
                    if (which == 0) {
                        File fileUri = new File(Environment.getExternalStorageDirectory().getPath()
                                + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = DownloadProvider.getUriForFile(mContext,
                                    DownloadProvider.getAuthor(mContext), fileUri);//??????FileProvider????????????content?????????Uri
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
     * ?????????????????????
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
