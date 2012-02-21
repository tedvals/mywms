/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.logviewer.processes.log.gui.object;

import de.linogistix.common.bobrowser.bo.editor.PlainObjectReadOnlyEditor;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.system.BundleHelper;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.common.util.GraphicUtil;
import java.awt.Image;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import org.mywms.globals.LogItemType;
import org.mywms.model.LogItem;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author artur
 */
public class LogItemNode extends AbstractNode {

    LogItem item;
    Sheet.Set sheet = null;

    @Override
    public Image getIcon(int arg0) {
        ImageIcon iconImage = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(IconType.INFORMATION)));
        if (LogItemType.ERROR == item.getType()) {
            iconImage = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(IconType.ERROR)));
        } else if (LogItemType.LOG == item.getType()) {
            iconImage = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(IconType.INFORMATION)));
        }
        Image image = iconImage.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        return image;//testImage.getImage();
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            LogProperty p = new LogProperty<String>("host", String.class, resolve("host"), "", item.getHost());
            p.setValue("suppressCustomEditor", Boolean.TRUE); //NOI18N            
            sheet.put(p);
            //Man muss Sheet nehmen da der Konstuktor von PropertySet selber abstract ist.
            sheet.put(new LogProperty<String>("created", String.class, resolve("created"), "", getDate(item.getCreated())));
            sheet.put(new LogProperty<String>("user", String.class, resolve("user"), "", item.getUser()));
            sheet.put(new LogProperty<String>("source", String.class, resolve("source"), "", item.getSource()));
//            sheet.put(new LogProperty<String>("message", String.class, resolve("message"), "", item.getMessageResourceKey() != null ? resolve(item.getMessageResourceKey()) : item.getMessage()));
            sheet.put(new LogProperty<String>("message", String.class, resolve("message"), "", item.getMessageResourceKey() != null ? resolveMyWMS(item) : item.getMessage()));
            resolveMyWMS(item);
        }
        return new PropertySet[]{sheet};
    }

    public static String resolve(String key) {
        return BundleResolve.resolve(new Class[]{de.linogistix.common.res.CommonBundleResolver.class,
            de.linogistix.logviewer.res.BundleResolver.class
        }, key, null);
    }

    public static String resolveMyWMS(LogItem item) {
//        return new BundleHelper("de/linogistix/los/location/LOSLocationLogBundle").resolve2(item.getMessageResourceKey(), item.getMessageParameters());
        return item.getMessage();
//        return new BundleHelper(item.getResourceBundleName()).resolve2(item.getMessageResourceKey(), item.getMessageParameters());
    }

    private String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 
     * @return
     */
    public static Property[] templateProperties() {

        Property[] properties = new Property[]{
            new LogProperty<String>("created", String.class, resolve("created"), "created", null),
            new LogProperty<String>("user", String.class, resolve("user"), "user", ""),
            new LogProperty<String>("source", String.class, resolve("source"), "source", null),
            new LogProperty<String>("message", String.class, resolve("message"), "message", null)
        };
        return properties;
    }

    public LogItemNode(LogItem item) {
        super(Children.LEAF);
        if (item == null) {
            throw new NullPointerException("Item null");
        }
        this.item = item;
        setName(item.getType().toString() + " " + item.getId());
        setDisplayName(item.getType().toString());
//        "de/linogistix/bobrowser/res/icon/Document.png"
//        setIconBaseWithExtension("de/linogistix/common/res/icon/Exception.png");
//        setIconBaseWithExtension("de/linogistix/common/res/icon/Exception.png");

    }

    public Property[] getProperties() {
        return getPropertySets()[0].getProperties();
    }

    private static class LogProperty<T> extends PropertySupport.ReadOnly<T> {

        @Override
        public PropertyEditor getPropertyEditor() {
            return new PlainObjectReadOnlyEditor();
        }
        T value;

        public LogProperty(String name, Class<T> type, String displayName, String shortDescription, T value) {
            super(name, type, displayName, shortDescription);
            this.value = value;
        }

        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }
    }
}
