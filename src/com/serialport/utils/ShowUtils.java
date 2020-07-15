package com.serialport.utils;

import javax.swing.JOptionPane;
/**
 * 提示框
 */
public class ShowUtils {

	/**
	 * 消息提示
	 * @param message
	 *            消息内容
	 */
	public static void message(String message) {
		JOptionPane.showMessageDialog(null, message);
	}
	/**
	 * 警告消息提示
	 * @param message
	 *            消息内容
	 */
	public static void warningMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "警告",
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * 错误消息提示
	 * 
	 * @param message
	 *            消息内容
	 */
	public static void errorMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "错误",
				JOptionPane.ERROR_MESSAGE);
	}
}
