package com.restore.trashfiles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.restore.trashfiles.R;
import com.restore.trashfiles.model.RecoverableItem;

import java.util.ArrayList;
import java.util.List;

public class PhotoRecoveryAdapter extends RecyclerView.Adapter<PhotoRecoveryAdapter.PhotoViewHolder> {

    private final Context context;
    private final List<RecoverableItem> items;

    public PhotoRecoveryAdapter(Context context, List<RecoverableItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_recovery, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        RecoverableItem item = items.get(position);
        
        holder.fileName.setText(item.getFile().getName());
        holder.fileSize.setText(item.getFormattedSize());
        holder.checkBox.setChecked(item.isSelected());
        
        // Load image thumbnail with Glide
        Glide.with(context)
                .load(item.getFile())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.ic_broken_image)
                .centerCrop()
                .into(holder.imagePreview);
                
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !item.isSelected();
            item.setSelected(newState);
            holder.checkBox.setChecked(newState);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    
    public List<RecoverableItem> getSelectedItems() {
        List<RecoverableItem> selectedItems = new ArrayList<>();
        for (RecoverableItem item : items) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePreview;
        TextView fileName;
        TextView fileSize;
        CheckBox checkBox;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePreview = itemView.findViewById(R.id.imagePreview);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
} 