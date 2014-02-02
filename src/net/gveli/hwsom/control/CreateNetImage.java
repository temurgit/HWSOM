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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import net.gveli.hwsom.model.Node;

/**
 *
 * @author TEMUR
 */
public class CreateNetImage<T, V> extends SwingWorker<BufferedImage, String> {

    @Override
    protected BufferedImage doInBackground() throws Exception {
        Node[][] nodes = SomControl.getInstance().getNet().getNodes();

        int width = nodes.length * 2;
        int height = nodes[0].length * 2;
        int imageType = BufferedImage.TYPE_INT_ARGB;
        BufferedImage imageBuffer = new BufferedImage(width, height, imageType);
        Graphics2D g2 = imageBuffer.createGraphics();
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                g2.setColor(nodes[i][j].getColor());
                g2.fillRect(i * 2, j * 2, 2, 2);
            }
        }
        return imageBuffer;
    }

    @Override
    protected void done() {
        super.done();
        try {
            ProgramControl.getInstance().saveImage(get());
        } catch (InterruptedException ex) {
            Logger.getLogger(CreateNetImage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(CreateNetImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
