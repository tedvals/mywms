package de.linogistix.common.bobrowser.crud.gui.component;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.TemplateQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.NbBundle;

public class CompleteVehicleWorkWizard extends WizardDescriptor {
  
  private static final Logger log = Logger.getLogger(CompleteVehicleWorkWizard.class.getName());
   
  private TemplateQuery templateQuery;
  
  private boolean completionSuccess;
  
  private String completionRemarks;
  
  private BO bo;
  
  public CompleteVehicleWorkWizard(BO bo) throws InstantiationException {
    super(createPanels());
    
    this.bo = bo;
        
    putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
//    putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_helpDisplayed", Boolean.TRUE);
//    putProperty("WizardPanel_contentData", new String[]{"contentData"});
//    putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/bobrowser/res/icon.Search32.png"));
    
    setTitle(NbBundle.getMessage(CommonBundleResolver.class,"CompleteVehicleWorkWizard.Title"));
//    setHelpCtx(new HelpCtx(NbBundle.getMessage(BundleResolver.class,"BOCreateWizard.HelpCtx"));
    
  }
  
  //-------------------------------------------------------------------------------

  public final static Panel[] createPanels() throws InstantiationException{
    List<Panel> panels = new ArrayList();
    
    panels.add(new CompleteVehicleWorkPanel());
    return (Panel[])panels.toArray(new Panel[0]);
  }

  public boolean getCompletionSuccess(){
      return this.completionSuccess;
  }
  
  public void setCompletionSuccess(boolean completionSuccess){
      this.completionSuccess = completionSuccess;
  }
  
  public String getCompletionRemarks(){
      return this.completionRemarks;
  }
  
  public void setCompletionRemarks(String completionRemarks){
      this.completionRemarks = completionRemarks;
  }

    public BO getBo() {
        return bo;
    }

}

