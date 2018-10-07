package com.bsh.dt.newwsdemo2;

/**
 * Created by XuTe on 2018/10/7.
 */

public interface WsClient {
    void connect(final String url);
    void close();
}
