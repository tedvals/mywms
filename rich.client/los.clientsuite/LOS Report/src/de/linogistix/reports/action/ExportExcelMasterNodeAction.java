/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.reports.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.report.businessservice.ReportService;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.BOQueryNode;
import de.linogistix.los.report.businessservice.ReportException;
import de.linogistix.los.report.businessservice.ReportServiceBean;
import de.linogistix.reports.res.ReportsBundleResolver;
import java.io.File;
import java.io.FileOutputStream;
import net.sf.jasperreports.engine.JRException;
import org.openide.util.Lookup;

public final class ExportExcelMasterNodeAction extends NodeAction {

    private static final Logger log = Logger.getLogger(ExportExcelMasterNodeAction.class.getName());

    public ExportExcelMasterNodeAction() {
    }

    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.FALSE);
    }

    public String getName() {
        return NbBundle.getMessage(ReportsBundleResolver.class, "ExcelExportAction");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Excel.pngs";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {

        for (Node n : activatedNodes) {
            if (!(n instanceof BOMasterNode)) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null || activatedNodes.length == 0) {
            return;
        }

        List masterNodes = Arrays.asList(activatedNodes);
        processExcelExport(masterNodes);

    }

    @SuppressWarnings("unchecked")
    public void processExcelExport(List<BOMasterNode> masterNodes) {
        try {

            if (masterNodes == null || masterNodes.size() == 0) {
                throw new ReportException();
            }

//            ReportService reportService = Lookup.getDefault().lookup(ReportService.class);
            ReportService reportService = new ReportServiceBean();
            String title = getName();
            Map pMap = new HashMap();
            Property[] props = getBO(masterNodes.get(0)).getBoMasterNodeProperties();
            for (Property p : props) {
                pMap.put(p.getName(), p.getDisplayName());
            }
            
            System.setProperty("net.sf.jasperreports.properties", "de/linogistix/reports/res/jasperreports.properties");

            byte[] xls = reportService.typeExportExcelGeneric(title, masterNodes, pMap);

            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(java.lang.System.getProperty("user.dir"));
            int returnValue = chooser.showSaveDialog(null);
            if ((returnValue == javax.swing.JFileChooser.APPROVE_OPTION)) { 
                File f = chooser.getSelectedFile();
                FileOutputStream out = new FileOutputStream(f);
                out.write(xls);
                out.close();
            }
                
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
    }

    private BO getBO(BOMasterNode masterNode) {
        BOQueryNode queryNode = (BOQueryNode) masterNode.getParentNode();
        return queryNode.getModel().getBoNode().getBo();
    }
}

