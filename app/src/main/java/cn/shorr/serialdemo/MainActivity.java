package cn.shorr.serialdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import cn.shorr.serialport.SerialPortConfig;
import cn.shorr.serialport.SerialPortUtil;
import cn.shorr.serialport.SerialRead;
import cn.shorr.serialport.SerialWrite;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView receive0Tv;
    private TextView receive1Tv;
    private EditText send0Et;
    private EditText send1Et;

    private SerialPortUtil serialPortUtil;
    private SerialRead serial0Read;
    private SerialRead serial1Read;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        startSerialPortConnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopSerialPortConnect();
    }

    /**
     * 开始串口连接
     */
    private void startSerialPortConnect() {
        serialPortUtil = new SerialPortUtil(this,
                new SerialPortConfig("/dev/ttyMT1", 115200), //串口0
                new SerialPortConfig("/dev/ttyMT2", 38400) //串口1

        );
        //设置为调试模式，打印收发数据
        serialPortUtil.setDebug(true);
        //绑定串口服务
        serialPortUtil.bindService();

        //串口0数据读取监听
        serial0Read = new SerialRead(this);
        serial0Read.registerListener(0, new Serial0ReadListener());//Or serial0Read.registerListener(new Serial0ReadListener());
        //串口1数据读取监听
        serial1Read = new SerialRead(this);
        serial1Read.registerListener(1, new Serial1ReadListener());
    }

    /**
     * 停止串口连接
     */
    private void stopSerialPortConnect() {
        serial0Read.unRegisterListener();
        serial1Read.unRegisterListener();
        serialPortUtil.unBindService();
    }

    private void initViews() {
        receive0Tv = (TextView) findViewById(R.id.receive0_tv);
        receive1Tv = (TextView) findViewById(R.id.receive1_tv);
        send0Et = (EditText) findViewById(R.id.send0_et);
        send1Et = (EditText) findViewById(R.id.send1_et);
        Button send0ClearBt = (Button) findViewById(R.id.send0_clear_bt);
        Button send0SendBt = (Button) findViewById(R.id.send0_send_bt);
        Button send1ClearBt = (Button) findViewById(R.id.send1_clear_bt);
        Button send1SendBt = (Button) findViewById(R.id.send1_send_bt);
        send0ClearBt.setOnClickListener(this);
        send0SendBt.setOnClickListener(this);
        send1ClearBt.setOnClickListener(this);
        send1SendBt.setOnClickListener(this);
    }


    /**
     * 串口0发送数据
     *
     * @param content
     */
    private void send0Data(String content) {
        try {
            //发送数据 Or SerialWrite.sendData(this,content.getBytes("GBK"));
            SerialWrite.sendData(this, 0, content.getBytes("GBK"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 串口1发送数据
     *
     * @param content
     */
    private void send1Data(String content) {
        try {
            //发送数据
            SerialWrite.sendData(this, 1, content.getBytes("GBK"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 串口0数据读取监听
     */
    private class Serial0ReadListener implements SerialRead.ReadDataListener {
        @Override
        public void onReadData(byte[] data) {
            try {
                String hex = FormatUtil.bytesToHexString(data);
                String content = new String(data, "GBK");
                receive0Tv.setText("Hex(" + data.length + "bytes):\n" + hex + "\n内容:\n" + content);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 串口1数据读取监听
     */
    private class Serial1ReadListener implements SerialRead.ReadDataListener {
        @Override
        public void onReadData(byte[] data) {
            try {
                String hex = FormatUtil.bytesToHexString(data);
                String content = new String(data, "GBK");
                receive1Tv.setText("Hex(" + data.length + "bytes):\n" + hex + "\n内容:\n" + content);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.send0_clear_bt:
                send0Et.setText("");
                break;
            case R.id.send0_send_bt:
                send0Data(send0Et.getText().toString());
                break;
            case R.id.send1_clear_bt:
                send1Et.setText("");
                break;
            case R.id.send1_send_bt:
                send1Data(send1Et.getText().toString());
                break;
        }
    }


}
