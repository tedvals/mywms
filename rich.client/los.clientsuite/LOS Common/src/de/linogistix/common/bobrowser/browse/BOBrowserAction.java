/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.browse;

import de.linogistix.common.res.CommonBundleResolver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows BOBrowser component.
 */
public class BOBrowserAction extends AbstractAction {
  
  static final String ICON_PATH = "de/linogistix/common/res/icon/BOBrowser.png";
  
  public BOBrowserAction() {
    super(NbBundle.getMessage(CommonBundleResolver.class, "CTL_BOBrowserAction"));
    putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(BOBrowserAction.ICON_PATH, true)));
    
  }
  
  public void actionPerformed(ActionEvent evt) {
    TopComponent win = BOBrowserTopComponent.findInstance();
    win.open();
    win.requestActive(); 
  }
  
}
