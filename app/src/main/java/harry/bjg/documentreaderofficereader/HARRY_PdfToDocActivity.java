package harry.bjg.documentreaderofficereader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eralp.circleprogressview.CircleProgressView;

import java.io.File;
import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.adapter.HARRY_ConvertAdapter;
import harry.bjg.documentreaderofficereader.convertapi.HARRY_ConverInterface;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_CloudConvertService;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_ZamzarService;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_FileUtil;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_HomePresenter;

import static harry.bjg.documentreaderofficereader.HARRY_Help.gone;
import static harry.bjg.documentreaderofficereader.HARRY_Help.showLog;

public class HARRY_PdfToDocActivity extends AppCompatActivity implements HARRY_ConverInterface {

    Activity mContext;
    TextView title;
    ImageView back;

    public HARRY_ConvertAdapter adapter;
    ImageView btnAddFiles, upload_icon;
    TextView filePath, txt_add_file;
    public static ArrayList<String> docPaths = new ArrayList<>();

    int SELECT_PDF = 1234;
    public static String txt_title;

    LinearLayout nofilelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_pdf_to_doc);

        mContext = this;
        HARRY_Help.FS(this);

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
        btnAddFiles = findViewById(R.id.btn);
        filePath = findViewById(R.id.filePath);

        nofilelay = findViewById(R.id.nofilelay);
        upload_icon = findViewById(R.id.upload_icon);
        txt_add_file = findViewById(R.id.txt_add_file);

        docPaths.clear();
        HARRY_FileUtil file = new HARRY_FileUtil(this);
//        docPaths = file.getFilesAddress();
        createList(docPaths);
        filePath.setText("Saved Path:" + file.defaultPath);

        title.setText(txt_title);
        showLog("position : " + HARRY_CategoryActivity.position);
    }

    private void setClick() {
        this.btnAddFiles.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HARRY_SelectPdfActivity.class);
                startActivityForResult(intent, SELECT_PDF);
            }
        });
    }

    private void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        filePath.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
        txt_add_file.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);
        HARRY_Help.setSize(btnAddFiles, 932, 154, false);
        HARRY_Help.setMargin(btnAddFiles, 0, 50, 0, 50, false);

        HARRY_Help.setSize(upload_icon, 336, 252, false);
    }

    private void createList(ArrayList<String> docPaths2) {
        RecyclerView recyclerView = findViewById(R.id.category_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        adapter = new HARRY_ConvertAdapter(this, docPaths2, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void updateRow(int pos, String path, ProgressBar progressBar, CircleProgressView circleProgressView, ImageView refreshButton, ImageView convert) {
        if (isNetworkAvailable()) {
            String name = new File(path).getName().replace(".pdf", "");
            doConversion(pos, name, path, progressBar, circleProgressView, refreshButton, convert);
        } else {
            HARRY_Help.Toast(mContext, "Please check internet connection");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PDF && resultCode == RESULT_OK) {
            createList(docPaths);
            gone(nofilelay);
            btnAddFiles.setImageResource(R.drawable.add1_click);
        }
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void doConversion(int pos, String filename, String path, ProgressBar progressBar, CircleProgressView circleProgressView, ImageView refreshButton, ImageView convert) {
        new HARRY_HomePresenter(this, pos, filename, new HARRY_FileUtil(this), new HARRY_CloudConvertService(this), new HARRY_ZamzarService(this), progressBar, circleProgressView, refreshButton, convert).prepareUri(Uri.parse(path));
    }
}
