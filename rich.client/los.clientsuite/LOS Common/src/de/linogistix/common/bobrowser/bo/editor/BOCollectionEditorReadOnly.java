/*
 * BOCollectionEditor.java
 *
 * Created on 27. Februar 2007, 03:21
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * A Property Editor for a {@link Collection}s of {@link BasicEntity}.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOCollectionEditorReadOnly extends BOCollectionEditor
        implements ExPropertyEditor,
        ActionListener {
  
  private PropertyEnv env;
  
  private Logger log = Logger.getLogger(BOCollectionEditorReadOnly.class.getName());
  
  @Override
  public Component getCustomEditor() {
    if (getEntities() == null || getEntities().size() == 0){
      if (env != null){
        env.setState(PropertyEnv.STATE_INVALID);
      }
      return new NoValueMessage();
    }else{
      try{
        this.typeHint = resolveType(getEntities());
      } catch (IllegalArgumentException iax){
        log.warning("Could not resolve Type: " + iax.toString());
      }
      return new BOCollectionEditorReadOnlyPanel(this);
    }
  }
}
