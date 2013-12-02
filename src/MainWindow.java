import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindow extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.WARNING, null, e);
                }
                MainWindow mainWindow = new MainWindow();
                mainWindow.setResizable(false);
                mainWindow.setLayout(new BoxLayout(mainWindow.getContentPane(), BoxLayout.X_AXIS));
                JPanel controlPanel = new JPanel();
                controlPanel.setAlignmentY(Component.TOP_ALIGNMENT);
                controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
                JButton clear = new JButton("Clear");
                JButton load = new JButton("Load");
                final JButton start = new JButton("Start");
                final JButton step = new JButton("Step");

                controlPanel.add(clear);
                controlPanel.add(start);
                controlPanel.add(load);
                controlPanel.add(step);

                step.setEnabled(false);

                final MainPanel mainPanel = new MainPanel();

                clear.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mainPanel.clear();
                    }
                });


                start.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mainPanel.start();
                        step.setEnabled(true);
                    }
                });

                load.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mainPanel.load();
                    }
                });

                step.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mainPanel.step();
                    }
                });

                mainWindow.getContentPane().add(mainPanel);
                mainWindow.getContentPane().add(controlPanel);
                mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainWindow.pack();
                mainWindow.setVisible(true);
            }
        });
    }
}
