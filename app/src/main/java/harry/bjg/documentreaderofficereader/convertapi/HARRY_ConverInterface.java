package harry.bjg.documentreaderofficereader.convertapi;

import android.widget.ImageView;
import android.widget.ProgressBar;
import com.eralp.circleprogressview.CircleProgressView;

public interface HARRY_ConverInterface {
    void updateRow(int i, String str, ProgressBar progressBar, CircleProgressView circleProgressView, ImageView imageView, ImageView button);
}
