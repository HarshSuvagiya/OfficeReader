package harry.bjg.documentreaderofficereader.FragmentFol;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.foobnix.dao2.FileMeta;
import com.foobnix.pdf.info.ExtUtils;

import java.util.ArrayList;

import harry.bjg.documentreaderofficereader.ActivityFol.HARRY_ExcelPage;
import harry.bjg.documentreaderofficereader.Ad_Folder.HARRY_Ad_FileList;
import harry.bjg.documentreaderofficereader.AsynckFol.HARRY_AsyngetFiles;
import harry.bjg.documentreaderofficereader.HARRY_MyUtils;
import harry.bjg.documentreaderofficereader.InterFol.HARRY_OnCommonProgress;
import harry.bjg.documentreaderofficereader.InterFol.HARRY_OnMyCommonItem;
import harry.bjg.documentreaderofficereader.ModelFol.HARRY_FileMod;
import harry.bjg.documentreaderofficereader.R;

public class HARRY_FragList extends Fragment {

    RecyclerView recyclerView;
    HARRY_Ad_FileList ad_fileList;
    ProgressDialog pd;

    //    TextView btnselect;
    ArrayList<HARRY_FileMod> allfile = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.harry_frag_list, container, false);

        init(view);
        resize();
        click();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Find The File");
        pd.setCancelable(false);
        pd.show();
        loadList(0);

        ad_fileList = new HARRY_Ad_FileList(getActivity(), allfile, new HARRY_OnMyCommonItem() {
            @Override
            public void OnMyClick1(int pos, Object object) {
                HARRY_FileMod fileMod = (HARRY_FileMod) object;
                if (fileMod.getExt().equalsIgnoreCase("xls")) {
                    startActivity(new Intent(getActivity(), HARRY_ExcelPage.class).putExtra("path", fileMod.getPath()));

//                }else if (fileMod.getExt().equalsIgnoreCase("ppt") || fileMod.getExt().equalsIgnoreCase("pptx")) {
//                    startActivity(new Intent(getActivity(), HARRY_PptPage.class).putExtra("path", fileMod.getPath()));

                } else {
                    FileMeta fileMeta = new FileMeta(fileMod.getPath());
                    ExtUtils.openFile(getActivity(), fileMeta);
                }
            }

            @Override
            public void OnMyClick2(int pos, Object object) {

            }
        });
        recyclerView.setAdapter(ad_fileList);

        return view;
    }

    private void click() {

       /* btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialogFormat();
            }

            private void displayDialogFormat() {

                final Dialog dialog = new Dialog(getActivity());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.harry_dialog_format);

                TextView btnall = dialog.findViewById(R.id.btnall);
                TextView btnpdf = dialog.findViewById(R.id.btnpdf);
                TextView btndoc = dialog.findViewById(R.id.btndoc);
                TextView btntext = dialog.findViewById(R.id.btntext);
                TextView btnexcel = dialog.findViewById(R.id.btnexcel);

                View.OnClickListener click = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int tag = Integer.parseInt(view.getTag().toString());
                        String text = ((TextView) view).getText().toString();
                        btnselect.setText(text);
                        dialog.dismiss();
                        loadList(tag);
                    }
                };

                btnall.setOnClickListener(click);
                btnpdf.setOnClickListener(click);
                btndoc.setOnClickListener(click);
                btntext.setOnClickListener(click);
                btnexcel.setOnClickListener(click);

                dialog.show();

            }
        });*/
    }

    public void loadList(int which) {
        new HARRY_AsyngetFiles(getActivity(), which, new HARRY_OnCommonProgress() {
            @Override
            public void OnComplete(Boolean iscomplete, Object object) {
                ArrayList<HARRY_FileMod> allfile1 = (ArrayList<HARRY_FileMod>) object;
                allfile.clear();
                allfile.addAll(allfile1);
                ad_fileList.notifyDataSetChanged();
                if (allfile.size() > 0) {
                    /*ad_fileList = new HARRY_Ad_FileList(getActivity(), allfile, new HARRY_OnMyCommonItem() {
                        @Override
                        public void OnMyClick1(int pos, Object object) {
                            HARRY_FileMod fileMod = (HARRY_FileMod) object;
                            if (fileMod.getExt().equalsIgnoreCase("xls")) {
                                startActivity(new Intent(getActivity(), HARRY_ExcelPage.class).putExtra("path", fileMod.getPath()));

                            } else {
                                FileMeta fileMeta = new FileMeta(fileMod.getPath());
                                ExtUtils.openFile(getActivity(), fileMeta);
                            }
                        }

                        @Override
                        public void OnMyClick2(int pos, Object object) {

                        }
                    });
                    recyclerView.setAdapter(ad_fileList);*/
                } else {
                    HARRY_MyUtils.Toast(getActivity(), "No data Found");
                }

                if (pd.isShowing())
                    pd.dismiss();
            }
        }).execute();
    }

    private void resize() {
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
//        btnselect = view.findViewById(R.id.btnselect);
    }
}