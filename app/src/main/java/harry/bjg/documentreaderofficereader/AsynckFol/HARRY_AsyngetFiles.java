package harry.bjg.documentreaderofficereader.AsynckFol;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;


import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.HARRY_MyUtils;
import harry.bjg.documentreaderofficereader.InterFol.HARRY_OnCommonProgress;
import harry.bjg.documentreaderofficereader.ModelFol.HARRY_FileMod;

import static android.text.format.Formatter.formatFileSize;

public class HARRY_AsyngetFiles extends AsyncTask<Void, Void, Void> {

    Activity cn;
    int which = 0;
    HARRY_OnCommonProgress onCommonProgress;
    ArrayList<HARRY_FileMod> allfiles = new ArrayList<>();


    public HARRY_AsyngetFiles(Activity cn, int which, HARRY_OnCommonProgress onCommonProgress) {
        this.cn = cn;
        this.which = which;
        this.onCommonProgress = onCommonProgress;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getalldata();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onCommonProgress.OnComplete(true, allfiles);
    }

    void getalldata() {
        String selection = "_data LIKE '%.pdf'";
        ContentResolver cr = cn.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        Cursor cursor = cr.query(uri, null, selection, null, "_id DESC");
        if (which == 0 || which == 1) {
            selection = "_data LIKE '%.pdf'";
            cr = cn.getContentResolver();
            uri = MediaStore.Files.getContentUri("external");
            cursor = cr.query(uri, null, selection, null, "_id DESC");

            if (cursor.moveToFirst()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    String ext = HARRY_MyUtils.getFileExtension(path);


                    Log.e("AAA", "Title : " + title);
                    Log.e("AAA", "Path : " + path);
                    String ssize = formatFileSize(cn, size);
                    Log.e("AAA", "Size : " + ssize);

                    HARRY_FileMod fileMod = new HARRY_FileMod(title, path, ext, ssize, size);
                    allfiles.add(fileMod);

                } while (cursor.moveToNext());

                int size = cursor.getCount();
                Log.e("AAA", "Total = " + size);
            }
        }


        if (which == 0 || which == 2) {
            selection = "_data LIKE '%.doc' OR _data LIKE '%.docx'";
            cursor = cr.query(uri, null, selection, null, "_id DESC");

            if (cursor.moveToFirst()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    String ext = HARRY_MyUtils.getFileExtension(path);

                    Log.e("AAA", "Title : " + title);
                    Log.e("AAA", "Path : " + path);
                    String ssize = formatFileSize(cn, size);
                    Log.e("AAA", "Size : " + ssize);
                    HARRY_FileMod fileMod = new HARRY_FileMod(title, path, ext, ssize, size);
                    allfiles.add(fileMod);

                } while (cursor.moveToNext());

                int size = cursor.getCount();
                Log.e("AAA", "Total = " + size);
            }
        }

        if (which == 0 || which == 3) {
            selection = "_data LIKE '%.xls'";
            cursor = cr.query(uri, null, selection, null, "_id DESC");

            if (cursor.moveToFirst()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    String ext = HARRY_MyUtils.getFileExtension(path);

                    Log.e("AAA", "Title : " + title);
                    Log.e("AAA", "Path : " + path);
                    String ssize = formatFileSize(cn, size);
                    Log.e("AAA", "Size : " + ssize);
                    HARRY_FileMod fileMod = new HARRY_FileMod(title, path, ext, ssize, size);
                    allfiles.add(fileMod);

                } while (cursor.moveToNext());

                int size = cursor.getCount();
                Log.e("AAA", "Total = " + size);
            }

            selection = "_data LIKE '%.xlsx'";
            cursor = cr.query(uri, null, selection, null, "_id DESC");

            if (cursor.moveToFirst()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    String ext = HARRY_MyUtils.getFileExtension(path);

                    Log.e("AAA", "Title : " + title);
                    Log.e("AAA", "Path : " + path);
                    String ssize = formatFileSize(cn, size);
                    Log.e("AAA", "Size : " + ssize);
                    HARRY_FileMod fileMod = new HARRY_FileMod(title, path, ext, ssize, size);
                    allfiles.add(fileMod);

                } while (cursor.moveToNext());

                int size = cursor.getCount();
                Log.e("AAA", "Total = " + size);
            }

        }

        if (which == 0 || which == 4) {

            selection = "_data LIKE '%.txt'";
            cursor = cr.query(uri, null, selection, null, "_id DESC");

            if (cursor.moveToFirst()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    String ext = HARRY_MyUtils.getFileExtension(path);

                    Log.e("AAA", "Title : " + title);
                    Log.e("AAA", "Path : " + path);
                    String ssize = formatFileSize(cn, size);
                    Log.e("AAA", "Size : " + ssize);
                    HARRY_FileMod fileMod = new HARRY_FileMod(title, path, ext, ssize, size);
                    allfiles.add(fileMod);

                } while (cursor.moveToNext());

                int size = cursor.getCount();
                Log.e("AAA", "Total = " + size);
            }
        }

        if (which == 0 || which == 5) {
            selection = "_data LIKE '%.pptx' OR _data LIKE '%.ppt'";
            cursor = cr.query(uri, null, selection, null, "_id DESC");

            if (cursor.moveToFirst()) {
                do {

                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    String ext = HARRY_MyUtils.getFileExtension(path);

                    Log.e("AAA", "Title : " + title);
                    Log.e("AAA", "Path : " + path);
                    String ssize = formatFileSize(cn, size);
                    Log.e("AAA", "Size : " + ssize);
                    HARRY_FileMod fileMod = new HARRY_FileMod(title, path, ext, ssize, size);
                    allfiles.add(fileMod);

                } while (cursor.moveToNext());

                int size = cursor.getCount();
                Log.e("AAA", "Total = " + size);
            }
        }


    }

}
