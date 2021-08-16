package com.mammoth.whymedia.ui.splash

import android.os.CountDownTimer
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {
    companion object {
        const val WORK_DURATION = 2000L
    }

    val _isReady: MutableLiveData<Boolean> = MutableLiveData(false)
    var isReady: LiveData<Boolean> = _isReady

    fun isDataReady() {
        object : CountDownTimer(WORK_DURATION, 1000) {
            override fun onFinish() {
                _isReady.postValue(true)
            }
            override fun onTick(millisUntilFinished: Long) {
            }
        }.start()
    }
}