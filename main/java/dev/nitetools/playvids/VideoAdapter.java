package dev.nitetools.playvids;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context context;
    private List<Video> videoList;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videos_card, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {
        //Video video = videoList.get(position);
        Video video = videoList.get(position);
        holder.videoTitle.setText(video.getTitle());
        holder.videoDuration.setText(video.getDuration());
        holder.videoThumbnail.setImageBitmap(video.getThumbnail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, VideoPlayerActivity.class);
                i.putExtra("videoPath", video.getPath());
                i.putExtra("videoTitle", video.getTitle());
                context.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView videoTitle, videoDuration;
        ImageView videoThumbnail;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoTitle = itemView.findViewById(R.id.txVideoTitle);
            videoDuration = itemView.findViewById(R.id.txVideoDuration);
            videoThumbnail = itemView.findViewById(R.id.ivVideoThumbnail);
        }
    }
}
