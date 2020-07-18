package command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
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

public class SelectCommand implements SelectCommandInterface {
    private final ConsoleHelper consoleHelper = new ConsoleHelper();
    private final CommandInterface command = new DoCommand();

    public void runCommand(String parameter) throws IOException {
        if (parameter.equals("insert")) {
            consoleHelper.showMessage("Для возвращения в главное меню изпользуйте команду 'back'");
            command.insert();
        }
        if (parameter.equals("delete")) {
            consoleHelper.showMessage("Для возвращения в главное меню изпользуйте команду 'back'");
            command.delete();
        }
        if (parameter.equals("update")) {
            consoleHelper.showMessage("Для возвращения в главное меню изпользуйте команду 'back'");
            command.update();
        }
        if (parameter.equals("show all")) {
            command.showAll();
        }
        if (parameter.equals("find")) {
            command.find();
        }
        if (parameter.equals("find like")) {
            command.findLike();
        }
        if (parameter.equals("less")) {
            command.less();
        }
    }
}
