package com.bsh.dt.newwsdemo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnConnect;
    private Button btnClose;

    private WsClient wsClient;

    public final String url = "wss://192.168.186.1:443/config";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btn_connect);
        btnClose = findViewById(R.id.btn_close);

        wsClient = new WebsocketWsClientImpl();
//        wsClient = new OKHttpWsClientImpl();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wsClient.connect(url);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wsClient.close();
            }
        });
    }
}
