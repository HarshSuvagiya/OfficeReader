package com.foobnix;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.foobnix.android.utils.TxtUtils;
import com.foobnix.pdf.info.Help1;
import com.foobnix.pdf.info.IMG;
import com.foobnix.pdf.info.PageUrl;
import com.foobnix.pdf.info.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

public class Ad_PageThumb extends RecyclerView.Adapter<Ad_PageThumb.Holder> {

    Context cn;
    File currentBook;
    private int currentPage;
    private int pageCount;
    OnCommonProgress onCommonProgress;
    private LayoutInflater inflater;


    public Ad_PageThumb(Context cn, File currentBook, int currentPage, int pageCount, OnCommonProgress onCommonProgress) {
        this.cn = cn;
        this.currentBook = currentBook;
        this.currentPage = currentPage;
        this.pageCount = pageCount;
        this.onCommonProgress = onCommonProgress;
        inflater = LayoutInflater.from(cn);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (view == null) {
            view = inflater.inflate(R.layout.ad_pagethumb, parent, false);
        }
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        if (currentPage == position) {
            holder.laymain.setBackgroundResource(R.drawable.select9);
//            holder.setpage.setTextColor(Color.WHITE);
        } else {
            holder.laymain.setBackgroundResource(R.drawable.unselect9);
//            holder.setpage.setTextColor(Color.BLACK);
        }


        PageUrl pageUrl = getPageUrl(position);
        final String url = pageUrl.toString();
        ImageLoader.getInstance().displayImage(url, holder.setthumb, IMG.displayImageOptionsNoDiscCache);

        holder.setpage.setText(TxtUtils.deltaPage((position + 1)));

        holder.laymain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommonProgress.OnComplete(true, position);
            }
        });

    }

    private PageUrl getPageUrl(int position) {
        return PageUrl.buildSmall(currentBook.getPath(), position);
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }

    public void setcurrent(int i) {
        currentPage = i;
        notifyDataSetChanged();
    }

    public class Holder extends ViewHolder {
        RelativeLayout laymain;
        TextView setpage;
        ImageView setthumb;

        public Holder(@NonNull View itemView) {
            super(itemView);
            laymain = itemView.findViewById(R.id.laymain);
            setpage = itemView.findViewById(R.id.text1);
            setthumb = itemView.findViewById(R.id.image1);

            Help1.setSize(laymain,278,284,false);
            Help1.setSize(setthumb,268,274,false);
            Help1.setSize(setpage,100,52,false);

            setpage.setTypeface(Typeface.createFromAsset(cn.getAssets(), "arial.ttf"));
        }
    }

}
