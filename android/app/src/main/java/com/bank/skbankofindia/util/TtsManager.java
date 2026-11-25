package com.bank.skbankofindia.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TtsManager implements TextToSpeech.OnInitListener {

    private static final String TAG = "TtsManager";
    private static final String PREF_NAME = "TtsProcessedTxns";
    private static final String KEY_PROCESSED_SET = "processed_txn_refs";

    private static TtsManager instance;
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private final SharedPreferences prefs;

    private TtsManager(Context context) {
        Context appContext = context.getApplicationContext();
        this.prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.tts = new TextToSpeech(appContext, this);
    }

    public static synchronized TtsManager getInstance(Context context) {
        if (instance == null) {
            instance = new TtsManager(context);
        }
        return instance;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("en", "IN"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.US);
            }
            isInitialized = true;
            Log.d(TAG, "TTS Engine Initialized Successfully.");
        } else {
            Log.e(TAG, "TTS Initialization Failed.");
        }
    }

    public boolean isTxnAlreadyProcessed(String txnRef) {
        if (txnRef == null || txnRef.isEmpty()) return false;
        Set<String> set = prefs.getStringSet(KEY_PROCESSED_SET, new HashSet<>());
        return set.contains(txnRef);
    }

    public void markTxnProcessed(String txnRef) {
        if (txnRef == null || txnRef.isEmpty()) return;
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_PROCESSED_SET, new HashSet<>()));
        set.add(txnRef);
        prefs.edit().putStringSet(KEY_PROCESSED_SET, set).apply();
    }

    public void speakSenderAnnouncement(String txnRef, String amountStr, String receiverName) {
        if (isTxnAlreadyProcessed(txnRef)) {
            Log.d(TAG, "Duplicate sender voice suppressed for TxnRef: " + txnRef);
            return;
        }
        markTxnProcessed(txnRef);

        if (isInitialized && tts != null) {
            String speechText = "Your payment of " + amountStr + " rupees has been sent successfully. Your account has been debited.";
            tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "SENDER_TXN_" + txnRef);
        }
    }

    public void speakReceiverAnnouncement(String txnRef, String amountStr) {
        if (isTxnAlreadyProcessed(txnRef)) {
            Log.d(TAG, "Duplicate receiver voice suppressed for TxnRef: " + txnRef);
            return;
        }
        markTxnProcessed(txnRef);

        if (isInitialized && tts != null) {
            String speechText = "You have received " + amountStr + " rupees.";
            tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "RECEIVER_TXN_" + txnRef);
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
