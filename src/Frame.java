import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private String fileName;
    private String appName;

    public Frame(JMenuBar jMenuBar, JScrollPane jScrollPane) {
        this.appName = "Text Editor";
        this.fileName = "untitled.txt";
        int width = 640;
        int height = 480;

        setTitle(this.appName + " - " + this.fileName);
        this.setSize(width, height);
        this.setIconImage(Toolkit.getDefaultToolkit().createImage("resources/icon.png"));
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setJMenuBar(jMenuBar);
        this.getContentPane().add(jScrollPane);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.setTitle(this.appName + " - " + this.fileName);
    }

    public String getFileName() {
        return this.fileName;
    }
}
