package harry.bjg.documentreaderofficereader.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eralp.circleprogressview.CircleProgressView;

import java.io.File;
import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.HARRY_CategoryActivity;
import harry.bjg.documentreaderofficereader.HARRY_Help;
import harry.bjg.documentreaderofficereader.R;
import harry.bjg.documentreaderofficereader.convertapi.HARRY_ConverInterface;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_ConvertedFile;

import static harry.bjg.documentreaderofficereader.HARRY_Help.visible;

public class HARRY_ConvertAdapter extends RecyclerView.Adapter<HARRY_ConvertAdapter.ViewHolder> {

    ArrayList<String> myList;
    public Activity context;
    HARRY_ConverInterface converInterface;
    int counter = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mainLay;
        public CircleProgressView circleProgressView;
        public TextView filename;
        public ImageView imgRefresh, type_img, btnConvert;
        public ProgressBar progress_bar;

        public ViewHolder(View view) {
            super(view);
            this.mainLay = view.findViewById(R.id.mainLay);
            this.filename = view.findViewById(R.id.file_name_tv);
            this.imgRefresh = view.findViewById(R.id.refresh_image);
            this.circleProgressView = view.findViewById(R.id.circle_progress_view);
            this.progress_bar = view.findViewById(R.id.progress_bar);
            this.btnConvert = view.findViewById(R.id.btnConvert);
            this.type_img = view.findViewById(R.id.type_img);

            HARRY_Help.setSize(mainLay,980,200,false);
            HARRY_Help.setSize(btnConvert,260,90,false);
            HARRY_Help.setSize(imgRefresh,110,110,false);
            HARRY_Help.setSize(type_img,84,106,false);
            HARRY_Help.setSize(progress_bar,90,90,false);
            HARRY_Help.setSize(circleProgressView,90,90,false);
        }
    }

    public HARRY_ConvertAdapter(Activity context2, ArrayList<String> android2, HARRY_ConverInterface converInterface2) {
        this.context = context2;
        this.myList = android2;
        this.converInterface = converInterface2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.harry_converted_file_item, viewGroup, false));
    }

    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final File file = new File((String) this.myList.get(i));
        viewHolder.filename.setText(file.getName());
        final String selectedInputFormat = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        visible(viewHolder.btnConvert);

        if (selectedInputFormat.equals(HARRY_ConvertedFile.PDF)) {
            viewHolder.type_img.setImageResource(R.drawable.pdf_file);
            if (HARRY_CategoryActivity.position == 0 || HARRY_CategoryActivity.position == 1 || HARRY_CategoryActivity.position == 2 || HARRY_CategoryActivity.position == 3) {
                viewHolder.btnConvert.setImageResource(R.drawable.convert_button);
                viewHolder.btnConvert.setEnabled(true);
            } else {
                viewHolder.btnConvert.setEnabled(false);
                viewHolder.btnConvert.setImageResource(R.drawable.done_button);
            }
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.DOCX) || selectedInputFormat.equals(HARRY_ConvertedFile.DOC) || selectedInputFormat.equals(HARRY_ConvertedFile.DOCM)) {
            viewHolder.type_img.setImageResource(R.drawable.doc_file);
            if (HARRY_CategoryActivity.position == 4 || HARRY_CategoryActivity.position == 5 || HARRY_CategoryActivity.position == 6 || HARRY_CategoryActivity.position == 7) {
                viewHolder.btnConvert.setImageResource(R.drawable.convert_button);
                viewHolder.btnConvert.setEnabled(true);
            } else {
                viewHolder.btnConvert.setEnabled(false);
                viewHolder.btnConvert.setImageResource(R.drawable.done_button);
            }
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.XLS) || selectedInputFormat.equals(HARRY_ConvertedFile.XLSX) || selectedInputFormat.equals(HARRY_ConvertedFile.XLSM)) {
            viewHolder.type_img.setImageResource(R.drawable.xls_file);
            if (HARRY_CategoryActivity.position == 8 || HARRY_CategoryActivity.position == 9 || HARRY_CategoryActivity.position == 10) {
                viewHolder.btnConvert.setImageResource(R.drawable.convert_button);
                viewHolder.btnConvert.setEnabled(true);
            } else {
                viewHolder.btnConvert.setEnabled(false);
                viewHolder.btnConvert.setImageResource(R.drawable.done_button);
            }
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.PPT) || selectedInputFormat.equals(HARRY_ConvertedFile.PPTX) || selectedInputFormat.equals(HARRY_ConvertedFile.PPTM)) {
            viewHolder.type_img.setImageResource(R.drawable.ppt_file);
            if (HARRY_CategoryActivity.position == 11 || HARRY_CategoryActivity.position == 12 || HARRY_CategoryActivity.position == 13 || HARRY_CategoryActivity.position == 14) {
                viewHolder.btnConvert.setImageResource(R.drawable.convert_button);
                viewHolder.btnConvert.setEnabled(true);
            } else {
                viewHolder.btnConvert.setEnabled(false);
                viewHolder.btnConvert.setImageResource(R.drawable.done_button);
            }
        } else {
            if (selectedInputFormat.equals(HARRY_ConvertedFile.TXT)) {
                viewHolder.type_img.setImageResource(R.drawable.txt_file);
            } else if (selectedInputFormat.equals(HARRY_ConvertedFile.HTML)) {
                viewHolder.type_img.setImageResource(R.drawable.html_file);
            } else {
                viewHolder.type_img.setImageResource(R.drawable.zip_file);
            }
            viewHolder.btnConvert.setEnabled(false);
            viewHolder.btnConvert.setImageResource(R.drawable.done_button);
        }

        final ViewHolder viewHolder2 = viewHolder;
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (viewHolder2.imgRefresh.getVisibility() == View.GONE && viewHolder2.btnConvert.getVisibility() == View.GONE && viewHolder2.progress_bar.getVisibility() == View.GONE && viewHolder2.circleProgressView.getVisibility() == View.GONE) {
                    try {
                        if (selectedInputFormat.equals(HARRY_ConvertedFile.DOCX)) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            String type = "application/msword";
                            intent.setDataAndType(Uri.fromFile(file), type);
                            context.startActivity(intent);
                        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.PDF)) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            String type = "application/pdf";
                            intent.setDataAndType(Uri.fromFile(file), type);
                            context.startActivity(intent);
                        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.TXT) || selectedInputFormat.equals(HARRY_ConvertedFile.HTML)) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            String type = "text/plain";
                            intent.setDataAndType(Uri.fromFile(file), type);
                            context.startActivity(intent);
                        } else if (selectedInputFormat.equals("zip")) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            String type = "application/zip";
                            intent.setDataAndType(Uri.fromFile(file), type);
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        HARRY_Help.Toast(context, "Application Not Found");
                    }
                }
            }
        });
        viewHolder.btnConvert.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                converInterface.updateRow(i, (String) myList.get(i), viewHolder.progress_bar, viewHolder.circleProgressView, viewHolder.imgRefresh, viewHolder.btnConvert);
            }
        });
        viewHolder.imgRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                converInterface.updateRow(i, (String) myList.get(i), viewHolder.progress_bar, viewHolder.circleProgressView, viewHolder.imgRefresh, viewHolder.btnConvert);
            }
        });
    }

    public int getItemCount() {
        return this.myList.size();
    }

    public void replaceRow(int position, String path) {
        if (path.endsWith(".jpg")) {
            File from = new File(path);
            File to = new File(HARRY_Help.getFolderPath(context), from.getName().replace(".jpg", ".zip"));
            if (from.exists()) {
                from.renameTo(to);
            }
            path = to.getAbsolutePath();
        }
        this.myList.remove(position);
        this.myList.add(position, path);
        notifyDataSetChanged();
    }
}
