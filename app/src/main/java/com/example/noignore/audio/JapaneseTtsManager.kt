package com.example.noignore.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Japanese Text-To-Speech Manager leveraging Android's built-in TextToSpeech engine.
 * Supports standard (1.0x) and slow (0.75x) pronunciation rates for language learners.
 */
class JapaneseTtsManager(context: Context) {

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    private val _isLanguageAvailable = MutableStateFlow(true)
    val isLanguageAvailable: StateFlow<Boolean> = _isLanguageAvailable.asStateFlow()

    private fun requestAudioFocus(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (audioFocusRequest == null) {
                    val playbackAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                    audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .setAudioAttributes(playbackAttributes)
                        .setAcceptsDelayedFocusGain(false)
                        .build()
                }
                audioFocusRequest?.let { audioManager?.requestAudioFocus(it) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED } ?: true
            } else {
                @Suppress("DEPRECATION")
                audioManager?.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        } catch (e: Exception) {
            Log.w("JapaneseTts", "Audio focus request failed", e)
            true
        }
    }

    private fun abandonAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(null)
            }
        } catch (e: Exception) {
            Log.w("JapaneseTts", "Audio focus abandon failed", e)
        }
    }

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.JAPANESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Try Locale.JAPAN if JAPANESE didn't match
                    val japanResult = tts?.setLanguage(Locale.JAPAN)
                    if (japanResult == TextToSpeech.LANG_MISSING_DATA || japanResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.w("JapaneseTts", "Japanese language data not available on this device")
                        _isLanguageAvailable.value = false
                    } else {
                        isInitialized = true
                        _isLanguageAvailable.value = true
                    }
                } else {
                    isInitialized = true
                    _isLanguageAvailable.value = true
                }

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        _currentUtteranceId.value = utteranceId
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        abandonAudioFocus()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        abandonAudioFocus()
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                        _currentUtteranceId.value = null
                        abandonAudioFocus()
                        Log.e("JapaneseTts", "TTS Error for $utteranceId with code $errorCode")
                    }
                })
            } else {
                Log.e("JapaneseTts", "TTS Initialization failed with status: $status")
            }
        }
    }

    companion object {
        /**
         * Cleans Japanese text and pronunciation guides for natural TTS synthesis.
         * Replaces slashes with natural pause commas, removes wave dashes from grammar forms,
         * and strips brackets to avoid robotic reading of punctuation.
         */
        fun cleanJapaneseForTts(text: String): String {
            return text
                .replace(" / ", "、")
                .replace("/", "、")
                .replace("〜", "")
                .replace("~", "")
                .replace("[", "")
                .replace("]", "")
                .replace("(", "")
                .replace(")", "")
                .replace("（", "")
                .replace("）", "")
                .replace("•", "、")
                .trim()
        }
    }

    /**
     * Speaks the provided Japanese text.
     * @param text Japanese text to speak (Kanji, Kana, or mixed)
     * @param isSlow If true, sets speech rate to 0.72x for learners to listen closely.
     * @param utteranceId Optional unique id for tracking completion.
     */
    fun speak(text: String, isSlow: Boolean = false, utteranceId: String = "jp_${System.currentTimeMillis()}") {
        val cleanText = cleanJapaneseForTts(text)
        if (cleanText.isEmpty()) return

        if (!isInitialized) {
            initTts()
        }

        try {
            requestAudioFocus()
            val speed = if (isSlow) 0.72f else 1.0f
            tts?.setSpeechRate(speed)
            tts?.setPitch(1.05f) // Slightly higher, natural friendly pitch

            val params = android.os.Bundle()
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
            _isSpeaking.value = true
            _currentUtteranceId.value = utteranceId
        } catch (e: Exception) {
            Log.e("JapaneseTts", "Error speaking text: $cleanText", e)
            _isSpeaking.value = false
            _currentUtteranceId.value = null
            abandonAudioFocus()
        }
    }

    /**
     * Helper to speak vocabulary or grammar points prioritizing hiragana/katakana reading if available.
     */
    fun speakPronunciation(japanese: String, reading: String = "", isSlow: Boolean = false) {
        val target = when {
            reading.isNotBlank() && !reading.contains("/") -> reading
            japanese.isNotBlank() -> japanese
            reading.isNotBlank() -> reading
            else -> ""
        }
        if (target.isNotBlank()) {
            speak(target, isSlow = isSlow)
        }
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
            _currentUtteranceId.value = null
            abandonAudioFocus()
        } catch (e: Exception) {
            Log.e("JapaneseTts", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
            _isSpeaking.value = false
            _currentUtteranceId.value = null
            abandonAudioFocus()
        } catch (e: Exception) {
            Log.e("JapaneseTts", "Error shutting down TTS", e)
        }
    }
}
