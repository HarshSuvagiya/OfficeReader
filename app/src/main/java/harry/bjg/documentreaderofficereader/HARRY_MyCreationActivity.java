package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import harry.bjg.documentreaderofficereader.adapter.HARRY_MyCS_Adapter;

import static harry.bjg.documentreaderofficereader.HARRY_Help.mBoolean;

public class HARRY_MyCreationActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back;

    RecyclerView list;
    ArrayList<String> audios = new ArrayList<>();
    HARRY_MyCS_Adapter myCS_adapter;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_my_creation);

        mContext = this;
        HARRY_Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_mycreation_id));
        adContainerView.addView(adView);
        loadBanner();

        loadInterstitial();

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        list = findViewById(R.id.list);
        myCS_adapter = new HARRY_MyCS_Adapter(mContext, audios);
        list.setAdapter(myCS_adapter);
        list.setLayoutManager(new GridLayoutManager(mContext, 1));

        if (mBoolean) {
            title.setText("Created Pdf");
        }

        setLay();
    }

    void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        HARRY_Help.setSize(back, 96, 96, false);
    }

    public void getFromSdcard() {
        audios.clear();
        File file;
        if (mBoolean) {
            file = new File(HARRY_Help.getFolderPath(mContext) + "/Created Pdf");
        } else {
            file = new File(HARRY_Help.getFolderPath(mContext));
        }
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();

            if (listFile != null) {
                Arrays.sort(listFile, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                            return -1;
                        } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }

                });

                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isFile()) {
                        audios.add(listFile[i].getAbsolutePath());
                    }
                }
                myCS_adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onResume() {
        getFromSdcard();
        super.onResume();
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
//        super.onBackPressed();
//        finish();
        if(interstitialAd.isLoaded()){
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                   finish();
                }
            });
            interstitialAd.show();
        }else {
            super.onBackPressed();
            finish();
        }

    }
    InterstitialAd interstitialAd;
    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_mycreation_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }
}
