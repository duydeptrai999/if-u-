package com.restore.trashfiles.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restore.trashfiles.R;
import com.restore.trashfiles.model.RecoverableItem;

import java.util.List;

public class PreviewFileAdapter extends RecyclerView.Adapter<PreviewFileAdapter.PreviewViewHolder> {

    private final List<RecoverableItem> items;

    public PreviewFileAdapter(List<RecoverableItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_preview_file, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        RecoverableItem item = items.get(position);
        
        // Thiết lập icon loại file
        setFileTypeIcon(holder.fileIcon, item.getType());
        
        // Thiết lập tên file (hiển thị rút gọn)
        String fileName = item.getFile().getName();
        if (fileName.length() > 10) {
            fileName = fileName.substring(0, 7) + "...";
        }
        holder.fileName.setText(fileName);
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

    @Override
    public int getItemCount() {
        // Giới hạn hiển thị tối đa 4 file trong preview
        return Math.min(items.size(), 4);
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        ImageView fileIcon;
        TextView fileName;

        PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.previewFileIcon);
            fileName = itemView.findViewById(R.id.previewFileName);
        }
    }
} 