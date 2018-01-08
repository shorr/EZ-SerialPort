package cn.shorr.serialport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 串口读数据工具类
 * Created by Shorr on 2017/10/25.
 */

public class SerialRead {

    private final static String ACTION = "cn.shorr.serial.read";

    private Context context;
    private ReadReceiver readReceiver;
    private ReadDataListener readDataListener;

    public SerialRead(Context context) {
        this.context = context;
    }

    /**
     * 发送读取的串口数据广播
     *
     * @param context
     * @param port
     * @param data
     */
    public static void sendData(Context context, int port, byte[] data) {
        Intent intent = new Intent(creatAction(port));
        intent.putExtra("data", data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * 注册串口读取广播接收
     *
     * @param listener
     */
    public void registerListener(ReadDataListener listener) {
        registerListener(0, listener);
    }

    /**
     * 注册串口读取广播接收
     *
     * @param port
     * @param listener
     */
    public void registerListener(int port, ReadDataListener listener) {
        this.readDataListener = listener;

        readReceiver = new ReadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(creatAction(port));
        LocalBroadcastManager.getInstance(context).registerReceiver(readReceiver, intentFilter);
    }

    /**
     * 取消注册广播
     */
    public void unRegisterListener() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(readReceiver);
    }

    /**
     * 创建Action
     *
     * @param port
     * @return
     */
    private static String creatAction(int port) {
        return ACTION + ".port_" + port;
    }


    /**
     * 读取数据的广播接收
     */
    private class ReadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra("data");
            if (readDataListener != null) {
                readDataListener.onReadData(data);
            }
        }

    }

    public interface ReadDataListener {
        void onReadData(byte[] data);
    }
}
