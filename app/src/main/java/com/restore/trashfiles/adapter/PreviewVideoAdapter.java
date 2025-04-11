package com.restore.trashfiles.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restore.trashfiles.R;
import com.restore.trashfiles.model.RecoverableItem;

import java.util.List;

public class PreviewVideoAdapter extends RecyclerView.Adapter<PreviewVideoAdapter.PreviewViewHolder> {

    private final List<RecoverableItem> items;
    private final Context context;

    public PreviewVideoAdapter(Context context, List<RecoverableItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_preview_video, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        RecoverableItem item = items.get(position);
        // Tải thumbnail video sử dụng MediaMetadataRetriever
        loadVideoThumbnail(item.getFile().getAbsolutePath(), holder.imageView);
        holder.imageView.setContentDescription(holder.itemView.getContext().getString(R.string.video_thumbnail));
    }
    
    private void loadVideoThumbnail(String videoPath, ImageView imageView) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            Bitmap bitmap = retriever.getFrameAtTime(0);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                // Nếu không lấy được thumbnail, hiển thị hình mặc định
                imageView.setImageResource(R.drawable.placeholder_video);
            }
            retriever.release();
        } catch (Exception e) {
            // Xử lý ngoại lệ, hiển thị hình mặc định
            imageView.setImageResource(R.drawable.placeholder_video);
        }
    }

    @Override
    public int getItemCount() {
        // Giới hạn hiển thị tối đa 4 video trong preview
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