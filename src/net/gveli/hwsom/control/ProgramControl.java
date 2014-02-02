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
package net.gveli.hwsom.control;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import net.gveli.hwsom.model.Coordinates2D;
import net.gveli.hwsom.model.NetworkConfig;
import net.gveli.hwsom.view.About;
import net.gveli.hwsom.view.ConfigField;
import net.gveli.hwsom.view.MainWindow;
import net.gveli.hwsom.view.Screen;

/**
 *
 * @author TEMUR
 */
public class ProgramControl {

    // <editor-fold defaultstate="collapsed" desc="static _ getInstance(); //Singleton Patern....">
    private static ProgramControl _instance;

    public static ProgramControl getInstance() {
        if (_instance == null) {
            _instance = new ProgramControl();
        }
        return _instance;
    }
    // </editor-fold>
    private boolean debug;
    private SwingWorker task;
    private boolean done;
    Preferences myPreferences;

    private ProgramControl() {
        super();
        System.out.println("ProgramControl constructor!!");
        myPreferences = Preferences.systemNodeForPackage(ProgramControl.class);
    }

    public SwingWorker getTask() {
        return task;
    }

    public boolean isDone() {
        return done;
    }

    public void exitMenuItemActionPerformed(ActionEvent evt) {
        System.out.println("Bye!");
        System.exit(0);
    }

    public void aboutMenuItemActionPerformed(ActionEvent evt, JFrame aThis) {
        System.out.println("Show About Dialog\n");
        new About(aThis).setVisible(true);
    }

