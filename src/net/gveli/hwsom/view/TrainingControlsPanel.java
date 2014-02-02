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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import net.gveli.hwsom.control.ProgramControl;
import net.java.sample.layout.SpringUtilities;

/**
 *
 * @author TEMUR
 *
 * container for training mode controls
 */
class TrainingControlsPanel extends JPanel {

    private ProgramControl control;
    private Map<String, ConfigField> configFields;
    private JPanel configPanel;
    private JToggleButton setConfigJToggle;
    private JButton initSomJButton;
    private JButton trainingSomJButton;

    public void setTrainingSomEnabled(boolean b) {
        this.trainingSomJButton.setEnabled(b);
    }
    public void setConfigToggleSelected(boolean b){
        setConfigJToggle.setSelected(b);
    }

    public TrainingControlsPanel() {
        control = ProgramControl.getInstance();
        setLayout(new FlowLayout());

        setBorder(BorderFactory.createTitledBorder("Controlls"));
        configPanel = new JPanel(new SpringLayout());
        configFields = Collections.synchronizedMap(new LinkedHashMap<String, ConfigField>());
        configFields.put("x_out", new ConfigField("colonnes (x_out): ", true));
        configFields.get("x_out").setIntValue(120);
        configFields.put("n_in", new ConfigField("capteurs (n_in): ", true));
        configFields.get("n_in").setIntValue(500000);
        configFields.put("y_out", new ConfigField("lignes (y_out): ", true));
        configFields.get("y_out").setIntValue(90);
        configFields.put("n_out", new ConfigField("neurones (n_out): ", false));

        configPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
        for (String key : configFields.keySet()) {
            configFields.get(key).addToPanel(configPanel);
        }
        configPanel.add(new JLabel(""));


        SpringUtilities.makeCompactGrid(configPanel,
                configFields.size() / 2, 4, //rows, cols
                6, 6, //initX, initY
                6, 6);//xPad, yPad
        add(configPanel);
        //--------------
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, 1));
        setConfigJToggle = new JToggleButton("Apply");
        setConfigJToggle.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ev) {
                setConfigToggleButtonStateChanged(ev);
            }
        });
        buttonsPanel.add(setConfigJToggle);
        initSomJButton = new JButton("initialize");
        initSomJButton.setEnabled(false);
        initSomJButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initSomJButtonActionPerformed(e);
            }
        });
        buttonsPanel.add(initSomJButton);

        trainingSomJButton = new JButton("Training");
        trainingSomJButton.setEnabled(false);
        trainingSomJButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trainingSomJButtonActionPerformed(e);
            }
        });
        buttonsPanel.add(trainingSomJButton);
        add(buttonsPanel);
        //--------------
    }

    // <editor-fold defaultstate="collapsed" desc="Call ProgramControl's methods...">
    private void setConfigToggleButtonStateChanged(ItemEvent ev) {
        JToggleButton jtb = (JToggleButton) ev.getItem();
        if (jtb.isSelected()) {
            jtb.setText("Edit Config");
            configFields.get("n_in").setEnabled(false);
            configFields.get("x_out").setEnabled(false);
            configFields.get("y_out").setEnabled(false);
            boolean success = control.configToggleButtonSelectedAction(configFields);
            initSomJButton.setEnabled(success);
        } else {
            jtb.setText("Apply");
            configFields.get("n_in").setEnabled(true);
            configFields.get("x_out").setEnabled(true);
            configFields.get("y_out").setEnabled(true);
            initSomJButton.setEnabled(false);
            trainingSomJButton.setEnabled(false);
        }
    }

    private void initSomJButtonActionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            ((JButton) e.getSource()).setEnabled(false);
        }
        control.initSomJButtonActionPerformed();
    }

    private void trainingSomJButtonActionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            ((JButton) e.getSource()).setEnabled(false);
        }
        control.trainingSomJButtonActionPerformed();
    }
    //</editor-fold>
}
