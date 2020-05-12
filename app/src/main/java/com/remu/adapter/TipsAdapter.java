package com.remu.adapter;

import android.app.Activity;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.remu.POJO.Tips;
import com.remu.R;

import java.util.ArrayList;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder> {

    private ArrayList<Tips> mDataset;
    private Application app;

    public TipsAdapter(Application app, ArrayList<Tips> mDataset) {
        this.app = app;
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public TipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_tips, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsAdapter.ViewHolder holder, int position) {
        try {
            holder.image.setImageDrawable(mDataset.get(position).getImage());
            holder.title.setText(mDataset.get(position).getTitle());

            holder.explore.setOnClickListener(v -> {

            });

            holder.bookmark.setOnClickListener(v -> {
                if (holder.isSaved) {
                    holder.bookmark.setImageDrawable(app.getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                    holder.isSaved = false;
                } else {
                    holder.bookmark.setImageDrawable(app.getDrawable(R.drawable.ic_bookmark_fill_black_24dp));
                    holder.isSaved = true;
                }
            });
        } catch (NullPointerException e) {

        }
    }

    @Override
    public int getItemCount() { return mDataset.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageView bookmark;
        ImageView explore;
        TextView title;
        boolean isSaved;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_tips);
            bookmark = itemView.findViewById(R.id.bookmark_tips);
            explore = itemView.findViewById(R.id.explore_tips);
            title = itemView.findViewById(R.id.title_tips);
            isSaved = false;
        }
    }

}
