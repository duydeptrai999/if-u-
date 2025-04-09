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

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private final List<RecoverableItem> items;
    private final Consumer<Boolean> onItemSelected;
    private boolean hasSelectedItems = false;

    public FileAdapter(List<RecoverableItem> items, Consumer<Boolean> onItemSelected) {
        this.items = items;
        this.onItemSelected = onItemSelected;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_recovery, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        RecoverableItem item = items.get(position);
        
        // Thiết lập icon loại file
        setFileTypeIcon(holder.fileIcon, item.getType());
        
        // Thiết lập tên file
        holder.fileName.setText(item.getFile().getName());
        
        // Thiết lập kích thước
        holder.fileSize.setText(formatFileSize(item.getSize()));
        
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

    private void setFileTypeIcon(ImageView imageView, String fileType) {
        switch (fileType) {
            case "pdf":
                imageView.setImageResource(R.drawable.ic_pdf);
                break;
            case "document":
                imageView.setImageResource(R.drawable.ic_document);
                break;
            case "spreadsheet":
                imageView.setImageResource(R.drawable.ic_spreadsheet);
                break;
            case "presentation":
                imageView.setImageResource(R.drawable.ic_presentation);
                break;
            case "text":
                imageView.setImageResource(R.drawable.ic_text);
                break;
            case "archive":
                imageView.setImageResource(R.drawable.ic_archive);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_file);
                break;
        }
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

    static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView fileIcon;
        TextView fileName;
        TextView fileSize;
        CheckBox checkbox;

        FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            checkbox = itemView.findViewById(R.id.fileCheckbox);
        }
    }
} 