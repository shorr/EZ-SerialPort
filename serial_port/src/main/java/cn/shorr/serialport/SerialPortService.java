package cn.shorr.serialport;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * 串口连接服务
 * Created by Shorr on 2017/10/25.
 */

public class SerialPortService extends Service {

    private final static String TAG = "SerialPort";
    private final static int BUF_SIZE = 1024; //接收缓存大小

    private boolean isDebug;
    private SerialPort[] serialPort;
    private SerialWrite[] writeUtil;
    protected OutputStream[] outputStream;
    private InputStream[] inputStream;
    private ReadThread[] readThread;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        isDebug = intent.getBooleanExtra("debug", false);
        ArrayList<SerialPortConfig> configs = (ArrayList<SerialPortConfig>)
                intent.getSerializableExtra("configs");
        //初始化参数
        initParams(configs.size());
        //打开串口
        openSerialPorts(configs);

        return new SerialBinder();
    }

    /**
     * 初始化参数
     */
    private void initParams(int ports) {
        serialPort = new SerialPort[ports];
        writeUtil = new SerialWrite[ports];
        outputStream = new OutputStream[ports];
        inputStream = new InputStream[ports];
        readThread = new ReadThread[ports];
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ReadThread thread : readThread) {
            if (thread != null) {
                thread.interrupt();
            }
        }
        //关闭串口
        closeSerialPort();
    }


    /**
     * 打开串口连接
     */
    private void openSerialPorts(ArrayList<SerialPortConfig> configs) {
        for (int port = 0; port < configs.size(); port++) {
            SerialPortConfig config = configs.get(port);
            try {
                // Open the serial port
                serialPort[port] = new SerialPort(new File(config.getPort()), config.getBaudrate(), 0);
                outputStream[port] = serialPort[port].getOutputStream();
                inputStream[port] = serialPort[port].getInputStream();
                // Create a receiving thread
                readThread[port] = new ReadThread(port);
                readThread[port].start();
                // Create write util
                writeUtil[port] = new SerialWrite(this);
                writeUtil[port].registerListener(port, new SerialWriteListener(port));
                Log.d(TAG, config.getPort() + "-" + port + "-: " +
                        ":open this serial port successfully.");
            } catch (IOException e) {
                Log.e(TAG, config.getPort() + "-" + port + "-: " +
                        ":no this serial port to connect.");
                // Close current service
                stopSelf();
            } catch (SecurityException e) {
                Log.e(TAG, config.getPort() + "-" + port + "-: " +
                        ":this serial port don't have read or write permission.");
                // Close current service
                stopSelf();
            }
        }
    }

    /**
     * 关闭串口连接
     */
    private void closeSerialPort() {
        for (SerialPort port : serialPort) {
            if (port != null) {
                port.close();
                port = null;
            }
        }
        for (SerialWrite write : writeUtil) {
            if (write != null) {
                write.unRegisterListener();
            }
        }
    }

    /**
     * 写数据
     *
     * @param port
     * @param cmd
     */
    private boolean writeData(int port, byte[] cmd) {
        if (outputStream[port] == null) {
            return false;
        }
        try {
            outputStream[port].write(cmd);
            if (isDebug)
                Log.d(TAG, "send-" + port + "-: " + bytesToHexString(cmd));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 接收数据
     *
     * @param port
     * @param buffer
     * @param size
     */
    private void onDataReceived(int port, final byte[] buffer, final int size) {
        byte[] data = new byte[size];
        System.arraycopy(buffer, 0, data, 0, size);
        //发送接收的数据广播
        SerialRead.sendData(this, port, data);
        if (isDebug)
            Log.d(TAG, "receive-" + port + "-: " + bytesToHexString(data));
    }

    /**
     * 读数据线程
     */
    private class ReadThread extends Thread {

        private int port;

        public ReadThread(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[BUF_SIZE];
                    if (inputStream[port] == null) return;
                    size = inputStream[port].read(buffer);
                    if (size > 0) {
                        onDataReceived(port, buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 串口写数据的监听
     */
    private class SerialWriteListener implements SerialWrite.WriteDataListener {

        private int port;

        public SerialWriteListener(int port) {
            this.port = port;
        }

        @Override
        public void onWriteData(byte[] data) {
            //往指定串口写数据
            writeData(port, data);
        }
    }

    /**
     * 串口服务Binder
     */
    public class SerialBinder extends Binder {
        SerialPortService service;

        public SerialBinder() {
            this.service = SerialPortService.this;
        }

        public SerialPortService getService() {
            return service;
        }
    }

    /**
     * byte[]转换为16进制字符串
     *
     * @param bytes
     * @return
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

}
