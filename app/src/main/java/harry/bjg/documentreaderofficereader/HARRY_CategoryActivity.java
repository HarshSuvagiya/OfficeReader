package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Locale;


import harry.bjg.documentreaderofficereader.adapter.HARRY_CategoryAdapter;

import static harry.bjg.documentreaderofficereader.HARRY_PdfToDocActivity.txt_title;

public class HARRY_CategoryActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back;

    LinearLayout search_lay;
    ImageView search_icon;
    EditText search;
    ListView list;
    HARRY_CategoryAdapter categoryAdapter;
    ArrayList<String> cats = new ArrayList<>();
    public static int position;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_category);

        mContext = this;
        HARRY_Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_categorylist_id));
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

        init();
        setClick();
        setLay();
    }

    private void init() {
        list = findViewById(R.id.list);
        search = findViewById(R.id.search);
        search_lay = findViewById(R.id.search_lay);
        search_icon = findViewById(R.id.search_icon);

        cats.clear();
        cats.add(getResources().getString(R.string.p2d));
        cats.add(getResources().getString(R.string.p2t));
        cats.add(getResources().getString(R.string.p2i));
        cats.add(getResources().getString(R.string.p2h));
        cats.add(getResources().getString(R.string.d2p));
        cats.add(getResources().getString(R.string.d2t));
        cats.add(getResources().getString(R.string.d2i));
        cats.add(getResources().getString(R.string.d2h));
        cats.add(getResources().getString(R.string.e2p));
        cats.add(getResources().getString(R.string.e2i));
        cats.add(getResources().getString(R.string.e2h));
        cats.add(getResources().getString(R.string.pp2p));
        cats.add(getResources().getString(R.string.pp2t));
        cats.add(getResources().getString(R.string.pp2i));
        cats.add(getResources().getString(R.string.pp2h));

        categoryAdapter = new HARRY_CategoryAdapter(mContext, cats);
        list.setAdapter(categoryAdapter);
    }

    private void setClick() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                txt_title = cats.get(pos);
                search.setText("");
                for (int i = 0; i < cats.size(); i++) {
                    if (cats.get(i).equals(txt_title)) {
                        position = i;
                    }
                }
                if (HARRY_Help.checkInternet(mContext)) {
//                    HARRY_Help.nextwithnew(mContext, HARRY_PdfToDocActivity.class);

                    if(interstitialAd.isLoaded()){
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                HARRY_Help.nextwithnew(mContext, HARRY_PdfToDocActivity.class);
                                loadInterstitial();
                            }
                        });
                        interstitialAd.show();
                    }else {
                        HARRY_Help.nextwithnew(mContext, HARRY_PdfToDocActivity.class);
                    }

                } else {
                    HARRY_Help.Toast(mContext, "No Internet Connection");
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {
                categoryAdapter.filter(search.getText().toString()
                        .toLowerCase(Locale.getDefault()));
            }

            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });
    }

    private void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);

        HARRY_Help.setSize(search_lay, 912, 144, false);
        HARRY_Help.setMargin(search_lay, 0, 50, 0, 50, false);

        HARRY_Help.setSize(search_icon, 60, 60, false);
        HARRY_Help.setMargin(search_icon, 50, 0, 50, 0, false);

        HARRY_Help.setMargin(list, 50, 0, 50, 200, false);
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
    InterstitialAd interstitialAd;
    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_category_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }
}
