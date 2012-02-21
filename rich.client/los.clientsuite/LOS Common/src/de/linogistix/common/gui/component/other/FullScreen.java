package de.linogistix.common.gui.component.other;

/**
 *
 * @author artur
 */
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import org.netbeans.core.windows.view.ui.MainWindow;
import org.openide.windows.WindowManager;

/**
 * Utility class to switch between full-screen and normal mode for any jframe.
 * @author Prashant Bhat
 * @since 0.1 - Dec 22, 2007 6:38:02 PM
 * @version 0.1
 */
public class FullScreen {

    private int delay = 500; //milliseconds
    private javax.swing.Timer timer;
    private boolean fullScreenMode;    //org.netbeans.core.windows.actions.ToggleFullScreenAction
    //org.netbeans.core.windows.view.ui.MainWindow
    //SystemAction.get(org.netbeans.core.windows.actions.ToggleFullScreenAction.class);
    private static FullScreen instance = null;
    private boolean componentShown = false;

    /** Creates a new instance of LiveHelp */
    private FullScreen() {
//        setGlobalListener();        
    }

    public synchronized static FullScreen getInstance() {
        if (instance == null) {
            instance = new FullScreen();
        }
        return instance;
    }

    /*    private void setGlobalListener() {
    final Frame mainWindow = WindowManager.getDefault().getMainWindow();
    mainWindow.addComponentListener(new ComponentAdapter() {       
    
    public void componentShown(ComponentEvent e) {
    System.out.println("componentShown");
    componentShown = true;
    }
    });
    
    }*/
    public void setFullScreenMode(boolean fullScreenMode) {
        this.fullScreenMode = fullScreenMode;
        processFullScreenMode(fullScreenMode);
    }

    /**
     * Force the fullscreenmode. Looking with the timer if the dialog 
     * is showing (A showing dialog is necessary to set a fullscreen mode) 
     * than yes, set to fullscreen.
     * @param fullScreenMode
     */
    public void setFullScreenModeForce(final boolean fullScreenMode) {
        this.fullScreenMode = fullScreenMode;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                timer = new javax.swing.Timer(delay, taskPerformer);
                timer.setRepeats(true);
                timer.start();
            }
        });
    }

    private void processFullScreenMode(final boolean fullScreenMode) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                MainWindow frame = (MainWindow) WindowManager.getDefault().getMainWindow();
                Component comp = frame.getFocusOwner();
                frame.setFullScreenMode(fullScreenMode);
                if (comp != null) {
                    if (comp.isFocusable()) {
                        comp.requestFocus();
                    }
                }
            }
        });

    }
    ActionListener taskPerformer = new ActionListener() {

        public void actionPerformed(ActionEvent evt) {
            if (WindowManager.getDefault().getMainWindow() != null) {
                if (WindowManager.getDefault().getMainWindow().isShowing()) {
                    timer.stop();
                    processFullScreenMode(fullScreenMode);
                }
            }
        }
    };
}