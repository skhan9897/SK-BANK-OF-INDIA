package com.bank.skbankofindia.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bank.skbankofindia.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 3500; // 3.5 Seconds Duration for Full Wave Animation
    private ProgressBar pbSplashProgress;
    private ImageView ivFlagWaveBg, ivBankLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pbSplashProgress = findViewById(R.id.pbSplashProgress);
        ivFlagWaveBg = findViewById(R.id.ivFlagWaveBg);
        ivBankLogo = findViewById(R.id.ivBankLogo);

        // Start Indian Flag Sea Wave Shaking & Undulating Animation
        startSeaWaveBgAnimation();

        // Logo Floating Animation
        if (ivBankLogo != null) {
            ObjectAnimator logoFloat = ObjectAnimator.ofFloat(ivBankLogo, "translationY", 0f, -20f, 0f);
            logoFloat.setDuration(1200);
            logoFloat.setRepeatCount(ValueAnimator.INFINITE);
            logoFloat.setRepeatMode(ValueAnimator.REVERSE);
            logoFloat.setInterpolator(new AccelerateDecelerateInterpolator());
            logoFloat.start();
        }

        // Smooth Tricolor Progress Bar Animation (0% to 100% in splash duration)
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

    private void startSeaWaveBgAnimation() {
        if (ivFlagWaveBg == null) return;

        // 1. Horizontal Sea Wave Oscillation
        ObjectAnimator waveX = ObjectAnimator.ofFloat(ivFlagWaveBg, "translationX", -50f, 50f);
        waveX.setDuration(1600);
        waveX.setRepeatCount(ValueAnimator.INFINITE);
        waveX.setRepeatMode(ValueAnimator.REVERSE);
        waveX.setInterpolator(new AccelerateDecelerateInterpolator());
        waveX.start();

        // 2. Vertical Sea Wave Motion
        ObjectAnimator waveY = ObjectAnimator.ofFloat(ivFlagWaveBg, "translationY", -35f, 35f);
        waveY.setDuration(1300);
        waveY.setRepeatCount(ValueAnimator.INFINITE);
        waveY.setRepeatMode(ValueAnimator.REVERSE);
        waveY.setInterpolator(new AccelerateDecelerateInterpolator());
        waveY.start();

        // 3. Liquid Wave Pulse Scale
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivFlagWaveBg, "scaleX", 1.0f, 1.15f);
        scaleX.setDuration(2000);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivFlagWaveBg, "scaleY", 1.0f, 1.15f);
        scaleY.setDuration(2000);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.start();
    }
}
