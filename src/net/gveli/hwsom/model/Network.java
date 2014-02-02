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
package net.gveli.hwsom.model;

import net.gveli.hwsom.control.SomControl;

/**
 *
 * @author TEMUR
 */
public class Network {

    private double alpha;	//coefficient d'apprentissage
    private Node[][] nodes;
    private int time;
    private double mapRadius;

    public Network(int w, int h) {
        time = 0;
        mapRadius = Math.max(w, h) / 2;
        alpha = SomControl.getInstance().getAlphaInirial();
    }

    synchronized public double getAlpha() {
        return alpha;
    }

    synchronized public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    synchronized public int getTime() {
        return time;
    }

    synchronized public void setTime(int time) {
        this.time = time;
    }

    synchronized public Node[][] getNodes() {
        return nodes;
    }

    public double getMapRadius() {
        return mapRadius;
    }

    synchronized public void setNodes(Node[][] nodes) {
        this.nodes = nodes;
    }
}
