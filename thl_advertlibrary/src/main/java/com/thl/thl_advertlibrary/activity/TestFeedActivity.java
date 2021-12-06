//package com.thl.thl_advertlibrary.activity;//package com.muzhenyi.signin;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.bytedance.sdk.openadsdk.AdSlot;
//import com.bytedance.sdk.openadsdk.TTAdNative;
//import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
//import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
//import com.thl.commonframe.base.BaseAppActivity;
//import com.thl.recycleviewutils.RecyclerDataHolder;
//import com.thl.recycleviewutils.RecyclerViewHolder;
//import com.thl.recycleviewutils.adapter.RecyclerAdapter;
//import com.thl.thl_advertlibrary.config.TTAdConfigManager;
//import com.thl.thl_advertlibrary.dialog.AdvertDislikeDialog;
//import com.thl.thl_advertlibrary.network.bean.AdvertModel;
//import com.thl.thl_advertlibrary.utils.AdvertUtils;
//import com.thl.thl_advertlibrary.utils.Fhad_DeviceUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
///**
// * 信息流广告
// * @author
// * @time 2020/7/31 14
// */
//public class TestFeedActivity extends BaseAppActivity {
//    RecyclerView recycler_view;
//    AppCompatActivity activity;
//    RecyclerAdapter adapter;
//    AdvertModel model;
//
//    private TTAdNative mTTAdNative;
//
//    @Override
//    public int thisLayoutResourceId() {
//        return R.layout.fragment_list_with_title;
//    }
//
//    @Override
//    public void initializeView() {
//        super.initPubTitleBar("测试信息流广告", true);
//        activity = this;
//        recycler_view = findViewById(R.id.recycler_view);
//        mTTAdNative = TTAdConfigManager.init(activity, "5056709").createAdNative(activity);
//        adapter = new RecyclerAdapter(activity);
//        recycler_view.setLayoutManager(new LinearLayoutManager(activity));
//        recycler_view.setAdapter(adapter);
//        model= AdvertUtils.searchFirstAdvertByLocation("123");
//        if (model==null){
//            model=new AdvertModel();
//            model.setAdvert_type(9);
//            model.setAdvert_param_0("5056709");
//            model.setAdvert_param_1("945362897");
//            model.setWidth(375);
//            model.setHeight(284);
//        }
//        recycler_view.post(new Runnable() {
//            @Override
//            public void run() {
//                float width=recycler_view.getWidth();
//                if (width==0){
//                    width=Fhad_DeviceUtil.Width(activity);
//                }
//                float screenWidth = width/ Fhad_DeviceUtil.Density(activity);  // 屏幕宽度(dp)
//                loadListAd(screenWidth);
//            }
//        });
//    }
//
//    /**
//     * 加载feed广告
//     */
//    private void loadListAd(float expressViewWidth) {
//        float expressViewHeight  =expressViewWidth * model.getHeight() / model.getWidth();
//        //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
//        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId("945362897")
//                .setSupportDeepLink(true)
//                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
//                .setAdCount(3) //请求广告数量为1到3条
//                .build();
//        //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
//        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
//            @Override
//            public void onError(int code, String message) {
//                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
//                if (ads == null || ads.isEmpty()) {
//                    Toast.makeText(activity, "on FeedAdLoaded: ad is null!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                List<RecyclerDataHolder> dataHolders = new ArrayList<>();
//                for (TTNativeExpressAd ad : ads) {
//                    dataHolders.add(new RecordHolder(ad));
//                }
//                adapter.setDataHolders(dataHolders);
//            }
//        });
//    }
//
//
//    /**
//     * 打卡日历
//     *
//     * @ Author     ：Azrael
//     * @ Date       ：Created in 5:15 PM 7/29/2019
//     */
//
//    public class RecordHolder extends RecyclerDataHolder<TTNativeExpressAd> {
//
//        public RecordHolder(TTNativeExpressAd data) {
//            super(data);
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(Context context, ViewGroup parent, int position) {
//            return new WaterView(createView(context, parent, R.layout.listitem_ad_native_express));
//        }
//
//        @Override
//        public void onBindViewHolder(Context context, int position, RecyclerView.ViewHolder vHolder, TTNativeExpressAd expressAd) {
//            WaterView holder = (WaterView) vHolder;
//            holder.frameLayout.removeAllViews();
//            bindAdListener(holder.frameLayout, expressAd);
//            bindDislike(expressAd, position);
//            bindDownloadListener(expressAd);
//        }
//    }
//
//    public static class WaterView extends RecyclerViewHolder {
//
//        RelativeLayout frameLayout;
//
//        WaterView(View view) {
//            super(view);
//            frameLayout = view.findViewById(R.id.rl_content);
//        }
//    }
//
//    /**
//     * 设置广告的不喜欢，注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
//     *
//     * @param ad
//     */
//    private void bindDislike(final TTNativeExpressAd ad, int postion) {
//        ad.setDislikeDialog(new AdvertDislikeDialog(activity)
//                .setOnDislikeItemClick(new AdvertDislikeDialog.OnDislikeItemClick() {
//                    @Override
//                    public void onItemClick() {
//                        adapter.removeDataHolder(postion);
//                    }
//                }));
//    }
//
//    private void bindAdListener(RelativeLayout frameLayout, TTNativeExpressAd ads) {
//        ads.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
//            @Override
//            public void onAdClicked(View view, int type) {
//                Toast.makeText(activity, "广告被点击", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdShow(View view, int type) {
//            }
//
//            @Override
//            public void onRenderFail(View view, String msg, int code) {
//            }
//
//            @Override
//            public void onRenderSuccess(View view, float width, float height) {
//                //返回view的宽高 单位 dp
//                frameLayout.addView(view);
//            }
//        });
//        ads.render();
//    }
//
//    private void bindDownloadListener(TTNativeExpressAd ad) {
//        TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
//            private boolean mHasShowDownloadActive = false;
//
//            @Override
//            public void onIdle() {
//                if (!isValid()) {
//                    return;
//                }
//                Toast.makeText(activity, "点击广告开始下载", Toast.LENGTH_SHORT).show();
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                if (!mHasShowDownloadActive) {
//                    mHasShowDownloadActive = true;
//                    Toast.makeText(activity, appName + " 下载中，点击暂停", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                Toast.makeText(activity, appName + " 下载暂停", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                Toast.makeText(activity, appName + " 下载失败，重新下载", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onInstalled(String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                Toast.makeText(activity, appName + " 安装完成，点击打开", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                if (!isValid()) {
//                    return;
//                }
//                Toast.makeText(activity, appName + " 下载成功，点击安装", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
//            private boolean isValid() {
//                return true;
//            }
//        };
//        //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
//        ad.setDownloadListener(downloadListener); // 注册下载监听器
//    }
//}
