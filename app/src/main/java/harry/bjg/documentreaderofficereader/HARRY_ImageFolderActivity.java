package harry.bjg.documentreaderofficereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.adapter.HARRY_IF_Adapter;
import harry.bjg.documentreaderofficereader.model.HARRY_ImageData;
import harry.bjg.documentreaderofficereader.model.HARRY_ImageFolderData;

import static harry.bjg.documentreaderofficereader.HARRY_Help.gone;
import static harry.bjg.documentreaderofficereader.HARRY_Help.myList;
import static harry.bjg.documentreaderofficereader.HARRY_Help.showLog;

public class HARRY_ImageFolderActivity extends AppCompatActivity {

    public static Activity mContext;
    TextView title;
    ImageView back;

    RecyclerView list;
    ArrayList<HARRY_ImageData> myVideo = new ArrayList<>();
    ArrayList<HARRY_ImageFolderData> folderData = new ArrayList<>();
    HARRY_IF_Adapter if_adapter;

    LinearLayout bottom_lay, addLay;
    ImageView done;
    TextView count;

    ProgressDialog progress;
    File pdfFolder,myPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_activity_image_folder);

        mContext = this;
        HARRY_Help.FS(mContext);

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
        HARRY_Help.myList.clear();
    }

    void init() {
        list = findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(mContext, 3));
        if_adapter = new HARRY_IF_Adapter(mContext, folderData);
        list.setAdapter(if_adapter);

        bottom_lay = findViewById(R.id.bottom_lay);
        addLay = findViewById(R.id.addLay);
        done = findViewById(R.id.done);
        count = findViewById(R.id.count);

        progress = HARRY_Help.setPD(mContext, "Creating Pdf...", false);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myList.size() >= 2) {
//                    StartConvert();
                    if(interstitialAd.isLoaded()){
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                StartConvert();
                            }
                        });
                        interstitialAd.show();
                    }else {
                        StartConvert();
                    }

                } else {
                    HARRY_Help.Toast(mContext, "2 Image Require");
                }
            }
        });

        if(HARRY_CreateDocActivity.mSingle){
            gone(bottom_lay);
            gone(done);
        }
    }

    void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        count.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

        HARRY_Help.setSize(back, 96, 96, false);
        HARRY_Help.setSize(done, 96, 96, false);

        HARRY_Help.setSize(bottom_lay, 1080, 210, false);
        HARRY_Help.setMargin(list, 20,0,20,0, false);
    }

    class Get_Video extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            myVideo.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT};
            // Can include more data for more details and check it.

            final Cursor audioCursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null,
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");

            if (audioCursor != null) {
                if (audioCursor.moveToFirst()) {
                    do {
                        try {
                            String name = audioCursor
                                    .getString(audioCursor
                                            .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                            String width = audioCursor
                                    .getString(audioCursor
                                            .getColumnIndex(MediaStore.Images.Media.WIDTH));

                            String height = audioCursor
                                    .getString(audioCursor
                                            .getColumnIndex(MediaStore.Images.Media.HEIGHT));

                            String path = audioCursor.getString(audioCursor
                                    .getColumnIndex(MediaStore.Images.Media.DATA));

                            int w = Integer.parseInt(width);
                            int h = Integer.parseInt(height);

                            if (!name.endsWith(".gif")) {
                                myVideo.add(new HARRY_ImageData(name, path, w, h));
                            }
                        } catch (Exception e) {
                            showLog(e.toString());
                        }

                    } while (audioCursor.moveToNext());
                }
            }
            audioCursor.close();

            folderData.clear();
            ArrayList<HARRY_ImageData> files;
            for (int i = 0; i < myVideo.size(); i++) {
                String folder = new File(myVideo.get(i).getImageUrl()).getParent();

                boolean b = false;
                int pos = 0;
                for (int k = 0; k < folderData.size(); k++) {
                    if (folderData.get(k).getfolder().equals(folder)) {
                        b = true;
                        pos = k;
                    }
                }

                if (b) {
                    ArrayList<HARRY_ImageData> fs = folderData.get(pos).getPath();
                    fs.add(myVideo.get(i));
                } else {
                    files = new ArrayList<>();
                    files.add(myVideo.get(i));
                    folderData.add(new HARRY_ImageFolderData(folder, files));
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Get_Video().execute();
        addLay.removeAllViews();
        for (int i = 0; i < myList.size(); i++) {
            addLay(myList.get(i).getImageUrl());
        }
    }

    public void addLay(final String Path) {

        count.setText("(" + myList.size() + ")");

        final View myview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.harry_myview, null, false);

        RelativeLayout mainLay = myview.findViewById(R.id.mainLay);
        HARRY_Help.setSize(mainLay,150,150,false);
        HARRY_Help.setMargin(mainLay,30,0,0,0,false);

        ImageView thumb = myview.findViewById(R.id.thumb);
        ImageView close = myview.findViewById(R.id.close);
        HARRY_Help.setSize(close,38,38,false);

        addLay.addView(myview);

        Glide.with(mContext).load(Path).into(thumb);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLay.removeView(myview);
                for (int i = 0; i < myList.size(); i++) {
                    if(Path.equals(myList.get(i).getImageUrl())){
                        myList.remove(i);
                    }
                }
                count.setText("(" + myList.size() + ")");
            }
        });
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
    InterstitialAd interstitialAd;
    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_imagefolderdone_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }
}
