package dev.nitetools.playvids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSourceFactory;
import androidx.media3.ui.PlayerView;

public class RemoteVideoPlayerActivity extends AppCompatActivity {

    // Fields and UI components
    String videoPath, videoTitle;
    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remote_video_player);

        // Retrieve video data
        Intent i = getIntent();
        videoPath = i.getStringExtra("videoPath");
        videoTitle = i.getStringExtra("videoTitle");

        // Initialise UI components
        playVideo(videoPath);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.release();
    }

    @OptIn(markerClass = UnstableApi.class)
    /**
     * This method checks if the video is supported by Exoplayer
     * Embedded video URLs are not supported by Exoplayer
     */
    private boolean isVideoSupportedByExoPlayer(String videoUrl) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, "user-agent");
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);

        try {

            MediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);
            mediaSourceFactory.createMediaSource(mediaItem);
            return true;
        } catch (Exception e) {

            return false;
        }
    }


    /**
     * I am checking if the isVideoSupportedByExoPlayer(videoUrl) method returns true
     * if it is, i will use Exoplayer to play videos, else i will use Web view.
     * @param videoUrl
     */
    private void playVideo(String videoUrl) {
        PlayerView playerView = findViewById(R.id.player_view);
        WebView webView = findViewById(R.id.web_view);

        if (isVideoSupportedByExoPlayer(videoUrl)) {
            // Use ExoPlayer
            playerView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);

            ExoPlayer player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        } else {
            // Use WebView
            playerView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(videoUrl);
        }
    }
}