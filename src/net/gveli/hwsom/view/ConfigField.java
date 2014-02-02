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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author TEMUR
 */
public class ConfigField {

    protected JLabel label;
    protected JTextField textField;

    public void setEnabled(boolean enabled) {
        this.textField.setEnabled(enabled);
    }

    public ConfigField(String label, boolean enabled) {
        this.label = new JLabel(label, JLabel.TRAILING);
        textField = new JTextField(10);
        textField.setEnabled(enabled);
        this.label.setLabelFor(textField);
        textField.setDisabledTextColor(Color.darkGray);
    }

    public int getIntValue() throws NumberFormatException {
        return Integer.parseInt(textField.getText());
    }

    public void setIntValue(Integer x) {
        this.textField.setText(x.toString());
    }

    public void addToPanel(JPanel panel) {
        panel.add(this.label);
        panel.add(this.textField);
    }
}