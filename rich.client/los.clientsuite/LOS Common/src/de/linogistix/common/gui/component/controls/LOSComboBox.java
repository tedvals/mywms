/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 *
 * @author Jordan
 */
public class LOSComboBox extends JPanel{

    private JComboBox myComboBox;
    
    private LosLabel textFieldLabel = new LosLabel();
    
    
    public LOSComboBox(){
        
        setLayout(new GridBagLayout());
        
        myComboBox = new JComboBox(){
          
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(150, 22);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(150, 22);
            }
            
        };
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(textFieldLabel, gbc);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(myComboBox, gbc);
        
    }
    
    public void setTitle(String text){
        textFieldLabel.setTitleText(text);
    }
    
    public void addItem(Object item){
        myComboBox.addItem(item);
    }
    
    public Object getSelectedItem(){
        return myComboBox.getSelectedItem();
    }
    
    public void setSelectedItem(Object sel){
        myComboBox.setSelectedItem(sel);
    }
}
