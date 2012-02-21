/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.los.inventory.pick.exception;

/**
 *
 * @author trautm
 */
public enum PickingExceptionKey {
    PICK_FROM_WRONG_LOCATION,
    PICK_FROM_WRONG_STOCKUNIT,
    PICK_WRONG_AMOUNT,
    NO_DESTINATION, WRONG_DESTINATION,
    TARGET_NOT_FOUND,
    SOURCE_NOT_FOUND,
    NO_CART, NO_PARENT_ORDER, 
    UNFINISHED_POSITIONS, 
    PICK_UNEXPECTED_NULL,
    STOCK_HAS_MORE_AMOUNT,
    UNITLOAD_HAS_MORE_STOCKS,
    
    PICKREQUEST_CREATION,
    PICKREQUEST_NOT_FINISHED,
    PICKREQUEST_CONSTRAINT_VIOLATED,
    PICK_WRONG_SOURCE,
    // delete picking position is not allowed 
    PICK_POSITION_CONTRAINT_VIOLATED, 
    //printing of label failed
    PRINT_LABEL_FAILED,
    //operator has to transport unitLoad to destination after cancelling the request
    MUST_GOTO_DESTINATION, 
    // After picking the Stockunit should become empty
    PICK_EXPECTED_NULL, 
    AMOUNT_MUSTBE_POSITIVE,
    //Operation is not allowed for this PickWithdrawalType
    PICK_WRONG_WITHDRAWALTYPE, 
    PICKREQUEST_WRONG_STATE, 
    //Wrong serialNumber has been scanned
    PICK_WRONG_SERIALNO,
    //No serialNo has been scanned
    PICK_MISSING_SERIALNO
}