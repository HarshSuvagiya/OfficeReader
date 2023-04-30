package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class HARRY_CreateDocActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView logo, back;
    ImageView i2p, mywork, t2p;
    TextView txt_i2p,txt_t2p,txt_mywork;
    LinearLayout lay2, lay3;
    public static boolean mSingle;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_create_doc);

        mContext = this;
        HARRY_Help.FS(mContext);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_createdoc_id));
        adContainerView.addView(adView);
        loadBanner();


        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        logo = findViewById(R.id.logo);
        i2p = findViewById(R.id.i2p);
        t2p = findViewById(R.id.t2p);
        lay2 = findViewById(R.id.lay2);
        lay3 = findViewById(R.id.lay3);
        mywork = findViewById(R.id.mywork);

        txt_i2p = findViewById(R.id.txt_i2p);
        txt_t2p = findViewById(R.id.txt_t2p);
        txt_mywork = findViewById(R.id.txt_mywork);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        i2p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_popup();
            }
        });

        t2p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HARRY_Help.nextwithnew(mContext, HARRY_TextPdfActivity.class);
            }
        });

        mywork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HARRY_Help.mBoolean = true;
                HARRY_Help.nextwithnew(mContext, HARRY_MyCreationActivity.class);
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (Build.VERSION.SDK_INT >= 18) {
            builder.detectFileUriExposure();
        }

        setLay();
    }

    private void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        txt_i2p.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_t2p.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_mywork.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);

        HARRY_Help.setSize(logo, 866, 532, false);
        HARRY_Help.setMargin(logo, 0, 100, 0, 100, false);

        HARRY_Help.setSize(i2p, 282, 282, false);
        HARRY_Help.setSize(t2p, 282, 282, false);
        HARRY_Help.setSize(mywork, 282, 282, false);
        HARRY_Help.setMargin(i2p, 0, 100, 0, 0, false);
        HARRY_Help.setMargin(lay2, 50, 100, 0, 0, false);
        HARRY_Help.setMargin(lay3, 50, 100, 0, 0, false);
    }

    void sel_popup() {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.harry_sel_popup);

        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        HARRY_Help.setSize(mainLay, 978, 750, false);

        TextView title = dialog.findViewById(R.id.title);
        ImageView single_image = dialog.findViewById(R.id.single_image);
        ImageView multiple_image = dialog.findViewById(R.id.multiple_image);

        title.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        HARRY_Help.setSize(single_image, 512, 162, false);
        HARRY_Help.setSize(multiple_image, 512, 162, false);
        HARRY_Help.setMargin(multiple_image, 0, 50, 0, 0, false);

        single_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSingle = true;
                HARRY_Help.nextwithnew(mContext, HARRY_ImageFolderActivity.class);
                dialog.dismiss();
            }
        });

        multiple_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSingle = false;
                HARRY_Help.nextwithnew(mContext, HARRY_ImageFolderActivity.class);
                dialog.dismiss();
            }
        });

        dialog.show();
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
