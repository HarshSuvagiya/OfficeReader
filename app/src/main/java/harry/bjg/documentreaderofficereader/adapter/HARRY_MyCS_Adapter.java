package harry.bjg.documentreaderofficereader.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.HARRY_Help;
import harry.bjg.documentreaderofficereader.HARRY_MyCreationActivity;
import harry.bjg.documentreaderofficereader.HARRY_OfficeFileViewActivity;
import harry.bjg.documentreaderofficereader.R;
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_ConvertedFile;

public class HARRY_MyCS_Adapter extends
        RecyclerView.Adapter<HARRY_MyCS_Adapter.MyViewHolder> {

    static Context mContext;
    ArrayList<String> list;
    public static int pos;

    public HARRY_MyCS_Adapter(Context context, ArrayList<String> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.harry_mysc_adapter, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,
                                 final int position) {

        final File file = new File(list.get(position));

        holder.name.setText(file.getName());

        final String selectedInputFormat = file.getName().substring(file.getName().lastIndexOf(".") + 1);

        if (selectedInputFormat.equals(HARRY_ConvertedFile.PDF)) {
            holder.type_img.setImageResource(R.drawable.pdf_file);
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.DOCX) || selectedInputFormat.equals(HARRY_ConvertedFile.DOC) || selectedInputFormat.equals(HARRY_ConvertedFile.DOCM)) {
            holder.type_img.setImageResource(R.drawable.doc_file);
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.XLS) || selectedInputFormat.equals(HARRY_ConvertedFile.XLSX) || selectedInputFormat.equals(HARRY_ConvertedFile.XLSM)) {
            holder.type_img.setImageResource(R.drawable.xls_file);
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.PPT) || selectedInputFormat.equals(HARRY_ConvertedFile.PPTX) || selectedInputFormat.equals(HARRY_ConvertedFile.PPTM)) {
            holder.type_img.setImageResource(R.drawable.ppt_file);
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.TXT)) {
            holder.type_img.setImageResource(R.drawable.txt_file);
        } else if (selectedInputFormat.equals(HARRY_ConvertedFile.HTML)) {
            holder.type_img.setImageResource(R.drawable.html_file);
        } else {
            holder.type_img.setImageResource(R.drawable.zip_file);
        }

        holder.info.setText("." + selectedInputFormat + "  " + HARRY_Help.getFileSize(file.length()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (selectedInputFormat.equals(HARRY_ConvertedFile.DOCX)) {
                        Intent intent = new Intent(mContext, HARRY_OfficeFileViewActivity.class);
                        intent.putExtra("name", file.getName());
                        intent.putExtra("path", file.getAbsolutePath());
                        mContext.startActivity(intent);
                    } else if (selectedInputFormat.equals(HARRY_ConvertedFile.PDF)) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = "application/pdf";
                        intent.setDataAndType(Uri.fromFile(file), type);
                        mContext.startActivity(intent);
                    } else if (selectedInputFormat.equals(HARRY_ConvertedFile.TXT) || selectedInputFormat.equals(HARRY_ConvertedFile.HTML)) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = "text/plain";
                        intent.setDataAndType(Uri.fromFile(file), type);
                        mContext.startActivity(intent);
                    } else if (selectedInputFormat.equals("zip")) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = "application/zip";
                        intent.setDataAndType(Uri.fromFile(file), type);
                        mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    HARRY_Help.Toast(mContext, "Application Not Found");
                }
            }
        });

        holder.dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sd_popup(file.getAbsolutePath(), selectedInputFormat);
            }
        });

//        holder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
//                alert.setTitle("Confirm Delete !");
//                alert.setMessage("Are you sure to delete File??");
//                alert.setPositiveButton("YES",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do your work here
//                                dialog.dismiss();
//                                if (file.delete()) {
//                                    HARRY_Help.Toast(mContext, "File Deleted");
//                                }
//                                ((HARRY_MyCreationActivity) mContext).getFromSdcard();
//                            }
//                        });
//                alert.setNegativeButton("NO",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                alert.show();
//            }
//        });

//        holder.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent share = new Intent(Intent.ACTION_SEND);
//                share.setType("application/" + selectedInputFormat);
//                Uri uri = Uri.parse("file://" + file.getAbsolutePath());
//                share.putExtra(Intent.EXTRA_STREAM, uri);
//                mContext.startActivity(Intent.createChooser(share, "Share"));
//            }
//        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mainLay;
        public TextView name, info;
        public ImageView type_img, dot;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainLay = itemView.findViewById(R.id.mainLay);
            name = itemView.findViewById(R.id.name);
            type_img = itemView.findViewById(R.id.type_img);
            info = itemView.findViewById(R.id.info);
            dot = itemView.findViewById(R.id.dot);

            HARRY_Help.setSize(mainLay, 980, 170, false);
            HARRY_Help.setSize(type_img, 76, 106, false);
            HARRY_Help.setSize(dot, 52, 52, false);

            name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));
            info.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void sd_popup(String path, String selectedInputFormat) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.harry_dot_popup);

        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        HARRY_Help.setSize(mainLay, 978, 750, false);

        TextView title=dialog.findViewById(R.id.title);
        title.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));

        ImageView share = dialog.findViewById(R.id.share);
        ImageView delete = dialog.findViewById(R.id.delete);

        HARRY_Help.setSize(share, 512, 162, false);
        HARRY_Help.setSize(delete, 512, 162, false);
        HARRY_Help.setMargin(delete, 0,50,0,0, false);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("application/" + selectedInputFormat);
                Uri uri = Uri.parse("file://" + path);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                mContext.startActivity(Intent.createChooser(share, "Share"));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Confirm Delete !");
                alert.setMessage("Are you sure to delete File??");
                alert.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do your work here
                                dialog.dismiss();
                                File file=new File(path);
                                if (file.delete()) {
                                    HARRY_Help.Toast(mContext, "File Deleted");
                                }
                                ((HARRY_MyCreationActivity) mContext).getFromSdcard();
                            }
                        });
                alert.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alert.show();
            }
        });

        dialog.show();
    }
}
