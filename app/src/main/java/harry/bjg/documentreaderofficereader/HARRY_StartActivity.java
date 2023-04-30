package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class HARRY_StartActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView start, mywork,logo,back;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_start);

        mContext = this;
        HARRY_Help.FS(mContext);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_docconverter_id));
        adContainerView.addView(adView);
        loadBanner();

        HARRY_Help.width = getResources().getDisplayMetrics().widthPixels;
        HARRY_Help.height = getResources().getDisplayMetrics().heightPixels;

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        logo = findViewById(R.id.logo);
        start = findViewById(R.id.start);
        mywork = findViewById(R.id.mywork);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkper()) {
                    HARRY_Help.nextwithnew(mContext, HARRY_CategoryActivity.class);
                } else {
                    HARRY_Help.Toast(mContext, "All Permission Require");
                    AllowPermission();
                }
            }
        });

        mywork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HARRY_Help.mBoolean = false;
                if (checkper()) {
                    HARRY_Help.nextwithnew(mContext, HARRY_MyCreationActivity.class);
                } else {
                    HARRY_Help.Toast(mContext, "All Permission Require");
                    AllowPermission();
                }
            }
        });

        AllowPermission();

        setLay();
    }

    private void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);

        HARRY_Help.setSize(logo,854,756,false);
        HARRY_Help.setMargin(logo,0,100,0,30,false);

        HARRY_Help.setSize(start,650,134,false);
        HARRY_Help.setSize(mywork,650,134,false);
        HARRY_Help.setMargin(start,0,70,0,0,false);
        HARRY_Help.setMargin(mywork,0,70,0,0,false);
    }

    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    void AllowPermission() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (Build.VERSION.SDK_INT >= 18) {
            builder.detectFileUriExposure();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permission, 100);
            }
        }
    }

    boolean checkper() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
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
}
