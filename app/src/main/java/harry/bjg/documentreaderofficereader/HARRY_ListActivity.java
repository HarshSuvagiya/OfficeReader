package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.adapter.HARRY_List_Adapter;
import harry.bjg.documentreaderofficereader.model.HARRY_ImageData;
import harry.bjg.documentreaderofficereader.model.HARRY_ImageFolderData;

import static harry.bjg.documentreaderofficereader.HARRY_Help.gone;
import static harry.bjg.documentreaderofficereader.HARRY_Help.myList;
import static harry.bjg.documentreaderofficereader.HARRY_Help.showLog;

public class HARRY_ListActivity extends AppCompatActivity {

    public static Activity mContext;
    ImageView back;
    TextView title;

    RecyclerView videoList;
    RecyclerView.LayoutManager lm;
    HARRY_List_Adapter listadapter;

    int int_position;
    public static Activity mActivity;

    LinearLayout bottom_lay, addLay;
    ImageView done;
    TextView count;

    public static HARRY_ImageFolderData imageFolderData;
    private ArrayList<HARRY_ImageData> myImages;

    ProgressDialog progress;
    File pdfFolder,myPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_list);

        mActivity = this;
        mContext = this;
        HARRY_Help.FS(this);

        back = findViewById(R.id.back);
        title = findViewById(R.id.title);

        videoList = findViewById(R.id.videoList);

        int_position = getIntent().getIntExtra("value", 0);

        lm = new GridLayoutManager(this, 3);
        videoList.setLayoutManager(lm);

        myImages = imageFolderData.getPath();

        listadapter = new HARRY_List_Adapter(this, myImages);
        videoList.setAdapter(listadapter);

        title.setText(new File(imageFolderData.getfolder()).getName());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        init();

        setLayout();
    }

    private void init() {
        bottom_lay = findViewById(R.id.bottom_lay);
        addLay = findViewById(R.id.addLay);
        done = findViewById(R.id.done);
        count = findViewById(R.id.count);

        progress = HARRY_Help.setPD(mContext, "Creating Pdf...", false);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myList.size() >= 2) {
                    StartConvert();
                } else {
                    HARRY_Help.Toast(mContext, "2 Image Require");
                }
            }
        });

        if (HARRY_CreateDocActivity.mSingle) {
            gone(bottom_lay);
            gone(done);
        }
    }

    public void setLayout() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        count.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);
        HARRY_Help.setSize(done, 96, 96, false);

        HARRY_Help.setSize(bottom_lay, 1080, 210, false);
        HARRY_Help.setMargin(videoList, 20,0,20,0, false);
    }

    public void addLay(final String Path, boolean b) {

        count.setText("(" + myList.size() + ")");

        final View myview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.harry_myview, null, false);

        RelativeLayout mainLay = myview.findViewById(R.id.mainLay);
        HARRY_Help.setSize(mainLay,150,150,false);
        HARRY_Help.setMargin(mainLay,30,0,0,0,false);

        ImageView thumb = myview.findViewById(R.id.thumb);
        ImageView close = myview.findViewById(R.id.close);
        HARRY_Help.setSize(close,38,38,false);

        if (b) {
            addLay.addView(myview);
        } else {
            addLay.addView(myview, 0);
        }

        Glide.with(mContext).load(Path).into(thumb);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLay.removeView(myview);
                for (int i = 0; i < myList.size(); i++) {
                    if (Path.equals(myList.get(i).getImageUrl())) {
                        myList.remove(i);
                    }
                }
                count.setText("(" + myList.size() + ")");
                listadapter.notifyDataSetChanged();
            }
        });

    }

    public void removeView(int pos) {
        addLay.removeViewAt(pos);
        count.setText("(" + myList.size() + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        addLay.removeAllViews();
        for (int i = 0; i < myList.size(); i++) {
            addLay(myList.get(i).getImageUrl(), true);
        }
        listadapter.notifyDataSetChanged();
    }

    public void StartConvert(){
        new SaveImage().execute();
    }

    class SaveImage extends AsyncTask<String, String, String> {

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
            if(HARRY_ImageFolderActivity.mContext!=null){
                HARRY_ImageFolderActivity.mContext.finish();
            }
            finish();
        }
    }

    public void createPdf() {
        try {
            pdfFolder = new File(HARRY_Help.getFolderPath(mContext)+"/Created Pdf");
            pdfFolder.mkdirs();

            myPDF = new File(pdfFolder.getAbsolutePath() + "/Pdf_" + System.currentTimeMillis() + ".pdf");

            OutputStream output = new FileOutputStream(myPDF);
            Document document;
            document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, output);
            document.open();
            for (int i = 0; i < myList.size(); i++) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(myList.get(i).getImageUrl())));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                // instantiate itext image
                com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(byteArray);
                img.scaleToFit(PageSize.A4);
                img.setAbsolutePosition(
                        (PageSize.A4.getWidth() - img.getScaledWidth()) / 2,
                        (PageSize.A4.getHeight() - img.getScaledHeight()) / 2
                );
                document.add(img);
                // add a new page to the document to maintain 1 image per page
                document.newPage();
            }
            myList.clear();
            document.close();
        } catch (Exception e) {
            showLog(e.toString());
        }
    }
}
