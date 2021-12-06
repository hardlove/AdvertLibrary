#  方和广告封装
## 1、配置初始化，在application中加入代码
  ```
    AdvertConfig.GENERAL_HOST_BUSS=BuildConfig.GENERAL_HOST_BUSS;//广告控制域名
    AdvertConfig.appName=getResources().getString(R.string.app_name);
    AdvertConfig.initAdvert(this,"");//如果其他地方也使用了litepal数据库，请把数据库名字传进去
  ```

## 2、启动页/激活页面广告继承Fhad_BaseSplashActivity：

```java
    public class SplashActivity extends Fhad_BaseSplashActivity {
    
        @Override
        public void initView() {
            setContentView(R.layout.activity_splash);
            rl_content = findViewById(R.id.rl_content);
            bt_confirm = findViewById(R.id.bt_confirm);
        }
    
        @Override
        public int numberUsed() {
            return PreferenceConfig.getKeyValue(Constant.NUMBER_OF_USE, Integer.class);
        }
    
        @Override
        public void skip() {
        
        // 替换成自己的跳过
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }
    
        @Override
        public void initSpannableString(TextView textView, String textString) {
            SpannableString spannableString = new SpannableString(textString);
            spannableString.setSpan(new ComplexClickText(this, AppConfig.url_agreement, "用户协议"),
                    textString.indexOf("《用户协议》"), textString.indexOf("《用户协议》") + 6,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ComplexClickText(this, AppConfig.url_private, "隐私政策"),
                    textString.indexOf("《隐私政策》"), textString.indexOf("《隐私政策》") + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
```
## 3、布局文件：activity_splash:

```

<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">

    <FrameLayout
        android:id="@id/fl_content"
        android:layout_width="wrap_content"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="@dimen/x67"
        android:layout_height="wrap_content">

        <androidx.cardview.widget.CardView
            android:layout_width="wrap_content"
            android:layout_centerHorizontal="true"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            app:cardCornerRadius="@dimen/x20"
            android:layout_height="wrap_content">

            <LinearLayout
                android:layout_width="wrap_content"
                android:background="@android:color/white"
                android:orientation="vertical"
                android:layout_height="wrap_content">

                <RelativeLayout
                    android:id="@id/rl_content"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content">

                </RelativeLayout>

                <LinearLayout
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:orientation="horizontal">
                    <Button
                        android:id="@id/bt_cancel"
                        android:layout_width="@dimen/x186"
                        android:layout_height="@dimen/x57"
                        android:layout_marginBottom="@dimen/x47"
                        android:background="@drawable/fhad_shape_gray_50"
                        android:textColor="@android:color/white"
                        android:textSize="@dimen/x20"
                        android:text="忍痛退出"/>
                    <Space
                        android:layout_width="@dimen/x64"
                        android:layout_height="0px" />
                    <Button
                        android:id="@id/bt_confirm"
                        android:layout_width="@dimen/x186"
                        android:layout_height="@dimen/x57"
                        android:layout_marginBottom="@dimen/x47"
                        android:background="@drawable/fhad_shape_orange_50"
                        android:textColor="@android:color/white"
                        android:textSize="@dimen/x20"
                        android:text="继续查看"/>
                </LinearLayout>
            </LinearLayout>
        </androidx.cardview.widget.CardView>
    </FrameLayout>

    <RelativeLayout
        android:id="@+id/rl_content1"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/x134" />
</RelativeLayout>

```

## 4、退出广告弹窗继承BaseClosedAdvertDialog；

 ```java
    public class ClosedAdvertDialog extends BaseClosedAdvertDialog {
    
        public ClosedAdvertDialog(Activity activity) {
            super(activity);
        }
        
        @Override
        public void initView() {
            setContentView(R.layout.layout_express_interaction_ad);
            rl_content = findViewById(R.id.rl_content);
            fl_content = findViewById(R.id.fl_content);
            rl_content1 = findViewById(R.id.rl_content1);
            bt_cancel = findViewById(R.id.bt_cancel);
            bt_confirm = findViewById(R.id.bt_confirm);
        }
    
    } 
 ```
