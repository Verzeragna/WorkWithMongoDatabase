import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import selectmode.Mode;
import selectmode.ModeInterface;

public class Main {
    @Parameter(names = "-mode", description = "Work mode")
    private int mode;
    @Parameter(names = "-path", description = "Path to upload file")
    private String path;
    ModeInterface selectMode = new Mode();

    public static void main(String[] args) {
        Main arg = new Main();
        JCommander.newBuilder()
                .addObject(arg)
                .build()
                .parse(args);
        arg.run();
    }
    void run(){
        selectMode.start(mode);
    }
}
