package com.restore.trashfiles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.restore.trashfiles.R;
import com.restore.trashfiles.model.RecoverableItem;

import java.io.File;
import java.util.List;

/**
 * Adapter for displaying preview thumbnails of recoverable items in a horizontal list
 */
public class RecoveryPreviewAdapter extends RecyclerView.Adapter<RecoveryPreviewAdapter.PreviewViewHolder> {

    private final Context context;
    private final List<RecoverableItem> items;

    public RecoveryPreviewAdapter(Context context, List<RecoverableItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_preview, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        RecoverableItem item = items.get(position);
        
        // Load thumbnail based on the file type
        if (item.getType().equals("image")) {
            // For images, use Glide to load the thumbnail
            Glide.with(context)
                 .load(new File(item.getPath()))
                 .placeholder(R.drawable.placeholder_image)
                 .error(R.drawable.ic_broken_image)
                 .centerCrop()
                 .into(holder.previewImage);
        } else if (item.getType().equals("video")) {
            // For videos, show a video icon
            holder.previewImage.setImageResource(R.drawable.ic_video);
        } else {
            // For other files, show a file icon
            holder.previewImage.setImageResource(R.drawable.ic_file);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        final ImageView previewImage;

        PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            previewImage = itemView.findViewById(R.id.previewImage);
        }
    }
} 