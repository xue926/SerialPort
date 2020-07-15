package com.serialport.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import com.serialport.utils.ArrayUtils;
import com.serialport.utils.ShowUtils;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RS485PortEvent;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * 串口管理
 * 
 * @author yangle
 */
@SuppressWarnings("all")
public class SerialPortManager {

	/**
	 * 查找所有可用端口
	 * 
	 * @return 可用端口名称列表
	 */
	public static final ArrayList<String> findPorts() {
		// 获得当前所有可用串口, CommPortIdentifier 通讯端口管理类  是RXTXcom.jar里面的
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier
				.getPortIdentifiers();
		// 不带参数的getPortIdentifiers方法获得一个CommPortIdentifier对象 枚举对象，

		ArrayList<String> portNameList = new ArrayList<String>();
		// 将可用串口名添加到List并返回该List
		while (portList.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portList.nextElement();
			String portName = portIdentifier.getName();
			portNameList.add(portName);
			System.out.println(portIdentifier.getName()+'-'+getPortTypeName(portIdentifier.getPortType()));
		}
		return portNameList;
	}

	public static String getPortTypeName(int portType) {
		switch (portType) {
		case CommPortIdentifier.PORT_I2C:
			return "I2C";
		case CommPortIdentifier.PORT_PARALLEL:// 井口
			return "Parallel";
		case CommPortIdentifier.PORT_RAW:
			return "Raw";
		case CommPortIdentifier.PORT_RS485:// RS485端口
			return "RS485";
		case CommPortIdentifier.PORT_SERIAL:// 串口
			return "Serial";
		default:
			return "unknown type";
		}
	}

