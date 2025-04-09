package com.htnguyen.ifu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htnguyen.ifu.R;
import com.htnguyen.ifu.model.RecoverableItem;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final List<RecoverableItem> items;
    private final Consumer<Boolean> onItemSelected;
    private boolean hasSelectedItems = false;

    public VideoAdapter(List<RecoverableItem> items, Consumer<Boolean> onItemSelected) {
        this.items = items;
        this.onItemSelected = onItemSelected;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_recovery, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        RecoverableItem item = items.get(position);
        
        // Thiết lập thumbnail
        holder.thumbnail.setImageURI(android.net.Uri.fromFile(item.getFile()));
        holder.thumbnail.setContentDescription(holder.itemView.getContext().getString(R.string.video_thumbnail));
        
        // Thiết lập kích thước
        holder.size.setText(formatFileSize(item.getSize()));
        
        // Thiết lập checkbox
        holder.checkbox.setChecked(item.isSelected());
        
        // Bắt sự kiện click trên checkbox
        holder.checkbox.setOnClickListener(v -> {
            item.setSelected(holder.checkbox.isChecked());
            updateSelectedState();
        });
        
        // Bắt sự kiện click trên toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !item.isSelected();
            item.setSelected(newState);
            holder.checkbox.setChecked(newState);
            updateSelectedState();
        });
    }

    private void updateSelectedState() {
        boolean hasSelected = false;
        for (RecoverableItem item : items) {
            if (item.isSelected()) {
                hasSelected = true;
                break;
            }
        }
        
        if (hasSelected != hasSelectedItems) {
            hasSelectedItems = hasSelected;
            onItemSelected.accept(hasSelected);
        }
    }

    private String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        float sizeKb = size / 1024f;
        float sizeMb = sizeKb / 1024f;
        float sizeGb = sizeMb / 1024f;
        
        if (sizeGb > 1) {
            return df.format(sizeGb) + " GB";
        } else if (sizeMb > 1) {
            return df.format(sizeMb) + " MB";
        } else if (sizeKb > 1) {
            return df.format(sizeKb) + " KB";
        } else {
            return size + " B";
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView size;
        CheckBox checkbox;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
            size = itemView.findViewById(R.id.videoSize);
            checkbox = itemView.findViewById(R.id.videoCheckbox);
        }
    }
} 