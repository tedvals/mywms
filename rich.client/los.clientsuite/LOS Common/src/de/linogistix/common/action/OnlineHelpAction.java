/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.action;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Action which shows StockTakingTopComponent component.
 */
public class OnlineHelpAction extends AbstractAction {


    public OnlineHelpAction() {
        super(NbBundle.getMessage(CommonBundleResolver.class, "OnlineHelpAction.name"));
        setEnabled(true);
    }

    public void actionPerformed(ActionEvent evt) {
        URL url;
        try {
            url = new URL("http://wiki.linogistix.com/Wiki.jsp?page=LOS_Dokumentation");
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        URLDisplayer.getDefault().showURL(url);
    }
}