	/**
	 * 打开串口
	 * 
	 * @param portName
	 *            端口名称
	 * @param baudrate
	 *            波特率
	 * @return 串口对象
	 * @throws PortInUseException
	 *             串口已被占用
	 */
	public static final SerialPort openPort(String portName, int baudrate,int dataBit,int stopBit,int pairity)
			throws PortInUseException {
		try {
	// getPortIdentifier(string name) 通过端口名 识别通讯端口,返回CommPortIdentifier通讯端口管理对象
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
			// 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
			CommPort commPort = portIdentifier.open(portName, 2000);
			
			// 判断是不是串口
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;// 通讯端口转化为串行通讯端口
				
				try {
					// 设置一下串口的波特率等参数
					// 数据位：8
					// 停止位：1
					// 校验位：None
					int DATABITS=SerialPort.DATABITS_8,STOPBITS=SerialPort.STOPBITS_1,PARITY=SerialPort.PARITY_NONE;
					switch (dataBit) {
					case 0:
						DATABITS=SerialPort.DATABITS_5;
						break;
					case 1:
						DATABITS=SerialPort.DATABITS_6;
						break;
					case 2:
						DATABITS=SerialPort.DATABITS_7;
						break;
					case 3:
						DATABITS=SerialPort.DATABITS_8;
						break;
					default:
						break;
					}
					switch (stopBit) {
					case 0:
						STOPBITS=SerialPort.STOPBITS_1;
						break;
					case 1:
						STOPBITS=SerialPort.STOPBITS_1_5;
						break;
					case 2:
						STOPBITS=SerialPort.STOPBITS_2;
						break;
					default:
						break;
					}
					switch (pairity) {
					case 0:
						PARITY=SerialPort.PARITY_NONE;
						break;
					case 1:
						PARITY=SerialPort.PARITY_ODD;
						break;
					case 2:
						PARITY=SerialPort.PARITY_EVEN;
						break;
					case 3:
						PARITY=SerialPort.PARITY_MARK;
						break;
					case 4:
						PARITY=SerialPort.PARITY_SPACE;
						break;
					default:
						break;
					}
					serialPort.setSerialPortParams(baudrate,
							DATABITS, STOPBITS,
							PARITY);
				} catch (UnsupportedCommOperationException e) {
					e.printStackTrace();
				}
				return serialPort;
			}
		} catch (NoSuchPortException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 关闭串口
	 * 
	 * @param serialport
	 *            待关闭的串口对象
	 */
	public static void closePort(SerialPort serialPort) {
		if (serialPort != null) {
			serialPort.close();
		}
	}

	/**
	 * 往串口发送数据
	 * 
	 * @param serialPort
	 *            串口对象
	 * @param order
	 *            待发送数据
	 */
	public static void sendToPort(SerialPort serialPort, byte[] order) {
		OutputStream out = null;
		try {
			out = serialPort.getOutputStream();
			//OutputStream 这个抽象类是表示字节输出流的所有类的超类。 输出流接收输出字节并将其发送到某个接收器。
			out.write(order);
			//将 order.length字节从指定的字节数组写入此输出流。
			out.flush();
			//刷新此输出流并强制任何缓冲的输出字节被写出。 flush的一般合同是，
			//呼叫它表明，如果先前写入的任何字节已经通过输出流的实现进行缓冲，则这些字节应该立即被写入到它们的预定目的地。
		} catch (IOException e) {
			e.printStackTrace();
		} finally {//无论 try / catch 结果如何都会执行的代码块
			try {
				//执行到这里了，out所需得到工作已经完成了，out保存了串口字节输出流的对象，非空才能关闭
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从串口读取数据
	 * 
	 * @param serialPort
	 *            当前已建立连接的SerialPort对象
	 * @return 读取到的数据
	 */
	public static byte[] readFromPort(SerialPort serialPort) {
		InputStream in = null;
		byte[] bytes = {};//保存所有的 字节 
		try {
			in = serialPort.getInputStream();
			// 缓冲区大小为一个字节
			byte[] readBuffer = new byte[1];
			//从输入流in中 读取一些（这里是1个）字节数，并将它们存储到缓冲区 readBuffer 。
			int bytesNum = in.read(readBuffer);
			while (bytesNum > 0) {
				//将读到的readBuffer中的 1字节  连接到bytes  字节数组后 
				bytes = ArrayUtils.concat(bytes, readBuffer);
				bytesNum = in.read(readBuffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	/**
	 * 添加监听器
	 * 
	 * @param port
	 *            串口对象
	 * @param listener
	 *            串口存在有效数据监听
	 */
	public static void addListener(SerialPort serialPort,
			DataAvailableListener listener) {
		try {
			// 给串口添加监听器
			serialPort.addEventListener(new SerialPortListener(listener));
			// 设置当有数据到达时唤醒监听接收线程
			serialPort.notifyOnDataAvailable(true);
			// 设置当通信中断时唤醒中断线程
			serialPort.notifyOnBreakInterrupt(true);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 串口监听
	 */
	//串口操作类，一定要继承  SerialPortEventListener
	public static class SerialPortListener implements SerialPortEventListener {

		private DataAvailableListener mDataAvailableListener;

		//SerialPortListener这个自定义的类，初始化要有DataAvailableListener 这个自定义类的参数
		public SerialPortListener(DataAvailableListener mDataAvailableListener) {
			this.mDataAvailableListener = mDataAvailableListener;
		}
          
		//实现接口SerialPortEventListener中的方法读取从串口中接收的数据
		//SerialPortEvent 串行端口事件 
		public void serialEvent(SerialPortEvent serialPortEvent) {
			switch (serialPortEvent.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE: // 1.串口存在有效数据
				if (mDataAvailableListener != null) {
					// 调用自定义类的函数处理 数据 
					mDataAvailableListener.dataAvailable();
				}
				break;

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2.输出缓冲区已清空
				break;

			case SerialPortEvent.CTS: // 3.清除待发送数据
				break;

			case SerialPortEvent.DSR: // 4.待发送数据准备好了
				break;

			case SerialPortEvent.RI: // 5.振铃指示
				break;

			case SerialPortEvent.CD: // 6.载波检测
				break;

			case SerialPortEvent.OE: // 7.溢位（溢出）错误
				break;

			case SerialPortEvent.PE: // 8.奇偶校验错误
				break;

			case SerialPortEvent.FE: // 9.帧错误
				break;

			case SerialPortEvent.BI: // 10.通讯中断
				ShowUtils.errorMessage("与串口设备通讯中断");
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 串口存在有效数据监听
	 */
	public interface DataAvailableListener {
		/**
		 * 串口存在有效数据
		 */
		void dataAvailable();//被外面调用时候 重载的
	}
}

