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

import java.awt.Color;

/**
 *
 * @author TEMUR
 */
public class Node {

    protected Coordinates2D coordinates;
    protected double[] weights;

    public Node(double[] weights, Coordinates2D coordinates) {
        this.weights = weights;
        this.coordinates = coordinates;
    }

    public Coordinates2D getCoordinates() {
        return coordinates;
    }

    public double[] getWeights() {
//        for (int i = 0; i < weights.length; i++) {
//            System.out.println("get - R: " + weights[0] + ", G: " + weights[1] + ", B: " + weights[2]);
//        }
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
//        for (int i = 0; i < weights.length; i++) {
//            System.out.println("set - R: " + weights[0] + ", G: " + weights[1] + ", B: " + weights[2]);
//        }
    }

    public Color getColor() {
        return new Color((float) weights[0], (float) weights[1], (float) weights[2]);
    }
}
