package org.ebookdroid.ui.viewer;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.foobnix.MyUtils;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.Intents;
import com.foobnix.android.utils.Keyboards;
import com.foobnix.android.utils.LOG;
//import com.foobnix.drive.GFile;
import com.foobnix.model.AppBook;
import com.foobnix.model.AppProfile;
import com.foobnix.model.AppSP;
import com.foobnix.model.AppState;
//import com.foobnix.pdf.info.ADS;
import com.foobnix.pdf.info.Android6;
import com.foobnix.pdf.info.ExtUtils;
import com.foobnix.pdf.info.Help1;
import com.foobnix.pdf.info.PasswordDialog;
import com.foobnix.pdf.info.R;
import com.foobnix.pdf.info.model.BookCSS;
import com.foobnix.pdf.info.view.BrightnessHelper;
import com.foobnix.pdf.info.widget.RecentUpates;
import com.foobnix.pdf.info.wrapper.DocumentController;
import com.foobnix.pdf.search.view.CloseAppDialog;
import com.foobnix.sys.TempHolder;
import com.foobnix.tts.TTSNotification;
import com.foobnix.ui2.FileMetaCore;
import com.foobnix.ui2.MainTabs2;
import com.foobnix.ui2.MyContextWrapper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import org.ebookdroid.common.settings.SettingsManager;
import org.ebookdroid.ui.viewer.viewers.PdfSurfaceView;
import org.emdev.ui.AbstractActionActivity;

import java.io.File;

public class VerticalViewActivity extends AbstractActionActivity<VerticalViewActivity, ViewerActivityController> {
    public static final DisplayMetrics DM = new DisplayMetrics();

