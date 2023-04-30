package harry.bjg.documentreaderofficereader.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import harry.bjg.documentreaderofficereader.HARRY_Help;
import harry.bjg.documentreaderofficereader.R;
import harry.bjg.documentreaderofficereader.model.HARRY_FileList;

public class HARRY_FileListAdapter extends BaseAdapter {

    private ArrayList<HARRY_FileList> title;
    private ArrayList<HARRY_FileList> list;
    LayoutInflater inflater = null;
    Context mContext;

    // constructor
    public HARRY_FileListAdapter(Context context, ArrayList<HARRY_FileList> mytitle) {
        this.mContext = context;
        this.title = mytitle;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = new ArrayList<>();
        this.list.addAll(mytitle);
    }

    public int getCount() {
        return title.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // returns the individual images to the widget as it requires them
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null)
            vi = inflater.inflate(R.layout.harry_file_list_adapter, null);

        LinearLayout mainLay = vi.findViewById(R.id.mainLay);
        HARRY_Help.setSize(mainLay, 980, 170, false);

        TextView listtext = vi.findViewById(R.id.listtext);
        TextView info = vi.findViewById(R.id.info);
        TextView date = vi.findViewById(R.id.date);

        listtext.setText(title.get(position).getTitle());
        final String format = title.get(position).getTitle().substring(title.get(position).getTitle().lastIndexOf(".") + 1);

        ImageView type = vi.findViewById(R.id.type);
        if (format.equalsIgnoreCase("doc") || format.equalsIgnoreCase("docx")) {
            type.setImageResource(R.drawable.doc_file);
        } else if (format.equalsIgnoreCase("ppt") || format.equalsIgnoreCase("pptx")) {
            type.setImageResource(R.drawable.ppt_file);
        } else if (format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("xlsx")) {
            type.setImageResource(R.drawable.xls_file);
        } else if (format.equalsIgnoreCase("pdf")) {
            type.setImageResource(R.drawable.pdf_file);
        } else if (format.equalsIgnoreCase("txt")) {
            type.setImageResource(R.drawable.txt_file);
        }
        HARRY_Help.setSize(type, 76, 106, false);

        info.setText("."+format+"  "+ HARRY_Help.getFileSize(new File(title.get(position).getPath()).length()));
        date.setText(HARRY_Help.getDateNew(new File(title.get(position).getPath()).lastModified()));

        listtext.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));
        info.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));
        date.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));

        return vi;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        title.clear();
        if (charText.length() == 0) {
            title.addAll(this.list);
        } else {
            Iterator<HARRY_FileList> it = this.list.iterator();
            while (it.hasNext()) {
                HARRY_FileList wp = (HARRY_FileList) it.next();
                if (wp.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    title.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
