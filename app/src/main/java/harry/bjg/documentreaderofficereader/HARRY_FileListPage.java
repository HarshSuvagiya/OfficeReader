package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;
import harry.bjg.documentreaderofficereader.model.HARRY_FileList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static harry.bjg.documentreaderofficereader.HARRY_Help.showLog;

public class HARRY_FileListPage extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back;

    TextView txt_doc_title, txt_doc_count, txt_all_files, txt_doc_files, txt_xls_files, txt_txt_files, txt_pdf_files, txt_ppt_files;
    ImageView all_files, doc_files, xls_files, txt_files, pdf_files, ppt_files;
    LinearLayout lay2, lay3, lay5, lay6;

    ProgressDialog progress;
    public static ArrayList<HARRY_FileList> filepaths = new ArrayList<>();
    public static ArrayList<HARRY_FileList> docpaths = new ArrayList<>();
    public static ArrayList<HARRY_FileList> xlspaths = new ArrayList<>();
    public static ArrayList<HARRY_FileList> txtpaths = new ArrayList<>();
    public static ArrayList<HARRY_FileList> pdfpaths = new ArrayList<>();
    public static ArrayList<HARRY_FileList> pptpaths = new ArrayList<>();
    String ASSORDER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_file_list_page);

        mContext = this;
        HARRY_Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_filelist_id));
        adContainerView.addView(adView);
        loadBanner();

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
        txt_doc_title = findViewById(R.id.txt_doc_title);
        txt_doc_count = findViewById(R.id.txt_doc_count);

        lay2 = findViewById(R.id.lay2);
        lay3 = findViewById(R.id.lay3);
        lay5 = findViewById(R.id.lay5);
        lay6 = findViewById(R.id.lay6);

        all_files = findViewById(R.id.all_files);
        doc_files = findViewById(R.id.doc_files);
        xls_files = findViewById(R.id.xls_files);
        txt_files = findViewById(R.id.txt_files);
        pdf_files = findViewById(R.id.pdf_files);
        ppt_files = findViewById(R.id.ppt_files);

        txt_all_files = findViewById(R.id.txt_all_files);
        txt_doc_files = findViewById(R.id.txt_doc_files);
        txt_xls_files = findViewById(R.id.txt_xls_files);
        txt_txt_files = findViewById(R.id.txt_txt_files);
        txt_pdf_files = findViewById(R.id.txt_pdf_files);
        txt_ppt_files = findViewById(R.id.txt_ppt_files);

        progress = HARRY_Help.setPD(mContext, "Loading...", false);
        new PDFFinder().execute();
    }

    private void setClick() {
        all_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNext("all");
            }
        });

        doc_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNext("doc");
            }
        });

        xls_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNext("xls");
            }
        });

        txt_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNext("txt");
            }
        });

        pdf_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNext("pdf");
            }
        });

        ppt_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveNext("ppt");
            }
        });
    }

    private void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        txt_doc_title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        txt_doc_count.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));

        txt_all_files.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_doc_files.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_xls_files.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_txt_files.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_pdf_files.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_ppt_files.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);

        HARRY_Help.setSize(all_files, 268, 268, false);
        HARRY_Help.setSize(doc_files, 268, 268, false);
        HARRY_Help.setSize(xls_files, 268, 268, false);
        HARRY_Help.setSize(txt_files, 268, 268, false);
        HARRY_Help.setSize(pdf_files, 268, 268, false);
        HARRY_Help.setSize(ppt_files, 268, 268, false);

        HARRY_Help.setMargin(all_files, 0, 60, 0, 0, false);
        HARRY_Help.setMargin(doc_files, 0, 60, 0, 0, false);
        HARRY_Help.setMargin(xls_files, 0, 60, 0, 0, false);
        HARRY_Help.setMargin(txt_files, 0, 60, 0, 0, false);
        HARRY_Help.setMargin(pdf_files, 0, 60, 0, 0, false);
        HARRY_Help.setMargin(ppt_files, 0, 60, 0, 0, false);

        HARRY_Help.setMargin(lay2, 60, 0, 0, 0, false);
        HARRY_Help.setMargin(lay3, 60, 0, 0, 0, false);
        HARRY_Help.setMargin(lay5, 60, 0, 0, 0, false);
        HARRY_Help.setMargin(lay6, 60, 0, 0, 0, false);
    }

    private void moveNext(String str) {
        HARRY_SelectFileActivity.type = str;
        HARRY_Help.next(mContext, HARRY_SelectFileActivity.class);
    }

    class PDFFinder extends AsyncTask<String, String, String> {
        PDFFinder() {
        }

        protected void onPreExecute() {
            filepaths.clear();
            docpaths.clear();
            xlspaths.clear();
            txtpaths.clear();
            pdfpaths.clear();
            pptpaths.clear();
            progress.show();
            super.onPreExecute();
        }

        protected void onPostExecute(String result) {
            Collections.sort(filepaths, new NameASS());
            progress.dismiss();
            txt_doc_count.setText("" + filepaths.size());
            super.onPostExecute(result);
        }

        protected String doInBackground(String... params) {
            File file = new File(Environment.getExternalStorageDirectory() + "");
            checkDictionary(file);
            return null;
        }
    }

    class NameASS implements Comparator<HARRY_FileList> {

        @Override
        public int compare(HARRY_FileList e1, HARRY_FileList e2) {
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
                } else if (inFile.isFile() || inFile.canRead() || inFile.getTotalSpace() > 0) {
                    if (inFile.getName().endsWith(".doc") ||
                            inFile.getName().endsWith(".docx") ||
                            inFile.getName().endsWith(".ppt") ||
                            inFile.getName().endsWith(".pptx") ||
                            inFile.getName().endsWith(".pdf") ||
                            inFile.getName().endsWith(".txt") ||
                            inFile.getName().endsWith(".xls") ||
                            inFile.getName().endsWith(".xlsx")) {
                        filepaths.add(new HARRY_FileList(inFile.getName(), inFile.getAbsolutePath()));
                    }
                    if (inFile.getName().endsWith(".doc") || inFile.getName().endsWith(".docx")) {
                        docpaths.add(new HARRY_FileList(inFile.getName(), inFile.getAbsolutePath()));
                    }
                    if (inFile.getName().endsWith(".ppt") || inFile.getName().endsWith(".pptx")) {
                        pptpaths.add(new HARRY_FileList(inFile.getName(), inFile.getAbsolutePath()));
                    }
                    if (inFile.getName().endsWith(".xls") || inFile.getName().endsWith(".xlsx")) {
                        xlspaths.add(new HARRY_FileList(inFile.getName(), inFile.getAbsolutePath()));
                    }
                    if (inFile.getName().endsWith(".pdf")) {
                        pdfpaths.add(new HARRY_FileList(inFile.getName(), inFile.getAbsolutePath()));
                    }
                    if (inFile.getName().endsWith(".txt")) {
                        txtpaths.add(new HARRY_FileList(inFile.getName(), inFile.getAbsolutePath()));
                    }
                }
            }
        } catch (Exception e) {
            showLog("Document : " + e.toString());
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