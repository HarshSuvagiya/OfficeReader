package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

import static harry.bjg.documentreaderofficereader.HARRY_Help.showLog;

public class HARRY_TextPdfActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back;

    ImageView create_pdf;
    ProgressDialog progress;
    File pdfFolder, myPDF;

    EditText pdf_title, paragraph_title, paragraph;
    String pt, pt2, p;
    View view1,view2,view3;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_text_pdf);

        mContext = this;
        HARRY_Help.FS(mContext);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_textpdf_id));
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

    void init() {
        progress = HARRY_Help.setPD(mContext, "Creating Pdf...", false);

        pdf_title = findViewById(R.id.pdf_title);
        paragraph_title = findViewById(R.id.paragraph_title);
        paragraph = findViewById(R.id.paragraph);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);

        create_pdf = findViewById(R.id.create_pdf);

        create_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pt = pdf_title.getText().toString();
                pt2 = paragraph_title.getText().toString();
                p = paragraph.getText().toString();

                if (HARRY_Help.hasChar(pt)) {
                    if (HARRY_Help.hasChar(pt2)) {
                        if (!p.equals("")) {
//                            new SavePdf().execute();

                            if(interstitialAd.isLoaded()){
                                interstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        new SavePdf().execute();
                                    }
                                });
                                interstitialAd.show();
                            }else {
                                new SavePdf().execute();
                            }

                        } else {
                            paragraph.setError("Invalid");
                            paragraph.requestFocus();
                        }
                    } else {
                        paragraph_title.setError("Invalid");
                        paragraph_title.requestFocus();
                    }
                } else {
                    pdf_title.setError("Invalid");
                    pdf_title.requestFocus();
                }
            }
        });
    }

    void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        pdf_title.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        paragraph_title.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        paragraph.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);
        HARRY_Help.setSize(create_pdf, 868, 148, false);
        HARRY_Help.setMargin(create_pdf, 0,150,0,0, false);

        HARRY_Help.setMargin(pdf_title, 50,50,50,0, false);
        HARRY_Help.setMargin(paragraph_title, 50,50,50,0, false);
        HARRY_Help.setMargin(paragraph, 50,50,50,0, false);

        HARRY_Help.setMargin(view1, 50,30,50,0, false);
        HARRY_Help.setMargin(view2, 50,30,50,0, false);
        HARRY_Help.setMargin(view3, 50,30,50,0, false);
    }

    class SavePdf extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            createPdf();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (HARRY_ImageFolderActivity.mContext != null) {
                HARRY_ImageFolderActivity.mContext.finish();
            }
            finish();
        }
    }

    public void createPdf() {
        try {
            pdfFolder = new File(HARRY_Help.getFolderPath(mContext) + "/Created Pdf");
            pdfFolder.mkdirs();

            myPDF = new File(pdfFolder.getAbsolutePath() + "/Pdf_" + System.currentTimeMillis() + ".pdf");

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(myPDF));

            // Open Document for Writting into document
            document.open();

            // User Define Method
            addTitlePage(document);
            document.close();
        } catch (Exception e) {
            showLog(e.toString());
        }
    }

    public void addTitlePage(Document document) throws DocumentException {
        // Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD, BaseColor.BLACK);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Start New Paragraph
        Paragraph prHead = new Paragraph();
        // Set Font in this Paragraph
        prHead.setFont(titleFont);
        // Add item into Paragraph
        prHead.add(pt + "\n");

        prHead.setAlignment(Element.ALIGN_CENTER);

        // Add all above details into Document
        document.add(prHead);

        Paragraph prProfile = new Paragraph();
        prProfile.setFont(catFont);
        prProfile.add("\n \n " + pt2 + " : \n ");
        prProfile.setFont(normal);
        prProfile.add("\n" + p);

        prProfile.setFont(smallBold);
        document.add(prProfile);

        // Create new Page in PDF
        document.newPage();
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
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_textpdf_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }
}