    /**
     *
     * @param labels
     */
    public boolean configToggleButtonSelectedAction(Map<String, ConfigField> labels) {
        NetworkConfig ntcfg = NetworkConfig.getInstance();
        int x, y;
        try {
            ntcfg.setnIn(labels.get("n_in").getIntValue());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Please enter number of training inputs first...",
                    "Il n'y a pas nombre des capteurs",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ntcfg.getnIn() < 1 || ntcfg.getnIn() > 16500000) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Please enter inputs number in range [1,16500000]",
                    "Number of inputs out of range!",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            x = labels.get("x_out").getIntValue();
            y = labels.get("y_out").getIntValue();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(MainWindow.getInstance(),
                    "Please input only INTEGERS",
                    "NumberFormatException",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        ntcfg.setxOut(x);
        ntcfg.setyOut(y);
        ntcfg.setnOut(x * y);
        labels.get("n_out").setIntValue(ntcfg.getnOut());
        System.out.println("ntcfg: " + ntcfg);
        SomControl.getInstance().setNetConf(ntcfg);
        return true;
    }

    public void initSomJButtonActionPerformed() {
        System.out.println("initialise SOM!");

        task = new InputsInitialiser();
        this.runTask();
    }

    public void trainingSomJButtonActionPerformed() {
        System.out.println("Training!");

        task = new SomTrainingTask();
        this.runTask();
    }

    private void runTask() {
        MainWindow.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final JProgressBar progressBar = MainWindow.getInstance().getProgressBar();
        done = false;
        task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!done) {
                    int progress = task.getProgress();
                    progressBar.setValue(progress);
                    MainWindow.getInstance().setProgressText(String.format(
                            "Completed %d%% of task.\n", progress));
                }
            }
        });
        task.execute();
        MainWindow.getInstance().getCancelButton().setEnabled(true);
    }

    void visualizeStep(Coordinates2D bmuCoord, double neighborhoodRadius) {
        if (debug) {
            Screen.getInstance().setDebugData(bmuCoord, neighborhoodRadius);
        }
        Screen.getInstance().repaint();
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void debugDelayActionPerformed() {
        System.out.println("Set Debug Delay Action");
    }

    void setTrainingButtonEnabled(boolean b) {
        MainWindow.getInstance().setTrainingButtonEnabled(b);
    }

    public void cancelTask() {
        if (this.task != null) {
            this.task.cancel(true);
        }
        done();
    }

    public void done() {
        //Tell progress listener to stop updating progress bar.
        done = true;
        Toolkit.getDefaultToolkit().beep();
        MainWindow.getInstance().getCancelButton().setEnabled(false);
        MainWindow.getInstance().setCursor(null); //turn off the wait cursor
        MainWindow.getInstance().getProgressBar().setValue(MainWindow.getInstance().getProgressBar().getMinimum());
        MainWindow.getInstance().setProgressText("Done! ");
    }

    public void savePictureActionPerformed(JMenuItem source) {
        if (!SomControl.getInstance().isNeedDraw()) {
            JOptionPane.showMessageDialog(source,
                    "Please first train network and / or  initialize\n"
                    + " at least, to save an image from the network.",
                    "There is nothing to save...",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isTaskRuning()) {
            JOptionPane.showMessageDialog(source,
                    "Network is under edition...",
                    "Not yet supported",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        new CreateNetImage().execute();
    }

    void saveImage(BufferedImage image) {
        String currentDir = myPreferences.get("CurrentDir", null);
        if ("null".equalsIgnoreCase(currentDir)) {
            currentDir = null;
        }
        JFileChooser fileChooser = new JFileChooser((currentDir == null) ? null : new File(currentDir));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setApproveButtonText("Save Image");
        fileChooser.setDialogTitle("Save a snapshot of SOM network");
        fileChooser.setSelectedFile(new File("ScreenShot.png"));
        File outputfile = new File(fileChooser.getCurrentDirectory().getAbsolutePath() + "/ScreenShot.png");
        if (outputfile.exists()) {
            outputfile = getNumberedFile(outputfile);
            System.out.println("outp: " + outputfile.getAbsolutePath());
        }
        fileChooser.setSelectedFile(outputfile);
        int result = fileChooser.showOpenDialog(MainWindow.getInstance());
        if (result == JFileChooser.APPROVE_OPTION) {
            outputfile = fileChooser.getSelectedFile();
            String filename = outputfile.getName();
            myPreferences.put("CurrentDir", outputfile.getParent());
            System.out.println("chosed file: " + outputfile.getName());
            if (!filename.endsWith(".png") && !filename.endsWith(".PNG")) {
                filename += ".png";
                outputfile = new File(outputfile.getAbsoluteFile() + ".png");
                System.out.println("save to file: " + filename);
            }
            if (outputfile.exists()) {
                int choise = JOptionPane.showConfirmDialog(
                        fileChooser,
                        "Overwrite file \"" + outputfile.getName()+"\" ?",
                        "File already exists!",
                        JOptionPane.OK_CANCEL_OPTION);
                if (choise != JOptionPane.OK_OPTION) {
                    System.out.println("Save image is canceled...");
                    return;
                }
            }
            try {
                ImageIO.write(image, "png", outputfile);
                System.out.println("image sadved: " + outputfile.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(ProgramControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Save image is canceled...");
        }

    }

    private File getNumberedFile(File outputfile) {
        int a, b;
        ArrayList<Integer> nums = new ArrayList<>();
        File fileDir = outputfile.getParentFile();
        for (File f : fileDir.listFiles()) {
            a = f.getName().lastIndexOf("(");
            b = f.getName().lastIndexOf(")");
            if (a < b && !(a < 0 || b < 0)) {
                try {
                    nums.add(Integer.parseInt(f.getName().substring(a + 1, b)));
                } catch (NumberFormatException ex) {
                    System.err.println("Number Format Exepion while parsing: " + f.getName());
                }
            }
        }
        Collections.sort(nums);
        String filename;
        if (nums.isEmpty()) {
            filename = "ScreenShot(1).png";
        } else {
            a = nums.get(nums.size() - 1) + 1;
            filename = "ScreenShot(" + a + ").png";
        }
        return new File(outputfile.getParentFile().getAbsolutePath() + "/" + filename);
    }

    public boolean isTaskRuning() {
        return task != null && !task.isDone();
    }
}
