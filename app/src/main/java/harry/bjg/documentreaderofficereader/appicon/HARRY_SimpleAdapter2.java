package harry.bjg.documentreaderofficereader.appicon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Random;

import harry.bjg.documentreaderofficereader.R;


public class HARRY_SimpleAdapter2 extends BaseAdapter {
    private final String pkg;
    int width = 0;
    Activity context;
    public HARRY_SimpleAdapter2(Activity context, String pkg) {
        this.context=context;
        this.pkg=pkg;
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int screen_width = displayMetrics.widthPixels; // width of the device
        width = screen_width / 3;
    }

    @Override
    public int getCount() {
        return HARRY_Java_Grid_Utils.packArr2.size();
    }

    @Override
    public Object getItem(int arg0) {
        return HARRY_Java_Grid_Utils.packArr2.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ViewHolder holder = null;

        Random r = new Random();
        int n=r.nextInt(3);

        String pack = context.getPackageName();

        int id = context.getResources().getIdentifier(
                ""+pack+":drawable/" + "frm"+n, null, null);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), id);

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.harry_java_row_online_applist_home,
                    null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.icon);
            holder.img = (ImageView) convertView
                    .findViewById(R.id.img);
            holder.tvTitle = (TextView) convertView
                    .findViewById(R.id.title);

            holder.btndownload = (Button) convertView
                    .findViewById(R.id.btn_install);
            holder.ll_row = (LinearLayout) convertView
                    .findViewById(R.id.ll_row);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try{
            String url1 = "http://phpstack-192394-571052.cloudwaysapps.com/AdsIcon/"+pkg+"/"
                    + HARRY_Java_Grid_Utils.packArr2.get(position) ;

// 			String url = getResources().getString(R.string.addUrl1icon)
// 					+ Utils.packArr2.get(position).packagename + ".jpg";


            Animation animFadein = AnimationUtils.loadAnimation(context.getApplicationContext(),
                    R.anim.icon_anim);
            Glide.with(context).load(url1)
                    .into(holder.imageView);
            holder.imageView.startAnimation(animFadein);

            holder.tvTitle.setSelected(true);

            holder.tvTitle.setText(""
                    +  HARRY_Java_Grid_Utils.packArr2.get(position).split("_")[1].substring(0, HARRY_Java_Grid_Utils.packArr2.get(position).split("_")[1].lastIndexOf(".")));
        }catch (Exception e){}

        holder.img.setImageBitmap(icon);

        holder.btndownload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.setData(Uri
                            .parse("market://details?id="
                                    + HARRY_Java_Grid_Utils.packArr2.get(position).split("_")[0]));
//                        new Java_Request_clickcounter(HARRY_Java_Grid_Utils.packArr2
//                                .get(position),
//                                getPackageName(), "icon")
//                                .execute();

                    context.startActivityForResult(intent,159);
                } catch (Exception e) {
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri
                            .parse("market://details?id="
                                    + HARRY_Java_Grid_Utils.packArr2.get(position).split("_")[0]));
//                        new Java_Request_clickcounter(HARRY_Java_Grid_Utils.packArr2
//                                .get(position),
//                                getPackageName(),
//                                "icon").execute();
                    context.startActivityForResult(intent,159);
                } catch (Exception e) {
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        ImageView img;
        TextView tvTitle;
        Button btndownload;
        LinearLayout ll_row;
    }
}