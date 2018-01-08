package cn.shorr.serialport;

import java.io.Serializable;

/**
 * 串口配置
 * Created by Shorr on 2017/10/25.
 */

public class SerialPortConfig implements Serializable {

    private String port; //端口号

    private int baudrate; //波特率


    public SerialPortConfig(String port, int baudrate) {
        this.port = port;
        this.baudrate = baudrate;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }
}
