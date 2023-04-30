package harry.bjg.documentreaderofficereader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.HARRY_Help;
import harry.bjg.documentreaderofficereader.HARRY_ListActivity;
import harry.bjg.documentreaderofficereader.model.HARRY_ImageFolderData;
import harry.bjg.documentreaderofficereader.R;

public class HARRY_IF_Adapter extends
        RecyclerView.Adapter<HARRY_IF_Adapter.MyViewHolder> {

    static Context mContext;
    ArrayList<HARRY_ImageFolderData> list;
    public static int pos;

    public HARRY_IF_Adapter(Context context, ArrayList<HARRY_ImageFolderData> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.harry_if_adapter, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,
                                 final int position) {

        final HARRY_ImageFolderData fileData = list.get(position);

        String folderName = fileData.getfolder();
        holder.foldername.setText(new File(folderName).getName());

        Glide.with(mContext).load(fileData.getPath().get(0).getImageUrl()).into(holder.img);

        holder.vid_count.setText(fileData.getPath().size() + " Images");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HARRY_ListActivity.imageFolderData = fileData;
                HARRY_Help.nextwithnew(mContext, HARRY_ListActivity.class);
            }
        });

        if(position==(list.size()-1)){
            HARRY_Help.setMargin(holder.mainLay, 0,40,0,40, false);
        }else {
            HARRY_Help.setMargin(holder.mainLay, 0,40,0,0, false);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mainLay;
        public RoundedImageView img;
        public TextView foldername, vid_count;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainLay = itemView.findViewById(R.id.mainLay);
            img = itemView.findViewById(R.id.img);
            foldername = itemView.findViewById(R.id.foldername);
            vid_count = itemView.findViewById(R.id.vid_count);

            HARRY_Help.setSize(img, 316, 314, false);
            img.setCornerRadius(HARRY_Help.w(20));

            foldername.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arialbd.ttf"));
            vid_count.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
