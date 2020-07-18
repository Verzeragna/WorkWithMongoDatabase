package mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class SendEmail {
    public void sendMessage(List<String> list) throws MessagingException {
        Properties properties = new Properties();
        //Хост или IP-адрес почтового сервера
        properties.put("mail.smtp.host", "smtp.mail.ru");
        //Требуется ли аутентификация для отправки сообщения
        properties.put("mail.smtp.auth", "true");
        //Порт для установки соединения
        properties.put("mail.smtp.socketFactory.port", "465");
        //Фабрика сокетов, так как при отправке сообщения Yandex требует SSL-соединения
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(properties,
                //Аутентификатор - объект, который передает логин и пароль
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("e-mail", "password");
                    }
                });
        //Создаем новое почтовое сообщение
        Message message = new MimeMessage(session);
        //От кого
        message.setFrom(new InternetAddress("e-mail"));
        //Кому
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("e-mail"));
        //Тема письма
        message.setSubject("Продление сертификатов!!!");
        //Текст письма
        message.setText(getMessageText(list));
        //Поехали!!!
        Transport.send(message);
    }

    private String getMessageText(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Подходит к концу срок следующих сертификатов: ");
        for (String el : list) {
            stringBuilder.append(el).append("; ");
        }
        return stringBuilder.toString();
    }
}
