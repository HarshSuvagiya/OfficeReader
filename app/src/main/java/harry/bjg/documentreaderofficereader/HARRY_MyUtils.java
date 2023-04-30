package harry.bjg.documentreaderofficereader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Admin on 05-03-2018.
 */

public class HARRY_MyUtils {

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    static Context cn;

    public HARRY_MyUtils(Context cn) {
        this.cn = cn;
        pref = cn.getSharedPreferences(cn.getPackageName(), Context.MODE_PRIVATE);
        editor = pref.edit();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    public static Bitmap getBitmapwithAlpha(Bitmap bitmap, int opacity) {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, Mode.DST_IN);
        return mutableBitmap;
    }


    public static Bitmap gettextbitmap(String text, int text_color, Typeface tf, int shadowx, int shadowy, int rad) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        if (tf != null)
            textPaint.setTypeface(tf);
        textPaint.setColor(text_color);
        textPaint.setTextSize(80);

        if (shadowx >= 0 && shadowy >= 0)
            textPaint.setShadowLayer(rad, shadowx, shadowy, Color.parseColor("#000000"));


        int width = (int) textPaint.measureText(text);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint, width, Layout.Alignment
                .ALIGN_CENTER, 1.0f, 4.0f, true);
// Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(width, mTextLayout.getHeight(), Config.ARGB_4444);
        Canvas c = new Canvas(b);
// Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        c.drawPaint(paint);
// Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();
        return b;
    }


    public static void openVideo(Context cn, String uri) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(uri), "video/*");
            cn.startActivity(intent);

        } catch (Exception e) {
            HARRY_MyUtils.Toast(cn, "can't play from here");
        }
    }

    public static void openFile(Context cn, String path) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String ext = path.substring(path.lastIndexOf(".") + 1, path.length());
        String mimeType = myMime.getMimeTypeFromExtension(ext);
        newIntent.setDataAndType(Uri.fromFile(new File(path)), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            cn.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            HARRY_MyUtils.Toast(cn, "No handler for this type of file.");
        }
    }

    public static int getgridEqualSpace(Context cn, int itemwidth, int numcolumn) {
        int width = cn.getResources().getDisplayMetrics().widthPixels;
        int itw = (itemwidth * width) / 1080;
        int space = (width - (numcolumn * itw)) / (numcolumn + 1);
        return space;
    }

    public static Bitmap getBitmapFromView(FrameLayout viewGroup) {
        try {
            viewGroup.setDrawingCacheEnabled(true);
            viewGroup.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(viewGroup.getDrawingCache());
            viewGroup.setDrawingCacheEnabled(false);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getBitmapFromText(String text, float textSize, int textColor, Typeface tp) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        if (tp != null)
            paint.setTypeface(tp);
        paint.setTextAlign(Paint.Align.LEFT);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    public static Bitmap getBitmapFromText(String text, float textSize, int textColor, Typeface tp, int shadowcolor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        if (tp != null)
            paint.setTypeface(tp);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setShadowLayer(12, 0, 0, shadowcolor);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public static String getmilliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


    public static boolean isAccessibilitySettingsOn(Context cn) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Secure.getInt(cn.getContentResolver(), "accessibility_enabled");
        } catch (SettingNotFoundException e) {
        }
        if (accessibilityEnabled == 1) {
            String services = Secure.getString(cn.getContentResolver(), "enabled_accessibility_services");
            if (services != null) {
                return services.toLowerCase().contains(cn.getPackageName().toLowerCase());
            }
        }
        return false;
    }

    public static void getPermissionAccessibilty(Activity activity) {
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 100);
    }

    public static Bitmap getBitmapfromPath(String path, View parent) {
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, parent.getWidth(), parent.getHeight(), true);
        return bitmap;
    }

    public static Bitmap getBitmapfromPath(String path, int width, int height) {
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return bitmap;
    }

    public static void pickImagefromGallery(Activity a, int RequstCode) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        a.startActivityForResult(intent, RequstCode);


    }

    public static void pickVideofromGallery(Activity a, int RequstCode) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        a.startActivityForResult(intent, RequstCode);
    }


    public static void pickFilefromType(Activity a, int RequstCode, String type) {
//        Intent i = new Intent(Intent.ACTION_CHOOSER);
//        i.setType(type);
//        a.startActivityForResult(i, RequstCode);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, type);

        a.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), RequstCode);


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }


    public static Boolean checkoverlay(Activity activity, int reqcode) {
        Boolean b = true;
        Context cn = activity.getApplicationContext();
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(cn)) {
                b = false;
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + cn.getPackageName()));
                activity.startActivityForResult(intent, reqcode);
            } else {
                b = true;
            }
        }
        return b;
    }


    public static boolean checkSystemWritePermission(Context cn, int Req_Code) {
        boolean retVal = true;
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            retVal = System.canWrite(cn);
        }
        if (!retVal)
            permissionSystemSettings(((Activity) cn), Req_Code);
        return retVal;
    }

    public static void permissionSystemSettings(Activity activity, int ReqCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, ReqCode);
    }


    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static void addContact(Context cn, String DisplayName, String MobileNumber, Uri Photo) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        if (Photo != null) {

            try {
                InputStream iStream = cn.getContentResolver().openInputStream(Photo);
                byte[] bytes = HARRY_MyUtils.getBytes(iStream);
//                ops.add(ContentProviderOperation.newInsert(
//                        ContactsContract.Data.CONTENT_URI)
//                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                        .withValue(ContactsContract.Data.MIMETYPE,
//                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                        .withValue(
//                                StructuredName.PHOTO_URI,
//                                bytes).build());


                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes)
                        .build());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        // Asking the Contact provider to create a new contact
        try {
            cn.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(cn, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("MissingPermission")
    public static void makeCall(Context cn, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cn.startActivity(intent);
    }

    public static void makeSms(Context cn, String number) {
        cn.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }


    public static String saveBitmap(Context cn, Bitmap bitmap, String prefix) {
        String path;
        try {
            path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            Integer counter = 0;
            File f = new File(path, cn.getString(R.string.app_name));
            if (!f.exists())
                f.mkdir();

            String tm = gettimestring(java.lang.System.currentTimeMillis(), "dd_MM_yy_ss");
            File file = new File(path, cn.getString(R.string.app_name) + "/" + prefix + tm + ".png");
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

//            Images.Media.insertImage(cn.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            HARRY_MyUtils.Toast(cn, "Saved Successfully");
            path = file.getAbsolutePath();
            MediaScannerConnection.scanFile(cn, new String[]{file.getPath()}, new String[]{"image/png"}, null);


            return path;

        } catch (Exception e) {
            HARRY_MyUtils.Toast(cn, "Failed to Save");
            return null;
        }
    }


    public static String saveBitmappath(Context cn, Bitmap bitmap, String prefix) {
        String path;
        try {
            path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            Integer counter = 0;
            File f = cn.getFilesDir();
//            File f = new File(path, cn.getString(R.string.app_name));
            if (!f.exists())
                f.mkdir();

//            String tm = gettimestring(java.lang.System.currentTimeMillis(), "dd_MM_yy_ss");
//            File file = new File(path, cn.getString(R.string.app_name) + "/" + prefix + tm + ".png");

            File file = new File(f, prefix + ".png");
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

//            Images.Media.insertImage(cn.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
//            HARRY_MyUtils.Toast(cn, "Saved Successfully");
            path = file.getAbsolutePath();
            return path;

        } catch (Exception e) {
            HARRY_MyUtils.Toast(cn, "Failed to Save");
            return null;
        }
    }


    public static ArrayList<Bitmap> getImageFromAsset(Context context, String nameFolder) {
        ArrayList<Bitmap> listStrImage = new ArrayList();
        String[] files = new String[0];
        try {
            files = context.getAssets().list(nameFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Arrays.asList(files).size(); i++) {
// nameFolder + "/" + ((String) Arrays.asList(files).get(i))

            String path = nameFolder + "/" + Arrays.asList(files).get(i);
            listStrImage.add(getBitmapFromAsset(context, path));
        }

        return listStrImage;
    }

    public static Bitmap getColorBit(String colorcode) {
        int colorInt = Color.parseColor(colorcode);
        Bitmap bmp = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(colorInt);

        return bmp;
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
// handle exception
            Log.e("", "");
        }

        return bitmap;
    }

    public static Bitmap bitmapOverlay(Bitmap bmp1, Bitmap bmp2, int left, int top) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);

        int l = left - (bmp2.getWidth() / 2);
        int t = top - (bmp2.getHeight() / 2);

        canvas.drawBitmap(bmp2, l, t, null);


        return bmOverlay;
    }


    public static Bitmap getRounndCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
