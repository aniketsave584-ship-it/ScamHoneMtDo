package com.scamhonemtdo.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class ScamNotificationService extends NotificationListenerService {

    private TextToSpeech tts;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getPackageName().equals(getPackageName())) return;

        Notification notification = sbn.getNotification();
        if (notification == null || notification.extras == null) return;

        Bundle extras = notification.extras;
        CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

        String fullMsg = ((title != null ? title.toString() : "") + " " +
                          (text != null ? text.toString() : "")).toLowerCase();

        if (fullMsg.trim().isEmpty()) return;

        if (fullMsg.contains(".apk")) {
            triggerAlert("🚨 खतरनाक APK फाइल आई है!", "मैसेज में नकली ऐप फाइल मिली है। इसे इंस्टॉल न करें।",
                    "सावधान! आपके फोन पर एक खतरनाक एपीके फाइल आई है। इसे बिल्कुल इंस्टॉल न करें।");
        } else if (fullMsg.contains("upi://pay") || (fullMsg.contains("pa=") && fullMsg.contains("am="))) {
            triggerAlert("🚨 फर्जी पेमेंट रिक्वेस्ट!", "मैसेज में पैसे कटने वाला UPI लिंक है।",
                    "सावधान! आए हुए मैसेज में पैसे कटने वाला यूपीआई लिंक है।");
        } else if ((fullMsg.contains("http://") || fullMsg.contains("https://") || fullMsg.contains(".xyz") || fullMsg.contains("bit.ly")) &&
                   (fullMsg.contains("kyc") || fullMsg.contains("pan") || fullMsg.contains("aadhar") || fullMsg.contains("block") ||
                    fullMsg.contains("suspend") || fullMsg.contains("reward") || fullMsg.contains("lottery") ||
                    fullMsg.contains("electricity") || fullMsg.contains("challan"))) {
            triggerAlert("🚨 फ्रॉड लिंक अलर्ट!", "आए हुए मैसेज में KYC या बिल के नाम पर फर्जी लिंक है।",
                    "खतरा! आपके फोन पर केवाईसी या इनाम के नाम पर एक फ्रॉड लिंक आया है। उस पर क्लिक न करें।");
        }
    }

    private void triggerAlert(String alertTitle, String alertBody, String voiceText) {
        if (tts != null) {
            tts.speak(voiceText, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "scam_alert_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "ScamHoneMtDo सुरक्षा अलर्ट", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, channelId)
                : new Notification.Builder(this);

        builder.setSmallIcon(android.R.drawable.ic_dialog_alert)
               .setContentTitle(alertTitle)
               .setContentText(alertBody)
               .setContentIntent(pi)
               .setAutoCancel(true);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
