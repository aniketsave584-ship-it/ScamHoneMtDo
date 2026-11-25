package com.aniket.scamhonemtdo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;
    private TextView resultBox;
    private EditText inputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int res = tts.setLanguage(new Locale("hi", "IN"));
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#F4F6F9"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(45, 50, 45, 50);

        TextView title = new TextView(this);
        title.setText("🛡️ ScamHoneMtDo");
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#1A237E"));
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("लिंक, UPI/QR डेटा या APK फाइल का नाम यहाँ डालकर जांचें");
        subtitle.setTextSize(14);
        subtitle.setTextColor(Color.DKGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 10, 0, 35);
        layout.addView(subtitle);

        inputField = new EditText(this);
        inputField.setHint("उदाहरण: shadi-card.apk या http://sbi-kyc.xyz या upi://pay...");
        inputField.setBackgroundColor(Color.WHITE);
        inputField.setPadding(30, 30, 30, 30);
        inputField.setMinLines(3);
        layout.addView(inputField);

        Button scanBtn = new Button(this);
        scanBtn.setText("🔍 अभी स्कैन करें (Check Scam)");
        scanBtn.setBackgroundColor(Color.parseColor("#D32F2F"));
        scanBtn.setTextColor(Color.WHITE);
        scanBtn.setTextSize(16);
        scanBtn.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 30, 0, 30);
        scanBtn.setLayoutParams(btnParams);
        layout.addView(scanBtn);

        resultBox = new TextView(this);
        resultBox.setText("नतीजा यहाँ दिखेगा...");
        resultBox.setTextSize(16);
        resultBox.setPadding(35, 35, 35, 35);
        resultBox.setBackgroundColor(Color.WHITE);
        resultBox.setTextColor(Color.BLACK);
        layout.addView(resultBox);

        scanBtn.setOnClickListener(v -> analyzeInput());

        scrollView.addView(layout);
        setContentView(scrollView);
    }

    private void analyzeInput() {
        String text = inputField.getText().toString().trim().toLowerCase();
        if (text.isEmpty()) {
            resultBox.setText("⚠️ कृपया पहले कोई लिंक, मैसेज या फाइल का नाम पेस्ट करें।");
            return;
        }

        boolean isScam = false;
        StringBuilder warning = new StringBuilder();
        String speechMsg = "";

        if (text.contains(".apk")) {
            isScam = true;
            warning.append("🚨 खतरनाक APK फाइल!\nव्हाट्सऐप या अनजान जगह से आई APK फाइल फोन हैक कर सकती है। इसे बिल्कुल इंस्टॉल न करें।\n\n");
            speechMsg = "सावधान! यह एक खतरनाक एपीके फाइल है। इसे इंस्टॉल करने से आपका फोन हैक हो सकता है।";
        }

        if (text.contains("upi://pay") || text.contains("pa=") || text.contains("am=")) {
            isScam = true;
            warning.append("🚨 UPI पेमेंट अलर्ट!\nयह पैसे भेजने (Pay) का लिंक या QR डेटा है। इसे स्वीकार करने पर आपके खाते से पैसे कटेंगे, मिलेंगे नहीं।\n\n");
            if (speechMsg.isEmpty()) {
                speechMsg = "रुकिए! यह पैसे कटने वाला यूपीआई लिंक है। पैसे लेने के लिए पिन न डालें।";
            }
        }

        if (text.contains(".xyz") || text.contains(".top") || text.contains(".tk") || text.contains(".buzz") ||
            text.contains("bit.ly") || text.contains("tinyurl") || text.contains("kyc") ||
            text.contains("challan") || text.contains("lottery") || text.contains("reward") ||
            text.contains("free-recharge") || text.contains("electricity") || text.contains("http://")) {
            isScam = true;
            warning.append("🚨 संदिग्ध या नकली लिंक!\nयह वेबसाइट असुरक्षित या फ्रॉड हो सकती है। इस पर अपनी बैंक या निजी जानकारी न डालें।\n");
            if (speechMsg.isEmpty()) {
                speechMsg = "सावधान! यह एक नकली या संदिग्ध लिंक हो सकता है। इस पर क्लिक न करें।";
            }
        }

        if (isScam) {
            resultBox.setBackgroundColor(Color.parseColor("#FFEBEE"));
            resultBox.setTextColor(Color.parseColor("#B71C1C"));
            resultBox.setText(warning.toString().trim());
            speakAlert(speechMsg);
        } else {
            resultBox.setBackgroundColor(Color.parseColor("#E8F5E9"));
            resultBox.setTextColor(Color.parseColor("#1B5E20"));
            resultBox.setText("✅ कोई बड़ा खतरा नहीं मिला।\n\nफिर भी अनजान व्यक्ति के साथ कभी भी OTP या UPI PIN साझा न करें।");
            speakAlert("इसमें कोई बड़ा खतरा नहीं दिखा, लेकिन फिर भी सावधान रहें।");
        }
    }

    private void speakAlert(String message) {
        if (tts != null) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
