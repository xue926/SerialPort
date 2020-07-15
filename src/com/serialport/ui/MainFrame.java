package com.serialport.ui;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.math.BigInteger;
import com.serialport.manager.SerialPortManager;
import com.serialport.utils.ShowUtils;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * 主界面
 */
@SuppressWarnings("all")
public class MainFrame extends JFrame {	
	public final int WIDTH = 550;// 程序界面宽度	
	public final int HEIGHT = 555;// 程序界面高度
	// 数据显示区
	private JTextArea mDataView = new JTextArea();
	private JScrollPane mScrollDataView = new JScrollPane(mDataView);//保证数据显示区的窗口可以滚动			
	// 串口设置面板
	private JPanel mSerialPortPanel = new JPanel();//容器
	private JLabel mSerialPortLabel = new JLabel("串口");//标签
	private JLabel mBaudrateLabel = new JLabel("波特率");
	private JComboBox mCommChoice = new JComboBox();//串口选择框	
	private JLabel mDataBitLabel = new JLabel("数据位");
	private JLabel mStopBitLabel = new JLabel("停止位");
	private JLabel mParityLabel = new JLabel("校验");
	private JComboBox mBaudrateChoice = new JComboBox();//波特率选择框
	private JComboBox mDataBitChoice = new JComboBox();//数据位选择框
	private JComboBox mStopBitChoice = new JComboBox();//停止位选择框
	private JComboBox mParityChoice = new JComboBox();//校验选择框	
	//用于为一组按钮创建多重排除范围 ，包含下面两个单选选项，打开一个关闭一个
	private ButtonGroup mDataChoice = new ButtonGroup();
	private JRadioButton mDataASCIIChoice = new JRadioButton("ASCII", true);
	private JRadioButton mDataHexChoice = new JRadioButton("Hex");
	// 操作面板
	private JPanel mOperatePanel = new JPanel();
	private JTextArea mDataInput = new JTextArea();
	private JButton mSerialPortOperate = new JButton("打开串口");
	private JButton mSendCommand = new JButton("发送命令");
	private JButton mSave = new JButton("保存文件");
	private JButton mhelp = new JButton("帮助");
	private JButton mClear = new JButton("清空数据区");
	// 串口列表
	private List<String> mCommList = null;
	// 串口对象
	private SerialPort mSerialport;

	public MainFrame() {
		initView();//初始化窗口
		initComponents();//初始化组件
		actionListener();//监控事件
		initData();
	}

