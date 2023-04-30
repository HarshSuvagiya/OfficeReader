package harry.bjg.documentreaderofficereader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import harry.bjg.documentreaderofficereader.HARRY_CreateDocActivity;
import harry.bjg.documentreaderofficereader.HARRY_Help;
import harry.bjg.documentreaderofficereader.HARRY_ListActivity;
import harry.bjg.documentreaderofficereader.model.HARRY_ImageData;
import harry.bjg.documentreaderofficereader.R;

import static harry.bjg.documentreaderofficereader.HARRY_Help.myList;

public class HARRY_List_Adapter extends
        RecyclerView.Adapter<HARRY_List_Adapter.MyViewHolder> {

    static Context mContext;
    ArrayList<HARRY_ImageData> folders;

    public HARRY_List_Adapter(Context context, ArrayList<HARRY_ImageData> myfolders) {
        this.mContext = context;
        this.folders = myfolders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.harry_list_adapter, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,
                                 final int position) {

        final File f=new File(folders.get(position).getImageUrl());

        holder.foldername.setText(folders.get(position).getTitle());

        Glide.with(mContext).load(folders.get(position).getImageUrl())
                .into(holder.folder_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(HARRY_CreateDocActivity.mSingle){
                    myList.add(0, folders.get(position));
                    ((HARRY_ListActivity) mContext).StartConvert();
                }else {
                    boolean b = false;
                    for (int i = 0; i < myList.size(); i++) {
                        if (myList.get(i).getImageUrl().equals(f.getAbsolutePath())) {
                            myList.remove(i);
                            ((HARRY_ListActivity) mContext).removeView(i);
                            b = true;
                        }
                    }
                    if (!b) {
                        myList.add(0, folders.get(position));
                        ((HARRY_ListActivity) mContext).addLay(f.getAbsolutePath(), false);
                    }

                    notifyDataSetChanged();
                }
            }
        });

        boolean b = false;
        for (HARRY_ImageData filepath : myList) {
            if (filepath.getImageUrl().equals(folders.get(position).getImageUrl())) {
                b = true;
            }
        }

        if (b) {
            HARRY_Help.VISIBLE(holder.selected);
        } else {
            HARRY_Help.GONE(holder.selected);
        }

        if(position==(folders.size()-1)){
            HARRY_Help.setMargin(holder.mainlay, 0,40,0,40, false);
        }else {
            HARRY_Help.setMargin(holder.mainlay, 0,40,0,0, false);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mainlay;
        public RelativeLayout centerLay;
        public RoundedImageView folder_img,selected;
        public TextView foldername;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainlay = itemView.findViewById(R.id.mainlay);
            folder_img = itemView.findViewById(R.id.folder_img);
            foldername = itemView.findViewById(R.id.foldername);
            centerLay = itemView.findViewById(R.id.centerLay);
            selected = itemView.findViewById(R.id.selected);

            HARRY_Help.setSize(centerLay, 316, 314, false);

            folder_img.setCornerRadius(HARRY_Help.w(20));

            foldername.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));
        }
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

}
