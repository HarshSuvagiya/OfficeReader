package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import harry.bjg.documentreaderofficereader.adapter.HARRY_PdfListAdapter;
import harry.bjg.documentreaderofficereader.model.HARRY_PdfList;

import static harry.bjg.documentreaderofficereader.HARRY_PdfToDocActivity.docPaths;

public class HARRY_SelectPdfActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back;

    LinearLayout search_lay;
    ImageView search_icon;
    EditText search;
    ListView list;
    HARRY_PdfListAdapter adapter;

    ProgressDialog progress;
    ArrayList<HARRY_PdfList> pdfpaths = new ArrayList<>();
    String ASSORDER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_select_pdf);

        mContext = this;
        HARRY_Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_selectpdflist_id));
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
        setLay();
    }

    void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        search.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);

        HARRY_Help.setSize(search_lay, 912, 144, false);
        HARRY_Help.setMargin(search_lay, 0, 50, 0, 50, false);

        HARRY_Help.setSize(search_icon, 60, 60, false);
        HARRY_Help.setMargin(search_icon, 50, 0, 50, 0, false);

        HARRY_Help.setMargin(list, 50, 0, 50, 200, false);
    }

    private void init() {
        list = findViewById(R.id.list);
        search = findViewById(R.id.search);
        search_lay = findViewById(R.id.search_lay);
        search_icon = findViewById(R.id.search_icon);

        progress = HARRY_Help.setPD(mContext, "Loading...", false);

        pdfpaths.clear();
        new PDFFinder().execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                docPaths.add(pdfpaths.get(position).getPath());
//                setResult(RESULT_OK);
//                finish();

                if(interstitialAd.isLoaded()){
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            docPaths.add(pdfpaths.get(position).getPath());
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    interstitialAd.show();
                }else {
                    docPaths.add(pdfpaths.get(position).getPath());
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });

        if (HARRY_CategoryActivity.position == 4 || HARRY_CategoryActivity.position == 5 || HARRY_CategoryActivity.position == 6 || HARRY_CategoryActivity.position == 7){
            title.setText("Doc List");
        }else if (HARRY_CategoryActivity.position == 8 || HARRY_CategoryActivity.position == 9 || HARRY_CategoryActivity.position == 10) {
            title.setText("Excel List");
        }else if (HARRY_CategoryActivity.position == 11 || HARRY_CategoryActivity.position == 12 || HARRY_CategoryActivity.position == 13 || HARRY_CategoryActivity.position == 14) {
            title.setText("PPT List");
        }
    }

    class PDFFinder extends AsyncTask<String, String, String> {
        PDFFinder() {
        }

        protected void onPreExecute() {
            pdfpaths.clear();
            progress.show();
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            Collections.sort(pdfpaths, new NameASS());
            progress.dismiss();
            adapter = new HARRY_PdfListAdapter(mContext, pdfpaths);
            list.setAdapter(adapter);

            search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable arg0) {
                    adapter.filter(search.getText().toString()
                            .toLowerCase(Locale.getDefault()));
                }

                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                }

                public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                }
            });

            progress.dismiss();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            }, 1000);
            super.onPostExecute(result);
        }

        protected String doInBackground(String... params) {
            File file = new File(Environment.getExternalStorageDirectory() + "");
            checkDictionary(file);
            return null;
        }
    }

    class NameASS implements Comparator<HARRY_PdfList> {

        @Override
        public int compare(HARRY_PdfList e1, HARRY_PdfList e2) {
            int pos1 = 0;
            int pos2 = 0;
            for (int i = 0; i < Math.min(e1.getTitle().length(), e2.getTitle()
                    .length())
                    && pos1 == pos2; i++) {
                pos1 = ASSORDER.indexOf(e1.getTitle().charAt(i));
                pos2 = ASSORDER.indexOf(e2.getTitle().charAt(i));
            }

            if (pos1 == pos2
                    && e1.getTitle().length() != e2.getTitle().length()) {
                return e2.getTitle().length() - e2.getTitle().length();
            }

            return pos1 - pos2;
        }
    }

    void checkDictionary(File file) {
        try {
            File[] files = file.listFiles();
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    checkDictionary(inFile);
                } else if (inFile.isFile() || inFile.canRead()
                        || inFile.getTotalSpace() > 0) {
                    if (HARRY_CategoryActivity.position == 0 || HARRY_CategoryActivity.position == 1 || HARRY_CategoryActivity.position == 2 || HARRY_CategoryActivity.position == 3) {
                        if (inFile.getName().endsWith(".pdf")) {
                            pdfpaths.add(new HARRY_PdfList(inFile.getName(),
                                    inFile.getAbsolutePath()));
                        }
                    } else if (HARRY_CategoryActivity.position == 4 || HARRY_CategoryActivity.position == 5 || HARRY_CategoryActivity.position == 6 || HARRY_CategoryActivity.position == 7) {
                        if (inFile.getName().endsWith(".doc") || inFile.getName().endsWith(".docx") || inFile.getName().endsWith(".docm")) {
                            pdfpaths.add(new HARRY_PdfList(inFile.getName(),
                                    inFile.getAbsolutePath()));
                        }
                    } else if (HARRY_CategoryActivity.position == 8 || HARRY_CategoryActivity.position == 9 || HARRY_CategoryActivity.position == 10) {
                        if (inFile.getName().endsWith(".xls") || inFile.getName().endsWith(".xlsx") || inFile.getName().endsWith(".xlsm")) {
                            pdfpaths.add(new HARRY_PdfList(inFile.getName(),
                                    inFile.getAbsolutePath()));
                        }
                    } else if (HARRY_CategoryActivity.position == 11 || HARRY_CategoryActivity.position == 12 || HARRY_CategoryActivity.position == 13 || HARRY_CategoryActivity.position == 14) {
                        if (inFile.getName().endsWith(".ppt") || inFile.getName().endsWith(".pptx") || inFile.getName().endsWith(".pptm")) {
                            pdfpaths.add(new HARRY_PdfList(inFile.getName(),
                                    inFile.getAbsolutePath()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("PDFListActivity", e.toString());
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
    InterstitialAd interstitialAd;
    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_selectpdf_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }

}
