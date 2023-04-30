package harry.bjg.documentreaderofficereader;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import harry.bjg.documentreaderofficereader.appicon.HARRY_Java_Grid_Utils;
import harry.bjg.documentreaderofficereader.appicon.HARRY_SimpleAdapter2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HARRY_Exit extends Activity {
    ImageView yes;
    LinearLayout lay;
    private boolean isFirstDone=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.harry_exit_dialog);

        initNativeAdvanceAds();
        getAppIcons(getResources()
                .getString(R.string.app_icon_packagename_exitscreen));

        yes = findViewById(R.id.ivok);
        lay = findViewById(R.id.lay);


        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 480 / 1080,
                getResources().getDisplayMetrics().heightPixels * 104 / 1920);
        params1.gravity = Gravity.CENTER;
        yes.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 480 / 1080,
                getResources().getDisplayMetrics().heightPixels * 200 / 1920);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lay.setLayoutParams(params2);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();

            }
        });
    }






    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<String> temp = new ArrayList<>();
        try {
            for (int i = 0; i < HARRY_Java_Grid_Utils.packArr2.size(); i++) {
                try {
                    if (checkPackageExist(HARRY_Java_Grid_Utils.packArr2.get(i).split("_")[0]))
                        temp.add(HARRY_Java_Grid_Utils.packArr2.get(i));
                } catch (Exception e) {
                }
            }
            HARRY_Java_Grid_Utils.packArr2.removeAll(temp);
            Collections.shuffle(HARRY_Java_Grid_Utils.packArr2);
            adapter.notifyDataSetChanged();
        } catch (Exception e1) {
        }


    }

    private HARRY_SimpleAdapter2 adapter;

    public class GetImagebennerIcon extends AsyncTask<String, Void, Boolean> {

        String pkg;

        @Override
        protected void onPreExecute() {


        }

        @Override
        public Boolean doInBackground(String... params) {
            try {
                pkg = params[0];
                if (params[0] != null && !params[0].toString().equalsIgnoreCase("")) {

                    final OkHttpClient client = new OkHttpClient();

                    Response response = null;


                    Request request = new Request.Builder()
                            .url("http://phpstack-192394-571052.cloudwaysapps.com/getappads.php?package=" + params[0])
                            .get()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("cache-control", "no-cache")
                            .build();
                    HARRY_Java_Grid_Utils.packArr2 = new ArrayList<String>();
                    try {
                        response = client.newCall(request).execute();
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
                        HARRY_Java_Grid_Utils.packArr2 = getVideoList(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                } else {
                    HARRY_Java_Grid_Utils.packArr2 = new ArrayList<String>();

                }


                if (HARRY_Java_Grid_Utils.packArr2.size() > 0)
                    return true;
                else
                    return false;

            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            HARRY_Java_Grid_Utils.packageisLoad2 = result;
            try {
                if(HARRY_Java_Grid_Utils.packArr2.size()>0){
                    gridViewapps.setNumColumns(HARRY_Java_Grid_Utils.packArr2.size());

                    adapter = new HARRY_SimpleAdapter2(HARRY_Exit.this,pkg);
                    Collections.shuffle(HARRY_Java_Grid_Utils.packArr2);
                    gridViewapps.setAdapter(adapter);
                    setDynamicWidth(gridViewapps);

                }else {
                    add_scroll.setVisibility(View.GONE);
                    ivads.setVisibility(View.GONE);
                    rrad.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }


        }
    }

    private void setDynamicWidth(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            return;
        }
        int totalWidth;
        int items = gridViewAdapter.getCount();
        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalWidth = listItem.getMeasuredWidth();
        totalWidth = totalWidth * items;
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = totalWidth;
        gridView.setLayoutParams(params);
    }

    private boolean checkPackageExist(String packagename) {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packagename,
                    PackageManager.GET_META_DATA);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private ArrayList<String> getVideoList(String result) {
        ArrayList<String> videos = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject data = jsonObj.getJSONObject("data");


            if (data != null) {
                videos.clear();
                JSONArray contacts = data.getJSONArray("posts");
                for (int i = 0; i < contacts.length(); i++) {
                    try {
                        if (!checkPackageExist(contacts.getString(i).split("_")[0]))
                            videos.add(contacts.getString(i));
                    } catch (Exception e) {
                    }
                }
            } else {

            }

        } catch (final JSONException e) {

        }
        return videos;
    }


    private LinearLayout add_scroll;
    private GridView gridViewapps;
    RelativeLayout rrad;
    ImageView ivads;

    private void getAppIcons(String s) {
        add_scroll = (LinearLayout) findViewById(R.id.add_scroll1);
        add_scroll.setVisibility(View.GONE);
        gridViewapps = (GridView) findViewById(R.id.gridView);
        rrad = (RelativeLayout) findViewById(R.id.rrad);
        ivads = (ImageView) findViewById(R.id.ivads);

        if (isNetworkAvailable()) {

            add_scroll.setVisibility(View.VISIBLE);
            try {

                new GetImagebennerIcon().execute(s);

            } catch (Exception e) {
            }
        } else {
            add_scroll.setVisibility(View.GONE);
            rrad.setVisibility(View.GONE);
            ivads.setVisibility(View.GONE);
            Toast.makeText(HARRY_Exit.this, "Please connect to Internet",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }


        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private UnifiedNativeAdView nativeAdView;



    private void initNativeAdvanceAds(){



// MobileAds.initialize(this,
// getString(R.string.admob_app_id));

        flNativeAds=findViewById(R.id.flNativeAds);
        flNativeAds.setVisibility(View.GONE);
        nativeAdView = (UnifiedNativeAdView) findViewById(R.id.ad_view);

// The MediaView will display a video asset if one is present in the ad, and the
// first image asset otherwise.
        nativeAdView.setMediaView((com.google.android.gms.ads.formats.MediaView) nativeAdView.findViewById(R.id.ad_media));

// Register the view used for each individual asset.
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_icon));
// nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
// nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        if(HARRY_MainActivity.unifiedNativeAd1==null)
            loadNativeAds();
        else {
            flNativeAds.setVisibility(View.VISIBLE);
            populateNativeAdView(HARRY_MainActivity.unifiedNativeAd1,nativeAdView);
        }
    }
    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {


        VideoController vc = nativeAd.getVideoController();


        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {

                super.onVideoEnd();
            }
        });

// Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

// These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
// check before trying to display them.
        com.google.android.gms.ads.formats.NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

// Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }



    private FrameLayout flNativeAds;
    private void loadNativeAds() {
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_exit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
//						mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            flNativeAds.setVisibility(View.VISIBLE);
                            populateNativeAdView(unifiedNativeAd,nativeAdView);
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("ELVIACOLEMAN_MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                        }
                    }
                }).withNativeAdOptions(adOptions).build();

        // Load the Native ads.
        adLoader.loadAd( ConsentSDK.getAdRequest(this));
    }


}
