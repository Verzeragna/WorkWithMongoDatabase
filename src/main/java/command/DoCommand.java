package command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import connectdatabase.ConnectToMongoDB;
import console.ConsoleHelper;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class DoCommand implements CommandInterface {
    private final ConnectToMongoDB connect = new ConnectToMongoDB();
    private final ConsoleHelper consoleHelper = new ConsoleHelper();

    public void insert() throws IOException {
        consoleHelper.showMessage("Введите данные в следующем формате: " +
                "Организация,Дата получения(dd.mm.yyyy),Срок действия(dd.mm.yyyy),Статус,Комментарий");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "cp866"));
        String line = "";
        while (!line.equals("back")) {
            line = reader.readLine();
            if (line.split(",").length == 5) {
                addDocument(connect.db.getCollection("certificates"), line.split(","));
                consoleHelper.showMessage("Запись успешно добавлена!");
            } else {
                if (!line.equals("back")) consoleHelper.showMessage("Не верный формат! Повторите ввод!");
            }
        }
        consoleHelper.showMessage("Вы находитесь в гланом меню...");
    }

    private void addDocument(MongoCollection<Document> col, String[] str) {
        Document emp1 = new Document();
        emp1.put("organization", str[0]);
        emp1.put("datestart", str[1]);
        emp1.put("dateend", str[2]);
        emp1.put("status", str[3]);
        emp1.put("comment", str[4]);
        col.insertOne(emp1);
    }

    public void delete() throws IOException {
        consoleHelper.showMessage("Введите наименование организации: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "cp866"));
        String line = "";
        while (!line.equals("back")) {
            line = reader.readLine();
            if (!line.equals("back")) {
                deleteDocument(line);
            }
        }
        consoleHelper.showMessage("Вы находитесь в гланом меню...");
    }

    private void deleteDocument(String line) {
        DeleteResult dr = connect.db.getCollection("certificates").deleteOne(new Document("organization", line));
        if (dr.getDeletedCount() > 0) {
            consoleHelper.showMessage("Документ удален!");
        } else {
            consoleHelper.showMessage("Документ не удален!");
        }
    }


    public void update() throws IOException {
        consoleHelper.showMessage("Введите название организации, а затем через запятую укажите данные для обовления.");
        consoleHelper.showMessage("Пример: Если вам нужно обновить дату окончания действия сертификата, то необходимо использовать следующий шаблон:");
        consoleHelper.showMessage("Аквамарин,,05.07.2021,,-");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "cp866"));
        String line = "";
        while (!line.equals("back")) {
            line = reader.readLine();
            if (!line.equals("back")) {
                if (line.split(",").length == 5) {
                    updateDocument(line.split(","));
                    consoleHelper.showMessage("Документ обновлен!");
                } else {
                    consoleHelper.showMessage("Не верный формат! Повторите ввод!");
                }
            }
        }
        consoleHelper.showMessage("Вы находитесь в гланом меню...");
    }

    private void updateDocument(String[] line) throws JsonProcessingException {
        Document document = connect.db.getCollection("certificates").find(new Document("organization", line[0])).first();
        ObjectMapper mapper = new ObjectMapper();
        assert document != null;
        Certificate cert = mapper.readValue(document.toJson(), Certificate.class);
        Document update = getUpdate(line, cert.toString().split(","));
        Bson filter = Filters.eq("organization", line[0]);
        connect.db.getCollection("certificates").updateOne(filter, new Document("$set", update));
    }

    private Document getUpdate(String[] line, String[] cert) {
        for (int i = 1; i < cert.length - 1; i++) {
            if (line[i].length() > 0) cert[i] = line[i];
        }
        if (line[4].length() > 1) cert[4] = line[4];
        Document emp1 = new Document();
        emp1.put("organization", cert[0]);
        emp1.put("datestart", cert[1]);
        emp1.put("dateend", cert[2]);
        emp1.put("status", cert[3]);
        emp1.put("comment", cert[4]);
        return emp1;
    }

    public void showAll() {
        ObjectMapper mapper = new ObjectMapper();
        MongoCollection<Document> col = connect.db.getCollection("certificates");
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while (cursor.hasNext()) {
                Certificate cert = mapper.readValue(cursor.next().toJson(), Certificate.class);
                consoleHelper.showMessage(cert.toString());
            }
        } catch (JsonMappingException e) {
            consoleHelper.showMessage("Ошибка чтения из базы данных: " + e.getMessage());
        } catch (JsonProcessingException e) {
            consoleHelper.showMessage("Ошибка чтения из базы данных: " + e.getMessage());
        } finally {
            cursor.close();
        }
    }

    public void find() throws IOException {
        consoleHelper.showMessage("Введите наименование организации: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "cp866"));
        String line = "";
        while (!line.equals("back")) {
            line = reader.readLine();
            if (!line.equals("back")) {
                findDocument(line);
            }
        }
        consoleHelper.showMessage("Вы находитесь в гланом меню...");
    }

    private void findDocument(String line) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MongoCollection<Document> col = connect.db.getCollection("certificates");
        Bson filter = Filters.eq("organization", line);
        Document doc = col.find(filter).first();
        if (doc != null) {
            Certificate cert = mapper.readValue(doc.toJson(), Certificate.class);
            consoleHelper.showMessage("Результат: " + cert.toString());
        } else {
            consoleHelper.showMessage("Документ не найден!");
        }
    }

    public void findLike() throws IOException {
        consoleHelper.showMessage("Введите часть наименования организации: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "cp866"));
        String line = "";
        while (!line.equals("back")) {
            line = reader.readLine();
            if (!line.equals("back")) {
                findDocumentLike(line);
            }
        }
        consoleHelper.showMessage("Вы находитесь в гланом меню...");
    }

    private void findDocumentLike(String line) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MongoCollection<Document> col = connect.db.getCollection("certificates");
        Bson filter = Filters.eq("organization", Pattern.compile(line));
        FindIterable<Document> docs = col.find(filter);
        MongoCursor<Document> cursor = docs.iterator();
        if (cursor.hasNext()) {
            while (cursor.hasNext()) {
                Certificate cert = mapper.readValue(cursor.next().toJson(), Certificate.class);
                consoleHelper.showMessage(cert.toString());
            }
        } else {
            consoleHelper.showMessage("Документ не найден!");
        }
        cursor.close();
    }

    public void less() {
        ObjectMapper mapper = new ObjectMapper();
        MongoCollection<Document> col = connect.db.getCollection("certificates");
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        if (!cursor.hasNext()) {
            consoleHelper.showMessage("Сертификаты не найдены!");
        }
        try {
            while (cursor.hasNext()) {
                Certificate cert = mapper.readValue(cursor.next().toJson(), Certificate.class);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = simpleDateFormat.parse(cert.dateend);
                Date today = new Date();
                long milliseconds = date.getTime() - today.getTime();
                int days = (int) (milliseconds / (24 * 60 * 60 * 1000));
                if (days > 0 && days < 8) {
                    consoleHelper.showMessage(cert.toString());
                }
            }
        } catch (JsonMappingException e) {
            consoleHelper.showMessage("Ошибка чтения из базы данных: " + e.getMessage());
        } catch (JsonProcessingException e) {
            consoleHelper.showMessage("Ошибка чтения из базы данных: " + e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }
}
