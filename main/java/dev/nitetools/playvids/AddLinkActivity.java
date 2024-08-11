package dev.nitetools.playvids;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class AddLinkActivity extends AppCompatActivity {

    // Fields and UI components
    String userUrl;
    Button playVideoButton, returnButton;
    EditText videoLinkInput;

    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_link);

        // Initialise UI components
        playVideoButton = findViewById(R.id.link_btn);
        returnButton = findViewById(R.id.return_btn);
        videoLinkInput = findViewById(R.id.link_input);

        // Insets Configurations
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setting onclick listeners for Buttons
        playVideoButton.setOnClickListener(v -> fetchInternetVideo());

        returnButton.setOnClickListener(v -> finish());

        // Check and request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
        } else {
            // Load videos if permission is already granted
            fetchInternetVideo();
        }
    }

    // Method to fetch video from the internet
    private void fetchInternetVideo() {
        userUrl = videoLinkInput.getText().toString();

        if (userUrl.isEmpty()) {
            Toast.makeText(this, "No video URL provided", Toast.LENGTH_LONG).show();
            return;
        } else {
            Intent i = new Intent(this, RemoteVideoPlayerActivity.class);
            i.putExtra("videoPath", userUrl);
            i.putExtra("videoTitle", userUrl);
            startActivity(i);
            Toast.makeText(this, userUrl, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    fetchInternetVideo();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                // permission denied
                Toast.makeText(this, "Permission denied, Play Vids cannot access the internet.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