## 5、布局文件：layout_express_interaction_ad

 ```
 
 <?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="@dimen/x534"
    android:layout_height="wrap_content">

    <FrameLayout
        android:id="@+id/fl_content"
        android:layout_width="match_parent"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="@dimen/x67"
        android:layout_height="wrap_content">

        <androidx.cardview.widget.CardView
            android:layout_width="match_parent"
            android:layout_centerHorizontal="true"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            app:cardCornerRadius="@dimen/x20"
            android:layout_height="wrap_content">

            <LinearLayout
                android:layout_width="match_parent"
                android:background="@android:color/white"
                android:orientation="vertical"
                android:layout_height="wrap_content">

                <RelativeLayout
                    android:id="@+id/rl_content"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content">

                </RelativeLayout>

                <LinearLayout
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:orientation="horizontal">
                    <Button
                        android:id="@+id/bt_cancel"
                        android:layout_width="@dimen/x186"
                        android:layout_height="@dimen/x57"
                        android:layout_marginBottom="@dimen/x47"
                        android:background="@drawable/shape_gray_50"
                        android:textColor="@android:color/white"
                        android:textSize="@dimen/x20"
                        android:text="忍痛退出"/>
                    <Space
                        android:layout_width="@dimen/x64"
                        android:layout_height="0px" />
                    <Button
                        android:id="@+id/bt_confirm"
                        android:layout_width="@dimen/x186"
                        android:layout_height="@dimen/x57"
                        android:layout_marginBottom="@dimen/x47"
                        android:background="@drawable/shape_orange_50"
                        android:textColor="@android:color/white"
                        android:textSize="@dimen/x20"
                        android:text="继续查看"/>
                </LinearLayout>
            </LinearLayout>
        </androidx.cardview.widget.CardView>
    </FrameLayout>

    <RelativeLayout
        android:id="@+id/rl_content1"
        android:layout_width="match_parent"
        android:layout_height="@dimen/x134" />
</RelativeLayout>

 ```

## 6、支付取消插屏广告:InteractionAdvertDialog;
 ```
 
    InteractionAdvertDialog advertDialog = new InteractionAdvertDialog(this);
    advertDialog.setCallback(new InteractionAdvertDialog.OnDialogCallback() {
            @Override
            public void onDialogDismiss() {
                finish();
            }
        });
    advertDialog.show();
    
 ```

## 7、更新广告：

1.   在常驻页面调用，用于更新广告

 ```
    Fhad_HttpMethodUtils.updateAdvert(this); 
    
 ```

## 8、展示banner广告；

1. 如果使用的是Xbanner，在banner显示页面，添加

```java
   public class BannerImageInfo extends SimpleBannerInfo {

       private AdvertModel bannerRes;
   
       public BannerImageInfo(AdvertModel bannerRes) {
           this.bannerRes = bannerRes;
       }
   
       @Override
       public Object getXBannerUrl() {
           return bannerRes;
       }}
```
2.  调用

