package com.bsh.dt.newwsdemo2;

import android.support.annotation.Nullable;
import android.util.Log;
import okhttp3.*;
import okio.ByteString;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class OKHttpWsClientImpl implements WsClient {
    private final OkHttpClient client;
    private WebSocket ws;

    public OKHttpWsClientImpl() {
        final OkHttpClient.Builder builder =
            new OkHttpClient.Builder().followRedirects(true).followSslRedirects(true)
                .retryOnConnectionFailure(true).cache(null).
                connectTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS).
                readTimeout(30, TimeUnit.SECONDS).
                hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(final String hostname, final SSLSession session) {
                        /// this prevents the validation of the hostname !!!
                        return true;
                    }
                });

        this.client = enableTls12OnPreLollipop(builder).build();
    }

    public void connect(final String url) {
        final Request request = new Request.Builder().url(url).build();
        this.ws = this.client.newWebSocket(request, new MyWebSocketListener());
    }

    public void close() {
        this.ws.close(1000, "byebye");
        this.ws = null;
    }

    private class MyWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(final WebSocket webSocket, final Response response) {
            super.onOpen(webSocket, response);
            Log.d(this.getClass().getName(), "onOpen '" + response.message() + "'");
            //webSocket.send("hello");
        }

        @Override
        public void onMessage(final WebSocket webSocket, final String text) {
            super.onMessage(webSocket, text);
            Log.d(this.getClass().getName(), "onMessage '" + text + "'");
        }

        @Override
        public void onMessage(final WebSocket webSocket, final ByteString bytes) {
            super.onMessage(webSocket, bytes);
            Log.d(this.getClass().getName(), "onMessage '" + bytes.toString() + "'");
        }

        @Override
        public void onClosing(final WebSocket webSocket, final int code, final String reason) {
            super.onClosing(webSocket, code, reason);
            Log.d(this.getClass().getName(), "onClosing '" + reason + "'");
        }

        @Override
        public void onClosed(final WebSocket webSocket, final int code, final String reason) {
            super.onClosed(webSocket, code, reason);
            Log.d(this.getClass().getName(), "onClosed '" + reason + "'");
        }

        @Override
        public void onFailure(final WebSocket webSocket, final Throwable t,
                              @Nullable final Response response)
        {
            super.onFailure(webSocket, t, response);
            String str = "N/A";
            if (response != null) {
                str = response.message();
            } else if (t != null) {
                str = t.getMessage();
            }
            Log.d(this.getClass().getName(), "onFailure '" + str + "'");
        }
    }

    private static OkHttpClient.Builder enableTls12OnPreLollipop(final OkHttpClient.Builder builder)
    {
        try {
            final X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType)
                    throws CertificateException
                {

                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType)
                    throws CertificateException
                {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[] {};
                }
            };

            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[] {trustManager}, null);
            final SSLSocketFactory tls12SocketFactory =
                new MySocketFactory(sslContext.getSocketFactory());
            builder.sslSocketFactory(tls12SocketFactory, trustManager);

            final ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.RESTRICTED_TLS)
                .cipherSuites(((MySocketFactory) tls12SocketFactory).cipherSuites())
                .supportsTlsExtensions(false).build();

            builder.connectionSpecs(Arrays.asList(spec));
        } catch (final Exception exc) {
            Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
        }

        return builder;
    }
}
