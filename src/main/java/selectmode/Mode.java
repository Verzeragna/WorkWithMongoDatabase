package selectmode;

import startmode.StartMode;
import startmode.StartModeInterface;

public class Mode implements ModeInterface {
    StartModeInterface startMode = new StartMode();
    public void start(int mode) {
        switch (mode){
            case 1:
                startMode.dataBaseMode();
                break;
            case 2:
                startMode.notifMode();
                break;
            default:
                break;
        }
    }
}
