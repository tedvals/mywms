/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.gui.object.LOSAutoFilteringComboBoxNode;
import de.linogistix.common.res.CommonBundleResolver;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.plaf.metal.MetalComboBoxUI;
import org.openide.util.NbBundle;

/**
 *
 * @author artur
 */
public class AutoFilteringComboBox extends JComboBox {
    //necessary for setting edit textfield
    private int delay = 100;
    private KeyEvent keyPressedEvent;
    private KeyListener keyCallbackListener;
    private javax.swing.Timer timer;
    private boolean filtering = false;
    private boolean popupMousepressed = false;
    //flag for control the setText in the Textfield. 
    //removeAllItems, addItem.. clear elsewher the TextField field too.
    //Set it on false to forbidden the clear of the TextField field and only
    //to clear the combobox.
    private boolean editing = true;
    
    // set by MyEditor.FocusListener 
    // considered by setMatchError(...)
    private boolean userIsEditing = true;
    
    private LosLabel editorLabel;
    
    private boolean mandatory = false;
    
    private boolean suppressWarnings = false;
    
    ActionListener taskPerformer = new ActionListener() {
        
        public void actionPerformed(ActionEvent evt) {

            if (isValidKey(keyPressedEvent)) {
                keyCallbackListener.keyPressed(keyPressedEvent);
                if (popupMousepressed == false) {
                    setKeyHandle(keyPressedEvent);
                }
            }
            filtering = false;
            popupMousepressed = false;
        }
    };

