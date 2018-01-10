# EZ-SerialPort

EZ-SerialPort是基于Google [android-serialport-api](https://github.com/cepr/android-serialport-api) 修改与扩展的Android串口操作库。

EZ-SerialPort简化了串口的配置与读写操作，支持了多串口通道的操作，支持了多页面串口数据的同时读取功能。

Demo效果如下：

<img src="/screenshot/demo.png" width=60% height=60% alt="screenshot">


## Gradle 依赖
在app的`build.gradle` 下添加：

### 添加依赖如下：
```groovy
dependencies {
    compile 'cn.shorr:ez-serialport:0.1.0'
}
```
### 添加适合的CPU平台ABI，如：
```groovy
defaultConfig {
	...

	ndk {
		abiFilters "armeabi", "armeabi-v7a", "x86"
	}
}
```
不配置将默认为所有平台的ABI。

## 使用

```java
//1.开始串口连接
private void startSerialPortConnect() {
	//配置串口参数
    serialPortUtil = new SerialPortUtil(this,
            new SerialPortConfig("/dev/ttyMT1", 115200), //串口0
            new SerialPortConfig("/dev/ttyMT2", 38400) //串口1

    );
    //设置为调试模式，打印收发数据
    serialPortUtil.setDebug(true);
    //绑定串口服务
    serialPortUtil.bindService();

    //串口0数据读取监听（可在不同Activity中同时设置监听）
    serial0Read = new SerialRead(this);
    serial0Read.registerListener(0/*默认为0，此参数可省略*/, new Serial0ReadListener());
    //串口1数据读取监听（可在不同Activity中同时设置监听）
    serial1Read = new SerialRead(this);
    serial1Read.registerListener(1, new Serial1ReadListener());
}

//2.设置串口读取监听
private class Serial0ReadListener implements SerialRead.ReadDataListener {
    @Override
    public void onReadData(byte[] data) {
       
    }
}

//3.串口发送数据
SerialWrite.sendData(Context context, int port, byte[] data)；
//如：串口0发送数据
SerialWrite.sendData(this, 0/*默认为0，此参数可省略*/, content.getBytes("GBK"));
//如：串口1发送数据
SerialWrite.sendData(this, 1, content.getBytes("GBK"));

//4.停止串口连接
private void stopSerialPortConnect() {
    serial0Read.unRegisterListener();
    serial1Read.unRegisterListener();
    serialPortUtil.unBindService();
}

//5.获取设备所有的串口信息
SerialPortFinder serialPortFinder = new SerialPortFinder();
String[] devices = serialPortFinder.getAllDevicesPath();

```

## 开发建议

由于Android各硬件平台与驱动的差异性，在接收串口数据，即在通过监听获取byte[]字节数组时，一条完整的指令数据有可能被无规律分隔为多条byte[]字节数组。所以此时，需要将多条byte[]字节数组拼接为一条完整的指令，以便于指令的解析工作。

所以，在串口数据格式制定的初期一定要做好相关工作。比如，可以在数据格式中指定`开始标记`、`数据长度`、`结束标记`等，这样可以方便数据的拼接以及校验等工作。
