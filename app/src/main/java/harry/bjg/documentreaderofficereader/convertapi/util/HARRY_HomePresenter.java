package harry.bjg.documentreaderofficereader.convertapi.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.eralp.circleprogressview.CircleProgressView;

import harry.bjg.documentreaderofficereader.HARRY_CategoryActivity;
import harry.bjg.documentreaderofficereader.HARRY_Help;
import harry.bjg.documentreaderofficereader.adapter.HARRY_ConvertAdapter;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_CloudConvertService;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_CloudConvertService.ConvertFileCallback;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_CloudConvertService.CreateConversionCallback;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_CloudConvertService.DownloadFileCallback;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_CloudConvertService.PrepareUploadingFileCallback;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_CloudConvertService.UploadFileCallback;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_ZamzarService;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_ZamzarService.RedirectDownloadFileCallback;
import harry.bjg.documentreaderofficereader.convertapi.service.HARRY_ZamzarService.StartConversionCallback;

import harry.bjg.documentreaderofficereader.HARRY_PdfToDocActivity;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_FileUtil.CopyFileCallback;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_HomeContract.Presenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class HARRY_HomePresenter implements Presenter {
    public CircleProgressView circleProgressView;
    private HARRY_CloudConvertService cloudConvertService;
    public HARRY_PdfToDocActivity context;
    public ImageView convert;
    public int currentRow;
    private HARRY_FileUtil fileUtil;
    private Uri incomingUri;
    private FileInputStream inputStream;
    private HARRY_ConvertAdapter mConvertAdapter;
    private String outPutFileNaem;
    public ProgressBar progressBar;
    public ImageView refreshButton;
    private String selectedInputFormat;
    private String sourceFilePath;
    private HARRY_ZamzarService zamzarService;

    public HARRY_HomePresenter(@NonNull HARRY_PdfToDocActivity context2, int currentRow2, String outputName, @NonNull HARRY_FileUtil fileUtil2, @NonNull HARRY_CloudConvertService cloudConvertService2, @NonNull HARRY_ZamzarService zamzarService2, ProgressBar progressBar2, CircleProgressView circleProgressView2, ImageView refreshButton2, ImageView convert2) {
        this.context = context2;
        this.fileUtil = fileUtil2;
        this.cloudConvertService = cloudConvertService2;
        this.zamzarService = zamzarService2;
        this.outPutFileNaem = outputName;
        this.circleProgressView = circleProgressView2;
        this.progressBar = progressBar2;
        this.refreshButton = refreshButton2;
        this.convert = convert2;
        this.currentRow = currentRow2;
    }

    public void handleIncomingIntent(Intent intent) {
        Log.d("HARRY_HomePresenter", "handleIncomingIntent");
        if (this.incomingUri != null) {
            prepareUri(this.incomingUri);
            intent.setAction("");
            this.incomingUri = null;
        }
    }

    public void prepareUri(Uri uri) {
        try {
            this.inputStream = new FileInputStream(new File(uri.toString()));
            this.sourceFilePath = uri.toString();
            this.selectedInputFormat = this.sourceFilePath.substring(this.sourceFilePath.lastIndexOf(".") + 1);
            if (HARRY_ConvertedFile.isFormatValid(this.selectedInputFormat)) {
                Log.e("Format", "Format of input  supported");
                selectOutputName(this.outPutFileNaem);
                return;
            }
            Log.e("error", "Format of input not supported");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("error", "File Not Found");
        }
    }

    public void selectOutputName(String outputName) {
//        String convert_type = HARRY_ConvertedFile.PPT;
        String convert_type = HARRY_ConvertedFile.DOCX;
        if (HARRY_CategoryActivity.position == 1 || HARRY_CategoryActivity.position == 5 || HARRY_CategoryActivity.position == 12) {
            convert_type = HARRY_ConvertedFile.TXT;
        } else if (HARRY_CategoryActivity.position == 2 || HARRY_CategoryActivity.position == 6 || HARRY_CategoryActivity.position == 9 || HARRY_CategoryActivity.position == 13) {
            convert_type = HARRY_ConvertedFile.JPG;
        } else if (HARRY_CategoryActivity.position == 3 || HARRY_CategoryActivity.position == 7 || HARRY_CategoryActivity.position == 10 || HARRY_CategoryActivity.position == 14) {
            convert_type = HARRY_ConvertedFile.HTML;
        } else if (HARRY_CategoryActivity.position == 4 || HARRY_CategoryActivity.position == 8 || HARRY_CategoryActivity.position == 11) {
            convert_type = HARRY_ConvertedFile.PDF;
        }
        HARRY_ConvertedFile convertedFile = new HARRY_ConvertedFile(this.sourceFilePath, convert_type, outputName);
        convertedFile.setInputFormat(this.selectedInputFormat);
        this.context.runOnUiThread(new Runnable() {
            public void run() {
                HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
                HARRY_HomePresenter.this.progressBar.setVisibility(View.VISIBLE);
                HARRY_HomePresenter.this.circleProgressView.setVisibility(View.GONE);
                HARRY_HomePresenter.this.refreshButton.setVisibility(View.GONE);
            }
        });
        startConversion(convertedFile);
    }

    public void startConversion(HARRY_ConvertedFile convertedFile) {
        String input = convertedFile.getInputFormat();
        String output = convertedFile.getOutputFormat();
        if (!input.equals(HARRY_ConvertedFile.PDF)) {
            createCloudConvertProcess(convertedFile);
        } else if (output.equals(HARRY_ConvertedFile.DOC) ||output.equals(HARRY_ConvertedFile.HTML) || output.equals(HARRY_ConvertedFile.JPG) || output.equals(HARRY_ConvertedFile.DOCX) || output.equals(HARRY_ConvertedFile.TXT) || output.equals(HARRY_ConvertedFile.ODT) || output.equals(HARRY_ConvertedFile.RTF)) {
            createCloudConvertProcess(convertedFile);
        } else if (output.equals(HARRY_ConvertedFile.XLS) || output.equals(HARRY_ConvertedFile.XLSX) || output.equals(HARRY_ConvertedFile.PPT) || output.equals(HARRY_ConvertedFile.PPTX)) {
            createZamzarProcess(convertedFile);
        }
    }

    public void onErrorConversion(HARRY_ConvertedFile convertedFile) {
        convertedFile.setStatus(5);
        this.context.runOnUiThread(new Runnable() {
            public void run() {
                HARRY_Help.Toast(context,"Unable to Convert");
                HARRY_HomePresenter.this.refreshButton.setVisibility(View.VISIBLE);
                HARRY_HomePresenter.this.circleProgressView.setVisibility(View.GONE);
                HARRY_HomePresenter.this.progressBar.setVisibility(View.GONE);
                HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
            }
        });
        Log.e("error", "" + convertedFile.getId());
    }

    public void createCloudConvertProcess(final HARRY_ConvertedFile convertedFile) {
        convertedFile.setHost(0);
        this.cloudConvertService.createConversionProcess(convertedFile, new CreateConversionCallback() {
            public void onSuccess(JSONObject responseJson) {
                try {
                    convertedFile.setProcessUrl(responseJson.getString("url"));
                    HARRY_HomePresenter.this.prepareUploadingFile(convertedFile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void prepareUploadingFile(final HARRY_ConvertedFile convertedFile) {
        convertedFile.setStatus(1);
        String[] splits = Uri.parse(convertedFile.getSourcePath()).getLastPathSegment().split("/");
        String fileName = splits[splits.length - 1];
        String checkExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        Log.e("checkExtension", checkExtension);
        if (!checkExtension.equals(convertedFile.getInputFormat())) {
            fileName = fileName + "." + convertedFile.getInputFormat();
        }
        this.cloudConvertService.prepareUploadFile(this.inputStream, fileName, new PrepareUploadingFileCallback() {
            public void onSuccess(String name, File file) {
                HARRY_HomePresenter.this.startCloudConvertUploading(convertedFile, file, name);
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void startCloudConvertUploading(final HARRY_ConvertedFile convertedFile, File file, String fileName) {
        this.cloudConvertService.createUploadProcess(convertedFile, file, fileName, (UploadFileCallback) new UploadFileCallback() {
            public void onCreateUploadProcess(String uploadUrl) {
                convertedFile.setUploadUrl(uploadUrl);
            }

            public void onUpload(int percentage) {
                if (percentage - convertedFile.getUploadPercentage() > 1) {
                    HARRY_HomePresenter.this.onUploadFile(convertedFile, percentage);
                }
            }

            public void onFinishUpload() {
                convertedFile.setStatus(2);
                HARRY_HomePresenter.this.context.runOnUiThread(new Runnable() {
                    public void run() {
                        HARRY_HomePresenter.this.refreshButton.setVisibility(View.GONE);
                        HARRY_HomePresenter.this.circleProgressView.setVisibility(View.GONE);
                        HARRY_HomePresenter.this.circleProgressView.setProgress(0.0f);
                        HARRY_HomePresenter.this.progressBar.setVisibility(View.VISIBLE);
                        HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
                    }
                });
                HARRY_HomePresenter.this.startCloudConvertProcess(convertedFile);
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void startCloudConvertUploading(HARRY_ConvertedFile convertedFile) {
    }

    public void startCloudConvertProcess(final HARRY_ConvertedFile convertedFile) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                HARRY_HomePresenter.this.updateCloudConvertProgress(convertedFile);
            }
        }, 3500);
    }

    public void updateCloudConvertProgress(final HARRY_ConvertedFile convertedFile) {
        this.cloudConvertService.updateConversionProgress(convertedFile, new ConvertFileCallback() {
            public void onUpdateProgress(JSONObject responseJson) {
                Log.e("updateConversion", responseJson.toString());
                try {
                    if (responseJson.getString("step").equals("finished")) {
                        convertedFile.setDownloadUrl(responseJson.getJSONObject("output").getString("url"));
                        HARRY_HomePresenter.this.startCloudConvertDownload(convertedFile);
                        return;
                    }
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            HARRY_HomePresenter.this.updateCloudConvertProgress(convertedFile);
                        }
                    }, 3500);
                } catch (JSONException e) {
                    Log.e("errorzem", "" + e.toString());
                    HARRY_HomePresenter.this.onErrorConversion(convertedFile);
                }
            }

            public void onFinishConversion() {
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void startCloudConvertDownload(final HARRY_ConvertedFile convertedFile) {
        convertedFile.setStatus(3);
        this.cloudConvertService.downloadFile(convertedFile, new DownloadFileCallback() {
            public void onDownload(int percentage) {
                if (percentage - convertedFile.getDownloadPercentage() > 1) {
                    HARRY_HomePresenter.this.onDownloadFile(convertedFile, percentage);
                }
            }

            public void onFinishDownload(File outputFile) {
                HARRY_HomePresenter.this.onFinishDownloadFile(convertedFile, outputFile);
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void createZamzarProcess(final HARRY_ConvertedFile convertedFile) {
        convertedFile.setHost(1);
        convertedFile.setStatus(1);
        this.context.runOnUiThread(new Runnable() {
            public void run() {
                HARRY_HomePresenter.this.refreshButton.setVisibility(View.GONE);
                HARRY_HomePresenter.this.circleProgressView.setVisibility(View.GONE);
                HARRY_HomePresenter.this.progressBar.setVisibility(View.VISIBLE);
                HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
            }
        });
        String[] splits = Uri.parse(convertedFile.getSourcePath()).getLastPathSegment().split("/");
        this.zamzarService.createConversionProcess(convertedFile, this.inputStream, splits[splits.length - 1], new StartConversionCallback() {
            public void onStartUpload() {
            }

            public void onUpload(int percentage) {
                HARRY_HomePresenter.this.onUploadFile(convertedFile, percentage);
            }

            public void onSuccess(JSONObject responseJson) {
                convertedFile.setStatus(2);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        HARRY_HomePresenter.this.updateZamzarProcess(convertedFile);
                    }
                }, 3500);
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void updateZamzarProcess(final HARRY_ConvertedFile convertedFile) {
        this.zamzarService.updateConversionProgress(convertedFile, new HARRY_ZamzarService.ConvertFileCallback() {
            public void onUpdateProgress(JSONObject responseJson) {
                Log.d("update zamzar", responseJson.toString());
                try {
                    if (responseJson.getString("status").equals("successful")) {
                        HARRY_HomePresenter.this.startZamzarRedirectDownload(convertedFile, responseJson);
                    } else if (responseJson.getString("status").equals("failed")) {
                        HARRY_HomePresenter.this.context.runOnUiThread(new Runnable() {
                            public void run() {
                                HARRY_HomePresenter.this.refreshButton.setVisibility(View.VISIBLE);
                                HARRY_HomePresenter.this.circleProgressView.setVisibility(View.GONE);
                                HARRY_HomePresenter.this.progressBar.setVisibility(View.GONE);
                                HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
                            }
                        });
                        HARRY_HomePresenter.this.onErrorConversion(convertedFile);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                HARRY_HomePresenter.this.updateZamzarProcess(convertedFile);
                            }
                        }, 3500);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFinishConversion() {
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void startZamzarRedirectDownload(final HARRY_ConvertedFile convertedFile, JSONObject jsonObject) {
        convertedFile.setStatus(3);
        try {
            this.zamzarService.redirectDownloadFile(jsonObject.getJSONArray("target_files").getJSONObject(0).getString("id"), new RedirectDownloadFileCallback() {
                public void onSuccess(String redirectUrl) {
                    HARRY_HomePresenter.this.startZamzartDownload(convertedFile, redirectUrl);
                }

                public void onFailure() {
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            onErrorConversion(convertedFile);
        }
    }

    public void startZamzartDownload(final HARRY_ConvertedFile convertedFile, String url) {
        this.zamzarService.downloadFile(convertedFile, url, new HARRY_ZamzarService.DownloadFileCallback() {
            public void onDownload(int percentage) {
                HARRY_HomePresenter.this.onDownloadFile(convertedFile, percentage);
            }

            public void onFinishDownload(File outputFile) {
                HARRY_HomePresenter.this.onFinishDownloadFile(convertedFile, outputFile);
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void onUploadFile(HARRY_ConvertedFile convertedFile, final int percentage) {
        convertedFile.setUploadPercentage(percentage);
        Log.e("Upload", "" + percentage);
        this.context.runOnUiThread(new Runnable() {
            public void run() {
                HARRY_HomePresenter.this.circleProgressView.setVisibility(View.VISIBLE);
                HARRY_HomePresenter.this.progressBar.setVisibility(View.GONE);
                HARRY_HomePresenter.this.refreshButton.setVisibility(View.GONE);
                HARRY_HomePresenter.this.circleProgressView.setProgress((float) percentage);
                HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
            }
        });
    }

    public void onDownloadFile(HARRY_ConvertedFile convertedFile, final int percentage) {
        convertedFile.setDownloadPercentage(percentage);
        Log.e("download", "" + percentage);
        this.context.runOnUiThread(new Runnable() {
            public void run() {
                HARRY_HomePresenter.this.progressBar.setVisibility(View.GONE);
                HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
                HARRY_HomePresenter.this.refreshButton.setVisibility(View.GONE);
                HARRY_HomePresenter.this.circleProgressView.setVisibility(View.VISIBLE);
                HARRY_HomePresenter.this.circleProgressView.setProgress((float) percentage);
            }
        });
    }

    public void onFinishDownloadFile(final HARRY_ConvertedFile convertedFile, File outputFile) {

        this.fileUtil.writeDownloadConvertedFile(convertedFile, outputFile, new CopyFileCallback() {
            public void onSuccess(final String outputPath) {
                convertedFile.setStatus(4);
                convertedFile.setLastModified(new Date());
                Log.e("Finish", "Downloading 100%");
                HARRY_HomePresenter.this.context.runOnUiThread(new Runnable() {
                    public void run() {
                        HARRY_HomePresenter.this.refreshButton.setVisibility(View.GONE);
                        HARRY_HomePresenter.this.circleProgressView.setVisibility(View.GONE);
                        HARRY_HomePresenter.this.circleProgressView.setProgress(0.0f);
                        HARRY_HomePresenter.this.progressBar.setVisibility(View.GONE);
                        HARRY_HomePresenter.this.convert.setVisibility(View.GONE);
                        HARRY_HomePresenter.this.context.adapter.replaceRow(HARRY_HomePresenter.this.currentRow, outputPath);
                    }
                });
            }

            public void onFailure() {
                HARRY_HomePresenter.this.onErrorConversion(convertedFile);
            }
        });
    }

    public void start() {
    }
}
