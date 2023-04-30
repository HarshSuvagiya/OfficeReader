package harry.bjg.documentreaderofficereader.convertapi.util;

import android.net.Uri;
import java.io.File;
import org.json.JSONObject;

interface HARRY_HomeContract {

    public interface Presenter extends HARRY_BasePresenter {
        void createCloudConvertProcess(HARRY_ConvertedFile convertedFile);

        void createZamzarProcess(HARRY_ConvertedFile convertedFile);

        void onDownloadFile(HARRY_ConvertedFile convertedFile, int i);

        void onErrorConversion(HARRY_ConvertedFile convertedFile);

        void onFinishDownloadFile(HARRY_ConvertedFile convertedFile, File file);

        void onUploadFile(HARRY_ConvertedFile convertedFile, int i);

        void prepareUploadingFile(HARRY_ConvertedFile convertedFile);

        void prepareUri(Uri uri);

        void selectOutputName(String str);

        void startCloudConvertDownload(HARRY_ConvertedFile convertedFile);

        void startCloudConvertProcess(HARRY_ConvertedFile convertedFile);

        void startCloudConvertUploading(HARRY_ConvertedFile convertedFile);

        void startCloudConvertUploading(HARRY_ConvertedFile convertedFile, File file, String str);

        void startConversion(HARRY_ConvertedFile convertedFile);

        void startZamzarRedirectDownload(HARRY_ConvertedFile convertedFile, JSONObject jSONObject);

        void startZamzartDownload(HARRY_ConvertedFile convertedFile, String str);

        void updateCloudConvertProgress(HARRY_ConvertedFile convertedFile);

        void updateZamzarProcess(HARRY_ConvertedFile convertedFile);
    }
}
