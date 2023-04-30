package harry.bjg.documentreaderofficereader.convertapi.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.common.net.UrlEscapers;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import harry.bjg.documentreaderofficereader.R;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_ConvertedFile;

public class HARRY_CloudConvertService {
    private final String CONVERT_BASE_URL = "https://api.cloudconvert.com/convert";
    private final String LOG_TAG = "CloudConvertAPI";
    private final String URL_START_PROCESS = "https://api.cloudconvert.com/process";
    public Context context;

    public interface ConvertFileCallback {
        void onFailure();

        void onFinishConversion();

        void onUpdateProgress(JSONObject jSONObject);
    }

    public interface CreateConversionCallback {
        void onFailure();

        void onSuccess(JSONObject jSONObject);
    }

    public interface DownloadFileCallback {
        void onDownload(int i);

        void onFailure();

        void onFinishDownload(File file);
    }

    public interface FileConversionCallback {
        void onDownload(int i);

        void onDownloadBegan();

        void onDownloadFinished();

        void onFailure(String str);

        void onSuccess(File file);

        void onUpload(int i);

        void onUploadBegan();

        void onUploadFinished();

        void onWaitBegan();

        void onWaitFinished();
    }

    public interface PrepareUploadingFileCallback {
        void onFailure();

        void onSuccess(String str, File file);
    }

    public interface UploadFileCallback {
        void onCreateUploadProcess(String str);

        void onFailure();

        void onFinishUpload();

        void onUpload(int i);
    }

    public HARRY_CloudConvertService(Context context2) {
        this.context = context2;
    }