//        paint.setAlpha(250);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        try {
            String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } catch (Exception e) {
            Log.e("AAA", e.toString());
        }


        return null;
    }

    public static Bitmap getBitmapResize(Context cn, Bitmap mainBitmap, int lw, int lh) {

        int layoutwidth = lw;
        int layoutheight = lh;
        int imagewidth = mainBitmap.getWidth();
        int imageheight = mainBitmap.getHeight();
        int newwidth = 0;
        int newheight = 0;
        if (imagewidth >= imageheight) {
            newwidth = layoutwidth;
            newheight = (newwidth * imageheight) / imagewidth;
            if (newheight > layoutheight) {
                newwidth = (layoutheight * newwidth) / newheight;
                newheight = layoutheight;
            }
        } else {
            newheight = layoutheight;
            newwidth = (newheight * imagewidth) / imageheight;
            if (newwidth > layoutwidth) {
                newheight = (newheight * layoutwidth) / newwidth;
                newwidth = layoutwidth;
            }
        }


        Bitmap bitmap = Bitmap.createScaledBitmap(mainBitmap, newwidth, newheight, true);
        return bitmap;
    }

    public static boolean checknotificationaccess(Context cn) {
        String pkg = cn.getPackageName();
        boolean b = false;
        try {
            b = Secure.getString(cn.getContentResolver(), "enabled_notification_listeners").contains(pkg);
        } catch (Exception e) {
            b = false;
        }
        if (!b) {
            String appname = cn.getString(R.string.app_name);
            HARRY_MyUtils.Toast(cn, appname + " Select and Give Permission", 1);
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            cn.startActivity(intent);
        }
        return b;
    }

    public static String dtformt(Long str) {
        return time_msg(str, java.lang.System.currentTimeMillis(), 1000).toString();
    }

    private static CharSequence time_msg(long j, long j2, long j3) {
        StringBuilder stringBuilder = new StringBuilder();
        Resources.getSystem();
        Integer obj = j2 >= j ? Integer.valueOf(1) : null;
        long abs = Math.abs(j2 - j);
        if (abs < 60000 && j3 < 60000) {
            abs /= 1000;
            if (abs <= 10) {
                stringBuilder.append("Just Now");
            } else if (obj != null) {
                stringBuilder.append(abs);
                stringBuilder.append("s");
            } else {
                stringBuilder.append("Just Now");
            }
        } else if (abs < 3600000 && j3 < 3600000) {
            abs /= 60000;
            if (obj != null) {
                stringBuilder.append(abs);
                stringBuilder.append("m");
            } else {
                stringBuilder.append("Just Now");
            }
        } else if (abs < 86400000 && j3 < 86400000) {
            abs /= 3600000;
            if (obj != null) {
                stringBuilder.append(abs);
                stringBuilder.append("h");
            } else {
                stringBuilder.append("Just Now");
            }
        } else if (abs >= 604800000 || j3 >= 604800000) {
            stringBuilder.append(DateUtils.formatDateRange(null, j, j, 0));
        } else {
            stringBuilder.append(DateUtils.getRelativeTimeSpanString(j, j2, j3));
        }
        return stringBuilder.toString();
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNumber;
        }
        String contactName = phoneNumber;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }


    public static boolean generatetxt(Context context, String path, String sFileName, String sBody) {
        try {
            File root = new File(path);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void sharetextonWhatsApp(Context cn, String txt) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, txt);
        try {
            cn.startActivity(whatsappIntent);
        } catch (ActivityNotFoundException ex) {
            HARRY_MyUtils.Toast(cn, "Whatsapp have not been installed.");
        }
    }

    public static void shareImage(Context cn, String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
        cn.startActivity(Intent.createChooser(intent, "Share Image"));
    }


    public static void shareMyMp4(Context cn, File fileWithinMyDir) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("video/mp4");
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileWithinMyDir));
            cn.startActivity(Intent.createChooser(i, "Share Video"));
        } catch (Exception e) {
            Log.e("Err", e.toString());
        }
    }

    public static void shareMyMp3(Context cn, File fileWithinMyDir) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("music/mp3");
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileWithinMyDir));
            cn.startActivity(Intent.createChooser(i, "Share Mp3"));
        } catch (Exception e) {
            Log.e("Err", e.toString());
        }
    }

    public static void shareAnyFile(Context cn, String path) {

        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_SEND);
        String ext = path.substring(path.lastIndexOf(".") + 1, path.length());
        String mimeType = myMime.getMimeTypeFromExtension(ext);
        if (ext.equalsIgnoreCase("pdf")) {
            newIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
            newIntent.setType(mimeType);
        } else if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg")) {

            newIntent.setType(mimeType);
            newIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cn.startActivity(Intent.createChooser(newIntent, "Share Image"));
            return;


        } else {
            newIntent.setDataAndType(Uri.fromFile(new File(path)), mimeType);
        }

        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            cn.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            HARRY_MyUtils.Toast(cn, "No handler for this type of file.");
        }

    }


    public static void share(Context cn) {
        String shareBody = "https://play.google.com/store/apps/details?id="
                + cn.getPackageName();
        Intent sharingIntent = new Intent(
                Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                shareBody);
        cn.startActivity(Intent.createChooser(sharingIntent,
                "Share via"));
    }

    public static void rate(Context cn) {
        try {
            cn.startActivity(new Intent(
                    "android.intent.action.VIEW", Uri
                    .parse("market://details?id="
                            + cn.getPackageName())));
        } catch (ActivityNotFoundException e) {
            cn.startActivity(new Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("https://play.google.com/store/apps/details?id="
                            + cn.getPackageName())));
        }
    }

    public static LayoutParams getParamsR(Context cn, int width, int height) {
        LayoutParams layoutParams = new LayoutParams(
                cn.getResources().getDisplayMetrics().widthPixels * width / 1080,
                cn.getResources().getDisplayMetrics().heightPixels * height / 1920
        );

        return layoutParams;
    }

    public static LinearLayout.LayoutParams getParamsL(Context cn, int width, int height) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                cn.getResources().getDisplayMetrics().widthPixels * width / 1080,
                cn.getResources().getDisplayMetrics().heightPixels * height / 1920
        );

        return layoutParams;
    }

    public static LinearLayout.LayoutParams getParamsLw(Context cn, int width, int height) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                cn.getResources().getDisplayMetrics().widthPixels * width / 1080,
                cn.getResources().getDisplayMetrics().widthPixels * height / 1080
        );

        return layoutParams;
    }


    public static LinearLayout.LayoutParams getParamsLSquare(Context cn, int width) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                cn.getResources().getDisplayMetrics().widthPixels * width / 1080,
                cn.getResources().getDisplayMetrics().widthPixels * width / 1080
        );

        return layoutParams;
    }

    public static LinearLayout.LayoutParams getParamsLSquareH(Context cn, int width) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                cn.getResources().getDisplayMetrics().heightPixels * width / 1920,
                cn.getResources().getDisplayMetrics().heightPixels * width / 1920
        );

        return layoutParams;
    }

    public static LayoutParams getParamsRSquare(Context cn, int width) {
        LayoutParams layoutParams = new LayoutParams(
                cn.getResources().getDisplayMetrics().widthPixels * width / 1080,
                cn.getResources().getDisplayMetrics().widthPixels * width / 1080
        );

        return layoutParams;
    }

    public static void copyClipboard(Context cn, String txt) {
        ClipboardManager clipboard = (ClipboardManager) cn.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy", txt);
        clipboard.setPrimaryClip(clip);
        HARRY_MyUtils.Toast(cn, "Copy Message...");
    }

    public static String getNumbers(String string) {
        return string.replaceAll("[^0-9]", "");
    }

    public static Bitmap getMask(Context mContext, Bitmap userimageBitmap, int rs, View v) {

        Bitmap maskBitmap = BitmapFactory.decodeResource(mContext.getResources(), rs);
        int w = v.getWidth();
        int m = v.getHeight();

        userimageBitmap = Bitmap.createScaledBitmap(userimageBitmap, w, m, true);
        maskBitmap = Bitmap.createScaledBitmap(maskBitmap, w, m, true);
        Bitmap result = Bitmap.createBitmap(maskBitmap.getWidth(),
                maskBitmap.getHeight(), Config.ARGB_8888);

        Canvas mCanvas = new Canvas(result);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        mCanvas.drawBitmap(userimageBitmap, 0, 0, null);
        mCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);

        return result;
    }

    public static Bitmap getMask(Context mContext, Bitmap bit1, Bitmap maskBitmap, View v) {

        int w = v.getWidth();
        int m = v.getHeight();

        bit1 = Bitmap.createScaledBitmap(bit1, w, m, true);
        maskBitmap = Bitmap.createScaledBitmap(maskBitmap, w, m, true);
        Bitmap result = Bitmap.createBitmap(maskBitmap.getWidth(),
                maskBitmap.getHeight(), Config.ARGB_8888);

        Canvas mCanvas = new Canvas(result);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        mCanvas.drawBitmap(bit1, 0, 0, null);
        mCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);

        return result;
    }

    public static Bitmap getMask(Context mContext, Bitmap bit1, Bitmap maskBitmap, int w, int m) {

        bit1 = Bitmap.createScaledBitmap(bit1, w, m, true);
        maskBitmap = Bitmap.createScaledBitmap(maskBitmap, w, m, true);
        Bitmap result = Bitmap.createBitmap(maskBitmap.getWidth(),
                maskBitmap.getHeight(), Config.ARGB_8888);

        Canvas mCanvas = new Canvas(result);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        mCanvas.drawBitmap(bit1, 0, 0, null);
        mCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);

        return result;
    }


    public static String downloadFile(String url, String foldername) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();
            String path = Environment.getExternalStorageDirectory() + "/" + foldername;
            File fs = new File(path);
            if (!fs.exists()) {
                fs.mkdir();
            }
            fs = new File(fs, "vdo_" + java.lang.System.currentTimeMillis());

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(fs));
            fos.write(buffer);
            fos.flush();
            fos.close();
            return fs.getAbsolutePath();
        } catch (FileNotFoundException e) {
            return null; // swallow a 404

        } catch (IOException e) {
            return null; // swallow a 404
        }
    }

    public static void copyRAWtoSDCard(Context cn, String folderName, int id) throws IOException {

        String path = Environment.getExternalStorageDirectory() + "/" + folderName;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        path = path + "/vdo_" + java.lang.System.currentTimeMillis() + ".mp4";
        File fd = new File(path);
        InputStream in = cn.getResources().openRawResource(id);
        FileOutputStream out = new FileOutputStream(fd);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static String copyRAWtoSDCard(Context cn, String folderName, String filename, String rawpath) throws IOException {

        String path = Environment.getExternalStorageDirectory() + "/" + folderName;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        path = path + "/" + filename + ".mp4";
        File fd = new File(path);
        rawpath = rawpath.substring(rawpath.lastIndexOf("/") + 1, rawpath.length());
        InputStream in = cn.getResources().openRawResource(Integer.parseInt(rawpath));
        FileOutputStream out = new FileOutputStream(fd);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
        return path;
    }


    public static void copyFileUsingStream(File source, String folderName, String filename) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {

            String path = Environment.getExternalStorageDirectory() + "/" + folderName;
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }
            File fd = new File(path + "/" + filename);
            is = new FileInputStream(source);
            os = new FileOutputStream(fd);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static ArrayList<File> getListFiles(String foldername, String extention) {
        ArrayList<File> inFiles = new ArrayList<File>();
        String path = Environment.getExternalStorageDirectory() + "/" + foldername;
        File parentDir = new File(path);
        if (!parentDir.exists())
            parentDir.mkdir();

        if (parentDir.exists()) {
            File[] files = parentDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    String ext = file.getAbsolutePath();
                    ext = ext.substring(ext.lastIndexOf(".") + 1, ext.length());
                    if (ext.equalsIgnoreCase(extention))
                        inFiles.add(file);
                }
            }
        }

        return inFiles;
    }


    public static ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        if (parentDir.exists()) {
            File[] files = parentDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    inFiles.add(file);
                }
            }
        }

        return inFiles;
    }

    public static ArrayList<String> getListFiles(String path) {
        ArrayList<String> inFiles = new ArrayList<String>();
        File parentDir = new File(path);
        if (parentDir.exists()) {
            File[] files = parentDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    String fpath = file.getPath();
                    inFiles.addAll(getListFiles(fpath));
                } else {
                    String fpath = file.getPath();
                    inFiles.add(fpath);
                }
            }
        }

        return inFiles;
    }

    public static ArrayList<String> getFolderfiles(String path) {
        ArrayList<String> inFiles = new ArrayList<String>();
        File parentDir = new File(path);
        if (parentDir.exists()) {
            File[] files = parentDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
//                    String fpath = file.getPath();
//                    inFiles.addAll(getListFiles(fpath));
                } else {
                    String fpath = file.getPath();
                    inFiles.add(fpath);
                }
            }
        }

        return inFiles;
    }


    public static String getFileExtension(String path) {
        File file = new File(path);
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getMimeType(String path) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static boolean checkPermission(Context context, String[] permission) {
        boolean b = true;
        for (String p : permission) {
            b = ActivityCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED;
            if (!b)
                break;
        }


        if (!b) {
            Activity a = ((Activity) context);
            ActivityCompat.requestPermissions(a, permission, 101);
        }

        return b;
    }


    public static void ToastImage(Context cn, Bitmap bitmap) {
        Toast t = new Toast(cn);
        ImageView i = new ImageView(cn);
        i.setImageBitmap(bitmap);
        t.setView(i);
        t.show();

    }

    public static void Toast(Context cn, String msg) {
//        Toast.makeText(cn,msg,Toast.LENGTH_SHORT).show();
        Toast(cn, msg, 0);
    }

    public static void Toast(Context cn, String msg, int lenght) {
        Toast.makeText(cn, msg, lenght).show();
    }


    public static long gettimeskip(int min) {
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        c.add(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, 0);
        long time = c.getTimeInMillis();
        return time;
    }

    public static long getskipday(int day) {
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.SECOND, 0);
        long time = c.getTimeInMillis();
        return time;
    }

    public static String gettimestring(long mili, String pattern) {
        SimpleDateFormat sd = new SimpleDateFormat(pattern);
        return sd.format(mili);
    }

    public static long getdatefromstring(String pattern, String sdate) throws ParseException {
        long mili;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = sdf.parse(sdate);
        mili = date.getTime();
        return mili;
    }

    public static String getRealPathFromURI(Activity activity, Uri contentUri) {

        String[] proj = {Images.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }


    @SuppressLint("MissingPermission")
    public static boolean isNetworkConnected(Context cn) {
        ConnectivityManager cm = (ConnectivityManager) cn.getSystemService(Context.CONNECTIVITY_SERVICE);

        Boolean b = cm.getActiveNetworkInfo() != null;
        if (!b)
            HARRY_MyUtils.Toast(cn, "Need Internet Connection");

        return cm.getActiveNetworkInfo() != null;
    }

    public static Uri getBitmapUri(Context cn, Bitmap cropBit) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        cropBit.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(cn.getContentResolver(), cropBit, "Title", null);
        return Uri.parse(path);

    }

    public static void setBrigtness(Context cn, int brightness) {

        brightness = (int) (brightness * (float) 255 / 100);
        ContentResolver cResolver = cn.getContentResolver();
        Window window = ((Activity) cn).getWindow();
        System.putInt(cResolver, System.SCREEN_BRIGHTNESS, brightness);
        WindowManager.LayoutParams layoutpars = window.getAttributes();
        layoutpars.screenBrightness = brightness;
        Log.e("AAA", "Brigtness : " + brightness);
        window.setAttributes(layoutpars);
    }

    public static void setAutoBrigtness(Context cn) {
        System.putInt(cn.getContentResolver(),
                System.SCREEN_BRIGHTNESS_MODE, System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }


    public static int getBrigtness(Context cn) {
        int brightness;
        try {
            ContentResolver cResolver = cn.getContentResolver();
            System.putInt(cResolver,
                    System.SCREEN_BRIGHTNESS_MODE, System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            brightness = System.getInt(cResolver, System.SCREEN_BRIGHTNESS);

        } catch (SettingNotFoundException e) {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
            brightness = -1;
        }
        return brightness;
    }


    public static boolean hasWriteSettingsPermission(Context context) {
        boolean ret = true;

        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            ret = System.canWrite(context);
        } else {
            ret = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        return ret;
    }

    public static void getWriteSettingsPermission(Context context, int reqcode) {
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            ((Activity) context).startActivityForResult(intent, reqcode);
        } else {
            ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.WRITE_SETTINGS}, reqcode);
        }
    }

    public static void setAutoRotation(Context context, Boolean enabled) {
        System.putInt(context.getContentResolver(), System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    public static boolean getAutoRotationOnOff(Context context) {
        int result = 0;
        try {
            result = System.getInt(context.getContentResolver(), System.ACCELEROMETER_ROTATION);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return result == 1;
    }

    public static Boolean hasGpsEnable(Context cn) {
        LocationManager locationManager = (LocationManager) cn
                .getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void getGpsManager(Context cn, int Req_gps) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ((Activity) cn).startActivityForResult(intent, Req_gps);
    }


    public static boolean isAirplaneModeOn(Context context) {
        if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR1) {
            return System.getInt(context.getContentResolver(),
                    System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public static void setFlightnMode(Context context, int Req_Flight) {
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivityForResult(intent, Req_Flight);
    }


    public static float getBatteryTemperature() {
        float temp = 0.0f;
        Intent batteryStatus = getBatteryStatusIntent(cn);
        if (batteryStatus != null) {
            temp = (float) (batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0);
        }
        return temp;
    }


    public static int getBatteryVoltage() {
        int volt = 0;
        Intent batteryStatus = getBatteryStatusIntent(cn);
        if (batteryStatus != null) {
            volt = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
        }
        return volt;
    }

    public static String getBatteryChargingSource() {
        Intent batteryStatus = getBatteryStatusIntent(cn);
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

        switch (chargePlug) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "WIRELESS";
            default:
                return "--";
        }
    }

    public static Intent getBatteryStatusIntent(Context context) {
        IntentFilter batFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        return context.registerReceiver(null, batFilter);
    }


    public static Boolean getConnectedStats(Context context) {

        Intent intent = context.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        return intent.getExtras().getBoolean("connected");
    }

    public static void setScreenTimeout(Context cn, int timeinsecond) {
        timeinsecond = timeinsecond * 1000;
        System.putInt(cn.getContentResolver(),
                System.SCREEN_OFF_TIMEOUT, timeinsecond);
    }

    public static void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        if (enabled) {
            if (isMobileDataOnOff(context)) {
                return;
            }
        } else {
            if (!isMobileDataOnOff(context)) {
                return;
            }
        }

        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }


    public static Boolean isMobileDataOnOff(Context cn) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) cn.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    public static Bitmap getBitmapfromUri(Context cn, Uri pickedImage) {
        String[] filePath = {Images.Media.DATA};
        Cursor cursor = cn.getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        cursor.close();
        return bitmap;
    }

    public static void deleteFile(String path) {
        File f = new File(path);
        if (f.exists())
            f.delete();

    }

    public static String formatTimeUnit(long millis) throws ParseException {
        String formatted = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(millis)));
        return formatted;
    }

    public static void launchApp(Context cn, String pckgName) {
        Intent launchIntent = cn.getPackageManager().getLaunchIntentForPackage(pckgName);
        if (launchIntent != null) {
            cn.startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }

    public static String getAppnameFromPckg(Context cn, String pckg) {
        final PackageManager pm = cn.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(pckg, 0);
        } catch (Exception e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    public static int getCountfromString(String word, String match) {
        String exp = "[^" + match + "]*" + match;
        Pattern pattern = Pattern.compile(exp);
//        Pattern pattern = Pattern.compile("[^#]*#");
        Matcher matcher = pattern.matcher(word);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }

    public static Boolean checkAppOverUsagePermission(Activity cn, int REQ_APPUSAGE) {
//        Boolean b = getstatusAppUsagePermission(cn);
        Boolean b = isAccessGranted(cn);
        if (!b) {

            try {
                HARRY_MyUtils.Toast(cn, "Need Prmission");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                cn.startActivityForResult(intent, REQ_APPUSAGE);
            } catch (Exception e) {
                b = false;
            }
        }
        return b;
    }


    public static boolean getstatusAppUsagePermission(final Context context) {
        // Usage Stats is theoretically available on API v19+, but official/reliable support starts with API v21.
        if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
            return false;
        }

        final AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        if (appOpsManager == null) {
            return false;
        }

        final int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            return false;
        }

        final long now = java.lang.System.currentTimeMillis();
        final UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 1000 * 10, now);
        Log.e("AAA", "Count : " + stats.size());
        Boolean b = stats != null && stats.size() > 0;
        Log.e("AAA", "Permission : " + b);
        return (stats != null && stats.size() > 0);
