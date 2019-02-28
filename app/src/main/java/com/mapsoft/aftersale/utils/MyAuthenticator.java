package com.mapsoft.aftersale.utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import android.util.Log;

public class MyAuthenticator extends javax.mail.Authenticator {
	private String strUser;
	private String strPwd;
	public MyAuthenticator(String user, String password)
	{
		this.strUser = user;
		this.strPwd = password;
	}


	protected PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(strUser, strPwd);
	}

	public static class MailStruct{
		public String str_to_mail;
		public String str_from_mail;
		public String str_smtp;
		public String str_user;
		public String str_pass;
		public String str_file_path;
	}

	public static void send_mail_file(MailStruct mailStruct) throws AddressException, MessagingException, UnsupportedEncodingException
	{
		Log.v("lengfeng","send_mail_file");


		String host = mailStruct.str_smtp;   //发件人使用发邮件的电子信箱服务器
		String from = mailStruct.str_from_mail;    //发邮件的出发地（发件人的信箱）
		String to 	= mailStruct.str_to_mail;   //发邮件的目的地（收件人信箱）


		Log.v("lengfeng",mailStruct.str_smtp);
		Log.v("lengfeng",mailStruct.str_from_mail);
		Log.v("lengfeng",mailStruct.str_to_mail);

		Properties props = System.getProperties();// Get system properties

		props.put("mail.smtp.host", host);// Setup mail server
		props.put("mail.smtp.auth", "true"); //这样才能通过验证


		MyAuthenticator myauth = new MyAuthenticator(mailStruct.str_user, mailStruct.str_pass);// Get session
		Session session = Session.getDefaultInstance(props, myauth);
		MimeMessage message = new MimeMessage(session); // Define message


		try {
			message.setFrom(new InternetAddress(from)); // Set the from address
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}


		message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));// Set the to address






		message.setText("");// Set the content




		MimeBodyPart attachPart = new MimeBodyPart();
		FileDataSource fds = new FileDataSource(mailStruct.str_file_path); //打开要发送的文件

		attachPart.setDataHandler(new DataHandler(fds));

		message.setSubject( MimeUtility.encodeText(fds.getName()+"采集坐标文件","gb2312","B"));// Set the subject

		attachPart.setFileName( MimeUtility.encodeText(fds.getName(),"gb2312","B"));


		MimeMultipart allMultipart = new MimeMultipart("mixed"); //附件

		allMultipart.addBodyPart(attachPart);//添加


		message.setContent(allMultipart);

		message.saveChanges();


		Transport.send(message);//开始发送


	}
}