    IView view;
    private FrameLayout frameLayout;
    TextView settitle;
    /**
     * Instantiates a new base viewer activity.
     */
    public VerticalViewActivity() {
        super();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        LOG.d("VerticalViewActivity", "onNewIntent");
        if (TTSNotification.ACTION_TTS.equals(intent.getAction())) {
            return;
        }
        if (!intent.filterEquals(getIntent())) {
            finish();
            startActivity(intent);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.emdev.ui.AbstractActionActivity#createController()
     */
    @Override
    protected ViewerActivityController createController() {
        return new ViewerActivityController(this);
    }

    private Handler handler;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
//        intetrstialTimeoutSec = ADS.FULL_SCREEN_TIMEOUT_SEC;
        Help1.FS(this);
//        DocumentController.doRotation(this);
        FileMetaCore.checkOrCreateMetaInfo(this);

        if (getIntent().getData() != null) {
            String path = getIntent().getData().getPath();
            final AppBook bs = SettingsManager.getBookSettings(path);
            // AppState.get().setNextScreen(bs.isNextScreen);
            if (bs != null) {
                // AppState.get().l = bs.l;
                AppState.get().autoScrollSpeed = bs.s;
                final boolean isTextFomat = ExtUtils.isTextFomat(bs.path);
                AppSP.get().isCut = isTextFomat ? false : bs.sp;
                AppSP.get().isCrop = bs.cp;
                AppSP.get().isDouble = false;
                AppSP.get().isDoubleCoverAlone = false;
                AppSP.get().isLocked = bs.getLock(isTextFomat);
                TempHolder.get().pageDelta = bs.d;
                if (AppState.get().isCropPDF && !isTextFomat) {
                    AppSP.get().isCrop = true;
                }
            }
            BookCSS.get().detectLang(path);
        }

        getController().beforeCreate(this);

        BrightnessHelper.applyBrigtness(this);

        if (AppState.get().isDayNotInvert) {
            setTheme(R.style.StyledIndicatorsWhite);
        } else {
            setTheme(R.style.StyledIndicatorsBlack);
        }
        super.onCreate(savedInstanceState);


        if (PasswordDialog.isNeedPasswordDialog(this)) {
            return;
        }
        setContentView(R.layout.activity_vertical_view);

        if (!Android6.canWrite(this)) {
            Android6.checkPermissions(this, true);
            return;
        }


        getController().createWrapper(this);
        frameLayout = (FrameLayout) findViewById(R.id.documentView);

        view = new PdfSurfaceView(getController());

        frameLayout.addView(view.getView());

        getController().afterCreate(this);

        // ADS.activate(this, adView);

        handler = new Handler();

        getController().onBookLoaded(new Runnable() {

            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isInitPosistion = Dips.screenHeight() > Dips.screenWidth();
                        isInitOrientation = AppState.get().orientation;
                    }
                }, 1000);
            }
        });

        init();
        resize();
        click();

        loadInterstitial();
        String path = null;
        try {
            path = getIntent().getData().getPath();
            settitle.setText(new File(path).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void click() {
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    ImageView btnback;


    private void resize() {
        settitle.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));
        Help1.setSize(btnback, 96, 96, false);
    }

    private void init() {
        btnback = findViewById(R.id.btnback);
        settitle = findViewById(R.id.settitle);
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(MyContextWrapper.wrap(context));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Android6.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DocumentController.doRotation(this);

        if (AppState.get().fullScreenMode == AppState.FULL_SCREEN_FULLSCREEN) {
            Keyboards.hideNavigation(this);
        }
        getController().onResume();
        if (handler != null) {
            handler.removeCallbacks(closeRunnable);
        }
        if (AppState.get().inactivityTime != -1) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            LOG.d("FLAG clearFlags", "FLAG_KEEP_SCREEN_ON", "add", AppState.get().inactivityTime);
        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int page = Math.round(getController().getDocumentModel().getPageCount() * Intents.getFloatAndClear(data, DocumentController.EXTRA_PERCENT));
            getController().getDocumentController().goToPage(page);
        }
    }

    boolean needToRestore = false;

    @Override
    protected void onPause() {
        super.onPause();
        LOG.d("onPause", this.getClass());
        getController().onPause();
        needToRestore = AppState.get().isAutoScroll;
        AppState.get().isAutoScroll = false;
        AppProfile.save(this);
        TempHolder.isSeaching = false;
        TempHolder.isActiveSpeedRead.set(false);

        if (handler != null) {
            handler.postDelayed(closeRunnable, AppState.APP_CLOSE_AUTOMATIC);
        }
//        GFile.runSyncService(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Analytics.onStart(this);
        if (needToRestore) {
            AppState.get().isAutoScroll = true;
            getController().getListener().onAutoScroll();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Analytics.onStop(this);
        RecentUpates.updateAll(this);
    }

    Runnable closeRunnable = new Runnable() {

        @Override
        public void run() {
            LOG.d("Close App");
            getController().closeActivityFinal(null);
            MainTabs2.closeApp(VerticalViewActivity.this);
        }
    };

    @Override
    protected void onDestroy() {

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    Dialog rotatoinDialog;
    Boolean isInitPosistion;
    int isInitOrientation;

    /*@Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        TempHolder.isActiveSpeedRead.set(false);
        if (isInitPosistion == null) {
            return;
        }

        final boolean currentPosistion = Dips.screenHeight() > Dips.screenWidth();

        if (ExtUtils.isTextFomat(getIntent()) && isInitOrientation == AppState.get().orientation) {

            if (rotatoinDialog != null) {
                try {
                    rotatoinDialog.dismiss();
                } catch (Exception e) {
                    LOG.e(e);
                }
            }

            if (isInitPosistion != currentPosistion) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setCancelable(false);
                dialog.setMessage(R.string.apply_a_new_screen_orientation_);
                dialog.setPositiveButton(R.string.yes, new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doConifChange();
                        isInitPosistion = currentPosistion;
                    }
                });
                rotatoinDialog = dialog.show();
                rotatoinDialog.getWindow().setLayout((int) (Dips.screenMinWH() * 0.8f), LayoutParams.WRAP_CONTENT);
            }
        } else {
            doConifChange();
        }

        isInitOrientation = AppState.get().orientation;
    }*/

    public void doConifChange() {
        try {
            if (!getController().getDocumentController().isInitialized()) {
                LOG.d("Skip onConfigurationChanged");
                return;
            }
        } catch (Exception e) {
            LOG.e(e);
            return;
        }

        AppProfile.save(this);

        if (ExtUtils.isTextFomat(getIntent())) {

            //float value = getController().getDocumentModel().getPercentRead();
            //Intents.putFloat(getIntent(),DocumentController.EXTRA_PERCENT, value);

            //LOG.d("READ PERCEnt", value);

            getController().closeActivityFinal(new Runnable() {

                @Override
                public void run() {
                    startActivity(getIntent());
                }
            });

        } else {
            getController().onConfigChanged();
//            activateAds();
        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getController().afterPostCreate();
    }

    @Override
    public boolean onGenericMotionEvent(final MotionEvent event) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 12) {
            return GenericMotionEvent12.onGenericMotionEvent(event, this);
        }
        return false;
    }

    @Override
    public boolean onKeyLongPress(final int keyCode, final KeyEvent event) {
        // Toast.makeText(this, "onKeyLongPress", Toast.LENGTH_SHORT).show();
        if (CloseAppDialog.checkLongPress(this, event)) {
            CloseAppDialog.showOnLongClickDialog(getController().getActivity(), null, getController().getListener());
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed() {

//        getController().closeActivityFinal(null);

        if (interstitialAd.isLoaded()) {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                    getController().closeActivityFinal(null);
                }
            });
            interstitialAd.show();
        } else {
            getController().closeActivityFinal(null);
        }
    }

    private volatile boolean isMyKey = false;

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        LOG.d("onKeyUp");
        if (isMyKey) {
            return true;
        }

        if (getController().getWrapperControlls().dispatchKeyEventUp(event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    long keyTimeout = 0;

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        LOG.d("onKeyDown");
        isMyKey = false;
        int repeatCount = event.getRepeatCount();
        if (repeatCount >= 1 && repeatCount < DocumentController.REPEAT_SKIP_AMOUNT) {
            isMyKey = true;
            return true;
        }

        if (repeatCount == 0 && System.currentTimeMillis() - keyTimeout < 250) {
            LOG.d("onKeyDown timeout", System.currentTimeMillis() - keyTimeout);
            isMyKey = true;
            return true;
        }

        keyTimeout = System.currentTimeMillis();


        if (getController().getWrapperControlls().dispatchKeyEventDown(event)) {
            isMyKey = true;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*    @Override
        public void onFinishActivity() {
            getController().closeActivityFinal(null);

        }*/
    InterstitialAd interstitialAd;

    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_verticalview_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }


}
