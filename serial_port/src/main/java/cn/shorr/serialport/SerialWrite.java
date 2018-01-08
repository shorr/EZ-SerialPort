package cn.shorr.serialport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 串口写数据工具类
 * Created by Shorr on 2017/10/25.
 */

public class SerialWrite {

    private final static String ACTION = "cn.shorr.serial.write";

    private Context context;
    private WriteReceiver writeReceiver;
    private WriteDataListener writeDataListener;

    public SerialWrite(Context context) {
        this.context = context;
    }

    /**
     * 发送串口数据
     *
     * @param context
     * @param data
     */
    public static void sendData(Context context, byte[] data) {
        sendData(context, 0, data);
    }

    /**
     * 发送串口数据
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
     * 注册广播接收
     *
     * @param port
     * @param listener
     */
    public void registerListener(int port, WriteDataListener listener) {
        this.writeDataListener = listener;

        writeReceiver = new WriteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(creatAction(port));
        LocalBroadcastManager.getInstance(context).registerReceiver(writeReceiver, intentFilter);
    }

    /**
     * 取消注册广播
     */
    public void unRegisterListener() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(writeReceiver);
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
    private class WriteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra("data");
            if (writeDataListener != null) {
                writeDataListener.onWriteData(data);
            }
        }

    }

    public interface WriteDataListener {
        void onWriteData(byte[] data);
    }
}
