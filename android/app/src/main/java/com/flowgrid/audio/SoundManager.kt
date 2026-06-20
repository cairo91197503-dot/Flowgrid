package com.flowgrid.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    fun playClick() {
        CoroutineScope(Dispatchers.Default).launch {
            playSound(600.0, 50)
        }
    }

    fun playVictory() {
        CoroutineScope(Dispatchers.Default).launch {
            playSound(440.0, 300) // A4
            playSound(554.37, 300) // C#5
            playSound(659.25, 600) // E5
        }
    }

    private fun playSound(frequency: Double, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = Math.round(durationMs * sampleRate / 1000f)
        val sample = DoubleArray(numSamples)
        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            sample[i] = sin(2 * Math.PI * i / (sampleRate / frequency))
        }

        var idx = 0
        for (dVal in sample) {
            val valShort = (dVal * 32767).toInt().toShort()
            generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build())
            .setBufferSizeInBytes(generatedSnd.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(generatedSnd, 0, generatedSnd.size)
        audioTrack.play()
        Thread.sleep(durationMs.toLong())
        audioTrack.release()
    }
}
