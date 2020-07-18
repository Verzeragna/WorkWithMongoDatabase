package frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

public class Frame extends JFrame {

    private final Image ICON_IMAGE = Toolkit.getDefaultToolkit().getImage(new File("").getAbsolutePath() + "\\ic_image.png");
    private TrayIcon trayIcon;

    public void run() {
        Frame frame = new Frame();
        frame.setVisible(true);
        frame.setIconImage(ICON_IMAGE);
        frame.addWindowListener(new WindowListener() {

            public void windowActivated(WindowEvent event) {
            }

            public void windowClosed(WindowEvent event) {
            }

            public void windowDeactivated(WindowEvent event) {
            }

            public void windowDeiconified(WindowEvent event) {
            }

            public void windowIconified(WindowEvent event) {
            }

            public void windowOpened(WindowEvent event) {
            }

            public void windowClosing(final WindowEvent event) {
                // TODO Auto-generated method stub
                event.getWindow().setVisible(false);
                final SystemTray systemTray = SystemTray.getSystemTray();

                PopupMenu trayPopupMenu = new PopupMenu();

                trayIcon = new TrayIcon(ICON_IMAGE, "Certificates", trayPopupMenu);

                trayIcon.setImageAutoSize(true);
                try {
                    systemTray.add(trayIcon);
                } catch (AWTException awtException) {
                    awtException.printStackTrace();
                }
            }

        });
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}


