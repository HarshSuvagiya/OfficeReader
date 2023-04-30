package harry.bjg.documentreaderofficereader.convertapi.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.ProtocolException;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.impl.client.DefaultRedirectHandler;
import cz.msebera.android.httpclient.protocol.HttpContext;
import harry.bjg.documentreaderofficereader.R;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_ConvertedFile;

public class HARRY_ZamzarService {
    private Context context;
    private String endpoint = "https://api.zamzar.com/v1/jobs";
    private String endpointDownload = "https://api.zamzar.com/v1/files/%s/content";

    public interface ConvertFileCallback {
        void onFailure();

        void onFinishConversion();

        void onUpdateProgress(JSONObject jSONObject);
    }

    public interface DownloadFileCallback {
        void onDownload(int i);

        void onFailure();

        void onFinishDownload(File file);
    }

    public interface RedirectDownloadFileCallback {
        void onFailure();

        void onSuccess(String str);
    }

    public interface StartConversionCallback {
        void onFailure();

        void onStartUpload();

        void onSuccess(JSONObject jSONObject);

        void onUpload(int i);
    }

    public HARRY_ZamzarService(Context context2) {
        this.context = context2;
    }

    public void createConversionProcess(@NonNull final HARRY_ConvertedFile convertedFile, InputStream inputStream, String fileName, @NonNull final StartConversionCallback callback) {
        try {
            RequestParams params = new RequestParams();
            params.put("source_file", inputStream, fileName);
            params.put("target_format", convertedFile.getOutputFormat());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBasicAuth(this.context.getString(R.string.zamzar_key), "", AuthScope.ANY);
            client.post(this.endpoint, params, new AsyncHttpResponseHandler() {
                public void onStart() {
                    super.onStart();
                    callback.onStartUpload();
                }

                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        try {
                            Log.d("zamzar", "id: " + json.getString("id"));
                            convertedFile.setProcessID(json.getString("id"));
                            callback.onSuccess(json);
                            JSONObject jSONObject = json;
                        } catch (JSONException e2) {
                            JSONObject jSONObject2 = json;
                            e2.printStackTrace();
                            callback.onFailure();
                        }
                    } catch (JSONException e3) {
                        e3.printStackTrace();
                        callback.onFailure();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (responseBody != null) {
                        Log.d("Zamzar", new String(responseBody));
                    }
                    error.printStackTrace();
                    callback.onFailure();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    public void createConversionProcess(@NonNull final HARRY_ConvertedFile convertedFile, @NonNull final StartConversionCallback callback) {
        try {
            RequestParams params = new RequestParams();
            params.put("source_file", convertedFile.getSourceFile());
            params.put("target_format", convertedFile.getOutputFormat());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBasicAuth(this.context.getString(R.string.zamzar_key), "", AuthScope.ANY);
            client.post(this.endpoint, params, new AsyncHttpResponseHandler() {
                public void onStart() {
                    super.onStart();
                    callback.onStartUpload();
                }

                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        try {
                            Log.d("zamzar", "id: " + json.getString("id"));
                            convertedFile.setProcessID(json.getString("id"));
                            callback.onSuccess(json);
                            JSONObject jSONObject = json;
                        } catch (JSONException e2) {
                            JSONObject jSONObject2 = json;
                            e2.printStackTrace();
                            callback.onFailure();
                        }
                    } catch (JSONException e3) {
                        e3.printStackTrace();
                        callback.onFailure();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (responseBody != null) {
                        Log.d("Zamzar", new String(responseBody));
                    }
                    error.printStackTrace();
                    callback.onFailure();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    public void updateConversionProgress(@NonNull HARRY_ConvertedFile convertedFile, @NonNull final ConvertFileCallback callback) {
        String updateEndpoint = this.endpoint + "/" + convertedFile.getProcessID();
        Log.d("zamzar", "update: " + updateEndpoint);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(this.context.getString(R.string.zamzar_key), "", AuthScope.ANY);
        client.get(updateEndpoint, new AsyncHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    callback.onUpdateProgress(new JSONObject(new String(responseBody)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    Log.d("zamzar", new String(responseBody));
                }
                error.printStackTrace();
                callback.onFailure();
            }
        });
    }

    public void redirectDownloadFile(@NonNull String downloadId, @NonNull final RedirectDownloadFileCallback callback) {
        String endpoint2 = String.format(this.endpointDownload, new Object[]{downloadId});
        Log.d("zamzar", "download: " + endpoint2);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(this.context.getString(R.string.zamzar_key), "", AuthScope.ANY);
        client.setEnableRedirects(false);
        client.setRedirectHandler(new DefaultRedirectHandler() {
            public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                String redirectURL = response.getFirstHeader("Location").getValue();
                Log.d("zamzar", "redirect: " + redirectURL);
                callback.onSuccess(redirectURL);
                return false;
            }

            public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
                return null;
            }
        });
        client.setLoggingEnabled(true);
        client.get(endpoint2, new AsyncHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("zamzar", "onSuccess");
                for (Header header : headers) {
                    Log.d("zamzar", header.getName() + ": " + header.getValue());
                }
            }

            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void downloadFile(@NonNull HARRY_ConvertedFile convertedFile, @NonNull String url, @NonNull final DownloadFileCallback callback) {
        Log.d("zamzar", "download: " + url);
        new SyncHttpClient().get(url, new FileAsyncHttpResponseHandler(this.context) {
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                callback.onDownload(HARRY_ZamzarService.this.getProgressPercentage(bytesWritten, totalSize));
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Log.d("zamzar", "status code: " + statusCode);
                for (Header header : headers) {
                    Log.d("zamzar", header.getName() + ": " + header.getValue());
                }
                throwable.printStackTrace();
                callback.onFailure();
            }

            public void onSuccess(int statusCode, Header[] headers, File file) {
                callback.onFinishDownload(file);
            }
        });
    }

    private void downloadRedirect(@NonNull String url, @NonNull final DownloadFileCallback callback) {
        new AsyncHttpClient().get(url, new FileAsyncHttpResponseHandler(this.context) {
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                callback.onDownload(HARRY_ZamzarService.this.getProgressPercentage(bytesWritten, totalSize));
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                callback.onFailure();
            }

            public void onSuccess(int statusCode, Header[] headers, File file) {
                callback.onFinishDownload(file);
            }
        });
    }

    /* access modifiers changed from: private */
    public int getProgressPercentage(long bytesWritten, long totalSize) {
        return (int) (((((double) bytesWritten) * 1.0d) / ((double) totalSize)) * 100.0d);
    }
}
