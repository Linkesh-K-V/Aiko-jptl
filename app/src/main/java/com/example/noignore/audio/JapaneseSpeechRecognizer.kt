package com.example.noignore.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * State representing speech recognition shadowing status.
 */
sealed class ShadowingSpeechState {
    object Idle : ShadowingSpeechState()
    object Listening : ShadowingSpeechState()
    data class Success(val recognizedText: String, val similarityPercent: Int) : ShadowingSpeechState()
    data class Error(val errorMessage: String) : ShadowingSpeechState()
}

/**
 * Japanese Speech Recognition & Pronunciation Shadowing Manager using Android's SpeechRecognizer.
 * Listens for user pronunciation, handles Japanese recognition (ja-JP), and computes
 * pronunciation similarity matching against target Japanese text.
 */
class JapaneseSpeechRecognizer(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _state = MutableStateFlow<ShadowingSpeechState>(ShadowingSpeechState.Idle)
    val state: StateFlow<ShadowingSpeechState> = _state.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private var currentTargetText: String = ""

    val isRecognitionAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening(targetText: String) {
        currentTargetText = targetText.trim()
        stopListening()

        if (!isRecognitionAvailable) {
            _state.value = ShadowingSpeechState.Error("Speech recognition service not available on this device.")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ja-JP")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "ja-JP")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            speechRecognizer?.startListening(intent)
            _isListening.value = true
            _state.value = ShadowingSpeechState.Listening
        } catch (e: Exception) {
            _isListening.value = false
            _state.value = ShadowingSpeechState.Error(e.localizedMessage ?: "Failed to start microphone")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        _isListening.value = false
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
                _state.value = ShadowingSpeechState.Listening
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
                val msg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Couldn't catch that. Try speaking closer to the microphone."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                    else -> "Speech recognition error ($error)"
                }
                _state.value = ShadowingSpeechState.Error(msg)
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognized = matches[0]
                    val similarity = calculateSimilarity(recognized, currentTargetText)
                    _state.value = ShadowingSpeechState.Success(
                        recognizedText = recognized,
                        similarityPercent = similarity
                    )
                } else {
                    _state.value = ShadowingSpeechState.Error("No match found. Please try again.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    /**
     * Calculates fuzzy matching similarity percentage between the recognized spoken phrase
     * and the reference Japanese phrase/reading.
     */
    fun calculateSimilarity(spoken: String, target: String): Int {
        val s1 = normalize(spoken)
        val s2 = normalize(target)
        if (s1.isEmpty() || s2.isEmpty()) return 0
        if (s1 == s2 || s1.contains(s2) || s2.contains(s1)) return 100

        val distance = levenshteinDistance(s1, s2)
        val maxLen = maxOf(s1.length, s2.length)
        val score = ((1.0 - (distance.toDouble() / maxLen.toDouble())) * 100).toInt()
        return score.coerceIn(0, 100)
    }

    private fun normalize(str: String): String {
        return str.replace("[\\s、。！？,.!?「」・（）()]".toRegex(), "").lowercase(Locale.ROOT)
    }

    private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = Array(lhsLength + 1) { it }
        var newCost = Array(lhsLength + 1) { 0 }

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = minOf(costInsert, costDelete, costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLength]
    }
}
