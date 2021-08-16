package com.mammoth.whymedia.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.animation.AnticipateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import android.window.SplashScreenView
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.os.BuildCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.mammoth.whymedia.MainActivity
import com.mammoth.whymedia.databinding.ActivityMainBinding
import com.mammoth.whymedia.databinding.ActivitySplashBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        if (Build.VERSION.SDK_INT > 30) {
            installSplashScreen()
        }
        viewModel.isReady.observe(this){
            if(it){
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
            }
        }
        viewModel.isDataReady()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

    }
}