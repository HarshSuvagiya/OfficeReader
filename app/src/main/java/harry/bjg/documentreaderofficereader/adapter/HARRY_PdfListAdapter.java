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
import harry.bjg.documentreaderofficereader.convertapi.util.HARRY_ConvertedFile;
import harry.bjg.documentreaderofficereader.model.HARRY_PdfList;


public class HARRY_PdfListAdapter extends BaseAdapter {

    private ArrayList<HARRY_PdfList> title;
    private ArrayList<HARRY_PdfList> list;
    LayoutInflater inflater = null;
    Context mContext;

    // constructor
    public HARRY_PdfListAdapter(Context context, ArrayList<HARRY_PdfList> mytitle) {
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
            vi = inflater.inflate(R.layout.harry_pdf_list_adapter, null);

        LinearLayout mainLay = vi.findViewById(R.id.mainLay);
        HARRY_Help.setSize(mainLay, 980, 170, false);

        ImageView type = vi.findViewById(R.id.type);
        HARRY_Help.setSize(type, 76, 106, false);

        TextView listtext = vi.findViewById(R.id.listtext);
        TextView info = vi.findViewById(R.id.info);
        TextView date = vi.findViewById(R.id.date);

        listtext.setText(title.get(position).getTitle());

        final String selectedInputFormat = title.get(position).getTitle().substring(title.get(position).getTitle().lastIndexOf(".") + 1);

        if (selectedInputFormat.equals(HARRY_ConvertedFile.PDF)) {
            type.setImageResource(R.drawable.pdf_file);
        }else if (selectedInputFormat.equals(HARRY_ConvertedFile.DOCX) || selectedInputFormat.equals(HARRY_ConvertedFile.DOC) || selectedInputFormat.equals(HARRY_ConvertedFile.DOCM)) {
            type.setImageResource(R.drawable.doc_file);
        }else if (selectedInputFormat.equals(HARRY_ConvertedFile.XLS) || selectedInputFormat.equals(HARRY_ConvertedFile.XLSX) || selectedInputFormat.equals(HARRY_ConvertedFile.XLSM)) {
            type.setImageResource(R.drawable.xls_file);
        }else if (selectedInputFormat.equals(HARRY_ConvertedFile.PPT) || selectedInputFormat.equals(HARRY_ConvertedFile.PPTX) || selectedInputFormat.equals(HARRY_ConvertedFile.PPTM)) {
            type.setImageResource(R.drawable.ppt_file);
        }

        info.setText("."+selectedInputFormat+"  "+ HARRY_Help.getFileSize(new File(title.get(position).getPath()).length()));
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
            Iterator<HARRY_PdfList> it = this.list.iterator();
            while (it.hasNext()) {
                HARRY_PdfList wp = (HARRY_PdfList) it.next();
                if (wp.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    title.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
