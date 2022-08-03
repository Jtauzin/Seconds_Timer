package com.bronzeswordstudios.vfe465b778

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val wholeSeconds = MutableLiveData<Int>()
    val tenthSeconds = MutableLiveData<Int>()
    val isComplete = MutableLiveData<Boolean>()
    val animationComplete = MutableLiveData<Boolean>()
    val currentUiPhase = MutableLiveData<UiPhase>()
    var cancelTimer = false

    enum class UiPhase {
        PHASE1, PHASE2, PHASE3
    }

    /** This function is called to begin our timer on a background thread **/
    fun runTimer() {
        // initialize variables
        currentUiPhase.value = UiPhase.PHASE2
        cancelTimer = false
        isComplete.value = false
        var timeSecs = wholeSeconds.value
        var timeTenth = tenthSeconds.value
        val startTime = System.currentTimeMillis()
        val userInputTime: Long = wholeSeconds.value!!.toLong()

        // begin timer loop
        viewModelScope.launch(context = Dispatchers.Default) {
            while (timeSecs!! > 0 || timeTenth!! > 0) {
                // cancel and reset if user hits the stop timer button
                if (cancelTimer) {
                    break
                }
                val timeElapsed = System.currentTimeMillis() - startTime
                timeSecs = ((userInputTime * 1000 - timeElapsed) / 1000).toInt()
                timeTenth = ((userInputTime * 1000 - timeElapsed) / 100).toInt() - (timeSecs!! * 10)
                wholeSeconds.postValue(timeSecs)
                tenthSeconds.postValue(timeTenth)
            }
            if (MainActivity.isBackground) MainActivity.showNotification() // if in background, send notification
            isComplete.postValue(true)
            currentUiPhase.postValue(UiPhase.PHASE3)
            delay(1800)
            animationComplete.postValue(true)
            currentUiPhase.postValue(UiPhase.PHASE1)
        }
    }
}