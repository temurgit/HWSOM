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

/**
 *
 * @author TEMUR
 *
 * Configuration for Network
 */
public class NetworkConfig {
    // <editor-fold defaultstate="collapsed" desc="static _ getInstance(); //Singleton Patern....">

    private static NetworkConfig _instance;

    synchronized public static NetworkConfig getInstance() {
        if (_instance == null) {
            _instance = new NetworkConfig();
        }
        return _instance;
    }

    private NetworkConfig() {
    }
    // </editor-fold>
    private int nIn;
    private int xOut;	//nb. de colonnes
    private int yOut;	//nb. de lignes
    private int nOut;	//nb. de neurones

    synchronized public int getnIn() {
        return nIn;
    }

    synchronized public void setnIn(int nIn) {
        this.nIn = nIn;
    }

    synchronized public int getxOut() {
        return xOut;
    }

    synchronized public void setxOut(int xOut) {
        this.xOut = xOut;
    }

    synchronized public int getyOut() {
        return yOut;
    }

    synchronized public void setyOut(int yOut) {
        this.yOut = yOut;
    }

    synchronized public int getnOut() {
        return nOut;
    }

    synchronized public void setnOut(int nOut) {
        this.nOut = nOut;
    }

    @Override
    synchronized public String toString() {
        return "n_in(nIn): " + nIn + "; x_out(xOut): " + xOut + "; y_out(yOut): " + yOut + "; n_out(nOut): " + nOut;
    }
}
