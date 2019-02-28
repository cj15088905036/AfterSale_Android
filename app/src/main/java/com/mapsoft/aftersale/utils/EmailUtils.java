package com.mapsoft.aftersale.utils;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailUtils {


    /**
     * 邮件发送程序
     *
     * @param host     邮件服务器 如：smtp.qq.com
     * @param address  发送邮件的地址 如：xxxxxxxx@qq.com
     * @param from     来自： xxxxxxxx@qq.com
     * @param password 您的邮箱密码
     * @param to       接收人
     * @param port     端口（QQ:25）
     * @param subject  邮件主题
     * @param content  邮件内容
     * @throws Exception
     */
    public static void SendEmail(String host, String address, String from, String password, String to, String port, String subject, String content) throws MessagingException {
        Multipart multiPart;
        String finalString = "";

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", address);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props, null);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setDataHandler(handler);
        multiPart = new MimeMultipart();
        InternetAddress toAddress;
        toAddress = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.setSubject(subject);
        message.setContent(multiPart);
        message.setText(content);

        Transport transport = session.getTransport("smtp");
        transport.connect(host, address, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}