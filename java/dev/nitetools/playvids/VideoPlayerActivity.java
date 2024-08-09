package dev.nitetools.playvids;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Rational;
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

public class VideoPlayerActivity extends AppCompatActivity {

    private SeekBar seekbar;
    private VideoView playback;
    RelativeLayout controlsLayout;

    boolean controlsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player); // Set content view first

        // Initialize Components
        TextView videoTitle = findViewById(R.id.txVideoTitle);
        playback = findViewById(R.id.videoPlayback);

        ImageButton playPauseBtn = findViewById(R.id.playPauseBtn);
        ImageButton pipButton = findViewById(R.id.pipBtn);
        ImageButton prevBtn = findViewById(R.id.prevBtn);
        ImageButton nextBtn = findViewById(R.id.nextBtn);
        ImageButton videoSettingBtn = findViewById(R.id.vidSettingBtn);
        seekbar = findViewById(R.id.sbPlayback);
        controlsLayout = findViewById(R.id.controls);

        Intent i = getIntent();
        String title = i.getStringExtra("videoTitle");
        String videoPath = i.getStringExtra("videoPath");

        if (videoPath != null) {
            videoTitle.setText(title);
            playback.setVideoURI(Uri.parse(videoPath));
            playback.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    seekbar.setMax(playback.getDuration());
                    playback.start();
                }
            });
        } else {
            videoTitle.setText("Video not found");
        }

        innitSeekbar();
        setHandler();

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playback.isPlaying()){
                    playback.pause();
                    playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }else{
                    playback.start();
                    playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));

                }

            }
        });

        // Play next video

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Play previous video

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        playback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playback.stopPlayback();
                playPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            }
        });

        pipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enterPictureInPictureMode(new PictureInPictureParams.Builder()
                            .setAspectRatio(new Rational(16, 9))
                            .build());
                }
            }
        });

        controlsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlsVisible){
                    hideControls();
                    controlsVisible = false;
                }else {
                    showControls();
                    controlsVisible = true;
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // METHODS


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        enterPictureInPictureMode(new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(16, 9))
                .build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode) {
            findViewById(R.id.controls).setVisibility(View.GONE);
        } else {
            findViewById(R.id.controls).setVisibility(View.VISIBLE);
        }
    }

    private void playInPipMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(playback.getWidth(), playback.getHeight());
            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(aspectRatio);
            enterPictureInPictureMode(pipBuilder.build());
        }
    }

    private void hideControls(){
        controlsLayout.setVisibility(View.GONE);

        final Window window = this.getWindow();
        if(window == null) return;
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View decorView = window.getDecorView();

        if(decorView != null){
            int uiOption = decorView.getSystemUiVisibility();

            if(Build.VERSION.SDK_INT >= 14){
                uiOption |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }

            if(Build.VERSION.SDK_INT >= 16){
                uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if(Build.VERSION.SDK_INT >= 19){
                uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOption);
        }
    }
    private void setHandler(){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(playback.getDuration() > 0){
                    int currentPosition = playback.getCurrentPosition();
                    seekbar.setProgress(currentPosition);
                }

                handler.postDelayed(this, 0);
            }
        };

        handler.postDelayed(runnable, 500);
    }

    private void innitSeekbar(){
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getId() == R.id.sbPlayback){
                    if(fromUser){
                        playback.seekTo(progress);
                        playback.start();
                    }
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void showControls(){
        controlsLayout.setVisibility(View.VISIBLE);

        final Window window = this.getWindow();
        if(window == null) return;
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View decorView = window.getDecorView();
        if(decorView != null){
            int uiOption = decorView.getSystemUiVisibility();

            if(Build.VERSION.SDK_INT >= 14){
                uiOption &= -View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }

            if(Build.VERSION.SDK_INT >= 16){
                uiOption &= -View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if(Build.VERSION.SDK_INT >= 19){
                uiOption &= -View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOption);
        }
    }
}















