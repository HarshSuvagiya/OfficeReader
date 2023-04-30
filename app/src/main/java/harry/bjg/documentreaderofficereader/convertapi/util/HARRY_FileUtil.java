package harry.bjg.documentreaderofficereader.convertapi.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.R;

public class HARRY_FileUtil {
    public static final String DEFAULT_SAVE_PATH = "DEFAULT_SAVE_PATH";
    public final String CAMERA_TEMP_FILE_NAME = "temp.jpg";
    private Context context;
    public String defaultPath;
    private String docPath;

    /* renamed from: com.pa.pdftoword.navigationdrawer.pdfconverter.util.HARRY_FileUtil$CopyFileCallback */
    public interface CopyFileCallback {
        void onFailure();

        void onSuccess(String str);
    }

    public HARRY_FileUtil(Context context2) {
        this.context = context2;
        this.docPath = Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.app_name);
        this.defaultPath = Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.app_name);
    }

    public String getPath(Uri uri) {
        boolean isKitKat;
        if (VERSION.SDK_INT >= 19) {
            isKitKat = true;
        } else {
            isKitKat = false;
        }
        if (VERSION.SDK_INT < 19) {
            return null;
        }
        if (isKitKat && DocumentsContract.isDocumentUri(this.context, uri)) {
            String[] split = new String[0];
            if (isExternalStorageDocument(uri)) {
                String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                if ("primary".equalsIgnoreCase(split2[0])) {
                    return Environment.getExternalStorageDirectory() + "/" + split2[1];
                }
                return null;
            } else if (isDownloadsDocument(uri)) {
                return getDataColumn(ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
            } else {
                if (!isMediaDocument(uri)) {
                    return null;
                }
                String type = DocumentsContract.getDocumentId(uri).split(":")[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String str = "_id=?";
                return getDataColumn(contentUri, "_id=?", new String[]{split[1]});
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else {
            return null;
        }
    }

    public String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        String str = null;
        Log.d("getDataColumn", uri.toString());
        Cursor cursor = null;
        String str2 = "_data";
        try {
            cursor = this.context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                if (cursor != null) {
                    cursor.close();
                }
            } else {
                str = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                if (cursor != null) {
                    cursor.close();
                }
            }
            return str;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void createDefaultFolder() {
        File docsFolder = new File(this.docPath);
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        new File(this.defaultPath).mkdir();
    }

    public File getConvertedFilePath() {
        createDefaultFolder();
        File path = new File(this.docPath);
        return path.exists() ? path : new File(this.defaultPath);
    }

    public String saveConvertedFilePath(String path) {
        if (new File(path).exists()) {
        }
        return getConvertedFilePath().getAbsolutePath();
    }

    public String getFileExtension(Uri data) {
        String extension;
        if (data.getScheme().equals("content")) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(this.context.getContentResolver().getType(data));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(data.getPath())).toString());
        }
        Log.d("extension", extension);
        return extension;
    }

    public void writeDownloadConvertedFile(HARRY_ConvertedFile convertedFile, File sourceFile, CopyFileCallback callback) {
        createDefaultFolder();
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            try {
                String outputName = convertedFile.getOutputName();
                String newName = convertedFile.getOutputName() + "." + convertedFile.getOutputFormat();
                String outputPath = getConvertedFilePath() + File.separator + newName;
                File file = new File(outputPath);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int read = fileInputStream.read(buffer);
                        if (read != -1) {
                            fileOutputStream.write(buffer, 0, read);
                        } else {
                            fileInputStream.close();
                            try {
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                Log.e("save file", newName);
                                callback.onSuccess(outputPath);
                                if (VERSION.SDK_INT >= 19) {
                                    Intent scanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                    File file2 = new File(outputPath);
                                    scanIntent.setData(Uri.fromFile(file2));
                                    this.context.sendBroadcast(scanIntent);
                                    return;
                                }
                                this.context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                                return;
                            } catch (Exception e) {
                                FileOutputStream fileOutputStream2 = fileOutputStream;
                                e.printStackTrace();
                                callback.onFailure();
                            }
                        }
                    }
                } catch (Exception e2) {
                    FileOutputStream fileOutputStream3 = fileOutputStream;
                    FileInputStream fileInputStream2 = fileInputStream;
                    e2.printStackTrace();
                    callback.onFailure();
                }
            } catch (Exception e3) {
                FileInputStream fileInputStream3 = fileInputStream;
                e3.printStackTrace();
                callback.onFailure();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
            callback.onFailure();
        }
    }

    public String renameFile(File oldFile, String newName, String format) {
        if (oldFile.exists()) {
            File newFile = new File(getConvertedFilePath() + File.separator + newName + "." + format);
            if (oldFile.renameTo(newFile)) {
                return newFile.getAbsolutePath();
            }
        }
        return oldFile.getAbsolutePath();
    }

    public String checkDuplicateFile(HARRY_ConvertedFile convertedFile) {
        int num = 0;
        File file = new File(getConvertedFilePath() + File.separator + convertedFile.getOutputFileName() + convertedFile.getOutputFormat());
        while (file.exists()) {
            num++;
            file = new File(getConvertedFilePath() + File.separator + convertedFile.getOutputName() + String.format("(%d)", new Object[]{Integer.valueOf(num)}) + convertedFile.getOutputFormat());
        }
        return file.getName();
    }

    public boolean deleteFile(File file) {
        return file.delete();
    }

    public static void saveTempFile(InputStream in, File destinationFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = in.read(buffer);
                    if (read != -1) {
                        fileOutputStream.write(buffer, 0, read);
                    } else {
                        in.close();
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        return;
                    }
                }
            } catch (FileNotFoundException e2) {
                FileOutputStream fileOutputStream2 = fileOutputStream;
                Log.e("tag", e2.getMessage());
            } catch (Exception e3) {
                FileOutputStream fileOutputStream3 = fileOutputStream;
                Log.e("tag", e3.getMessage());
            }
        } catch (FileNotFoundException e4) {
            Log.e("tag", e4.getMessage());
        } catch (Exception e5) {
            Log.e("tag", e5.getMessage());
        }
    }

    public static String getFileName(Context context2, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context2.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex("_display_name"));
                    }
                } catch (Throwable th) {
                    cursor.close();
                }
            }
            cursor.close();
        }
        if (result != null) {
            return result;
        }
        String result2 = uri.getPath();
        int cut = result2.lastIndexOf(47);
        return cut != -1 ? result2.substring(cut + 1) : result2;
    }

    private boolean checkFileExtensionExist(String name) {
        return name.substring(name.lastIndexOf(".") + 1).isEmpty();
    }

    public ArrayList<String> getFilesAddress() {
        File[] files;
        ArrayList<String> path = new ArrayList<>();
        String directory = this.defaultPath;
        if (new File(this.defaultPath).exists()) {
            for (File file : new File(directory).listFiles()) {
                if (file.isFile()) {
                    path.add(file.getAbsolutePath());
                }
            }
        }
        return path;
    }
}
