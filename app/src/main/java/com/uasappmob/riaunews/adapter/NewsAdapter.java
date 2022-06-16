package com.uasappmob.riaunews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uasappmob.riaunews.R;
import com.uasappmob.riaunews.config.ServerConfig;
import com.uasappmob.riaunews.listener.OnDeleteClickListener;
import com.uasappmob.riaunews.listener.OnDetailClickListener;
import com.uasappmob.riaunews.listener.OnUpdateClickListener;
import com.uasappmob.riaunews.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    List<News> news;

    OnDetailClickListener onDetailClickListener;
    OnDeleteClickListener onDeleteClickListener;
    OnUpdateClickListener onUpdateClickListener;

    public NewsAdapter(Context context) {
        this.context = context;
        news = new ArrayList<>();
    }

    public void add(News item) {
        news.add(item);
        notifyItemInserted(news.size() - 1);
    }

    public void addAll(List<News> items) {
        for (News item : items) {
            add(item);
        }
    }

    public void setOnDetailClickListener(OnDetailClickListener onDetailClickListener) {
        this.onDetailClickListener = onDetailClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnUpdateClickListener(OnUpdateClickListener onUpdateClickListener) {
        this.onUpdateClickListener = onUpdateClickListener;
    }

    public News getNews(int position) {
        return news.get(position);
    }

    public void remove(int position) {
        if (position >= 0 && position < news.size()) {
            news.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(news.get(position));
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCategory, tvCreatedAt;
        ImageView imgCover;
        Button btnEdit, btnRemove;
        RelativeLayout rlDetail;

        public ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_news, parent, false));
            initViews();
            rlDetail.setOnClickListener(view -> onDetailClickListener.onDetailClick(getAbsoluteAdapterPosition()));
            btnEdit.setOnClickListener(view -> onUpdateClickListener.onUpdateClick(getAbsoluteAdapterPosition()));
            btnRemove.setOnClickListener(view -> onDeleteClickListener.onDeleteClick(getAbsoluteAdapterPosition()));
        }

        public void bind(News item) {
            tvTitle.setText(item.getTitle());
            tvCategory.setText(item.getCategory());
            Picasso.get()
                    .load(ServerConfig.BASE_URL + "images/news/" + item.getCover())
                    .into(imgCover);
            tvCreatedAt.setText(item.getCreatedAt());
        }

        public void initViews() {
            rlDetail = itemView.findViewById(R.id.rl_item_detail);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvCategory = itemView.findViewById(R.id.tv_item_category);
            imgCover = itemView.findViewById(R.id.img_item_cover);
            tvCreatedAt = itemView.findViewById(R.id.tv_item_created);
            btnEdit = itemView.findViewById(R.id.btn_item_edit);
            btnRemove = itemView.findViewById(R.id.btn_item_delete);
        }
    }
}
