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
public class TrainerNode extends Node {

    private Color rgbColor;
    int hashCode;

    public TrainerNode(double[] weights, Coordinates2D coordinates) {
        super(weights, coordinates);
        this.hashCode = 0;
        for (int i = 0; i < weights.length; i++) {
            this.hashCode += (((Double) (weights[i] * 1000)).intValue() * Math.pow(1000, i));
        }
    }

    public Color getRgbColor() {
        if (this.rgbColor == null) {
            this.rgbColor = new Color((float) weights[0], (float) weights[1], (float) weights[2]);
        }
        return rgbColor;
    }

    @Override
    public int hashCode() {
//        System.out.println("hashcode check!!!!!!!");
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
//            System.out.println("Node Comparation!!!");
            return this.getRgbColor().equals(((TrainerNode) obj).getRgbColor());
        }
        return false;
    }
}
