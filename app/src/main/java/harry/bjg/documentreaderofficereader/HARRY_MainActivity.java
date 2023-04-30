package harry.bjg.documentreaderofficereader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HARRY_MainActivity extends AppCompatActivity {

    private static final int REQ_PICK = 101;
    Context cn = this;
    ImageView btnbrows;
    String[] permission = new String[]
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

    ImageView logo, doc_convert, doc_create,share,pp, title;
    LinearLayout ads_lay;

    private FrameLayout adContainerView;
    public static UnifiedNativeAd unifiedNativeAd1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_main);
//        HARRY_MyUtils.checkPermission(cn, permission);

        HARRY_Help.FS(this);
        HARRY_Help.width = getResources().getDisplayMetrics().widthPixels;
        HARRY_Help.height = getResources().getDisplayMetrics().heightPixels;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_main_id));
        adContainerView.addView(adView);

        initConsentSDK(this);
        if (!isConsentDone() && isNetworkAvailable() && ConsentSDK.isUserLocationWithinEea(this)) {
//        if(!isConsentDone()&& isNetworkAvailable()){
            consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {

                @Override
                public void onResult(boolean isRequestLocationInEeaOrUnknown) {
                    setPref();
                    ConsentSDK.Builder.dialog.dismiss();
                    goToMain();
                }
            });


        } else {
            goToMain();
        }

        init();
        resize();
        click();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HARRY_Help.FS2(this);
    }

    void click() {

        btnbrows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HARRY_MyUtils.checkPermission(cn, permission)) {
//                    startActivity(new Intent(cn, HARRY_FileListPage.class));

                    if (interstitialAd.isLoaded()) {
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(cn, HARRY_FileListPage.class));
                                loadInterstitial();
                            }
                        });
                        interstitialAd.show();
                    } else {
                        startActivity(new Intent(cn, HARRY_FileListPage.class));
                    }
                }
            }
        });

        doc_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HARRY_Help.nextwithnew(cn, HARRY_StartActivity.class);

                if(interstitialAd.isLoaded()){
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            HARRY_Help.nextwithnew(cn, HARRY_StartActivity.class);
                            loadInterstitial();
                        }
                    });
                    interstitialAd.show();
                }else {
                    HARRY_Help.nextwithnew(cn, HARRY_StartActivity.class);
                }
            }
        });

        doc_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HARRY_Help.nextwithnew(cn, HARRY_CreateDocActivity.class);

                if(interstitialAd.isLoaded()){
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            HARRY_Help.nextwithnew(cn, HARRY_CreateDocActivity.class);
                            loadInterstitial();
                        }
                    });
                    interstitialAd.show();
                }else {
                    HARRY_Help.nextwithnew(cn, HARRY_CreateDocActivity.class);
                }
            }
        });
    }

    void resize() {
        HARRY_Help.setSize(logo, 924, 628, false);
        HARRY_Help.setMargin(logo, 0, 100, 0, 0, false);

        HARRY_Help.setSize(title, 732, 132, false);
        HARRY_Help.setMargin(title, 0, 80, 0, 0, true);
        HARRY_Help.setSize(share, 116, 112, false);
        HARRY_Help.setSize(pp, 116, 112, false);
        HARRY_Help.setMargin(share, 0, 0, 0, 30, true);
        HARRY_Help.setMargin(pp, 0, 0, 0, 30, true);

        HARRY_Help.setSize(btnbrows, 420, 396, false);
        HARRY_Help.setSize(doc_create, 420, 396, false);
        HARRY_Help.setSize(doc_convert, 900, 174, false);
        HARRY_Help.setMargin(doc_create, 50, 0, 0, 0, false);
        HARRY_Help.setMargin(doc_convert, 0, 50, 0, 0, false);

        HARRY_Help.setSize(ads_lay, 1080, 160, false);
    }

    void init() {
        btnbrows = findViewById(R.id.btnbrows);
        logo = findViewById(R.id.logo);
        doc_convert = findViewById(R.id.doc_convert);
        doc_create = findViewById(R.id.doc_create);

        share = findViewById(R.id.share);
        pp = findViewById(R.id.pp);
        title = findViewById(R.id.title);
        ads_lay = findViewById(R.id.ads_lay);

        HARRY_Help.set_share_rate(cn,share,null);

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HARRY_Help.nextwithnew(cn, HARRY_Policy.class);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQ_PICK == requestCode && resultCode == RESULT_OK && data != null) {
            try {

                String path = HARRY_PathUtil.getPath(cn, data.getData());
                if (path != null) {
                    Log.e("AAA", "Path : " + path);
//                    FileMeta fileMeta = new FileMeta(path);
//                    ExtUtils.openFile(HARRY_MainActivity.this, fileMeta);
//                    ExtUtils.openFile(cn, new File(path));
                } else {
                    HARRY_MyUtils.Toast(cn, "path null");
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
                HARRY_MyUtils.Toast(cn, "Select Another File");
            }

        }
    }

    InterstitialAd interstitialAd;

    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_main_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }

    private ConsentSDK consentSDK;

    private void initConsentSDK(Context context) {
        // Initialize ConsentSDK
        consentSDK = new ConsentSDK.Builder(this)
//                .addTestDeviceId("77259D4779E9E87A669924752B4E3B2B")
                .addCustomLogTag("CUSTOM_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy(getString(R.string.privacy_link)) // Add your privacy policy url
                .addPublisherId(getString(R.string.admob_publisher_id)) // Add your admob publisher id
                .build();
    }

    private void goToMain() {

        HARRY_MyUtils.checkPermission(cn, permission);

        loadInterstitial();

        loadInterstitialExit();

        initNativeAdvanceAds();

        loadBanner();
    }

    void setPref() {

        SharedPreferences.Editor editor = getSharedPreferences("consentpreff", MODE_PRIVATE).edit();
        editor.putBoolean("isDone", true);
        editor.apply();
    }

    boolean isConsentDone() {


        SharedPreferences prefs = getSharedPreferences("consentpreff", MODE_PRIVATE);
        return prefs.getBoolean("isDone", false);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    public UnifiedNativeAdView nativeAdView;

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
        loadNativeAds();
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

// if (nativeAd.getPrice() == null) {
// adView.getPriceView().setVisibility(View.INVISIBLE);
// } else {
// adView.getPriceView().setVisibility(View.VISIBLE);
// ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
// }

// if (nativeAd.getStore() == null) {
// adView.getStoreView().setVisibility(View.INVISIBLE);
// } else {
// adView.getStoreView().setVisibility(View.VISIBLE);
// ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
// }

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

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_main_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
//						mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            logo.setVisibility(View.INVISIBLE);
                            flNativeAds.setVisibility(View.VISIBLE);
                            unifiedNativeAd1=unifiedNativeAd;
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

    AdView adView;

    private void loadBanner() {
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(ConsentSDK.getAdRequest(this));
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(getApplicationContext(),HARRY_Exit.class));

        if(interstitialAd1.isLoaded()){
            interstitialAd1.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(getApplicationContext(),HARRY_Exit.class));
                    loadInterstitialExit();
                }
            });
            interstitialAd1.show();
        }else {
            startActivity(new Intent(getApplicationContext(),HARRY_Exit.class));
        }
    }
    InterstitialAd interstitialAd1;
    // Load Interstitial
    private void loadInterstitialExit() {
        interstitialAd1 = new InterstitialAd(this);
        interstitialAd1.setAdUnitId(getString(R.string.admob_fullscreen_exit_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd1.loadAd(ConsentSDK.getAdRequest(this));

    }
}