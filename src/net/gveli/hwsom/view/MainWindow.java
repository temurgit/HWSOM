/**
 * Copyright  2013  Temur Vibliani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.gveli.hwsom.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.gveli.hwsom.control.ProgramControl;
import net.gveli.hwsom.control.SomControl;
import net.gveli.hwsom.model.DelayInMilliseconds;

/**
 * @author TEMUR
 */
public class MainWindow extends JFrame {

    private static MainWindow _instance;
    private ProgramControl controll;
    private Screen screen;
    private TrainingControlsPanel trainingControls;
    private JMenuItem ExitMenuItem;
    private JMenuItem aboutMenuItem;
    private JMenu fileMenu;
    private JCheckBoxMenuItem pauseIterationsCheckBoxMenuItem;
    private JMenuItem debugDelay;
    private JMenu helpMenu;
    private JMenuBar menuBar;
    private static int programControllPanelHeight = 165;
    private static int programControllPanelWidth = 30;
    private JProgressBar progressBar;
    private JButton cancelButton;
    private JLabel progressLabel;
    private JMenuItem setNumIterationsMenuItem;
    private JSlider speedControlSlider;
    private JPanel sliderPanel;

    synchronized public JButton getCancelButton() {
        return cancelButton;
    }

    public static int getProgramControllPanelWidth() {
        return programControllPanelWidth;
    }

    public static int getProgramControllPanelHeight() {
        return programControllPanelHeight;
    }

    private MainWindow() {
        this.controll = ProgramControl.getInstance();
        initComponents();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        _instance = this;//fseudo singletone
        setIconImage((new ImageIcon(MainWindow.class.getResource("/resources/icon.png")).getImage()));
    }