    public void createConversionProcess(@NonNull final HARRY_ConvertedFile convertedFile, @NonNull final CreateConversionCallback callback) {
        RequestParams params = new RequestParams();
        params.add("apikey", this.context.getString(R.string.cloud_convert_key));
        params.add("inputformat", convertedFile.getInputFormat());
        params.add("outputformat", convertedFile.getOutputFormat());
        new AsyncHttpClient().post("https://api.cloudconvert.com/process", params, new AsyncHttpResponseHandler() {
            public void onStart() {
                super.onStart();
                Log.d("START_PROCESS", "input: " + convertedFile.getInputFormat() + ", output: " + convertedFile.getOutputFormat());
            }

            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("START_PROCESS", "SUCCESS: " + new String(responseBody));
                try {
                    callback.onSuccess(new JSONObject(new String(responseBody)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                callback.onFailure();
            }
        });
    }

    public void createUploadProcess(@NonNull HARRY_ConvertedFile convertedFile, final File file, final String fileName, @NonNull final UploadFileCallback callback) {
        RequestParams params = new RequestParams();
        params.add("input", "upload");
        params.add("outputformat", convertedFile.getOutputFormat());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(600000);
        client.post("https:" + convertedFile.getProcessUrl(), params, new AsyncHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("Create upload", new String(responseBody));
                try {
                    String uploadUrl = new JSONObject(new String(responseBody)).getJSONObject("upload").getString("url");
                    callback.onCreateUploadProcess(uploadUrl);
                    HARRY_CloudConvertService.this.uploadFile(UrlEscapers.urlFragmentEscaper().escape("https:" + uploadUrl + "/" + fileName), file, callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.onFailure();
            }
        });
    }

    public void createUploadProcess(@NonNull HARRY_ConvertedFile convertedFile, final InputStream inputStream, final String fileName, @NonNull final UploadFileCallback callback) {
        RequestParams params = new RequestParams();
        params.add("input", "upload");
        params.add("outputformat", convertedFile.getOutputFormat());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(600000);
        client.post("https:" + convertedFile.getProcessUrl(), params, new AsyncHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("Create upload", new String(responseBody));
                try {
                    String uploadUrl = new JSONObject(new String(responseBody)).getJSONObject("upload").getString("url");
                    callback.onCreateUploadProcess(uploadUrl);
                    HARRY_CloudConvertService.this.uploadFile(uploadUrl, inputStream, fileName, callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                callback.onFailure();
            }
        });
    }

    public void prepareUploadFile(final FileInputStream in, final String name, final PrepareUploadingFileCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            File cacheFolder;
            File file;

            /* access modifiers changed from: protected */
            public void onPreExecute() {
                super.onPreExecute();
                this.cacheFolder = new File(HARRY_CloudConvertService.this.context.getCacheDir() + File.separator + "Cache");
                if (!this.cacheFolder.exists()) {
                    this.cacheFolder.mkdirs();
                }
                this.file = new File(this.cacheFolder + File.separator + name);
            }

            /* access modifiers changed from: protected */
            public Boolean doInBackground(Void... params) {
                try {
                    OutputStream out = new FileOutputStream(this.file);
                    byte[] buf = new byte[1024];
                    while (true) {
                        int len = in.read(buf);
                        if (len > 0) {
                            out.write(buf, 0, len);
                        } else {
                            out.close();
                            in.close();
                            return Boolean.valueOf(true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Boolean.valueOf(false);
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success.booleanValue()) {
                    callback.onSuccess(name, this.file);
                } else {
                    callback.onFailure();
                }
            }
        }.execute(new Void[0]);
    }

    private File copyInputStreamToFile(InputStream in, String name) {
        File cacheFolder = new File(this.context.getCacheDir() + File.separator + "Cache");
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        File file = new File(cacheFolder + File.separator + name);
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            while (true) {
                int len = in.read(buf);
                if (len > 0) {
                    out.write(buf, 0, len);
                } else {
                    out.close();
                    in.close();
                    return file;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadFile(String uploadUrl, final File fileWillBeUploaded, @NonNull final UploadFileCallback callback) {
        try {
            Log.d("upload url", uploadUrl);
            Log.d("upload file", fileWillBeUploaded.getAbsolutePath());
            RequestParams params = new RequestParams();
            params.setForceMultipartEntityContentType(true);
            params.put("file", fileWillBeUploaded);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(600000);
            client.put(uploadUrl, params, new AsyncHttpResponseHandler() {
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    callback.onUpload(HARRY_CloudConvertService.this.getProgressPercentage(bytesWritten, totalSize));
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("UPLOAD", new String(responseBody));
                    Log.d("DELETE", "delete: " + fileWillBeUploaded.getAbsolutePath() + ": " + fileWillBeUploaded.delete());
                    callback.onFinishUpload();
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                    callback.onFailure();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    public void uploadFile(String uploadUrl, InputStream inputStream, String fileName, @NonNull final UploadFileCallback callback) {
        final File fileWillBeUploaded = copyInputStreamToFile(inputStream, fileName);
        if (fileWillBeUploaded != null) {
            try {
                String uploadUrl2 = UrlEscapers.urlFragmentEscaper().escape("https:" + uploadUrl + "/" + fileName);
                Log.d("upload url", uploadUrl2);
                Log.d("upload file", fileWillBeUploaded.getAbsolutePath());
                RequestParams params = new RequestParams();
                params.setForceMultipartEntityContentType(true);
                params.put("file", fileWillBeUploaded);
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(600000);
                client.put(uploadUrl2, params, new AsyncHttpResponseHandler() {
                    public void onProgress(long bytesWritten, long totalSize) {
                        super.onProgress(bytesWritten, totalSize);
                        callback.onUpload(HARRY_CloudConvertService.this.getProgressPercentage(bytesWritten, totalSize));
                    }

                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("UPLOAD", new String(responseBody));
                        Log.d("DELETE", "delete: " + fileWillBeUploaded.getAbsolutePath() + ": " + fileWillBeUploaded.delete());
                        callback.onFinishUpload();
                    }

                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        error.printStackTrace();
                        callback.onFailure();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure();
            }
        } else {
            callback.onFailure();
        }
    }

    public void updateConversionProgress(@NonNull HARRY_ConvertedFile convertedFile, @NonNull final ConvertFileCallback callback) {
        Log.d("Conversion", "update: https:" + convertedFile.getProcessUrl());
        new AsyncHttpClient().get("https:" + convertedFile.getProcessUrl(), new AsyncHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    callback.onUpdateProgress(new JSONObject(new String(responseBody)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                callback.onFailure();
            }
        });
    }

    public void downloadFile(@NonNull HARRY_ConvertedFile convertedFile, @NonNull final DownloadFileCallback callback) {
        new AsyncHttpClient().get("https:" + convertedFile.getDownloadUrl(), new FileAsyncHttpResponseHandler(this.context) {
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                callback.onDownload(HARRY_CloudConvertService.this.getProgressPercentage(bytesWritten, totalSize));
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                callback.onFailure();
            }

            public void onSuccess(int statusCode, Header[] headers, File file) {
                callback.onFinishDownload(file);
            }
        });
    }

    public void convertWordToPdf(File inputFile, String inputFormat, FileConversionCallback callback) {
        final Boolean[] doneUploading = {Boolean.valueOf(false)};
        final Boolean[] beginDownloading = {Boolean.valueOf(false)};
        RequestParams params = new RequestParams();
        params.add("apikey", this.context.getString(R.string.cloud_convert_key));
        params.add("input", "upload");
        params.add("inputformat", inputFormat);
        params.add("outputformat", HARRY_ConvertedFile.PDF);
        Log.d("CloudConvertAPI", "input: " + inputFile.getName());
        try {
            params.put("file", inputFile);
            final FileConversionCallback fileConversionCallback = callback;
            new AsyncHttpClient().post("https://api.cloudconvert.com/convert", params, new FileAsyncHttpResponseHandler(this.context) {
                public void onStart() {
                    super.onStart();
                    Log.d("CloudConvertAPI", "onStart: ");
                    fileConversionCallback.onUploadBegan();
                }

                public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                    super.onPreProcessResponse(instance, response);
                    beginDownloading[0] = Boolean.valueOf(true);
                    Log.d("CloudConvertAPI", "onPreProcessResponse: " + response.toString());
                }

                public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                    super.onPostProcessResponse(instance, response);
                    Log.d("CloudConvertAPI", "onPostProcessResponse: " + response.toString());
                }

                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    double percentage = ((((double) bytesWritten) * 1.0d) / ((double) totalSize)) * 100.0d;
                    if (!doneUploading[0].booleanValue()) {
                        fileConversionCallback.onUpload((int) percentage);
                        if (percentage >= 100.0d) {
                            fileConversionCallback.onUploadFinished();
                            fileConversionCallback.onWaitBegan();
                            doneUploading[0] = Boolean.valueOf(true);
                        }
                    } else if (beginDownloading[0].booleanValue()) {
                        fileConversionCallback.onDownload((int) percentage);
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                    Log.d("CloudConvertAPI", "onFailure code: " + statusCode + ", message: " + throwable.getMessage());
                    fileConversionCallback.onFailure(throwable.getMessage());
                }

                public void onSuccess(int statusCode, Header[] headers, File file) {
                    fileConversionCallback.onSuccess(file);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public int getProgressPercentage(long bytesWritten, long totalSize) {
        return (int) (((((double) bytesWritten) * 1.0d) / ((double) totalSize)) * 100.0d);
    }
}
