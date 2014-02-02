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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import net.gveli.hwsom.model.Coordinates2D;
import net.gveli.hwsom.model.Network;
import net.gveli.hwsom.model.NetworkConfig;
import net.gveli.hwsom.model.Node;
import net.gveli.hwsom.model.TrainerNode;
import net.gveli.hwsom.view.MainWindow;

/**
 *
 * @author TEMUR
 */
public class InputsInitialiser<T, V> extends SwingWorker<Network, String> {

    @Override
    protected Network doInBackground() throws Exception {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        System.out.println("\nAllocation of memory is starts.");
        System.out.println("Total available memory: " + runtime.totalMemory() / mb + " mb.");
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb + " mb.");
        System.out.println("Free Memory:" + runtime.freeMemory() / mb + " mb.");
        System.out.println("Max Memory:" + runtime.maxMemory() / mb + " mb.");
        setProgress(0);
        NetworkConfig netConf = NetworkConfig.getInstance();
        int nIn = netConf.getnIn();
        int w = netConf.getxOut();
        int h = netConf.getyOut();
        double wona = (netConf.getnIn() + netConf.getnOut()) / 100;
        int currentIteration = 0;
        Network net = new Network(w, h);
        Node[][] nodes = new Node[w][h];
        try {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    nodes[i][j] = new Node(new double[]{Math.random(), Math.random(), Math.random()}, new Coordinates2D(i, j));
                    currentIteration++;
                    setProgress(((Double) (currentIteration / wona)).intValue());
//                System.out.println("progres: "+getProgress());
                }
                if (this.isCancelled()) {
                    System.out.println("Canceled...");
                    break;
                }
            }
        } catch (java.lang.OutOfMemoryError er) {
            JOptionPane.showMessageDialog(
                    MainWindow.getInstance().getContentPane(),
                    "java.lang.OutOfMemoryError: Java heap space\n"
                    + "no more memory available to the program \n"
                    + "please run the program with bigger heap size or \n"
                    + "use less dimensions for network..",
                    "java.lang.OutOfMemoryError",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

//        normalize network:
        ArrayList<Node> tmp;
        for (int i = 0; i < nodes.length; i++) {
            tmp = new ArrayList<>();
            Collections.addAll(tmp, nodes[i]);
            SomControl.getInstance().normalizeNodeList(tmp);
        }
//      ---------------------
        net.setNodes(nodes);
        System.out.println("network is alocated! used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb + " mb.");

        List<Node> inputs = SomControl.getInstance().getInputs();
        HashSet<TrainerNode> tmpInputSet = new HashSet<>(nIn);
        try {
            for (int i = 0; i < nIn; i++) {
                if (tmpInputSet.add(new TrainerNode(new double[]{Math.random(), Math.random(), Math.random()}, null))) {
                    currentIteration++;
                    setProgress(((Double) (currentIteration / wona)).intValue());
                } else {
                    i--;
                }
//            System.out.println("progres: " + getProgress() + ",  i:" + i);
                if (this.isCancelled()) {
                    System.out.println("Canceled...");
                    break;
                }
            }
        } catch (java.lang.OutOfMemoryError er) {
            SomControl.getInstance().setNet(null);
            JOptionPane.showMessageDialog(
                    MainWindow.getInstance().getContentPane(),
                    "java.lang.OutOfMemoryError: Java heap space\n"
                    + "no more memory available to the program \n"
                    + "please run the program with bigger heap size or \n"
                    + "use less number of inputs",
                    "java.lang.OutOfMemoryError",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        inputs.clear();
        inputs.addAll(tmpInputSet);
        System.out.println("Inputs are initialized,  size = " + inputs.size() + "  used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / mb + " mb.");
        return net;
    }

    @Override
    protected void done() {
        System.out.println("Initialisation DONE!!!");
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb + " mb.");
        System.out.println("Free Memory:" + runtime.freeMemory() / mb + " mb.");

        if (!isCancelled()) {
            try {
                setProgress(99);
                SomControl.getInstance().setNet(get());
                ProgramControl.getInstance().setTrainingButtonEnabled(true);
                setProgress(100);
                ProgramControl.getInstance().done();
            } catch (ExecutionException ex) {
                SomControl.getInstance().setNet(null);
                if (ex.getCause() instanceof OutOfMemoryError) {
                    System.out.println("Out Of Memory...");
                    SomControl.getInstance().setNet(null);
                    JOptionPane.showMessageDialog(
                            MainWindow.getInstance().getContentPane(),
                            "java.lang.OutOfMemoryError: Java heap space\n"
                            + "no more memory available to the program \n"
                            + "please run the program with bigger heap size or \n"
                            + "use less number of inputs and/or less dimensions for network",
                            "java.lang.OutOfMemoryError",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                Logger.getLogger(InputsInitialiser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(InputsInitialiser.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            setProgress(0);
        }
        super.done();
    }
}
