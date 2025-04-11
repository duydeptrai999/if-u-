package com.restore.trashfiles.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.restore.trashfiles.R;
import com.restore.trashfiles.model.RecoverableItem;

import java.util.List;

public class PreviewPhotoAdapter extends RecyclerView.Adapter<PreviewPhotoAdapter.PreviewViewHolder> {

    private final List<RecoverableItem> items;

    public PreviewPhotoAdapter(List<RecoverableItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_preview_photo, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        RecoverableItem item = items.get(position);
        // Sử dụng Glide để tải ảnh
        Glide.with(holder.itemView.getContext())
                .load(item.getFile())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.ic_broken_image)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        // Giới hạn hiển thị tối đa 4 ảnh trong preview
        return Math.min(items.size(), 4);
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.previewImage);
        }
    }
} 