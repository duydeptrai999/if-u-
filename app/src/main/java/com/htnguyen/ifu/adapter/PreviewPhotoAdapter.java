package com.htnguyen.ifu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htnguyen.ifu.R;
import com.htnguyen.ifu.model.RecoverableItem;

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
        // Tải ảnh bằng thumbnail hoặc thư viện tải ảnh
        // Trong ví dụ đơn giản này, chúng ta chỉ đặt nguồn từ tệp
        holder.imageView.setImageURI(android.net.Uri.fromFile(item.getFile()));
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