/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.picking.chooseOrder;

public class PickRequestTableBean {

    
    private PerInfo[] perInfoAll = new PerInfo[]{
        new PerInfo("Auftrags-Nr:", "9891444444"),
        new PerInfo("Mandant", "Ing-Diba"),
        new PerInfo("Erzeugt", "23.04.2008"),
    };

    public PerInfo[] getperInfoAll() {
        return perInfoAll;
    }

    public class PerInfo {


        String name;
        String phone;
        

        public PerInfo(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getname() {
            return name;
        }

        public String getphone() {
            return phone;
        }

    }
}
