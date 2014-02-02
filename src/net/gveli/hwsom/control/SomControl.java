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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.gveli.hwsom.model.Coordinates2D;
import net.gveli.hwsom.model.Network;
import net.gveli.hwsom.model.NetworkConfig;
import net.gveli.hwsom.model.Node;
import net.gveli.hwsom.view.Screen;

/**
 *
 * @author TEMUR
 */
public class SomControl {

    // <editor-fold defaultstate="collapsed" desc="static _ getInstance(); //Singleton Patern....">
    private static SomControl _instance;

    public static SomControl getInstance() {
        if (_instance == null) {
            _instance = new SomControl();
        }
        return _instance;
    }
    //</editor-fold>
    private NetworkConfig netConf;
    private List<Node> inputs;
    private Network net;
    private Integer NUMBER_ITERATIONS = 10000;
    private double alphaInirial = 0.9;
    private boolean needDraw;
    private Double tc = null;
    private DecimalFormat df = new DecimalFormat("#.####"); //DEBUG

    private SomControl() {
        System.out.println("SomControl constructor!!");
        needDraw = false;
    }

    public double getAlphaInirial() {
        return alphaInirial;
    }

    synchronized public int getNumberIterations() {
        return NUMBER_ITERATIONS;
    }

    synchronized public void setNUMBER_ITERATIONS(Integer NUMBER_ITERATIONS) {
        this.NUMBER_ITERATIONS = NUMBER_ITERATIONS;
    }

    synchronized public boolean isNeedDraw() {
        return needDraw;
    }

    synchronized public Network getNet() {
        return net;
    }

    public NetworkConfig getNetConf() {
        return netConf;
    }

    public void setNetConf(NetworkConfig netConf) {
        this.netConf = netConf;
    }

    public List<Node> getInputs() {
        if (inputs == null) {
            inputs = Collections.synchronizedList((List) new ArrayList<Node>());
        }
        return inputs;
    }

    synchronized public void setNet(Network net) {
        this.net = net;
        if (net != null) {
            needDraw = true;
            Screen.getInstance().repaint();
            System.out.println("#Step 1) Network is inicialized.");
        } else {
            System.gc();
        }
    }

    /**
     * Radius of the neighborhood for current step.
     *
     * @return Radius of neighborhood
     */
    double radiusOfNeighborhood() {
//        return net.getMapRadius() * Math.pow(Math.E, (-net.getTime() / getTimeConstant()));
        return net.getMapRadius() * (1.0d - (((double) net.getTime()) / ((double) NUMBER_ITERATIONS)));
    }

    /**
     * Find the Best Match Unit (BMU) in the network, for given Node(- training
     * input's node...)
     *
     * @param input node to search BMU for...
     * @return BMU..
     */
    public Coordinates2D findBMU(Node input) {
        Node[][] nodes = net.getNodes();
        ArrayList<Node> bmuList = new ArrayList();
        double minDistance = euclideanDistancePow2(input, nodes[0][0]);
        bmuList.add(nodes[0][0]);

        double tmpDistance;
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                tmpDistance = euclideanDistancePow2(input, nodes[i][j]);
                if (minDistance > tmpDistance) {
                    bmuList.clear();
                    minDistance = tmpDistance;
                    bmuList.add(nodes[i][j]);
                } else if (minDistance == tmpDistance) {
                    bmuList.add(nodes[i][j]);
                }
            }
        }
        Random r = new Random();
        return bmuList.get(r.nextInt(bmuList.size())).getCoordinates();
    }

    /**
     * Euclidean distance^2 (squared)
     *
     * @param a
     * @param b
     * @return distance^2 (squared) between a and b
     */
    double euclideanDistancePow2(Node a, Node b) {
        double ans = 0;
        double[] weightsA = a.getWeights();
        double[] weightsB = b.getWeights();
        for (int i = 0; i < weightsA.length; i++) {
            ans += Math.pow(weightsA[i] - weightsB[i], 2);
        }
        return ans;
    }

    double distancePow2(Coordinates2D a, Coordinates2D b) {
        return Math.pow((a.getX() - b.getX()), 2)
                + Math.pow((a.getY() - b.getY()), 2);
    }

    public void changeWeightsInNeighbourhood(Node trainer, Coordinates2D bmuCoord, double neighborhoodRadius) {
        double[] trainerWeights = trainer.getWeights();
        Node[][] nodes = net.getNodes();

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                if (isInNeighbourhood(bmuCoord, new Coordinates2D(i, j), neighborhoodRadius)) {
                    nodes[i][j].setWeights(newWeights(nodes[i][j].getWeights(), trainerWeights));
                    //System.out.println("+included i=" + i + ", j=" + j + " Bmu Dist^2: " + df.format(distFromBmuPow2) + " radius^2: " + df.format(neighborhoodRadiusPow2) + " radius: " + neighborhoodRadius);
                } else {
                    //System.out.println("-excluded i=" + i + ", j=" + j + " Bmu Dist^2: " + df.format(distFromBmuPow2) + " radius^2: " + df.format(neighborhoodRadiusPow2) + " radius: " + neighborhoodRadius);
                }
            }
        }
    }

    private boolean isInNeighbourhood(Coordinates2D bmuCoord, Coordinates2D coordinates, double neighborhoodRadius) {
        int bmuX = bmuCoord.getX();
        int bmuY = bmuCoord.getY();
        int nodeX = coordinates.getX();
        int nodeY = coordinates.getY();
        if ((Math.abs(bmuX - nodeX) > neighborhoodRadius) || (Math.abs(bmuY - nodeY) > neighborhoodRadius)) {
            return false;
        }
        return true;
    }

    private double[] newWeights(double[] weights, double[] trainerWeights) {
        double[] ans = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            ans[i] = weights[i]
                    + net.getAlpha()
                    * (trainerWeights[i] - weights[i]);
        }
        return ans;
    }

    public void updateNetworkState() {
        net.setTime(net.getTime() + 1);
        net.setAlpha(alphaInirial * (1.0d - (((double) net.getTime()) / ((double) NUMBER_ITERATIONS))));
    }

    public void normalizeNodeList(List<Node> inputs) {
        Node tmp;
        double[] tmpWeights;
        double d;
        for (int i = 0; i < inputs.size(); i++) {
            tmp = inputs.get(i);
            d = getLen(tmp.getWeights());
            tmpWeights = new double[3];
            for (int j = 0; j < tmpWeights.length; j++) {
                tmpWeights[j] = tmp.getWeights()[j] / d;
            }
            tmp.setWeights(tmpWeights);
        }

    }

    private double getLen(double[] v) {
        double ans = 0;
        for (int i = 0; i < v.length; i++) {
            ans += Math.pow(v[i], 2);
        }
        return Math.sqrt(ans);
    }

    private double getTimeConstant() {
        return (tc == null) ? initTc() : tc;
    }

    private Double initTc() {
        this.tc = ((double) NUMBER_ITERATIONS) / net.getMapRadius();
        return this.tc;
    }
}
