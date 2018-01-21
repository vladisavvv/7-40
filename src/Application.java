import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Application extends JFrame {
    JButton selectFile;
    JLabel html;
    JScrollPane panelHtml;

    Application(String nameApplication) {
        super(nameApplication);

        selectFile = new JButton();
        selectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int i = fc.showOpenDialog(getParent());

                if (i == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    String filepath = f.getPath();

                    Game game = new Game(filepath);

                    List<String> report = game.getReport();

                    StringBuffer reportString = new StringBuffer();
                    for(String j : report) {
                        reportString.append(j + "\n");
                    }

                    selectFile.setVisible(false);

                    html = new JLabel();
                    html.setText(reportString.toString());
                    panelHtml = new JScrollPane(html);
                    add(panelHtml);
                    panelHtml.setSize(
                            panelHtml.getPreferredSize().width + 20,
                            (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.94)
                    );

                    //copy to clipboard
                    StringSelection stringSelection = new StringSelection(reportString.toString());
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(stringSelection, null);

                    setPreferredSize(
                            new Dimension(panelHtml.getPreferredSize().width + 20,
                            (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.95))
                    );
                    pack();
                }
            }
        });
        selectFile.setText("Выбрать файл");

        add(selectFile);

        pack();
    }

    private List<String> loadingFile(String path) {
        List<String> lines = null;

        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {}

        return lines;
    }
}

