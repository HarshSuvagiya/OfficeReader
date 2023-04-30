package harry.bjg.documentreaderofficereader.Ad_Folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.HARRY_MyUtils;
import harry.bjg.documentreaderofficereader.InterFol.HARRY_OnMyCommonItem;
import harry.bjg.documentreaderofficereader.ModelFol.HARRY_FileMod;
import harry.bjg.documentreaderofficereader.R;

public class HARRY_Ad_FileList extends RecyclerView.Adapter<HARRY_Ad_FileList.Holder> {

    Context cn;
    ArrayList<HARRY_FileMod> allfile = new ArrayList<>();
    HARRY_OnMyCommonItem onMyCommonIteml;

    public HARRY_Ad_FileList(Context cn, ArrayList<HARRY_FileMod> allfile, HARRY_OnMyCommonItem onMyCommonIteml) {
        this.cn = cn;
        this.allfile = allfile;
        this.onMyCommonIteml = onMyCommonIteml;
    }

    public class Holder extends RecyclerView.ViewHolder {

        LinearLayout laymain;
        TextView setname;
        TextView setsize;
        TextView setext;
        ImageView setthumb;

        public Holder(@NonNull View itemView) {
            super(itemView);
            laymain = itemView.findViewById(R.id.laymain);
            setname = itemView.findViewById(R.id.setname);
            setsize = itemView.findViewById(R.id.setsize);
            setext = itemView.findViewById(R.id.setext);
            setthumb = itemView.findViewById(R.id.setthumb);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(cn).inflate(R.layout.harry_ad_filelist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {

        resize(holder);

        HARRY_FileMod fileMod = allfile.get(position);
        holder.setname.setText(fileMod.getName());
        holder.setsize.setText(fileMod.getSize());
        holder.setext.setText("." + fileMod.getExt());

        holder.laymain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMyCommonIteml.OnMyClick1(position, allfile.get(position));
            }
        });

        String thumb = "file:///android_asset/thumb/" + fileMod.getExt().toLowerCase() + "_file.png";
        Glide.with(cn).load(thumb).into(holder.setthumb);


    }

    private void resize(Holder holder) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                cn.getResources().getDisplayMetrics().widthPixels * 990 / 1080,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        holder.laymain.setLayoutParams(layoutParams);

        layoutParams = HARRY_MyUtils.getParamsLSquare(cn, 116);
        holder.setthumb.setLayoutParams(layoutParams);

    }

    @Override
    public int getItemCount() {
        return allfile.size();
    }


}
