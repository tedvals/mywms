/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.common.system;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.Option;
import org.netbeans.spi.sendopts.OptionProcessor;

/**
 * Processes Command line options.
 *
 * Listens to <code>-- showDownModal on|off</code> command line options. 
 * If off, no modal dialog "server unreachable" is shown when there is no
 * connection. 
 *
 * @author trautm
 */
public class CommonOptions extends OptionProcessor{

    private static final Logger logger = Logger.getLogger(CommonOptions.class.getName());

    private static Option showDownModal = Option.requiredArgument(Option.NO_SHORT_NAME, "showDownModal");

    @Override
    protected Set<Option> getOptions() {
        HashSet<Option> options = new HashSet<Option>();
        options.add(showDownModal);

        return options;
    }

    @Override
    protected void process(Env env, Map<Option, String[]> maps) throws CommandException {
        String[] args ;
        String arg;

        if ( (args=maps.get(showDownModal)) != null && args.length > 0){
            if (args[0] != null ){
                arg = args[0];
                if (arg.trim().equals("off")){
                    ServerDown.showDownModal = false;
                    logger.severe("!!! showDownModal has been set to off !!!");
                } else  if (arg.trim().equals("off")){
                    ServerDown.showDownModal = true;
                } else{
                    ServerDown.showDownModal = true;
                }
            }
        }

    }


}
