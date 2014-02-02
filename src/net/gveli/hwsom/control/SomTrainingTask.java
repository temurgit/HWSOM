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

import java.text.DecimalFormat;
import java.util.List;
import javax.swing.SwingWorker;
import net.gveli.hwsom.model.Coordinates2D;
import net.gveli.hwsom.model.DelayInMilliseconds;
import net.gveli.hwsom.model.Network;
import net.gveli.hwsom.model.Node;
import net.gveli.hwsom.view.MainWindow;
import net.gveli.hwsom.view.Screen;

/**
 *
 * @author TEMUR
 */
public class SomTrainingTask extends SwingWorker<Object, Object> {

    private SomControl somControl;
    private DecimalFormat df = new DecimalFormat("#.####"); //DEBUG
    private ProgramControl programControl;

    @Override
    protected Object doInBackground() throws Exception {
        this.somControl = SomControl.getInstance();
        this.programControl = ProgramControl.getInstance();

        Network net = SomControl.getInstance().getNet();
        List<Node> inputs = SomControl.getInstance().getInputs();
        CyclicRandomIndexGenerator r = new CyclicRandomIndexGenerator(inputs.size());
        int trainerInd;
        int NUMBER_ITERATIONS = somControl.getNumberIterations();
        double wona = NUMBER_ITERATIONS / 100;
        long currentIteration = 0;

        somControl.normalizeNodeList(inputs);

        //training loop:
        while (net.getTime() < NUMBER_ITERATIONS) {
            trainerInd = r.nextRandomIndex();
            Node trainer = inputs.get(trainerInd);
//            System.out.println("\n#Step 2) chosed input for training: " + (trainerInd + 1));
            Coordinates2D bmuCoord = somControl.findBMU(trainer);
//            System.out.println("#Step 3) BMU found: " + bmuCoord);
            double neighborhoodRadius = somControl.radiusOfNeighborhood();
//            System.out.println("#Step 4) Radius of the neighborhood: " + neighborhoodRadius);
            somControl.changeWeightsInNeighbourhood(trainer, bmuCoord, neighborhoodRadius);
//            System.out.println("#Step 5) neighbours of the BMU are adjusted.");
            somControl.updateNetworkState();
            programControl.visualizeStep(bmuCoord, neighborhoodRadius);
            Thread.yield();
//            System.out.println("#Step 6) aprentisage: " + df.format(net.getAlpha()) + ", is last step(network_time = " + net.getTime() + ") ? end : goto #Step 2");
            currentIteration++;
            setProgress(((Double) (currentIteration / wona)).intValue());

            if (neighborhoodRadius < 1) {
                System.out.println("END... degree of training = " + df.format(net.getAlpha()) + "  network time: " + net.getTime() + " radius: " + neighborhoodRadius);
                break;
            }
            if (isCancelled()) {
                System.out.println("Canceled...");
                break;
            }
            if (ProgramControl.getInstance().isDebug()) {
                Thread.sleep(DelayInMilliseconds.getInstance().getDelay());
            }
        }

        return null;
    }

    @Override
    protected void process(List<Object> chunks) {
        super.process(chunks);
        Screen.getInstance().repaint();
    }

    @Override
    protected void done() {
        System.out.println("DONE Training!!!");
        if (!isCancelled()) {
            setProgress(99);
            MainWindow.getInstance().setConfigToggleSelected(false);
            setProgress(100);
            ProgramControl.getInstance().done();
        }
        super.done();
    }
}
