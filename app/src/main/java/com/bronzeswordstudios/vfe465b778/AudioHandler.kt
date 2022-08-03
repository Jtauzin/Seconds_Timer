package com.bronzeswordstudios.vfe465b778

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class AudioHandler(context: Context) {
    // create our audioAttributes,soundPool, and empty clip ID
    private var audioAttributes: AudioAttributes =
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
    private var soundPool: SoundPool =
        SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build()
    private var explosionID: Int? = null

    init {
        // initialize the sound clip id and load the sound
        explosionID = soundPool.load(context, R.raw.boom, 1)
    }

    /** This function is called to play our explosion sound when needed **/
    fun playExplosion() {
        soundPool.play(explosionID!!, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}