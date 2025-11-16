package com.example.spendsprout_opsc.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.spendsprout_opsc.R
import com.example.spendsprout_opsc.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var plantImageView: ImageView
    private lateinit var appNameTextView: TextView
    private lateinit var taglineTextView: TextView
    private lateinit var tapToContinueButton: TextView
    private lateinit var backgroundGradient: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide status bar for fullscreen experience
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        
        setContentView(R.layout.activity_splash)
        
        // Initialize views
        plantImageView = findViewById(R.id.plantImageView)
        appNameTextView = findViewById(R.id.appNameTextView)
        taglineTextView = findViewById(R.id.taglineTextView)
        tapToContinueButton = findViewById(R.id.tapToContinueButton)
        backgroundGradient = findViewById(R.id.backgroundGradient)
        
        // Start animations
        startSplashAnimations()
        
        // Set up tap to continue
        val splashContainer = findViewById<View>(R.id.splashContainer)
        splashContainer.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun startSplashAnimations() {
        // Initial state - views hidden
        plantImageView.alpha = 0f
        plantImageView.scaleX = 0.5f
        plantImageView.scaleY = 0.5f
        appNameTextView.alpha = 0f
        appNameTextView.translationY = 50f
        taglineTextView.alpha = 0f
        taglineTextView.translationY = 30f
        tapToContinueButton.alpha = 0f
        tapToContinueButton.isVisible = false

        // Animate plant - grow and fade in
        plantImageView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1200)
            .setInterpolator(OvershootInterpolator())
            .setStartDelay(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Subtle breathing animation
                    startBreathingAnimation()
                }
            })

        // Animate app name - slide up and fade in
        appNameTextView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setStartDelay(800)

        // Animate tagline - slide up and fade in
        taglineTextView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setStartDelay(1200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Show tap to continue button with fade in
                    tapToContinueButton.isVisible = true
                    tapToContinueButton.animate()
                        .alpha(1f)
                        .setDuration(600)
                        .setStartDelay(200)
                        .start()
                    
                    // Add pulsing animation to tap to continue
                    startPulseAnimation()
                }
            })
    }

    private fun startBreathingAnimation() {
        val animator = ValueAnimator.ofFloat(1f, 1.05f, 1f)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            plantImageView.scaleX = scale
            plantImageView.scaleY = scale
        }
        animator.start()
    }

    private fun startPulseAnimation() {
        val animator = ValueAnimator.ofFloat(1f, 1.1f, 1f)
        animator.duration = 1500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            tapToContinueButton.scaleX = scale
            tapToContinueButton.scaleY = scale
        }
        animator.start()
    }

    private fun navigateToLogin() {
        // Fade out animation
        val splashContainer = findViewById<View>(R.id.splashContainer)
        splashContainer.animate()
            .alpha(0f)
            .setDuration(400)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })
            .start()
    }

    override fun onBackPressed() {
        // Disable back button on splash screen
        // User must tap to continue
    }
}

