/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.gui.component.gui_builder.AbstractBOAutoFilteringComboBox;
import de.linogistix.common.gui.component.gui_builder.AbstractBOChooser;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.gui.object.LOSAutoFilteringComboBoxNode;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.openide.util.Lookup;

/**
 * Live help component. 
 * 
 * The user types some text in a JComboBox like control and gets a list of 
 * matching entities in the database.
 * <p>
 * All queries are remote calls to instances of {@link BusinessObjectQueryRemote}
 * <p>
 * Different modes are available:
 * <ul>
 * <li><code>AUTO_COMPLETE</code>: Most common. Calls {@link BusinessObjectQueryRemote#autoCompletion(String)}
   <li><code>AUTO_COMPLETE_CLIENT</code>. Calls {@link BusinessObjectQueryRemote#autoCompletion(String, Client)}
   <li><code>BY_IDENTITY</code>:Calls {@link BusinessObjectQueryRemote#queryByIdentity(String)}
   <li><code>BY_ID</code>:Calls {@link BusinessObjectQueryRemote#queryById(Long)}. Then the user must type in the complete id and gets one match!
   <li><code>INVOKE_QUERYMETHOD</code>:Most flexible. Calls an arbitrary method.
 * </ul> 
 * 
 * <p>
 * <strong>Example</strong> for default usage</code>:    
 * <p>
 * <code>
 *      this.clientCombo = new BOAutoFilteringComboBox<Client>(Client.class);
        this.clientCombo.setEditorLabelTitle(NbBundle.getMessage(BundleResolver.class, "Lot"));
 *      add(this.clientCombo); // add to a JComponent
 * </code>
 * 
 * <p>
 * <strong>Example</strong> for mode <code>INVOKE_QUERYMETHOD</code>:
 * <p>
 * <code>
 * 
 *  this.lotCombo = new BOAutoFilteringComboBox(Lot.class);
        this.lotCombo.setMode(Mode.INVOKE_QUERYMETHOD);
        try {
            m = this.lotCombo.getQueryRemote().getClass().getDeclaredMethod(
                    "autoCompletionByClientAndItemData", 
                    new Class[]{String.class, BODTO.class, BODTO.class});
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
            return;
        }
        this.lotCombo.setQueryMethod(m);
        this.lotCombo.setReplaceArgByTypedText(0);
        this.lotCombo.setQueryMethodArgs(new Object[]{"",null, null});
        this.lotCombo.setEditorLabelTitle(NbBundle.getMessage(BundleResolver.class, "Lot"));
        this.lotCombo.addItemChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
               lotChanged(evt);
            }
        });
 * 
 * </code>
 * @author trautm
 */
public class BOAutoFilteringComboBox<T extends BasicEntity> extends AbstractBOAutoFilteringComboBox{

    public final static String ITEM_CHANGED = "ItemChanged";
    
    private final static Logger log = Logger.getLogger(BOAutoFilteringComboBox.class.getName());
    
    private Class boCLass;
    private BusinessObjectQueryRemote queryRemote;
    private boolean processKeyPressed = true;
    private Mode mode = Mode.AUTO_COMPLETE;
    private List<PropertyChangeListener> listeners = new ArrayList();

    private Client client;
    
    private Method queryMethod;  
    private Object[] queryMethodArgs;
    private int replaceArgByTypedText;
    
    private BOAutoFilteringComboBoxModel<T> myModel;
    
    private BODTO<T> selectedItem = null;
        
    public BOAutoFilteringComboBox(Class boClass) {
        
        this.boCLass = boClass;
        BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
        BO bo = (BO) l.lookup(boClass);
        if( bo == null ) {
            log.severe("Cannot lookup BOClass: " + boClass.getCanonicalName());
            return;
        } 
        
        this.queryRemote = bo.getQueryService();
        setSearchListener();
        clear();
        setEditorLabelTitle(bo.getSingularDisplayName());
        setEditorLabelText();
        getAutoFilteringComboBox().setEditorLabel(getEditorLabel());
    }
    
    public void setEditorLabelTitle(String text) {
        getEditorLabel().setTitleText(text);
    }

    public void setEditorLabelText() {
        getEditorLabel().setText();
    }

