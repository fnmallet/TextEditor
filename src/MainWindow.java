import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MainWindow {
    private Frame frame;
    private JFileChooser fileChooser = new JFileChooser();
    private JScrollPane scrollPane;
    private JTextArea textArea = new JTextArea();
    private JMenuBar menuBar = new JMenuBar();
    private enum DialogAnswer {
        YES,
        NO,
        CANCEL
    }

    private Menu[] menus = new Menu[] {
            new Menu("File",
                    new String[] {"New", "Open", "Save", "Save as", "Exit"},
                    new ActionListener[] {
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    newFile();
                                }
                            },
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    open();
                                }
                            },
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    save();
                                }
                            },
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    saveAs();
                                }
                            },
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    exit();
                                }
                            }
                    }),
            new Menu("Edit",
                    new String[] {"Copy", "Paste"},
                    new ActionListener[] {
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    copy(textArea.getText());
                                }
                            },
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    paste();
                                }
                            }
                    }),
            new Menu("Help",
                    new String[] {"About"},
                    new ActionListener[] {
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {

                                }
                            }
                    })
    };
    public MainWindow() {
        initMenuBar();
        initTextArea();
        initFileChooser();
        initFrame();
    }

    private DialogAnswer showSaveConfirmDialog(String fileName, boolean replace) {
        String question;
        String[] options;
        int questionType;

        if(replace) {
            question = "\"" + fileName + "\"" + "already exists. Do you want to replace it ?";
            questionType = JOptionPane.WARNING_MESSAGE;
            options = new String[] {"Yes", "No"};
        } else {
            question = "Do you want to save changes to " + fileName + "?";
            questionType = JOptionPane.QUESTION_MESSAGE;
            options = new String[] {"Yes", "No", "Cancel"};
        }

        String selectedOption  = options[JOptionPane.showOptionDialog (frame,
                question,
                frame.getTitle(),
                JOptionPane.DEFAULT_OPTION,
                questionType,
                null,
                options,
                options[0]
        )];

        return switch (selectedOption) {
            case "Yes" -> DialogAnswer.YES;
            case "No" -> DialogAnswer.NO;
            default -> DialogAnswer.CANCEL;
        };
    }

    private void copy(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection data = new StringSelection(text);
        clipboard.setContents(data, data);
    }

    private void save() {
        if (fileChooser.getSelectedFile() == null) {
            if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                saveFile(fileChooser.getSelectedFile().getParent(), fileChooser.getSelectedFile().getName());
            }
        } else {
            saveFile(fileChooser.getSelectedFile().getParent(), fileChooser.getSelectedFile().getName());
        }
    }

    private void open() {
        DialogAnswer answer = showSaveConfirmDialog(frame.getFileName(), false);
        if(answer == DialogAnswer.YES) {
            save();
            if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
                openFile(fileChooser.getSelectedFile());
        } else if(answer == DialogAnswer.NO) {
            if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
                openFile(fileChooser.getSelectedFile());
        }

    }

    private void saveAs() {
        if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            saveFile(fileChooser.getSelectedFile().getParent(), fileChooser.getSelectedFile().getName());
        }
    }

    private void paste() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        try {
            Transferable t = clipboard.getContents(null);
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                String textToInsert = t.getTransferData(DataFlavor.stringFlavor).toString();
                if(this.textArea.getSelectedText() == null) {
                    this.textArea.insert(textToInsert, this.textArea.getCaretPosition());
                } else {
                    this.textArea.replaceSelection(textToInsert);
                }
            }

        } catch (Exception ignore) {
        }
    }

    private void initTextArea() {
        this.textArea.setLineWrap(false);
        this.scrollPane = new JScrollPane(this.textArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private void initFrame() {
        this.frame = new Frame(menuBar, scrollPane);

        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    exit();
            }
        });
    }

    private void initMenuBar() {
        Color backgroundColor = new Color(231, 231, 231);

        for(Menu menu : this.menus)
            this.menuBar.add(menu);

        this.menuBar.setBackground(backgroundColor);
    }

    private void initFileChooser() {
        FileNameExtensionFilter fileNameExtensionFilter =
                new FileNameExtensionFilter("Text documents (*.txt)","txt");
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.fileChooser.setFileFilter(fileNameExtensionFilter);
    }

    private void openFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            if (scanner.hasNextLine())
                this.textArea.append(scanner.nextLine());
            while (scanner.hasNextLine()) {
                this.textArea.append("\n");
                this.textArea.append(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        frame.setFileName(file.getName());
    }
    private void saveFile(String filePath, String fileName) {
        if(fileName.lastIndexOf(".txt") == -1)
            fileName += ".txt";

        File newFile = new File(filePath, fileName);

        try {
            if(!newFile.createNewFile()) {
                if(showSaveConfirmDialog(fileName, true) == DialogAnswer.NO)
                    return;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            FileWriter writer = new FileWriter(newFile);
            writer.write(this.textArea.getText());
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        frame.setFileName(fileName);
    }

    private void reset() {
        fileChooser.setSelectedFile(null);
        this.textArea.setText("");
        frame.setFileName("untitled.txt");
    }
    private void newFile() {
        DialogAnswer answer = showSaveConfirmDialog(frame.getFileName(), false);
        if(answer == DialogAnswer.YES) {
            save();
            reset();
        } else if(answer == DialogAnswer.NO)
            reset();
    }

    private void exit() {
        DialogAnswer answer = showSaveConfirmDialog(frame.getFileName(), false);
        if(answer == DialogAnswer.YES) {
            save();
            System.exit(0);
        } else if(answer == DialogAnswer.NO)
            System.exit(0);
    }
}
