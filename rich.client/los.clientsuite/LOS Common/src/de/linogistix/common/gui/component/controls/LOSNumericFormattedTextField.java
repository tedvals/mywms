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
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import org.openide.util.NbBundle;

public class LOSNumericFormattedTextField extends JPanel implements
        LOSNumericDocumentListener {

    private JLabel unitNameLabel;
    private JTextField myTextField;
    private LosLabel textFieldLabel = new LosLabel();
    
    private int scale = 0; 
    private Locale locale = Locale.GERMANY;
    
    private BigDecimal minValue = BigDecimal.valueOf(Double.MIN_VALUE);     
    private boolean minValueInclusive;
    
    private BigDecimal maxValue = BigDecimal.valueOf(Double.MAX_VALUE);
    private boolean maxValueInclusive;
    
    private String oldValue;
    private List<PropertyChangeListener> changeListeners = new ArrayList<PropertyChangeListener>();

    public LOSNumericFormattedTextField() {

        setLayout(new GridBagLayout());

        myTextField = new JTextField() {

            private static final long serialVersionUID = 1L;

            @Override
            protected Document createDefaultModel() {
                return new LOSNumericDocument();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(150, 22);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(150, 22);
            }
        };

        unitNameLabel = new JLabel(""){
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(48, 22);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(24, 22);
            }
        };
        
        myTextField.addKeyListener(new KeyAdapter() {
        
            @Override
            public void keyReleased(KeyEvent e) {
                
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    fireItemChangedEvent();
                }
                
            }
        
        });
        
        // init text field with 0,00 if empty
        myTextField.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                String initText = getFormat().format(0.0);
                if (myTextField.getText().equals(initText)) {
                    myTextField.setText("");
                } else {
                    myTextField.setCaretPosition(myTextField.getText().length());
                }
                oldValue = myTextField.getText();
            }

            public void focusLost(FocusEvent e) {
                String initText = getFormat().format(0.0);
                if (myTextField.getText().equals("")) {
                    myTextField.setText(initText);
                } else {
                    try {
                        double val = getValue().doubleValue();
                        String formattedInput = getFormat().format(val);
                        myTextField.setText(formattedInput);
                    } catch (ParseException e1) {
                    }
                }
                
                checkSanity();
                
                fireItemChangedEvent();
            }
        });

        ((LOSNumericDocument) myTextField.getDocument()).setInsertListener(this);

        textFieldLabel.setText("");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(textFieldLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(myTextField, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(unitNameLabel, gbc);

        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(locale);
        nf.setMinimumFractionDigits(scale);
        nf.setMaximumFractionDigits(scale);
        nf.setGroupingUsed(true);
        nf.setGroupingSize(3);
        setFormat(nf);

        setValue(new BigDecimal(0));

    }

    public void setScale(int scale) {
        this.scale = scale;
        DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance(locale);
        nf.setMinimumFractionDigits(scale);
        nf.setMaximumFractionDigits(scale);
        nf.setGroupingUsed(true);
        nf.setGroupingSize(3);
        setFormat(nf);

        if(myTextField.getText().length()>0){
            BigDecimal val = new BigDecimal(0);
            try {
                val = getValue();
            } catch (ParseException e) {
                val = new BigDecimal(0);
                setValue(val);
            }

            // prevent document from checking again and to run into endless loop
            ((LOSNumericDocument) myTextField.getDocument()).setUserInput(false);

            String formattedInput = getFormat().format(val);
            myTextField.setText(formattedInput);

            ((LOSNumericDocument) myTextField.getDocument()).setUserInput(true);
        }
    }

    public boolean checkSanity(){
        try {

            BigDecimal val = getValue();
            
            if(val == null){
                return false;
            }
            
            if(minValueInclusive && val.compareTo(minValue) < 0){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "BELOW_MIN_VALUE"), IconType.ERROR);
                return false;
            }
            else if(!minValueInclusive && val.compareTo(minValue) <= 0){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "BELOW_MIN_VALUE"), IconType.ERROR);
                return false;
            }
            else if(maxValueInclusive && val.compareTo(maxValue) > 0){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "ABOVE_MAX_VALUE"), IconType.ERROR);
                return false;
            }
            else if(!maxValueInclusive && val.compareTo(maxValue) >= 0){
                textFieldLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "ABOVE_MAX_VALUE"), IconType.ERROR);
                return false;
            }
            
            textFieldLabel.setText();
            return true;
            
        } catch (ParseException ex) {
            return false;
        }
    }
    
    public void setUnitName(String unitName) {
        unitNameLabel.setText(unitName);
    }
    
    public LosLabel getTextFieldLabel() {
        return textFieldLabel;
    }
    
    public void setTitle(String text){
        textFieldLabel.setTitleText(text);
    }

    public void setFormat(DecimalFormat format) {
        ((LOSNumericDocument) myTextField.getDocument()).setFormat(format);
    }

    public DecimalFormat getFormat() {
        return ((LOSNumericDocument) myTextField.getDocument()).getFormat();
    }

    public BigDecimal getValue() throws ParseException {
        return ((LOSNumericDocument) myTextField.getDocument()).getValue();
    }

    public void setValue(BigDecimal val) {
        String formatedVal = getFormat().format(val.doubleValue());
        myTextField.setText(formatedVal);
        myTextField.setCaretPosition(formatedVal.length());
    }

    public void setMinimumValue(BigDecimal min, boolean inclusive) {
        this.minValue = min;
        this.minValueInclusive = inclusive;
    }

    public void setMaximumValue(BigDecimal max, boolean inclusive) {
        this.maxValue = max;
        this.maxValueInclusive = inclusive;
    }
    
    public void setMandatory(boolean isMandatory) {
        textFieldLabel.setShowMandatoryFlag(isMandatory);
    }
    
    // Override to handle insertion error
    public void insertFailed(LOSNumericDocument doc, int offset, String str,
            AttributeSet a) {

        char decSep = ((LOSNumericDocument) myTextField.getDocument()).decimalSeparator;

        if (str.length() == 1 && str.toCharArray()[0] == decSep) {

            if (myTextField.getText().indexOf(decSep) == offset) {
                myTextField.setCaretPosition(offset + 1);

                ((LOSNumericDocument) myTextField.getDocument()).setUserInput(false);

                myTextField.setText(myTextField.getText().substring(0, offset + 1));

                ((LOSNumericDocument) myTextField.getDocument()).setUserInput(true);
            }

        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void insertSucceeded(LOSNumericDocument doc, String resultOfInsert, int offset) {

        try {

            double val = getValue().doubleValue();

            char decSep = ((LOSNumericDocument) myTextField.getDocument()).decimalSeparator;

            if (resultOfInsert.indexOf(decSep) > 0 && resultOfInsert.indexOf(decSep) <= offset) {
                return;
            }

            // prevent document from checking again and to run into endless loop
            ((LOSNumericDocument) myTextField.getDocument()).setUserInput(false);

            String formattedInput = getFormat().format(val);
            myTextField.setText(formattedInput);

            ((LOSNumericDocument) myTextField.getDocument()).setUserInput(true);

            char groupSep = ((LOSNumericDocument) myTextField.getDocument()).groupingSeparator;
            int groupingInsert = resultOfInsert.split("\\" + groupSep).length - 1;
            int groupingFormated = formattedInput.split("\\" + groupSep).length - 1;

            if (groupingFormated > groupingInsert) {
                myTextField.setCaretPosition(offset + 2);
            } else if (groupingFormated == groupingInsert) {
                myTextField.setCaretPosition(offset + 1);
            } else {
                myTextField.setCaretPosition(offset);
            }

            fireItemChangedEvent();
            
        } catch (ParseException nfe) {
            // in case of first sign and input == '-'
            if (resultOfInsert.equals("-")) {
                // do nothing
            } else {
                nfe.printStackTrace();
            }
        }
    }
    
    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        myTextField.setEnabled(enable);
    }
    
    @Override
    public void requestFocus() {
        myTextField.requestFocus();
    }

    public void clear(){
        myTextField.setText("");
    }
    
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
                PropertyChangeEvent pce = new PropertyChangeEvent(this, "ITEM_CHANGED", null, myTextField.getText());
                for (PropertyChangeListener p : changeListeners) {
                    p.propertyChange(pce);
                }
            }
        }
    }
}
