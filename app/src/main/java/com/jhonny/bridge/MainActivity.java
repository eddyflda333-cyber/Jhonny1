
package com.jhonny.bridge;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.SslErrorHandler;
import android.webkit.ServiceWorkerController;
import android.webkit.ServiceWorkerWebSettings;
import android.net.http.SslError;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String JHONNY_URL =
        "https://eddyflda333-cyber.github.io/Jhonny1/";

    private WebView webView;

    public class AndroidBridge {

        @JavascriptInterface
        public String openApp(String app) {
            String pkg = packageFor(app);

            if (pkg == null) {
                return "Aplicación no autorizada";
            }

            runOnUiThread(() -> launchPackage(pkg));
            return "Solicitando apertura";
        }
    }

    private String packageFor(String app) {
        if (app == null) return null;

        switch (app.toLowerCase()) {
            case "lark":
            case "lark player":
                return "com.dywx.larkplayer";
            case "whatsapp":
                return "com.whatsapp";
            case "calculadora":
                return "com.google.android.calculator";
            case "calendar":
            case "calendario":
                return "com.google.android.calendar";
            default:
                return null;
        }
    }

    private void launchPackage(String pkg) {
        try {
            PackageManager pm = getPackageManager();
            Intent launch = pm.getLaunchIntentForPackage(pkg);

            if (launch == null) {
                Toast.makeText(this,
                    "No se encontró la aplicación instalada.",
                    Toast.LENGTH_LONG).show();
                return;
            }

            startActivity(launch);

        } catch (Exception e) {
            Toast.makeText(this,
                "Android no pudo abrir la aplicación.",
                Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // Configuración de Service Workers
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ServiceWorkerWebSettings swSettings =
                ServiceWorkerController.getInstance()
                    .getServiceWorkerWebSettings();

            swSettings.setBlockNetworkLoads(false);
            swSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            swSettings.setAllowFileAccess(false);
            swSettings.setAllowContentAccess(false);
        }

        webView = new WebView(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(
            WebSettings.MIXED_CONTENT_NEVER_ALLOW
        );
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.addJavascriptInterface(
            new AndroidBridge(),
            "JhonnyAndroid"
        );

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                WebView view,
                WebResourceRequest request
            ) {
                String host = request.getUrl().getHost();
                String scheme = request.getUrl().getScheme();

                return !"eddyflda333-cyber.github.io".equals(host)
                    || !"https".equals(scheme);
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                WebView view,
                String url
            ) {
                return !url.startsWith(JHONNY_URL);
            }

            @Override
            public void onReceivedSslError(
                WebView view,
                SslErrorHandler handler,
                SslError error
            ) {
                handler.cancel();
            }
        });

        setContentView(webView);
        webView.loadUrl(JHONNY_URL);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
