/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.openide.util.NbBundle;

/**
 *
 * Componenten for standardized JTextField and a JLabel within one JPanel.
 * 
 * Careful: when added as JPanel having a BoderLayout via Matisse, component 
 * might not been shown properly.  
 * 
 * @author Jordan
 */
public class LOSTextField extends JPanel implements FocusListener{

    public final static String ITEM_CHANGED = "ItemChanged";
    private JTextField myTextField;
    
    private LosLabel textFieldLabel = new LosLabel();
    
    private boolean mandatory = false;
    
    private boolean suppressWarnings = false;
    
    private boolean upperCase = false;
    
    private LOSTextFieldListener keyListener = null;
    private List<PropertyChangeListener> changeListeners = new ArrayList<PropertyChangeListener>();
    private String oldValue;
    
    public LOSTextField() {
        this(false);
    }
    public LOSTextField( boolean isPassword ) {
        
        setLayout(new GridBagLayout());

        if( isPassword ) {
            myTextField = new JPasswordField();
            myTextField.setPreferredSize( new Dimension(150, 22) );
            myTextField.setMinimumSize( new Dimension(150, 22) );
            /*
            {

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
            */
        }
        else {
            myTextField = new JTextField();
            myTextField.setPreferredSize( new Dimension(150, 22) );
            myTextField.setMinimumSize( new Dimension(150, 22) );
            /*
            {

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
            */
        }
        myTextField.addFocusListener(this);
        
        myTextField.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        checkSanity();
                        fireItemChangedEvent();
                    }
                }
                );
                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    myTextField.selectAll();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    myTextField.transferFocus();
                }
            }
            
        });
        
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

        add(myTextField, gbc);
    }
    
    public boolean checkSanity(){
        
        if(myTextField.getText().startsWith(" ")){
            if(!suppressWarnings){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "NO_LEADING_SPACE"), IconType.ERROR);
            }
            return false;
        }
        
        if(upperCase){
            myTextField.setText(myTextField.getText().toUpperCase());
        }
        
        if(mandatory && myTextField.getText().length() == 0){
            if(!suppressWarnings){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "INPUT_REQUIRED"), IconType.ERROR);
            }
            return false;
        }
        else if(keyListener != null && keyListener.checkValue(myTextField.getText(),textFieldLabel) == false ) {
            // the label text has to be set by the keyListener
            return false;
        }
        else{
            textFieldLabel.setText();
            return true;
        }
        
    }
    
    public LosLabel getTextFieldLabel() {
        return textFieldLabel;
    }
    
    public void setText(String text){
        myTextField.setText(text);
    }
    
    public String getText(){
        return myTextField.getText();
    }
    
    public void setTitle(String text){
        textFieldLabel.setTitleText(text);
    }
    
    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean isMandatory) {
        textFieldLabel.setShowMandatoryFlag(isMandatory);
        this.mandatory = isMandatory;
    }
    
    public boolean isSuppressWarnings() {
        return suppressWarnings;
    }

    public void setSuppressWarnings(boolean suppressWarnings) {
        this.suppressWarnings = suppressWarnings;
    }
    
    public boolean isUpperCase() {
        return upperCase;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        myTextField.setEnabled(enabled);
        textFieldLabel.setEnabled(enabled);
    }
        
    /**
     *       Implementation of FocusListener
     */
    public void focusGained(FocusEvent e) {
        myTextField.selectAll();
        oldValue = myTextField.getText();
    }
    
    public void focusLost(FocusEvent e) {
        myTextField.select(1,1);
        
        checkSanity();
        
        fireItemChangedEvent();
        
    }
    
    /**
     * Registering a listener for the field. It is called each time a key is pressed.
     * @param listener
     */
    public void setKeyListener(LOSTextFieldListener listener) {
        this.keyListener = listener;
    }

    public void removeKeyListener() {
        this.keyListener = null;
    }	  

    /**
     * Registering a listener for the field.
     * It is called, when the input of the field is finished (focusLost).
     * @param PropertyChangeListener
     */
    public void addItemChangeListener(PropertyChangeListener l) {
        if(!changeListeners.contains(l))
            changeListeners.add(l);
    }
    public void removeItemChangedListener(PropertyChangeListener l) {
        changeListeners.remove(l);
    }
    
    public void fireItemChangedEvent(){
        
        if( changeListeners.size() > 0 ) {
            if( oldValue != null && !oldValue.equals(myTextField.getText()) ) {
                PropertyChangeEvent pce = new PropertyChangeEvent(this, ITEM_CHANGED, null, myTextField.getText());
                for (PropertyChangeListener p : changeListeners) {
                    p.propertyChange(pce);
                }
            }
        }
        oldValue = myTextField.getText();
    }

    @Override
    public void requestFocus() {
        myTextField.requestFocus();
    }

    @Override
    public boolean hasFocus() {
        return myTextField.hasFocus();
    }

    
    public void selectAll() {
        myTextField.selectAll();
    }

    public JTextField getTextField() {
        return myTextField;
    }
}
