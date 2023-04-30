package harry.bjg.documentreaderofficereader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.wxiwei.office.constant.EventConstant;
import com.wxiwei.office.macro.DialogListener;
import com.wxiwei.office.res.ResKit;
import com.wxiwei.office.ss.sheetbar.SheetBar;
import com.wxiwei.office.system.IMainFrame;
import com.wxiwei.office.system.MainControl;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HARRY_OfficeFileViewActivity extends AppCompatActivity implements IMainFrame {

    Activity mContext;
    TextView title;
    ImageView back;

    private LinearLayout appFrame;
    private Object bg = Integer.valueOf(-1);
    private SheetBar bottomBar;
    public MainControl control;
    private String filePath;
    private View gapView;
    private boolean isThumbnail;
    private boolean writeLog = true;
    private FrameLayout adContainerView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.harry_activity_office_files);

        HARRY_Help.width = getResources().getDisplayMetrics().widthPixels;
        HARRY_Help.height = getResources().getDisplayMetrics().heightPixels;

        mContext = this;
        HARRY_Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_officeview_id));
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

        control = new MainControl(this);
        appFrame = (LinearLayout) findViewById(R.id.dataLayout);
        String str = "path";
        filePath = getIntent().getStringExtra(str);
        if (getIntent() != null) {
            filePath = getIntent().getStringExtra(str);
            title.setText((CharSequence) getIntent().getStringExtra("name"));
            control.openFile(filePath);
        }

        setLay();
    }

    private void setLay() {
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        HARRY_Help.setSize(back, 96, 96, false);
    }

    public void onBackPressed() {
//        Object actionValue = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
//        if (actionValue == null || !((Boolean) actionValue).booleanValue()) {
//            if (control.getReader() != null) {
//                control.getReader().abortReader();
//            }
//            MainControl mainControl = control;
//            if (mainControl == null || !mainControl.isAutoTest()) {
//                super.onBackPressed();
//            } else {
//                System.exit(0);
//            }
//        } else {
//            control.actionEvent(EventConstant.PG_SLIDESHOW_END, null);
//        }

        if(interstitialAd.isLoaded()){
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    Object actionValue = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
                    if (actionValue == null || !((Boolean) actionValue).booleanValue()) {
                        if (control.getReader() != null) {
                            control.getReader().abortReader();
                        }
                        MainControl mainControl = control;
                        if (mainControl == null || !mainControl.isAutoTest()) {
                            finish();
                        } else {
                            System.exit(0);
                        }
                    } else {
                        control.actionEvent(EventConstant.PG_SLIDESHOW_END, null);
                    }
                }
            });
            interstitialAd.show();
        }else {
            Object actionValue = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
            if (actionValue == null || !((Boolean) actionValue).booleanValue()) {
                if (control.getReader() != null) {
                    control.getReader().abortReader();
                }
                MainControl mainControl = control;
                if (mainControl == null || !mainControl.isAutoTest()) {
                    super.onBackPressed();
                } else {
                    System.exit(0);
                }
            } else {
                control.actionEvent(EventConstant.PG_SLIDESHOW_END, null);
            }
        }

    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    public void onDestroy() {
        dispose();
        super.onDestroy();
    }

    public void showProgressBar(boolean z) {
        setProgressBarIndeterminateVisibility(z);
    }

    public void fileShare() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Uri.fromFile(new File(filePath)));
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.putExtra("android.intent.extra.STREAM", arrayList);
        intent.setType("application/octet-stream");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.sys_share_title)));
    }

    public Dialog onCreateDialog(int i) {
        return control.getDialog(this, i);
    }

    public boolean doActionEvent(int i, Object obj) {
        if (i == 0) {
            onBackPressed();
        } else if (i == 25) {
            setTitle((String) obj);
        } else if (i == 536870913) {
            fileShare();
        } else if (i == 788529152) {
            String trim = ((String) obj).trim();
            if (trim.length() <= 0 || !control.getFind().find(trim)) {
                setFindBackForwardState(false);
//                toast.setText(getLocalString("DIALOG_FIND_NOT_FOUND"));
//                toast.show();
            } else {
                setFindBackForwardState(true);
            }
        } else if (i != 1073741828) {
            switch (i) {
                case EventConstant.APP_DRAW_ID:
                    control.getSysKit().getCalloutManager().setDrawingMode(1);
                    appFrame.post(new Runnable() {
                        public void run() {
                            control.actionEvent(EventConstant.APP_INIT_CALLOUTVIEW_ID, null);
                        }
                    });
                    break;
                case EventConstant.APP_BACK_ID:
                    try {
                        control.getSysKit().getCalloutManager().setDrawingMode(0);
                        break;
                    } catch (Exception e) {
                        control.getSysKit().getErrorKit().writerLog(e);
                        break;
                    }
                default:
                    return false;
            }
        } else {
            bottomBar.setFocusSheetButton(((Integer) obj).intValue());
        }
        return true;
    }

    public void openFileFinish() {
        View view = new View(getApplicationContext());
        gapView = view;
        view.setBackgroundColor(-7829368);
        appFrame.addView(gapView, new LayoutParams(-1, 1));
        appFrame.addView(control.getView(), new LayoutParams(-1, -1));
    }

    public int getBottomBarHeight() {
        SheetBar sheetBar = bottomBar;
        if (sheetBar != null) {
            return sheetBar.getSheetbarHeight();
        }
        return 0;
    }

    public String getAppName() {
        return getString(R.string.app_name);
    }

    public String getLocalString(String str) {
        return ResKit.instance().getLocalString(str);
    }

    public void setWriteLog(boolean z) {
        writeLog = z;
    }

    public boolean isWriteLog() {
        return writeLog;
    }

    public void setThumbnail(boolean z) {
        isThumbnail = z;
    }

    public Object getViewBackground() {
        return bg;
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public File getTemporaryDirectory() {
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir != null) {
            return externalFilesDir;
        }
        return getFilesDir();
    }

    public void dispose() {
        MainControl mainControl = control;
        if (mainControl != null) {
            mainControl.dispose();
            control = null;
        }
        bottomBar = null;
        LinearLayout linearLayout = appFrame;
        if (linearLayout != null) {
            int childCount = linearLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                appFrame.getChildAt(i);
            }
            appFrame = null;
        }
    }

    public void changePage() {
    }

    public void changeZoom() {
    }

    public void completeLayout() {
    }

    public void error(int i) {
    }

    @Override
    public void fullScreen(boolean fullscreen) {
    }

    public Activity getActivity() {
        return this;
    }

    public DialogListener getDialogListener() {
        return null;
    }

    public byte getPageListViewMovingPosition() {
        return 0;
    }

    public String getTXTDefaultEncode() {
        return "GBK";
    }

    public int getTopBarHeight() {
        return 0;
    }

    public byte getWordDefaultView() {
        return 0;
    }

    public boolean isChangePage() {
        return true;
    }

    public boolean isDrawPageNumber() {
        return true;
    }

    public boolean isIgnoreOriginalSize() {
        return false;
    }

    public boolean isPopUpErrorDlg() {
        return true;
    }

    public boolean isShowFindDlg() {
        return true;
    }

    public boolean isShowPasswordDlg() {
        return true;
    }

    public boolean isShowProgressBar() {
        return true;
    }

    public boolean isShowTXTEncodeDlg() {
        return true;
    }

    public boolean isShowZoomingMsg() {
        return true;
    }

    public boolean isTouchZoom() {
        return true;
    }

    public boolean isZoomAfterLayoutForWord() {
        return true;
    }

    public boolean onEventMethod(View view, MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2, byte b) {
        return false;
    }

    public void setFindBackForwardState(boolean z) {
    }

    public void setIgnoreOriginalSize(boolean z) {
    }

    public void updateToolsbarStatus() {
    }

    public void updateViewImages(List<Integer> list) {
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
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_officeview_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }

}
