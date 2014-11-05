package com.elex.odin.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailManager {
    static class MailConstants {
        public static String HOST = "smtp.qq.com";
        public static String EMAIL_FROM = "xamonitor@xingcloud.com";
        public static String EMAIL_TO = "liqiang@xingcloud.com";
        public static String USER_NAME = "xamonitor@xingcloud.com";
        public static String PASSWORD = "22C1NziwxZI5F";
    }

    private static MailManager m_instance = null;

    private MailManager() {
    }
    
    public synchronized static MailManager getInstance() {
        if(m_instance == null) {
            m_instance = new MailManager();
        }
        return m_instance;
    }
    
    public boolean sendEmail(String subject, String content, Exception e) {
        return sendEmail(subject,content + "\n" + e.getCause().getMessage());
    }
    
    public boolean sendEmail(String subject, String content) {
        // 创建Properties 对象
        Properties props = System.getProperties();
        props.put("mail.smtp.host", MailConstants.HOST); // 全局变量
        props.put("mail.smtp.auth", "true");
     
        // 创建邮件会话
        Session session = Session.getDefaultInstance(props,
        new Authenticator() { // 验账账户
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailConstants.USER_NAME,
                        MailConstants.PASSWORD);
            }
        });
     
        try {
            // 定义邮件信息
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MailConstants.EMAIL_FROM));
            
            String[] mailToList = MailConstants.EMAIL_TO.split(",");
            
            for(String mailTo : mailToList) {
                message.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress(
                            mailTo
                        )
                    );
            }

            // 要编码，否则中文会出乱码，貌似这个方法是对数据进行了
            //("=?GB2312?B?"+enc.encode(subject.getBytes())+"?=")形势的包装
            message.setSubject(MimeUtility.encodeText(subject, "gbk", "B"));
     
            MimeMultipart mmp = new MimeMultipart();
            MimeBodyPart mbp_text = new MimeBodyPart();
            // "text/plain"是文本型，没有样式，
            //"text/html"是html样式，可以解析html标签
            mbp_text.setContent(content, "text/plain;charset=gbk");
            mmp.addBodyPart(mbp_text); // 加入邮件正文
     
            message.setContent(mmp);
            // message.setText(data.get(MailConstants.EMAIL_TEXT));
     
            // 发送消息
            // session.getTransport("smtp").send(message); //也可以这样创建Transport对象
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void main(String[] args) {
        MailManager.getInstance().sendEmail("Test", "Ha");
    }
    
}
