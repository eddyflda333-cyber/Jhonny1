package com.jhonny.bridge;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Color;

public class MainActivity extends Activity {
    private static final String JHONNY_URL = "https://eddyflda333-cyber.github.io/Jhonny1/";
    private WebView webView;

    public class AndroidBridge {
        @JavascriptInterface
        public String openApp(String app) {
            String pkg = packageFor(app);
            if (pkg == null) return "Aplicación no autorizada";
            runOnUiThread(() -> launchPackage(pkg));
            return "Solicitando apertura";
        }
    }

    private String packageFor(String app) {
        if (app == null) return null;
        switch (app.toLowerCase()) {
            case "lark":
            case "lark player": return "com.dywx.larkplayer";
            case "whatsapp": return "com.whatsapp";
            case "calculadora": return "com.google.android.calculator";
            case "calendar":
            case "calendario": return "com.google.android.calendar";
            default: return null;
        }
    }

    private void launchPackage(String pkg) {
        try {
            PackageManager pm = getPackageManager();
            Intent launch = pm.getLaunchIntentForPackage(pkg);
            if (launch == null) {
                Toast.makeText(this, "No se encontró la aplicación instalada.", Toast.LENGTH_LONG).show();
                return;
            }
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
        } catch (Exception e) {
            Toast.makeText(this, "Android no pudo abrir la aplicación.", Toast.LENGTH_LONG).show();
        }
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        Button test = new Button(this);
        test.setText("Probar apertura de Lark Player");
        test.setOnClickListener(v -> launchPackage("com.dywx.larkplayer"));
        root.addView(test, new LinearLayout.LayoutParams(-1, -2));

        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "JhonnyAndroid");
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String host = request.getUrl().getHost();
                if (host != null && (host.equals("eddyflda333-cyber.github.io") || host.endsWith(".github.io"))) return false;
                return false;
            }
        });
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
        webView.loadUrl(JHONNY_URL);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