    /** Creates a new instance of AutofilteringComboBox */
    public AutoFilteringComboBox() {
        setEditor(new MyEditor());
        setEditable(true);
        
        
        //by choosing an item by the mouse from the popup, 
        //the callback mechanism will be called 
        addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                    
                if ((evt.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0) {
                    sendDummyEvent();
                }
            }
        });
        
        setUI(new MetalComboBoxUIWithoutArrow());
    }
    
    /**
     * send a custom Event
     */
    private void sendDummyEvent() {
        //generate a custom event
        if (keyCallbackListener != null && userIsEditing) {
            System.out.println("--- Send Dummy Event ---");
            
            popupMousepressed = true;
            filtering = true;
            KeyEvent k = new KeyEvent((JTextField) getEditor().getEditorComponent(), 
                                       KeyEvent.KEY_PRESSED, 0, 0, 
                                       KeyEvent.VK_ENTER, 
                                       KeyEvent.CHAR_UNDEFINED);
            keyPressedEvent = k;
            timer.restart();
        }
    }

    public void setCallbackListener(KeyListener keyCallbackListener) {
        this.keyCallbackListener = keyCallbackListener;
        timer = new javax.swing.Timer(delay, taskPerformer);
        timer.setRepeats(false);
    }

    public void setCallbackListener(KeyListener keyCallbackListener, int delay) {
        this.keyCallbackListener = keyCallbackListener;
        this.delay = delay;
        timer = new javax.swing.Timer(delay, taskPerformer);
    }

    /**
     * Check if the entry exist in the combobox at first position. If yes it gives true back
     * @return if item exist the result will be true else false
     */
    @Override
    public boolean isValid() {
        if (getModel() != null) {
            if (getModel().getSize() > 0) {
                    return true;
            }
        }
        return false;
    }

    public boolean hasItemContainsInput() {
        if (getModel() != null) {
            if (getModel().getSize() > 0) {
                String input = getText();
                
                for(int i=0;i<getModel().getSize();i++){
                    String item = ((LOSAutoFilteringComboBoxNode) getItemAt(i)).toString().trim().toLowerCase();
       
                    if (item.indexOf(input.toLowerCase()) >= 0) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 
     * @param label
     * @param aTop
     * @deprecated use setMatchError(LosLabel label) instead
     */
    @Deprecated
    public void setMatchError(LosLabel label, FocusListener aTop) {
        setMatchError(label, "Entry does not exist", aTop);
    }
    
    /**
     * 
     * @param label
     * @param errorKey
     * @param aTop
     * @deprecated use setMatchError(final LosLabel label, final String errorKey) instead
     */
    @Deprecated
    public void setMatchError(final LosLabel label, final String errorKey, FocusListener aTop) {
        if (getText().equals("")) {
            label.setText();
        } else if (isValid() == false) {
            label.setHiddenText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            if (hasItemContainsInput() == false) {
                label.setText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            }
        }
    }
    
    /**
     * Set the Error to the given Label if not matching with the user entry
     * @param label
     */
    public void setMatchError(LosLabel label) {    
        setMatchError(label, "Entry does not exist");        
    }
   
    /**
     * 
     * @param label
     * @param errorKey The key for the Bundle.properties
     */
   public void setMatchError(final LosLabel label, final String errorKey) {

       if(!userIsEditing){
           return;
       }
       
       // clear labels error message
       label.setText();
       
       // set error message or leave label without
        if (getText().equals("")) {
            label.setText();
        } else if (isValid() == false && !suppressWarnings) {
            label.setHiddenText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            if (hasItemContainsInput() == false) {
                label.setText(NbBundle.getMessage(CommonBundleResolver.class, errorKey), IconType.ERROR);
            }
        }       
   }
   
   public void showDefaultErrorMessage(){
       if(!suppressWarnings){
            editorLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "Entry does not exist"), IconType.ERROR);
       }
   }

    public void showEmptyErrorMessage(){
        if(!suppressWarnings){
            editorLabel.setText(NbBundle.getMessage(CommonBundleResolver.class, "Empty field"), IconType.ERROR);
        }
   }
    
    public boolean isEmpty() {
        if (getText().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * Set items to combobox without chance the textfield editor
     * @param items array of items to set
     */
    public void addItems(List items) {
        editing = false;
        removeAllItems();
        Iterator<String> iter = items.iterator();
        while (iter.hasNext()) {
            super.addItem(iter.next());
        }
        setSelectedIndex(-1);
        editing = true;
    }

    public Object getItemByText(){

        for (int i=0; i<getItemCount();i++){
            Object o = getItemAt(i);
            if (o != null && o.toString().equals(getText())){
                return o;
            }
        }
        
        return null;
    }
    
    @Override
    public void addItem(Object item) {
        editing = false;
        super.addItem(item);
        setSelectedIndex(-1);
        editing = true;
    }

    public void addItem(String input) {
        setText(input);
        sendDummyEvent();
    }
    
    private boolean isValidKey(KeyEvent e) {
        
        if ((e.getKeyCode() != KeyEvent.VK_DOWN) &&
                (e.getKeyCode() != KeyEvent.VK_UP) &&
                (e.getKeyCode() != KeyEvent.VK_LEFT) &&
                (e.getKeyCode() != KeyEvent.VK_END) &&
                (e.getKeyCode() != KeyEvent.VK_BEGIN) &&
                (e.getKeyCode() != KeyEvent.VK_RIGHT)) {
            return true;
        }
        return false;
    }

    public void setKeyHandle(KeyEvent e) {
        JTextField text = (JTextField) getEditor().getEditorComponent();
        if (isValidKey(e)) {
            //set the momentan selecting entry to the textfield editor
            if ((e.getKeyCode() == KeyEvent.VK_ENTER)){
                if (getSelectedItem() != null) {
                    text.setText(getSelectedItem().toString());
                    text.transferFocus();
                }
                else if( isPopupVisible() && hasItemContainsInput() ) {
                    String item = ((LOSAutoFilteringComboBoxNode) getItemAt(0)).toString().trim();
                    text.setText(item);
                }
            }
            else if ((e.getKeyCode() == KeyEvent.VK_F6)){
                setPopupVisible(true);
            } 
            else if((e.isControlDown())){
                // enable operation with Strg-Keys
                setPopupVisible(false);
            }
            else if((e.isAltDown())){
                // enable operation with Alt-Keys
                setPopupVisible(false);
            }
            else {
                if (isDisplayable()) {
                    if ((text.getText().equals("") || (getModel().getSize()) == 0)) {
                        if (text.getText().equals("")) {
                            removeAllItems();
                        }
                        setPopupVisible(false);
                    } else {
                        //necessary for (re)calcute the height for the popup
                        setPopupVisible(false);
                        setPopupVisible(true);
                    }
                }
                //disable all selection in the combobox        
                setSelectedIndex(-1);
            }
        }
    }

    public String getText() {
        String ret = ((JTextField) getEditor().getEditorComponent()).getText();
        
        return ret != null ? ret : "";
    }

    public void setText(String text) {
        ((JTextField) getEditor().getEditorComponent()).setText(text);
    }
    
    public LosLabel getEditorLabel() {
        return editorLabel;
    }

    public void setEditorLabel(LosLabel editorLabel) {
        this.editorLabel = editorLabel;
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
    }

    public void removeAllItems(boolean clearTextField) {
        if (clearTextField == false) {
            editing = false;
            super.removeAllItems();
            editing = true;
        } else {
            super.removeAllItems();
        }
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isSuppressWarnings() {
        return suppressWarnings;
    }

    public void setSuppressWarnings(boolean suppressWarnings) {
        this.suppressWarnings = suppressWarnings;
    }
    
    public boolean checkSanity(){
        
        if(getText().length() == 0){
            if (isMandatory()){
                showEmptyErrorMessage();
                return false;
            }
        } else if (getItemByText() == null ){
            showDefaultErrorMessage();
            return false;
        }
        return true;
    }
    
    public void setUserIsEditing(boolean userIsEditing) {
        this.userIsEditing = userIsEditing;
    }

    @Override
    public void requestFocus() {
        ((JTextField) getEditor().getEditorComponent()).requestFocus();
    }
    
    
    class CustomTextField extends JTextField {

        public CustomTextField(int columns) {
            super(columns);
        }

        @Override
        public void setText(String t) {
            if (editing) {
                super.setText(t);
            }
        }
    }

    /**
     * Custom textfield editor which will be set to the combobox
     */
    public class MyEditor implements ComboBoxEditor {

//        private JTextField text;
        private CustomTextField text;
        private String startValue = null;

        public MyEditor() {
            text = new CustomTextField(20);
            text.setBorder(new EmptyBorder(0, 4, 0, 4));
            text.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    //necessary for setting the text from popup in the editfield without waiting on the timer
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if( (startValue == null && text.getText() == null) ||
                            (startValue != null && startValue.equals(text.getText())) ) {
                            text.transferFocus();
                            return;
                        }
                        setKeyHandle(e);
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_F2) {
                        text.selectAll();
                    }
                    else {
                        keyPressedEvent = e;
                        if (keyCallbackListener != null) {
                            filtering = true;
                            timer.restart();
                        }

                    }
                }
            });

            text.addFocusListener(new java.awt.event.FocusListener() {
                
                public void focusLost(FocusEvent e)  {
                    if (getSelectedItem() != null) {
                        text.setText(getSelectedItem().toString());
                    }
                    else if(text.getText().length() == 0){
                        if (isMandatory()){
                            showEmptyErrorMessage();
                        }
                    } 
                    else {
                        Object o = getItemByText();
                        if ( o == null ){
                            showDefaultErrorMessage();
                        }
                        else {
                            setSelectedItem(o);
                        }
                    }
                    text.select(1,1);
                    
                    sendDummyEvent();
                    userIsEditing = false;
                }

                public void focusGained(FocusEvent e) {
                    startValue = text.getText();
                    userIsEditing = true;
                    text.selectAll();
                }

            }); 

            /**
             * if you pressed in the textfield so it make sure that the popup
             * will be closed.
             */
            text.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    
                    //close the popup if user clicked in the edit field
                    if (isPopupVisible()) {
                        setPopupVisible(false);
                    }
                }
            });
        }

        /**
         * 
         * @return editor
         */
        public Component getEditorComponent() {
            return text;
        }

        /**
         * Here, you can handle the textfield edit entry, which will be shown to 
         * the user
         * @param item
         */
        public void setItem(Object item) {
            if (filtering) {
                return;
            }
            //needed by mousepressed in the combobox popup
            String newText = (item == null) ? "" : item.toString();
            text.setText(newText);
        }

        /**
         * 
         * @return item
         */
        public Object getItem() {
            return text.getText();
        }

        /**
         * select all text
         */
        public void selectAll() {
            text.selectAll();
        }

        /**
         * 
         * @param l
         */
        public void addActionListener(ActionListener l) {
            text.addActionListener(l);
        }

        /**
         * 
         * @param l
         */
        public void removeActionListener(ActionListener l) {
            text.removeActionListener(l);
        }
    }
    
    static class MetalComboBoxUIWithoutArrow extends MetalComboBoxUI{

        
        @Override
        protected JButton createArrowButton() {
            JButton button =  super.createArrowButton();
            
            button.setBorderPainted(false);
            
            MetalComboBoxButton mcb = (MetalComboBoxButton) button;
            mcb.setComboIcon(new Icon(){

                public void paintIcon(Component c, Graphics g, int x, int y) {
                    // do not paint anything within the combo box
                }

                public int getIconWidth() {
                    return 10;
                }

                public int getIconHeight() {
                    return 5;
                }
                
            });
            
            return button;
        }   
    }
}
