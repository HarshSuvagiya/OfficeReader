package harry.bjg.documentreaderofficereader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import harry.bjg.documentreaderofficereader.HARRY_Help;
import harry.bjg.documentreaderofficereader.R;


public class HARRY_CategoryAdapter extends BaseAdapter {

    private ArrayList<String> title;
    private ArrayList<String> list;
    LayoutInflater inflater = null;
    Context mContext;

    // constructor
    public HARRY_CategoryAdapter(Context context, ArrayList<String> mytitle) {
        this.mContext = context;
        this.title = mytitle;
        this.list = new ArrayList<>();
        this.list.addAll(mytitle);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.harry_category_adapter, null);

        LinearLayout mainLay = vi.findViewById(R.id.mainLay);
        HARRY_Help.setSize(mainLay, 980, 200, false);

        ImageView icon = vi.findViewById(R.id.icon);
        HARRY_Help.setSize(icon, 88, 106, false);

        TextView listtext = vi.findViewById(R.id.listtext);
        listtext.setText(title.get(position));
        listtext.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "arial.ttf"));
        return vi;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        title.clear();
        if (charText.length() == 0) {
            title.addAll(this.list);
        } else {
            Iterator<String> it = this.list.iterator();
            while (it.hasNext()) {
                String wp = (String) it.next();
                if (wp.toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    title.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
