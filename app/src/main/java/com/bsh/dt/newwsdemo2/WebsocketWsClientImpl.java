package com.bsh.dt.newwsdemo2;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.cert.CertificateException;

public class WebsocketWsClientImpl implements WsClient {

    private WebSocketClient webSocketClient;

    @Override
    public void connect(final String url) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URI uri = new URI(url);
                    webSocketClient = new WebSocketClient(uri) {
                        @Override
                        public void onOpen(final ServerHandshake handshakedata) {
                            Log.d(this.getClass().getName(),
                                "onOpen: '" + handshakedata.getHttpStatusMessage() + "'");
                        }

                        @Override
                        public void onMessage(final String message) {
                            Log.d(this.getClass().getName(), "onMessage: '" + message + "'");
                        }

                        @Override
                        public void onClose(final int code, final String reason,
                                            final boolean remote)
                        {
                            Log.d(this.getClass().getName(), "onClose: '" + reason + "'");
                        }

                        @Override
                        public void onError(final Exception ex) {
                            Log.d(this.getClass().getName(), "onError: '" + ex.getMessage() + "'");
                        }
                    };

                    final X509TrustManager trustManager = new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(
                            final java.security.cert.X509Certificate[] chain, final String authType)
                            throws CertificateException
                        {

                        }

                        @Override
                        public void checkServerTrusted(
                            final java.security.cert.X509Certificate[] chain, final String authType)
                            throws CertificateException
                        {

                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[] {};
                        }
                    };

                    final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, new TrustManager[] {trustManager},
                        null); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates

                    final SSLSocketFactory sslSocketFactory =
                        new MySocketFactory(sslContext.getSocketFactory());
                    webSocketClient
                        .setSocket(sslSocketFactory.createSocket(uri.getHost(), uri.getPort()));
                    webSocketClient.connect();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "Error: '" + e.getMessage() + "'");
                }
            }
        });

        thread.start();
    }

    @Override
    public void close() {
        if (this.webSocketClient != null) {
            this.webSocketClient.close();
        }
        this.webSocketClient = null;
    }
}
