package com.bsh.dt.newwsdemo2;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.CipherSuite;

/**
 * Created by XuTe on 2018/10/7.
 */

public class MySocketFactory extends SSLSocketFactory {

    private static final String[] CIPHER_SUITES =
            {CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384.toString(),
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256.toString(),
                    CipherSuite.TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384.toString(),
                    CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256.toString()};

    private SSLSocketFactory base;

    public MySocketFactory(SSLSocketFactory base) {
        this.base = base;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return base.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return base.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return patch(base.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return patch(base.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return patch(base.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return patch(base.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return patch(base.createSocket(address, port, localAddress, localPort));
    }

    private Socket patch(final Socket s) {
        if (s instanceof SSLSocket) {
            ((SSLSocket) s).setEnabledCipherSuites(this.cipherSuites());
        }
        return s;
    }

    public String[] cipherSuites() {
        /// retrieve supported cipher-suites
        final List<String> supportedCiPherSuites = Arrays.asList(this.getSupportedCipherSuites());
        final List<String> usedCipherSuites = new ArrayList<>();
        for (String cipher : CIPHER_SUITES) {
            if (supportedCiPherSuites.contains(cipher)) {
                usedCipherSuites.add(cipher);
            }
        }
        Log.i(MySocketFactory.class.getName(), "Cipher-suites: " + usedCipherSuites);
        return usedCipherSuites.toArray(new String[0]);
    }
}