```
    /*
     * 处理banner广告
     *
     */
    public  void showBannerAdvert() {
        List<AdvertModel> list = AdvertUtils.searchAdvertByLocation("banner");
        List<BaseBannerInfo> advertList = new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (AdvertModel model : list) {
                advertList.add(new BannerImageInfo(model));
                AdvertUtils.showAdvertRecord(getActivity(),model);
            }
        }else {
            advertList.add(new LocalImageInfo(R.drawable.img_tu));
        }
        mXBanner.setBannerData(R.layout.fhad_item_banner, advertList);
        mXBanner.loadImage((banner, object, view, position) -> {
            RelativeLayout rl_content=(RelativeLayout)view;
            rl_content.removeAllViews();
            if (object instanceof BannerImageInfo) {
                AdvertModel model =(AdvertModel) ((BannerImageInfo) object).getXBannerUrl();
                helper. showAdvertModelItem(getActivity(),rl_content, model);
            }else if (object instanceof LocalImageInfo) {
                ImageView imageView = new ImageView(getActivity());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                rl_content.addView(imageView, 0,params);
                int advertId = ((LocalImageInfo) object).getXBannerUrl();
                Glide.with(view.getContext()).load(advertId).into(imageView);
            }
        });
        mXBanner.setOnItemClickListener((banner, object, view, position) -> {
            if (object instanceof BannerImageInfo) {
                AdvertModel advertModel = (AdvertModel) ((BannerImageInfo) object).getXBannerUrl();
                if (advertModel.getAdvert_type()!=4){
                    AdvertUtils.clickAdvert(getActivity(), advertModel);
                    return;
                }
            }
            SizeChooseActivity.actionStart((BaseActivity)getActivity(), "");
        });

        mXBanner.startAutoPlay();
    }
```
## 9、普通插屏广告： (2020.02.18更新)
```
    InterctionAdvertHelper  helper=new InterctionAdvertHelper(activity,advertModel);
        listener=new InterctionAdvertHelper.OnIntercrionAdvertListener() {
            
            @Override
            public void initSuccess(View view) {
                helper.showAdvert(rl_content,advertView);
            }

            @Override
            public void advertDismiss() {
                dismiss();
                activity.finish();
            }

            @Override
            public void advertClick() {
                dismiss();
            }
        };
        helper.initAdvert();


``` 

## 10、Fhad_BaseCallBack（2020.01.16更新）

## 11、激励视频TTAdRewardVideoHelper（2020.04.16更新）
```
        TTAdRewardVideoHelper.TTAdVideoListener listener = new TTAdRewardVideoHelper.TTAdVideoListener() {
                @Override
                public void onPlayComplete() {//广告视频观看完成
                    PreferenceConfig.setKeyValue(Constant.TIMES_ADPRICE, 0);
                }

                @Override
                public void onSkip() {//没有广告，或观看完成后用户点击跳过
                    int freeTrialNum = PreferenceConfig.getKeyValue(Constant.TIMES_FREETRIAL, Integer.class) + 1;
                    PreferenceConfig.setKeyValue(Constant.TIMES_FREETRIAL, freeTrialNum);
                    int adPriceNum = PreferenceConfig.getKeyValue(Constant.TIMES_ADPRICE, Integer.class) + 1;
                    PreferenceConfig.setKeyValue(Constant.TIMES_ADPRICE, adPriceNum);
                    getSumContext().pushFragmentToBackStack(AnswerFragment.class, answerSelected);
                }
            };

            TTAdRewardVideoHelper helper = new TTAdRewardVideoHelper(getActivity());
            int freeTrialNum = PreferenceConfig.getKeyValue(Constant.TIMES_FREETRIAL, Integer.class);
            int adPriceNum = PreferenceConfig.getKeyValue(Constant.TIMES_ADPRICE, Integer.class);
            if (AdvertConfig.freeTimeModel == null || freeTrialNum < AdvertConfig.freeTimeModel.getF_NoAdTimes() || adPriceNum < AdvertConfig.freeTimeModel.getF_ViewAdTimes()) {
                listener.onSkip();
                return;
            }
         helper.showWaterModel("观看视频查看测试详情哦", listener);
```
## 12、打开网页Fhad_WebPageActivity.openActivity(context,url);

## 13、增加底部横幅类型广告（2020/5/19更新）：
```
 //初始化底部横幅广告
      InterBannerAdvertHelper advertHelper = new InterBannerAdvertHelper((AppCompatActivity) getActivity(), AdvertUtils.searchFirstAdvertByLocation("neirong1"));
        advertHelper.setListener(new InterBannerAdvertHelper.OnIntercrionAdvertListener() {

            @Override
            public void initSuccess(View view) {
                advertHelper.showAdvert(rl_content, view);
            }

            @Override
            public void advertDismiss() {
            }

            @Override
            public void advertClick() {
            }
        });
        advertHelper.initAdvert(rl_content);//展示广告
```
## 14、增加手动关闭/打开广告方法（2020/5/19更新）：AdvertUtils.switchAdvert(boolean isOpen, String... advert_locations);


