package com.scamhonemtdo.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import java.util.Locale;

public class MainActivity extends Activity {

    private EditText inputBox;
    private TextView resultBox;
    private TextToSpeech tts;
    private GmsBarcodeScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });

        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build();
        scanner = GmsBarcodeScanning.getClient(this, options);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.parseColor("#F4F6F9"));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(45, 55, 45, 55);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("🛡️ ScamHoneMtDo");
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#1A237E"));
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("लिंक, UPI/QR कोड या APK फाइल की तुरंत जांच करें");
        subtitle.setTextSize(13);
        subtitle.setTextColor(Color.parseColor("#546E7A"));
        subtitle.setPadding(0, 8, 0, 35);
        subtitle.setGravity(Gravity.CENTER);
        layout.addView(subtitle);

        Button qrBtn = new Button(this);
        qrBtn.setText("📷 QR कोड स्कैन करें (SCAN QR)");
        qrBtn.setTextSize(15);
        qrBtn.setTypeface(null, Typeface.BOLD);
        qrBtn.setTextColor(Color.WHITE);
        qrBtn.setBackgroundColor(Color.parseColor("#1565C0"));
        qrBtn.setPadding(20, 32, 20, 32);
        LinearLayout.LayoutParams qrParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        qrParams.setMargins(0, 0, 0, 25);
        qrBtn.setLayoutParams(qrParams);
        layout.addView(qrBtn);

        inputBox = new EditText(this);
        inputBox.setHint("उदाहरण: shadi-card.apk या http://sbi-kyc.xyz या upi://pay...");
        inputBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputBox.setMinLines(3);
        inputBox.setGravity(Gravity.TOP | Gravity.START);
        inputBox.setBackgroundColor(Color.WHITE);
        inputBox.setPadding(30, 30, 30, 30);
        inputBox.setTextColor(Color.BLACK);
        inputBox.setHintTextColor(Color.GRAY);
        layout.addView(inputBox);

        LinearLayout actionRow = new LinearLayout(this);
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 18, 0, 22);
        actionRow.setLayoutParams(rowParams);

        Button pasteBtn = new Button(this);
        pasteBtn.setText("📋 पेस्ट करें");
        pasteBtn.setTextSize(13);
        pasteBtn.setBackgroundColor(Color.parseColor("#E3F2FD"));
        pasteBtn.setTextColor(Color.parseColor("#0D47A1"));
        LinearLayout.LayoutParams halfParams1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        halfParams1.setMargins(0, 0, 10, 0);
        pasteBtn.setLayoutParams(halfParams1);

        Button clearBtn = new Button(this);
        clearBtn.setText("🗑️ साफ़ करें");
        clearBtn.setTextSize(13);
        clearBtn.setBackgroundColor(Color.parseColor("#ECEFF1"));
        clearBtn.setTextColor(Color.parseColor("#37474F"));
        LinearLayout.LayoutParams halfParams2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        halfParams2.setMargins(10, 0, 0, 0);
        clearBtn.setLayoutParams(halfParams2);

        actionRow.addView(pasteBtn);
        actionRow.addView(clearBtn);
        layout.addView(actionRow);

        Button scanBtn = new Button(this);
        scanBtn.setText("🔍 अभी स्कैन करें (CHECK SCAM)");
        scanBtn.setTextSize(16);
        scanBtn.setTypeface(null, Typeface.BOLD);
        scanBtn.setTextColor(Color.WHITE);
        scanBtn.setBackgroundColor(Color.parseColor("#D32F2F"));
        scanBtn.setPadding(20, 35, 20, 35);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 0, 0, 30);
        scanBtn.setLayoutParams(btnParams);
        layout.addView(scanBtn);

        resultBox = new TextView(this);
        resultBox.setText("नतीजा यहाँ दिखेगा...");
        resultBox.setTextSize(15);
        resultBox.setPadding(35, 35, 35, 35);
        resultBox.setBackgroundColor(Color.WHITE);
        resultBox.setTextColor(Color.parseColor("#333333"));
        layout.addView(resultBox);

        scroll.addView(layout);
        setContentView(scroll);

        pasteBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item.getText() != null) {
                    inputBox.setText(item.getText().toString());
                    analyzeText(item.getText().toString());
                }
            }
        });

        clearBtn.setOnClickListener(v -> {
            inputBox.setText("");
            resultBox.setText("नतीजा यहाँ दिखेगा...");
            resultBox.setBackgroundColor(Color.WHITE);
            resultBox.setTextColor(Color.parseColor("#333333"));
        });

        qrBtn.setOnClickListener(v -> {
            scanner.startScan()
                    .addOnSuccessListener(barcode -> {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null) {
                            inputBox.setText(rawValue);
                            analyzeText(rawValue);
                        }
                    })
                    .addOnFailureListener(e -> {
                        resultBox.setBackgroundColor(Color.parseColor("#FFF8E1"));
                        resultBox.setTextColor(Color.parseColor("#E65100"));
                        resultBox.setText("⚠️ QR स्कैन रद्द हुआ या कैमरा तैयार नहीं है।");
                    });
        });

        scanBtn.setOnClickListener(v -> analyzeText(inputBox.getText().toString()));
    }

    private void analyzeText(String raw) {
        String text = raw.trim().toLowerCase();
        if (text.isEmpty()) {
            resultBox.setBackgroundColor(Color.parseColor("#FFEBEE"));
            resultBox.setTextColor(Color.parseColor("#B71C1C"));
            resultBox.setText("⚠️ कृपया पहले कोई लिंक, मैसेज या फाइल का नाम पेस्ट करें।");
            return;
        }

        if (text.contains(".apk")) {
            showDanger("🚨 महा-खतरा! यह एक नकली APK ऐप फाइल है। इसे इंस्टॉल करते ही आपका बैंक अकाउंट खाली हो सकता है। इसे तुरंत डिलीट करें!",
                    "सावधान! यह एक खतरनाक एपीके फाइल है। इसे बिल्कुल इंस्टॉल न करें।");
        } else if (text.startsWith("upi://pay") || (text.contains("pa=") && text.contains("am="))) {
            showDanger("🚨 UPI भुगतान चेतावनी!\n\nइस QR कोड या लिंक से आपके खाते से पैसे कटेंगे, पैसे मिलेंगे नहीं! पैसे लेने के लिए कभी भी QR स्कैन या UPI PIN न डालें।",
                    "सावधान! इस क्यूआर कोड से आपके खाते से पैसे कटेंगे, मिलेंगे नहीं। यूपीआई पिन न डालें।");
        } else if (text.contains("kyc") || text.contains("pan") || text.contains("aadhar") || text.contains("block") || text.contains("suspend") || text.contains("reward") || text.contains("lottery") || text.contains("prize") || text.contains("electricity") || text.contains("challan")) {
            showDanger("🚨 संदिग्ध फ्रॉड अलर्ट!\n\nयह मैसेज या लिंक बैंक KYC, बिजली बिल, चालान या इनाम के नाम पर ठगी (Phishing) हो सकता है। इस पर क्लिक न करें।",
                    "खतरा! यह केवाईसी या इनाम के नाम पर फ्रॉड लिंक हो सकता है। इस पर क्लिक न करें।");
        } else if (text.contains(".xyz") || text.contains(".top") || text.contains(".tk") || text.contains(".club") || text.contains("bit.ly") || text.contains("tinyurl") || text.startsWith("http://")) {
            showDanger("⚠️ असुरक्षित लिंक!\n\nयह एक अनजान या छोटा किया गया (Shortened) लिंक है जो नकली वेबसाइट पर ले जा सकता है।",
                    "सावधान! यह एक असुरक्षित वेबसाइट लिंक है।");
        } else {
            resultBox.setBackgroundColor(Color.parseColor("#E8F5E9"));
            resultBox.setTextColor(Color.parseColor("#1B5E20"));
            resultBox.setText("✅ कोई बड़ा खतरा नहीं मिला।\n\nफिर भी अनजान व्यक्ति के साथ कभी भी OTP या UPI PIN साझा न करें।");
            speak("कोई बड़ा खतरा नहीं मिला, फिर भी सावधान रहें।");
        }
    }

    private void showDanger(String message, String voiceAlert) {
        resultBox.setBackgroundColor(Color.parseColor("#FFCDD2"));
        resultBox.setTextColor(Color.parseColor("#B71C1C"));
        resultBox.setText(message);
        speak(voiceAlert);
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