    public static MainWindow getInstance() {
        //fseudo singletone
        return _instance;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Som HW Starts!");
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            javax.swing.UIManager.LookAndFeelInfo[] installedLookAndFeels = javax.swing.UIManager.getInstalledLookAndFeels();
            for (int idx = 0; idx < installedLookAndFeels.length; idx++) {
                if ("Nimbus".equals(installedLookAndFeels[idx].getName())) {
                    javax.swing.UIManager.setLookAndFeel(installedLookAndFeels[idx].getClassName());
                    programControllPanelWidth = 45;
                    programControllPanelHeight = 217;
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    private void initComponents() {
        System.out.println("initialize main window...");

        menuBar = new JMenuBar();
        menuBar.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        fileMenu = new JMenu();
        ExitMenuItem = new JMenuItem();
        helpMenu = new JMenu();
        aboutMenuItem = new JMenuItem();
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("SOM - Self-Organizing Maps *1982 *Teuvo Kohonen");

        fileMenu.setText("File");

        pauseIterationsCheckBoxMenuItem = new JCheckBoxMenuItem("Pausing of iterations", true);

        controll.setDebug(pauseIterationsCheckBoxMenuItem.isSelected());
        pauseIterationsCheckBoxMenuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                controll.setDebug(((JCheckBoxMenuItem) e.getItem()).isSelected());
                System.out.println("Pausing of iterations = " + controll.isDebug());
                sliderPanel.setVisible(controll.isDebug());
                DelayInMilliseconds.getInstance().setDelay(speedControlSlider.getValue());
            }
        });

        fileMenu.add(pauseIterationsCheckBoxMenuItem);
        setNumIterationsMenuItem = new JMenuItem("Set Number Iterations");
        setNumIterationsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int minIterations = 100, maxIterations = 2000000000;
                String input = JOptionPane.showInputDialog(
                        setNumIterationsMenuItem,
                        "Currently Number of iterations in \ntraining loop is set to "
                        + SomControl.getInstance().getNumberIterations()
                        + "\n\n\nEnter new Number of iterations\nin ragne [ " + minIterations + "; " + maxIterations + " ] :",
                        "Set new value for Number of Iterations",
                        JOptionPane.OK_CANCEL_OPTION);
                if (input != null) {
                    Integer n;
                    try {
                        n = Integer.parseInt(input);
                        if (n < minIterations || n > maxIterations) {
                            JOptionPane.showMessageDialog(
                                    setNumIterationsMenuItem,
                                    "value must be in range [ " + minIterations + "; " + maxIterations + " ]",
                                    "Number Iterations is out of range",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            SomControl.getInstance().setNUMBER_ITERATIONS(n);
                            System.out.println("Nubmer Iterations is set to: " + SomControl.getInstance().getNumberIterations());
                        }
                    } catch (NumberFormatException err) {
                        JOptionPane.showMessageDialog(
                                setNumIterationsMenuItem,
                                "Eroor in number format!",
                                "Error in number format\n(value must be in range [ " + minIterations + "; " + maxIterations + " ]):",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        fileMenu.add(setNumIterationsMenuItem);
        fileMenu.add(new Separator());

        JMenuItem savePicture = new JMenuItem("Save Network's snapshoot");
        savePicture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePictureActionPerformed(e);
            }
        });
        fileMenu.add(savePicture);
        fileMenu.add(new Separator());
        ExitMenuItem.setText("Exit");
        ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(ExitMenuItem);
        
        fileMenu.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        for (Component mi : fileMenu.getMenuComponents()) {
            mi.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        menuBar.add(fileMenu);
        
        helpMenu.setText("Help");
        aboutMenuItem.setText("About");
        aboutMenuItem.setToolTipText("");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);
        
        helpMenu.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        for (Component mi : helpMenu.getMenuComponents()) {
            mi.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        setSize(new Dimension(800, 600));
        getContentPane().setLayout(new BorderLayout());

        screen = Screen.getInstance();
        getContentPane().add(screen);

        trainingControls = new TrainingControlsPanel();
        getContentPane().add(trainingControls, "North");
        //----------------------------------------------
        JPanel footer = new JPanel(new BorderLayout());//new HorizBagLayout(2));
        JPanel proggressPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        progressLabel = new JLabel();
        proggressPanel.add(progressLabel);
        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        proggressPanel.add(progressBar);
        cancelButton = new JButton("Cancel");
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProgramControl.getInstance().cancelTask();
                MainWindow.getInstance().trainingControls.setConfigToggleSelected(false);
            }
        });
        cancelButton.setEnabled(false);
        proggressPanel.add(cancelButton);
        footer.add(proggressPanel);
        //----------------------------
        sliderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sliderPanel.setVisible(pauseIterationsCheckBoxMenuItem.isSelected());
        speedControlSlider = new JSlider(1, 701, 17);
        speedControlSlider.setInverted(true);
        speedControlSlider.setMajorTickSpacing(50);
        speedControlSlider.setMinorTickSpacing(25);
        speedControlSlider.setPaintTicks(true);
        speedControlSlider.setPaintTrack(false);
        speedControlSlider.addChangeListener(new ChangeListener() {
            JSlider source;
            DelayInMilliseconds del = DelayInMilliseconds.getInstance();

            @Override
            public void stateChanged(ChangeEvent e) {
                source = (JSlider) e.getSource();
                int delay = (int) source.getValue();
                del.setDelay(delay);
                if (!source.getValueIsAdjusting()) {
                    System.out.println("Pausing iterations by " + delay + " miliseconds.");
                }
            }
        });

        JLabel speedLabel = new JLabel("Speed control: ");
        speedLabel.setLabelFor(speedControlSlider);
        sliderPanel.add(speedLabel);
        sliderPanel.add(speedControlSlider);
        footer.add(sliderPanel, BorderLayout.WEST);

        footer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        getContentPane().add(footer, BorderLayout.SOUTH);

    }
    // <editor-fold defaultstate="collapsed" desc="Simple Redirects to ProgramControl...">

    private void aboutMenuItemActionPerformed(ActionEvent evt) {
        controll.aboutMenuItemActionPerformed(evt, this);
    }

    private void exitMenuItemActionPerformed(ActionEvent evt) {
        controll.exitMenuItemActionPerformed(evt);
    }

    private void savePictureActionPerformed(ActionEvent e) {
        controll.savePictureActionPerformed((JMenuItem) e.getSource());
    }
    //</editor-fold>

    public void setTrainingButtonEnabled(boolean b) {
        this.trainingControls.setTrainingSomEnabled(b);

    }

    public void setConfigToggleSelected(boolean b) {
        this.trainingControls.setConfigToggleSelected(b);

    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressText(String status) {
        progressLabel.setText(status);
    }
}
