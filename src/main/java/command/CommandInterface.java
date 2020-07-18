package command;

import java.io.IOException;

public interface CommandInterface {
    void insert() throws IOException;
    void delete() throws IOException;
    void update() throws IOException;
    void showAll() throws IOException;
    void find() throws IOException;
    void findLike() throws IOException;
    void less() throws IOException;
}
