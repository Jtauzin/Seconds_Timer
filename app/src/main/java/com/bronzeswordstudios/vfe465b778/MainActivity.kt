package com.bronzeswordstudios.vfe465b778

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.bronzeswordstudios.vfe465b778.databinding.ActivityMainBinding
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    //--------------------------- Begin Global Declarations   ---------------------------//
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var notification: NotificationCompat.Builder
    private lateinit var audioHandler: AudioHandler


    //--------------------------- Begin Core App Logic / Functions ---------------------------//
    override fun onCreate(savedInstanceState: Bundle?) {

        // set up our variables, view, and instanceState
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        audioHandler = AudioHandler(this)
        instance = WeakReference(this)
        setContentView(binding.root)
        buildNotification()

        // monitor our live data for updates to update our views
        viewModel.wholeSeconds.observe(this) {
            binding.timerViewSeconds.text = it.toString()
        }
        viewModel.tenthSeconds.observe(this) {
            binding.timerViewSecondTenth.text = it.toString()
        }
        // if timer complete, play animation and sound if in foreground, else reset and toast only
        viewModel.isComplete.observe(this) {
            if (it && !isBackground) {
                binding.explosionAnimation.setImageResource(R.drawable.explosion_animation)
                viewModel.animationComplete.value = false
                (binding.explosionAnimation.drawable as AnimationDrawable).start()
                audioHandler.playExplosion()
                displayToast("Timer has stopped!")
                binding.timerInput.text = null
            } else if (it) {
                displayToast("Timer has stopped!")
                binding.timerInput.text = null
            }
        }

        viewModel.currentUiPhase.observe(this) {
            adjustUIVisibility(it)
        }

        // set on click listeners for our 3 buttons
        binding.startButton.setOnClickListener {
            // we only need to run another timer thread if we are finished the previous, or
            //  one has not yet been created
            val isAnimationComplete =
                viewModel.animationComplete.value == true || viewModel.animationComplete.value == null
            val isTimerComplete =
                viewModel.isComplete.value == true || viewModel.isComplete.value == null
            if (isAnimationComplete && isTimerComplete) {
                // parse our input to an int and send to view model
                val input = binding.timerInput.text.toString()
                viewModel.tenthSeconds.value = 0
                if (input == "") viewModel.wholeSeconds.value = 0
                else viewModel.wholeSeconds.value = input.toInt()
                viewModel.runTimer()
            }
        }
        // send the cancel signal if the stop button is clicked
        binding.stopButton.setOnClickListener {
            viewModel.cancelTimer = true
        }
        // I could not resist
        binding.whyNotButton.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.bronzeswordstudios.glorpythespacething")
                    )
                )
            } catch (throwable: Throwable) {
                displayToast("There was an error trying to open the play store.")
            }
        }
    }


    /** This function used to toggle visibility of the main views **/
    private fun adjustUIVisibility(phase: MainViewModel.UiPhase) {
        when (phase) {
            MainViewModel.UiPhase.PHASE1 -> { // display input
                binding.explosionAnimation.visibility = View.GONE
                binding.timerInput.visibility = View.VISIBLE
                binding.counterLayout.visibility = View.GONE
            }
            MainViewModel.UiPhase.PHASE2 -> { // display updating timer countdown
                binding.explosionAnimation.visibility = View.GONE
                binding.timerInput.visibility = View.GONE
                binding.counterLayout.visibility = View.VISIBLE
            }
            MainViewModel.UiPhase.PHASE3 -> { // display finishing animation view
                binding.explosionAnimation.visibility = View.VISIBLE
                binding.timerInput.visibility = View.GONE
                binding.counterLayout.visibility = View.GONE
            }
        }
    }

    /** This function used for code legibility when we need to call a snackbar **/
    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /** This function build our global notification, then latch it to a WeakReference to prevent
     * static memory leaks **/
    private fun buildNotification() {
        // build our global notification variable
        notification = NotificationCompat.Builder(this, "ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Timer Completed")
            .setContentText("The timer has finished counting down")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // create our notification channel
        val channel =
            NotificationChannel(
                "ID", "myChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Timer Channel"
            }

        // Register the channel with NotificationManager
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Link WeakReference to our global notification builder variable. Note, not referencing a
        // global variable may cause the WeakReference value to revert to null
        notificationReference = WeakReference(notification)
    }


    //--------------------------- Begin Static Methods / Variables ---------------------------//
    companion object {
        // Variables
        private lateinit var notificationReference: WeakReference<NotificationCompat.Builder>
        private lateinit var instance: WeakReference<MainActivity>
        private var notificationID = 0
        var isBackground = false

        // Methods
        /** This function used to show our notification from our WeakReferenced builder **/
        fun showNotification() {
            with(NotificationManagerCompat.from(instance.get()!!.applicationContext)) {
                notify(notificationID, notificationReference.get()!!.build())
                notificationID += 1     // by incrementing we allow for multiple notifications
            }
        }
    }


    //--------------------------- Begin Additional Lifecycle Logic ---------------------------//
    override fun onPause() {
        isBackground = true
        super.onPause()
    }

    override fun onResume() {
        isBackground = false
        super.onResume()
    }

    override fun onDestroy() {
        audioHandler.release()
        super.onDestroy()
    }
}