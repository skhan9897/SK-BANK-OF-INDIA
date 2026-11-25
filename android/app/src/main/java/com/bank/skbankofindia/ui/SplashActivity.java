package com.bank.skbankofindia.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 3000; // Exact 3-Second Duration
    private ProgressBar pbSplashProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pbSplashProgress = findViewById(R.id.pbSplashProgress);

        // Smooth Tricolor Progress Wave Animation (0% to 100% in 3 seconds)
        if (pbSplashProgress != null) {
            ObjectAnimator animator = ObjectAnimator.ofInt(pbSplashProgress, "progress", 0, 100);
            animator.setDuration(SPLASH_DURATION_MS);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, SPLASH_DURATION_MS);
    }
}
