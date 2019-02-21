package pl.kopec.vr_android

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class SpeechHelper constructor(context: Context) : TextToSpeech.OnInitListener {
    private val textToSpeech = TextToSpeech(context, this)
    private var isInitialized = false

    fun speak(text: String) {
        if (isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, text)
        }
    }

    fun stop() {
        textToSpeech.stop()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US).apply {
                if (this == TextToSpeech.LANG_MISSING_DATA ||
                    this == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.e("SpeechHelper", "This Language is not supported")
                } else {
                    isInitialized = true
                }
            }
        } else {
            Log.e("SpeechHelper", "Initilization Failed!")
        }
    }
}