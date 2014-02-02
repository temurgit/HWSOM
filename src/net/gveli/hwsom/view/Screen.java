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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import net.gveli.hwsom.control.ProgramControl;
import net.gveli.hwsom.control.SomControl;
import net.gveli.hwsom.model.Coordinates2D;
import net.gveli.hwsom.model.Node;

/**
 *
 * @author TEMUR
 */
public class Screen extends JLabel {
// <editor-fold defaultstate="collapsed" desc="static _ getInstance(); //Singleton Patern....">

    private static Screen _instance;

    public static Screen getInstance() {
        if (_instance == null) {
            _instance = new Screen();
        }
        return _instance;
    }
    // </editor-fold>
    private SomControl somControl;
    private ProgramControl programControl;
    private Graphics2D g2;
    private int w;
    private int h;
    private Coordinates2D bmu2D;
    private double neighbourRadius;

    private Screen() {
        setBorder(javax.swing.BorderFactory.createTitledBorder("SOM Network"));
        somControl = SomControl.getInstance();
        programControl = ProgramControl.getInstance();
    }

    synchronized public void setDebugData(Coordinates2D bmuCoord, double neighborhoodRadius) {
        this.bmu2D = bmuCoord;
        this.neighbourRadius = neighborhoodRadius;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (!somControl.isNeedDraw()) {
            return;
        }
        w = getWidth();
        h = getHeight();
        Node[][] nodes = somControl.getNet().getNodes();
        int squareSize = Math.min(
                (w - 40) / nodes.length,
                (h - 50) / nodes[0].length);
        int startX = 20 + (((w - 40) - squareSize * nodes.length) / 2);
        int startY = 30 + (((h - 50) - squareSize * nodes[0].length) / 2);

        g2 = (Graphics2D) g;
        g2.setClip(startX, startY, nodes.length * squareSize, nodes[0].length * squareSize);
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                g2.setColor(nodes[i][j].getColor());
                g2.fillRect(i * squareSize + startX, j * squareSize + startY, squareSize, squareSize);
            }
        }
        if (!programControl.isDebug() || bmu2D == null || !programControl.isTaskRuning()) {
            return;
        }

        int debugX = bmu2D.getX() * squareSize + startX;
        int debugY = bmu2D.getY() * squareSize + startY;
        g2.setColor(Color.BLACK);
        g2.drawLine(
                debugX + squareSize / 2, debugY - squareSize * 2,
                debugX + squareSize / 2, debugY + squareSize * 3);

        g2.drawLine(
                debugX - squareSize * 2, debugY + squareSize / 2,
                debugX + squareSize * 3, debugY + squareSize / 2);
        
    }
}
