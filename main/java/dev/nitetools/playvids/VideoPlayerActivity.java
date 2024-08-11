package dev.nitetools.playvids;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;

public class VideoPlayerActivity extends AppCompatActivity {

    private SeekBar seekbar;
    private VideoView playback;
    private RelativeLayout controlsLayout;
    private boolean controlsVisible = true;  // Initial state is visible

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Initialize UI components
        TextView videoTitle = findViewById(R.id.txVideoTitle);
        playback = findViewById(R.id.videoPlayback);
        ImageButton playPauseBtn = findViewById(R.id.playPauseBtn);
        ImageButton pipButton = findViewById(R.id.pipBtn);
        ImageButton prevBtn = findViewById(R.id.prevBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        seekbar = findViewById(R.id.sbPlayback);
        controlsLayout = findViewById(R.id.controls);

        // Retrieve video data from intent
        Intent i = getIntent();
        String title = i.getStringExtra("videoTitle");
        String videoPath = i.getStringExtra("videoPath");

        if (videoPath != null) {
            videoTitle.setText(title);

            if(videoPath.startsWith("https://") || videoPath.startsWith("http://")){
                // Video from internet
                MediaItem mediaItem = MediaItem.fromUri(videoPath);

            }else{
                // Local video
                playback.setVideoURI(Uri.parse(videoPath));
                playback.setOnPreparedListener(mp -> {
                    // Set seekbar max value to video duration
                    // Start Playback
                    seekbar.setMax(playback.getDuration());
                    playback.start();
                });

            }
        } else {
            videoTitle.setText("Video not found");
        }

        // Initialize seekbar and control visibility handler
        initSeekbar();
        setHandler();

        // Toggle play/pause functionality
        playPauseBtn.setOnClickListener(v -> {
            if (playback.isPlaying()) {
                playback.pause();
                playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            } else {
                playback.start();
                playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            }
        });

        // Picture-in-Picture button functionality
        /**
            PiP is based on a tutorial on Geeks for Geeks article.
            Article URL: https://www.geeksforgeeks.org/how-to-implement-picture-in-picture-pip-in-android
         */

        pipButton.setOnClickListener(v -> {
            enterPiPMode();
        });

        // Toggle controls visibility when layout is clicked
        controlsLayout.setOnClickListener(v -> toggleControls());
        playback.setOnClickListener( v -> toggleControls());

        // Configure window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPiPMode() {
        Display d = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        int width = p.x;
        int height = p.y;
        Rational ratio = new Rational(width, height);
        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setAspectRatio(ratio);
        enterPictureInPictureMode(pipBuilder.build());
    }

    // Method to handle entering Picture-in-Picture mode
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void playInPipMode() {
        Rational aspectRatio = new Rational(playback.getWidth(), playback.getHeight());
        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setAspectRatio(aspectRatio);
        boolean isPiP = enterPictureInPictureMode(pipBuilder.build());
        Log.d("PiP", "PiP mode entered: " + isPiP);
    }

    // Automatically enter PiP mode when the user leaves the app
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
       // playInPipMode();
        enterPiPMode();
    }

    // Adjust UI when entering or exiting PiP mode
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            // Hide controls in PiP mode
            hideControls();
        } else {
            // Show controls when exiting PiP mode
            showControls();
        }
    }

    // Toggle video controls visibility
    private void toggleControls() {
        if (controlsVisible) {
            hideControls();
        } else {
            showControls();
        }
        controlsVisible = !controlsVisible;
    }

    // Hide video controls
    private void hideControls() {
        controlsLayout.setVisibility(View.GONE);
        controlsVisible = false;

        final Window window = this.getWindow();
        if (window == null) return;
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View decorView = window.getDecorView();
        if (decorView != null) {
            int uiOption = decorView.getSystemUiVisibility();

            if (Build.VERSION.SDK_INT >= 14) {
                uiOption |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 19) {
                uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOption);
        }
    }

    // Show video controls
    private void showControls() {
        controlsLayout.setVisibility(View.VISIBLE);
        controlsVisible = true;

        final Window window = this.getWindow();
        if (window == null) return;
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View decorView = window.getDecorView();
        if (decorView != null) {

            int uiOption = decorView.getSystemUiVisibility();

            uiOption &= -View.SYSTEM_UI_FLAG_LOW_PROFILE;

            if (Build.VERSION.SDK_INT >= 16) {
                uiOption &= -View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 19) {
                uiOption &= -View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            decorView.setSystemUiVisibility(uiOption);
        }
    }

    // Handler to update the seekbar based on video playback progress
    private void setHandler() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (playback.getDuration() > 0) {
                    int currentPosition = playback.getCurrentPosition();
                    seekbar.setProgress(currentPosition);
                }

                handler.postDelayed(this, 500);

            }
        };
        handler.postDelayed(runnable, 500);
    }

    // Initialize the seekbar and its listener
    private void initSeekbar() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playback.seekTo(progress);
                    playback.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}

/*
* Errors:
*   -Picture in picture mode not working.
*   -toggleControls() not working.
* */
