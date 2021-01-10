package com.example.mybrowser;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//ignore ssl errors in webViewClient
class IgnoreSSLErrorWebViewClient extends WebViewClient {
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); // When an error occurs, ignore and go on
    }
}