	/**
	 * 初始化主窗口
	 */
	private void initView() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setResizable(true);		
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();// 设置程序窗口居中显示
		setBounds(p.x - WIDTH / 2, p.y - HEIGHT / 2, WIDTH, HEIGHT);
		this.setLayout(null);		
		setTitle("控制系统");
	}

	/**
	 * 初始化主窗口里面的控件
	 */
	private void initComponents() {
		// 数据显示
		mDataView.setFocusable(false);//设置数据显示窗口无法点击
		mScrollDataView.setBounds(10, 10, 505, 200);
		add(mScrollDataView);//添加滚动效果
		//总串口设置
		mSerialPortPanel.setBorder(BorderFactory.createTitledBorder("串口设置"));
		mSerialPortPanel.setBounds(10, 220, 170, 285);
		mSerialPortPanel.setLayout(null);//清空布局管理器
		add(mSerialPortPanel);
		//串口设置
		mSerialPortLabel.setForeground(Color.gray);//串口 两个字的标签 字体是灰色的
		mSerialPortLabel.setBounds(10, 25, 40, 20);
		mSerialPortPanel.add(mSerialPortLabel);//容器添加标签

		mCommChoice.setFocusable(false);//串口选择框，不可聚焦状态，确保数据点击后就可以提交
		mCommChoice.setBounds(60, 25, 100, 20);
		mSerialPortPanel.add(mCommChoice);//容器添加 串口选择框
		
		//波特率设置
		mBaudrateLabel.setForeground(Color.gray);//波特率字体是灰色的
		mBaudrateLabel.setBounds(10, 60, 40, 20);
		mSerialPortPanel.add(mBaudrateLabel);

		mBaudrateChoice.setFocusable(false);
		mBaudrateChoice.setBounds(60, 60, 100, 20);
		mSerialPortPanel.add(mBaudrateChoice);//容器添加 波特率选择框

		//数据位
		mDataBitLabel.setForeground(Color.gray);//波特率字体是灰色的
		mDataBitLabel.setBounds(10, 95, 40, 20);
		mSerialPortPanel.add(mDataBitLabel);

		mDataBitChoice.setFocusable(false);
		mDataBitChoice.setBounds(60, 95, 100, 20);
		mSerialPortPanel.add(mDataBitChoice);//容器添加 波特率选择框
		//停止位
		mStopBitLabel.setForeground(Color.gray);//波特率字体是灰色的
		mStopBitLabel.setBounds(10, 130, 40, 20);
		mSerialPortPanel.add(mStopBitLabel);

		mStopBitChoice.setFocusable(false);
		mStopBitChoice.setBounds(60, 130, 100, 20);
		mSerialPortPanel.add(mStopBitChoice);//容器添加 波特率选择框
		//校验选择 
		mParityLabel.setForeground(Color.gray);//波特率字体是灰色的
		mParityLabel.setBounds(10, 165, 40, 20);
		mSerialPortPanel.add(mParityLabel);

		mParityChoice.setFocusable(false);
		mParityChoice.setBounds(60, 165, 100, 20);
		mSerialPortPanel.add(mParityChoice);//容器添加 波特率选择框
		//数据类型选择
		mDataASCIIChoice.setBounds(20, 195, 55, 20);
		mDataHexChoice.setBounds(95, 195, 55, 20);
		mDataChoice.add(mDataASCIIChoice);//组按钮添加单选按钮
		mDataChoice.add(mDataHexChoice);//组按钮添加单选按钮
		mSerialPortPanel.add(mDataASCIIChoice);//容器添加两个单元按钮
		mSerialPortPanel.add(mDataHexChoice);
	    // 操作面板 
		mOperatePanel.setBorder(BorderFactory.createTitledBorder("操作"));
		mOperatePanel.setBounds(200, 220, 315, 285);
		mOperatePanel.setLayout(null);
		add(mOperatePanel);//主窗口添加 操作容器

		mDataInput.setBounds(25, 25, 265, 120);
		mDataInput.setLineWrap(true);
		mDataInput.setWrapStyleWord(true);
		mOperatePanel.add(mDataInput);
		//打开关闭串口 按钮
		mSerialPortOperate.setFocusable(false);
		mSerialPortOperate.setBounds(45, 160, 90, 20);
		mOperatePanel.add(mSerialPortOperate);
		//发送数据按钮
		mSendCommand.setFocusable(false);//
		mSendCommand.setBounds(180, 160, 90, 20);
		mOperatePanel.add(mSendCommand);
		//实时曲线
		mSave.setFocusable(false);//
		mSave.setBounds(45, 195, 90, 20);
		mOperatePanel.add(mSave);
		
		mhelp.setFocusable(false);//
		mhelp.setBounds(180, 195, 90, 20);
		mOperatePanel.add(mhelp);
		
		mClear.setFocusable(false);//
		mClear.setBounds(100, 230, 115, 20);
		mOperatePanel.add(mClear);
	}

	/**
	 * 初始化数据      
	 */
	private void initData() {
		mCommList = SerialPortManager.findPorts();
		// 检查是否有可用串口，有则加入选项中
		if (mCommList == null || mCommList.size() < 1) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			for (String s : mCommList) {
				mCommChoice.addItem(s);
			}
		}
		//波特率
		mBaudrateChoice.addItem("2400");
		mBaudrateChoice.addItem("4800");
		mBaudrateChoice.addItem("9600");
		mBaudrateChoice.addItem("19200");
		mBaudrateChoice.addItem("38400");
		mBaudrateChoice.addItem("57600");		
		mBaudrateChoice.setSelectedIndex(2);// 数据位 8是默认选项 
			//数据位
		mDataBitChoice.addItem("5");
		mDataBitChoice.addItem("6");
		mDataBitChoice.addItem("7");
		mDataBitChoice.addItem("8");
		mDataBitChoice.setSelectedIndex(3);// 数据位 8是默认选项 
			//停止位
		mStopBitChoice.addItem("1");
		mStopBitChoice.addItem("1.5");
		mStopBitChoice.addItem("2");
			//校验位
		mParityChoice.addItem("None");
		mParityChoice.addItem("Odd");
		mParityChoice.addItem("Even");
		mParityChoice.addItem("Mark");
		mParityChoice.addItem("Space");		
	}
	/**
	 * 按钮监听事件      
	 */
	private void actionListener() {
		// 串口的复选框添加一个监听事件，这个事件主要就是当串口信息变化的时候更新复选框
		mCommChoice.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				mCommList = SerialPortManager.findPorts();
				// 检查是否有可用串口，有则加入选项中
				if (mCommList == null || mCommList.size() < 1) {
					ShowUtils.warningMessage("没有搜索到有效串口！");
				} else {
					int index = mCommChoice.getSelectedIndex();
					mCommChoice.removeAllItems();
					for (String s : mCommList) {
						mCommChoice.addItem(s);
					}
					mCommChoice.setSelectedIndex(index);				
				}
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// NO OP
			}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// NO OP
			}
		});
		// 打开|关闭串口 按钮添加监控事件 
		mSerialPortOperate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ("打开串口".equals(mSerialPortOperate.getText()) && mSerialport == null) {
					openSerialPort(e);
				} else {
					closeSerialPort(e);
				}
				mDataView.setCaretPosition(mDataView.getDocument().getLength());//保证每次显示的都是最新接收或者发送的值
			}
		});
		// 发送数据   按钮添加监控事件 
		mSendCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mSendCommand(e);
				mDataView.setCaretPosition(mDataView.getDocument().getLength());
			}
		});
		mSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(mSave)==JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    WriteToFile(file.getPath());
                    ShowUtils.message("保存成功");
                }
			}
		});
		mhelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ShowUtils.message("输入命令，点击发送：\n o:启动下位机\ns:终止下位机\nx:采集x路的信息\ny:采集y路的信息\nz:采集z路的信息\n点击保存：保存数据窗口显示的所有数据");
			}
		});
		mClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mDataView.setText("");
			}
		});
	}

	/**
	 * 打开串口
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void openSerialPort(java.awt.event.ActionEvent evt) {		
		String commName = (String) mCommChoice.getSelectedItem();// 获取串口名称
		String bps = (String) mBaudrateChoice.getSelectedItem();//返回选择的波特率的值
		int baudrate = Integer.parseInt(bps);		
		int dataBit = mDataBitChoice.getSelectedIndex();//返回数据位的值
		int stopBit =  mStopBitChoice.getSelectedIndex();//返回停止位的值
		int pairity =  mParityChoice.getSelectedIndex();//返回校验位的值		
		// 检查串口名称是否获取正确
		if (commName == null || commName.equals("")) {
			ShowUtils.warningMessage("没有搜索到有效串口！");
		} else {
			try {
				mSerialport = SerialPortManager.openPort(commName, baudrate,dataBit, stopBit, pairity);//打开串口
				if (mSerialport != null) {
					mDataView.append(commName+"串口已打开" + "\r\n");//数据显示区域
					mSerialPortOperate.setText("关闭串口");//按钮
				}
			} catch (PortInUseException e) {
				ShowUtils.warningMessage("串口已被占用！");
			}
		}
		// 添加串口监听
		SerialPortManager.addListener(mSerialport, new SerialPortManager.DataAvailableListener() {
			@Override  //重载 最初初始化函数里面什么都没有
			public void dataAvailable() {
				byte[] data = null;
				try {
					if (mSerialport == null) {
						ShowUtils.errorMessage("串口对象为空，监听失败！");
					} else {
						// 读取串口数据
						data = SerialPortManager.readFromPort(mSerialport);
						//mDataHexChoice.setSelected(true);
						// 以字符串的形式接收数据
						if (mDataASCIIChoice.isSelected()) {
							//mDataView.append(new String(data) + "\r\n");
							String a=new String(data);
							//System.out.println(a);
							int result=((Integer.parseInt(String.valueOf(a.charAt(0)))*100)+
									(Integer.parseInt(String.valueOf(a.charAt(1)))*10)+
									(Integer.parseInt(String.valueOf(a.charAt(2)))));
							mDataView.append("接收模拟量："+ result+"\t接收开关量"+a.charAt(3)+"\r\n");
							//mDataView.append("发送 "+ result/2+ "\r\n");
							if(result>240){								
								String w="w";
								SerialPortManager.sendToPort(mSerialport, w.getBytes());
								ShowUtils.warningMessage("数值过大！\n");
							}else{
								mDataView.append("发送 "+ result/2+ "\r\n");		
								String b=String.valueOf(result/2);
								SerialPortManager.sendToPort(mSerialport, b.getBytes());
							}														
						}
						mDataView.setCaretPosition(mDataView.getDocument().getLength());
					}
				} catch (Exception e) {
					
				}
			}
		});
	}
	/**
	 * 关闭串口
	 * @param evt
	 *            点击事件
	 */
	private void closeSerialPort(java.awt.event.ActionEvent evt) {
		SerialPortManager.closePort(mSerialport);
		mDataView.append((String) mCommChoice.getSelectedItem()+"串口已关闭" + "\r\n");
		String file=mDataView.getText().toString().toLowerCase();
		
		mSerialPortOperate.setText("打开串口");
		mSerialport = null;
	} 
	/**
	 * 保存文件
	 * @param savepath
	 *            点击事件
	 */
	private void WriteToFile(String savepath){
		FileOutputStream fos=null;
		try{
		fos=new FileOutputStream(savepath);
		fos.write(mDataView.getText().getBytes());
        fos.close();
		}catch (Exception e) {			
		}		
	}
	/**
	 * 发送数据
	 * 
	 * @param evt
	 *            点击事件
	 */
	private void mSendCommand(java.awt.event.ActionEvent evt) {
		// 待发送数据
		String Command = mDataInput.getText().toString().toLowerCase();

		if (mSerialport == null) {
			ShowUtils.warningMessage("请先打开串口！");
			return;
		}
		if ("".equals(Command) || Command == null) {
			ShowUtils.warningMessage("请输入要发送的命令！");
			return;
		}
		mDataASCIIChoice.setSelected(true);
		// 以字符串的形式发送数据
		//if (mDataASCIIChoice.isSelected()) {
		SerialPortManager.sendToPort(mSerialport, Command.getBytes());
		// 以字符串的形式发送数据
		String x="x";
		String y="y";
		String z="z";
		String o="o";
		String s="s";
		if(Command.equals(x)||Command.equals(y)||Command.equals(z)){
			mDataView.append("采集 "+Command +" 路数据:"+"\r\n");			
		}else if(Command.equals(o)){
			mDataView.append("启动下位机"+"\r\n");
		}else if(Command.equals(s)){
			mDataView.append("终止下位机"+"\r\n");			
		}else{
			ShowUtils.warningMessage("请输入正确的命令：\n 启动下位机 o\n终止下位机 s\n采集三路信号 x、y、z\n");
		}
		mDataInput.setText("");//发送命令之后清空输入框的内容			
		mDataView.setCaretPosition(mDataView.getDocument().getLength());
	}	
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
				
			}
		});
	}
}
