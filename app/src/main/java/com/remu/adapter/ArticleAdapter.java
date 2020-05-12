package com.remu.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remu.POJO.Article;
import com.remu.R;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private ArrayList<Article> mDataset;
    private Application app;

    public ArticleAdapter(Application app, ArrayList<Article> mDataset) {
        this.app = app;
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_article, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
//            holder.image.setImageDrawable(mDataset.get(position).getImage());
            holder.title.setText(mDataset.get(position).getTitle());
            holder.highlight.setText((mDataset.get(position).getHighlight()));
        } catch (NullPointerException e) {

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView highlight;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_article);
            title = itemView.findViewById(R.id.title_article);
            highlight = itemView.findViewById(R.id.highlight_article);
        }
    }

}
