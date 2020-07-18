package startmode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import command.Certificate;
import command.SelectCommand;
import connectdatabase.ConnectToMongoDB;
import console.ConsoleHelper;
import frame.Frame;
import mail.SendEmail;
import org.bson.Document;

import javax.mail.MessagingException;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StartMode implements StartModeInterface {
    ConsoleHelper consoleHelper = new ConsoleHelper();
    private final static String ERRORS_LOG = new File("").getAbsolutePath() + "\\errors_log.txt";
    private static FileWriter nWriter;

    public void dataBaseMode() {
        showInfo();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
            while (!line.equals("exit")) {
                line = reader.readLine();
                new SelectCommand().runCommand(line);
            }
        } catch (IOException ex) {
            consoleHelper.showMessage("Ошибка: " + ex.getMessage());
        }
    }

    private void showInfo() {
        consoleHelper.showMessage("В программе имеются следующие команды:");
        consoleHelper.showMessage("insert - добавление документа в базу;");
        consoleHelper.showMessage("delete - удаление документа из базы;");
        consoleHelper.showMessage("update - обновление документа базы;");
        consoleHelper.showMessage("find - поиск документа в базе;");
        consoleHelper.showMessage("find like - поиск документа в базе по части наименования организации;");
        consoleHelper.showMessage("less - выводит список документов, где до окончания действия сертификата осталось менее 8 дней;");
        consoleHelper.showMessage("show all - выводит список всех документов в базе.");
    }

    public void notifMode() {
        new Frame().run();
        checkLogFile();
        new Thread() {
            @Override
            public void run() {
                ObjectMapper mapper = new ObjectMapper();
                ConnectToMongoDB connect = new ConnectToMongoDB();
                MongoCollection<Document> col = connect.db.getCollection("certificates");
                FindIterable<Document> fi = col.find();
                MongoCursor<Document> cursor = fi.iterator();
                ArrayList<String> list = new ArrayList();
                while (true) {
                    try {
                        while (cursor.hasNext()) {
                            Certificate cert = mapper.readValue(cursor.next().toJson(), Certificate.class);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            Date date = simpleDateFormat.parse(cert.dateend);
                            Date today = new Date();
                            long milliseconds = date.getTime() - today.getTime();
                            int days = (int) (milliseconds / (24 * 60 * 60 * 1000));
                            if (days > 0 && days < 8) {
                                list.add(cert.toString());
                            }
                        }
                        if (list.size() > 0) {
                            try {
                                new SendEmail().sendMessage(list);
                            } catch (MessagingException ex) {
                                writeToLogFile(ex.getMessage());
                            } finally {
                                list.clear();
                            }
                        }
                        try {
                            Thread.sleep(216000000);
                        } catch (InterruptedException e) {
                            writeToLogFile(e.getMessage());
                        }
                    } catch (JsonMappingException e) {
                        writeToLogFile(e.getMessage());
                    } catch (JsonProcessingException e) {
                        writeToLogFile(e.getMessage());
                    } catch (ParseException e) {
                        writeToLogFile(e.getMessage());
                    }
                }
            }
        }.start();
    }

    private void checkLogFile() {
        File logFile = new File(ERRORS_LOG);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                nWriter = new FileWriter(logFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            try {
                nWriter = new FileWriter(logFile, true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    private void writeToLogFile(String message) {
        try {
            nWriter.write(message);
            nWriter.append("\r\n");
            nWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