//        return (stats != null && !stats.isEmpty());
    }

    public static boolean isAccessGranted(Context cn) {
        try {
            PackageManager packageManager = cn.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(cn.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) cn.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (VERSION.SDK_INT > VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static void sortArray(ArrayList<File> fileList) {

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                long k = file1.lastModified() - file2.lastModified();
                if (k > 0) {
                    return -1;
                } else if (k == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

    }

    public static File makeBaseFolder(Context cn) {
        File root = Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/" + cn.getResources().getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;

    }

    public static void setRingtone(Context mContext, String filepath) {

        String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
        String Ringtonename = filename.substring(0, filename.lastIndexOf("."));

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, filepath);
        values.put(MediaStore.MediaColumns.TITLE, Ringtonename);
        values.put(MediaStore.MediaColumns.SIZE, filepath.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.ARTIST, mContext.getString(R.string.app_name));
        // values.put(MediaStore.Audio.Media.DURATION, 230);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        try {
            ContentResolver cr = mContext.getContentResolver();
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(filepath);
            cr.delete(uri, MediaStore.MediaColumns.DATA + "=\"" + filepath + "\"", null);
            Uri newUri = cr.insert(uri, values);
            Log.e("", "=====Enter ====" + newUri);
            RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE,
                    newUri);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("", "Error" + e.getMessage());
        }
    }


}