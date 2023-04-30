package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;
import harry.bjg.documentreaderofficereader.adapter.HARRY_FileListAdapter;
import harry.bjg.documentreaderofficereader.model.HARRY_FileList;

import android.app.Activity;
import android.content.Intent;
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
import com.foobnix.dao2.FileMeta;
import com.foobnix.pdf.info.ExtUtils;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class HARRY_SelectFileActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back;

    EditText search;
    ListView list;
    HARRY_FileListAdapter adapter;

    public static String type;
    ArrayList<HARRY_FileList> myFile;

    LinearLayout search_lay;
    ImageView search_icon;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_select_file);

        mContext = this;
        HARRY_Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_selectfile_id));
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
        setLay();
    }

    private void setLay() {
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

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filePath = myFile.get(position).getPath();
//                HARRY_Help.mTempPath = filePath;
                final String format = myFile.get(position).getTitle().substring(myFile.get(position).getTitle().lastIndexOf(".") + 1);

                if (format.equalsIgnoreCase("pdf") || format.equalsIgnoreCase("txt")) {
                    FileMeta fileMeta = new FileMeta(filePath);
                    ExtUtils.openFile(mContext, fileMeta);
                } else {
                    Intent intent = new Intent(mContext, HARRY_OfficeFileViewActivity.class);
                    intent.putExtra("name", new File(filePath).getName());
                    intent.putExtra("path", filePath);
                    startActivity(intent);
                }

            }
        });

        if (type.equals("doc")) {
            myFile = HARRY_FileListPage.docpaths;
            title.setText("Doc Files");
        } else if (type.equals("xls")) {
            myFile = HARRY_FileListPage.xlspaths;
            title.setText("Xls Files");
        } else if (type.equals("txt")) {
            myFile = HARRY_FileListPage.txtpaths;
            title.setText("Text Files");
        } else if (type.equals("pdf")) {
            myFile = HARRY_FileListPage.pdfpaths;
            title.setText("Pdf Files");
        } else if (type.equals("ppt")) {
            myFile = HARRY_FileListPage.pptpaths;
            title.setText("Ppt Files");
        } else {
            myFile = HARRY_FileListPage.filepaths;
        }

        adapter = new HARRY_FileListAdapter(mContext, myFile);
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