    private void setSearchListener() {
//        ((JTextField)partIDComoboBox.getEditor().getEditorComponent()).setDocument(new PartIdDocument());        
        getAutoFilteringComboBox().setCallbackListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
            
                if (isProcessKeyPressed()) {
                    processBO();
                    
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        BODTO<T> oldSelectedItem = getSelectedItem();
                        boolean hasChanged = false;
                        
                        if(getAutoFilteringComboBox().isValid()){
                            LOSAutoFilteringComboBoxNode n = (LOSAutoFilteringComboBoxNode) getAutoFilteringComboBox().getItemByText();
                            if (n != null) {
                                setSelectedItem((BODTO)n.getObject());
                                if( oldSelectedItem == null || !oldSelectedItem.toString().equals(n.toString()) )
                                    hasChanged = true;
                            } else {
                                setSelectedItem(null);
                                if( oldSelectedItem != null )
                                    hasChanged = true;
                            }
                        }
                        if( hasChanged ) {
                            fireItemChangeEvent();
                        }
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                        setSelectedItem(null);
                        fireItemChangeEvent();
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_DELETE){
                        setSelectedItem(null);
                        fireItemChangeEvent();
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_F3){
                        openChooserButtonActionPerformedListener(null);
                        
                    }
                }
            }
        });
    }

    private void processBO() {
        List<BODTO<T>> items = null;

        getAutoFilteringComboBox().removeAllItems(false);

        String searchString = getAutoFilteringComboBox().getText();
        
        if (searchString == null || searchString.length() == 0){
            return;
        } else if(searchString.equals(" ")){
            searchString = "";
        } 
        
        switch (mode) {
            case AUTO_COMPLETE_CLIENT:
                items = queryRemote.autoCompletion(searchString, client);
                break;
            case AUTO_COMPLETE:
                items = queryRemote.autoCompletion(searchString);
                break;
            case BY_IDENTITY:
                try {
                    BasicEntity e = queryRemote.queryByIdentity(getAutoFilteringComboBox().getText());
                    BODTO<T> dto = new BODTO<T>(e.getId(), e.getVersion(), e.toUniqueString());
                    items = new ArrayList<BODTO<T>>();
                    items.add(dto);
                } catch (BusinessObjectNotFoundException ex) {
                    items = null;
                }
                break;
            case BY_ID:
                try {
                    Long id = Long.parseLong(getAutoFilteringComboBox().getText());
                    BasicEntity e = queryRemote.queryById(id);
                    BODTO<T> dto = new BODTO<T>(e.getId(), e.getVersion(), e.toUniqueString());
                    items = new ArrayList<BODTO<T>>();
                    items.add(dto);
                } catch (BusinessObjectNotFoundException ex) {
                    items = null;
                } catch (BusinessObjectSecurityException ex) {
                    items = null;
                } catch (NumberFormatException ex) {
                    items = null;
                }
                break;
            case INVOKE_QUERYMETHOD:
                
                int i = 1;
                for(Object arg : queryMethodArgs){
                    String argStr = "NULL";
                    if(arg != null)
                        argStr = arg.toString();
                    
                    System.out.println("----- Query Method Arg "+i+" : "+argStr);
                    i++;
                }
                
                try {
                    if (replaceArgByTypedText < queryMethodArgs.length ){
                        queryMethodArgs[replaceArgByTypedText] = searchString;
                        items = (List<BODTO<T>>) queryMethod.invoke(queryRemote,queryMethodArgs );
                    } else{
                        FacadeException ex = new FacadeException("Not initialized", "BusinessException.InvalidUserArgs", null);
                        ex.setBundleResolver(CommonBundleResolver.class);
                        throw ex;
                    }
                } catch (Throwable ex) {
                    ExceptionAnnotator.annotate(ex);
                }
                break;
                
            case MODEL:
                
                items = myModel.getResults(searchString, new QueryDetail(0, 30));
        }

        if (items != null) {
            for (BODTO pos : items) {
                getAutoFilteringComboBox().addItem(new LOSAutoFilteringComboBoxNode(pos.getName(), pos));
            }
        }

        getAutoFilteringComboBox().setMatchError(getEditorLabel());
        
    }

    @Override
    public void openChooserButtonActionPerformedListener(ActionEvent evt) {
        if( getComboBoxModel() != null && getComboBoxModel().isSingleResult() ) {
            return;
        }
        
        BOChooser dialog;
             
        //System.out.println("--- BOAutofilteringComboBox : Open Chooser Dialog");
        
        if(mode == Mode.MODEL && myModel != null){
            dialog = new BOChooser(boCLass, myModel);
        }
        else{
            dialog = new BOChooser(boCLass);
        }
        
        if(getAutoFilteringComboBox().getItemByText() != null){
            dialog.setSelection(getAutoFilteringComboBox().getText());
        }
        
        dialog.showDialog();
        
        if (dialog.dialogDescriptor.getValue() instanceof AbstractBOChooser.CustomButton) {
            BOChooser.CustomButton button = (BOChooser.CustomButton) dialog.dialogDescriptor.getValue();
            if (button.getActionCommand().equals(BOChooser.OK_BUTTON)) {
                BODTO dto = dialog.getValue();
                getEditorLabel().setText();
                getAutoFilteringComboBox().setText(dto.getName());
                getAutoFilteringComboBox().removeAllItems(false);
                getAutoFilteringComboBox().addItem(new LOSAutoFilteringComboBoxNode(dto.getName(), dto));
                getAutoFilteringComboBox().setMatchError(getEditorLabel());
                setSelectedItem(dto);
                fireItemChangeEvent();
            }
        }
    }

    @Deprecated
    public BODTO<T> getItem() {
        return getSelectedItem();
    }
    
    public BODTO<T> getSelectedItem() {
        return selectedItem;
    }
    
    public void setSelectedItem(BODTO<T> selected){
        selectedItem = selected;
    }

    public void addItem(BODTO<T> o) {
        getEditorLabel().setText();
        getAutoFilteringComboBox().setText(o.getName());
        getAutoFilteringComboBox().removeAllItems(false);
        getAutoFilteringComboBox().addItem(new LOSAutoFilteringComboBoxNode(o.getName(), o));
        getAutoFilteringComboBox().setMatchError(getEditorLabel());
        setSelectedItem(o);
    }

    public void clear() {
        getAutoFilteringComboBox().setText("");
        getAutoFilteringComboBox().removeAllItems();
//        getEditorLabel().setIcon(GraphicUtil.getInstance().getIcon(IconType.INFORMATION));
        getEditorLabel().setText();
        setClient(null);
        setSelectedItem(null);
        
        if(getComboBoxModel() != null){
            getComboBoxModel().clear();
        }
    }

    public boolean isProcessKeyPressed() {
        return processKeyPressed;
    }

    public void setProcessKeyPressed(boolean process) {
        this.processKeyPressed = process;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Method getQueryMethod() {
        return queryMethod;
    }

     /**
     * Just useful in conjunction with Mode.INVOKE_QUERYMETHOD.
     * 
     * Use this method on remoteservice to retrieve a resultset.
      *  
     * @param queryRemote
     */
    public void setQueryMethod(Method queryMethod) {
        this.queryMethod = queryMethod;
    }

    public BusinessObjectQueryRemote getQueryRemote() {
        return queryRemote;
    }

    public void setQueryRemote(BusinessObjectQueryRemote queryRemote) {
        this.queryRemote = queryRemote;
    }

    public Object[] getQueryMethodArgs() {
        return queryMethodArgs;
    }

    /**
     * Just useful in conjunction with Mode.INVOKE_QUERYMETHOD
     * 
     * @param queryMethodArgs
     */
    public void setQueryMethodArgs(Object[] queryMethodArgs) {
        this.queryMethodArgs = queryMethodArgs;
    }

    public int getReplaceArgByTypedText() {
        return replaceArgByTypedText;
    }

    public void setComboBoxModel(BOAutoFilteringComboBoxModel<T> model){
        mode = Mode.MODEL;
        myModel = model;
    }
    
    public BOAutoFilteringComboBoxModel<T> getComboBoxModel(){
        return myModel;
    }
    
    /**
     * 
     * Just useful in conjunction with Mode.INVOKE_QUERYMETHOD
     * 
     * The argument at <code>replaceArgByTypedText</code> 
     * of the arguments set via {@link #setQueryMethodArgs} will be replaced
     * by the String the user typed in the ComboBox on the fly. This argument must 
     * be of type String.
     * @see #setQueryMethodArgs
     * @see #setQueryRemote
     * @see #setQueryMethod
     * @param replaceArgByTypedText
     */
    public void setReplaceArgByTypedText(int replaceArgByTypedText) {
        this.replaceArgByTypedText = replaceArgByTypedText;
    }

    public static enum Mode {

        AUTO_COMPLETE,
        AUTO_COMPLETE_CLIENT,
        BY_IDENTITY,
        BY_ID,
        INVOKE_QUERYMETHOD,
        MODEL
    }

    //--------------------------------------------------------------------
    public void addItemChangeListener(PropertyChangeListener l) {
        listeners.add(l);
    }

    public void removeItemChangedListener(PropertyChangeListener l) {
        listeners.remove(l);
    }

    protected void fireItemChangeEvent() {
        PropertyChangeEvent e = new PropertyChangeEvent(this, ITEM_CHANGED, null, getSelectedItem());
        for (PropertyChangeListener p : listeners) {
            p.propertyChange(e);
        }
    }

    //--------------------------------------------------------------------
    public T getSelectedAsEntity() {
        BODTO<T> to = getSelectedItem();
        try {
            if (getSelectedItem() != null) {

                return (T) queryRemote.queryById(to.getId());

            } else {
                return null;
            }
        } catch (Throwable t) {
            return null;
        }
    }
}
