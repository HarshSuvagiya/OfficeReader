package harry.bjg.documentreaderofficereader.ActivityFol;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import harry.bjg.documentreaderofficereader.R;
import harry.bjg.documentreaderofficereader.HARRY_XlsSheetView;
import jxl.Sheet;
import jxl.Workbook;

public class HARRY_ExcelPage extends AppCompatActivity {

    Context cn = this;
//    XlsSheetView xlsSheetView;

    String path;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(1024);
        setContentView(R.layout.harry_excel_page);

        loadInterstitial();

        path = getIntent().getStringExtra("path");

        pd = new ProgressDialog(cn);
        pd.setMessage("Load Data.....");
        pd.setCancelable(false);
        pd.show();
//        xlsSheetView = findViewById(R.id.xlsView);
        TabHost tabHost = (TabHost) findViewById(R.id.sheets);
        tabHost.setup();


        Runnable r = new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        LoadData(tabHost);
                    }
                });

            }
        };


        Thread t = new Thread(r);
        t.start();


//        new AsynckLoadExcel(tabHost).execute();


    }

    private void LoadData(TabHost tabHost) {
        File file = new File(path);
        InputStream xlsStream = null;
        try {
            xlsStream = new FileInputStream(file);
            Workbook xl = null;
            try {
                xl = Workbook.getWorkbook(xlsStream);
                Workbook finalXl = xl;

                for (final Sheet sheet : finalXl.getSheets()) {
                    TabHost.TabSpec tabSpec = tabHost.newTabSpec(sheet.getName());
                    tabSpec.setContent(new TabHost.TabContentFactory() {
                        @Override
                        public View createTabContent(String s) {
                            HARRY_XlsSheetView view = new HARRY_XlsSheetView(cn);
                            view.setSheet(sheet);
                            return view;
                        }
                    });
                    tabSpec.setIndicator("    " + sheet.getName() + "    ");
                    tabHost.addTab(tabSpec);
                }
                if (pd.isShowing())
                    pd.dismiss();


            } catch (Exception e) {
                e.printStackTrace();
                if (pd.isShowing())
                    pd.dismiss();

                finish();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (pd.isShowing())
                pd.dismiss();
            Toast.makeText(cn, "Unable To Open", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public class AsynckLoadExcel extends AsyncTask<Void, Void, Void> {

        TabHost tabHost;

        public AsynckLoadExcel(TabHost tabHost) {
            this.tabHost = tabHost;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(cn);
            pd.setMessage("wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            File file = new File(path);
            InputStream xlsStream = null;
            try {
                xlsStream = new FileInputStream(file);
                Workbook xl = null;
                try {
                    xl = Workbook.getWorkbook(xlsStream);
                    Workbook finalXl = xl;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (final Sheet sheet : finalXl.getSheets()) {
                                TabHost.TabSpec tabSpec = tabHost.newTabSpec(sheet.getName());
                                tabSpec.setContent(new TabHost.TabContentFactory() {
                                    @Override
                                    public View createTabContent(String s) {
//                            xlsSheetView.setSheet(sheet);
//                            return xlsSheetView;
                                        HARRY_XlsSheetView view = new HARRY_XlsSheetView(cn);
                                        view.setSheet(sheet);
                                        return view;
                                    }
                                });
                                tabSpec.setIndicator("    " + sheet.getName() + "    ");
                                tabHost.addTab(tabSpec);
                            }
                            if (pd.isShowing())
                                pd.dismiss();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
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
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_excelpage_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }
}
