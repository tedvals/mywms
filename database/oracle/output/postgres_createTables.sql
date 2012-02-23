create table JBOSS.los_area (id number(19,0) not null, areaType varchar2(255 char), primary key (id));
create table JBOSS.los_avisreq (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), adviceNumber varchar2(255 char) not null unique, externalNo varchar2(255 char), receiptAmount number(17,4), expectedDelivery date, expireBatch number(1,0) not null, notifiedAmount number(17,4), adviceState varchar2(255 char), externalId varchar2(255 char), client_id number(19,0) not null, itemData_id number(19,0) not null, lot_id number(19,0), primary key (id));
create table JBOSS.los_bom (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), amount number(17,4) not null, pickable number(1,0) not null, index number(10,0) not null, parent_id number(19,0) not null, child_id number(19,0) not null, primary key (id), unique (parent_id, child_id));
create table JBOSS.los_extinguishreq (id number(19,0) not null, lot_id number(19,0), primary key (id));
create table JBOSS.los_extorder (id number(19,0) not null, authorizedBy_id number(19,0), lot_id number(19,0), primary key (id));
create table JBOSS.los_fixassgn (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), desiredAmount number(17,4) not null, itemData_id number(19,0) not null, assignedLocation_id number(19,0) not null, primary key (id), unique (assignedLocation_id));
create table JBOSS.los_goodsreceipt (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), driverName varchar2(255 char), gr_number varchar2(255 char) not null unique, licencePlate varchar2(255 char), forwarder varchar2(255 char), delnote varchar2(255 char), receiptDate date, receiptState varchar2(255 char), referenceNo varchar2(255 char), goodsInLocation_id number(19,0), operator_id number(19,0) not null, client_id number(19,0) not null, primary key (id));
create table JBOSS.los_goodsreceipt_los_avisreq (los_goodsreceipt_id number(19,0) not null, assignedAdvices_id number(19,0) not null);
create table JBOSS.los_grrposition (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), scale number(10,0) not null, itemData varchar2(255 char), amount number(17,4), unitLoad varchar2(255 char), lot varchar2(255 char), positionNumber varchar2(255 char) unique, orderReference varchar2(255 char), stockUnitStr varchar2(255 char), receiptType varchar2(255 char), qaFault varchar2(1024 char), state number(10,0), stockUnit_id number(19,0), goodsReceipt_id number(19,0) not null, client_id number(19,0) not null, relatedAdvice_id number(19,0), primary key (id));
create table JBOSS.los_inventory (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), itemDataRef varchar2(255 char), reserved number(19,2), available number(19,2), locked number(19,2), advised number(19,2), inStock number(19,2), lastIncoming timestamp, lastAmount number(19,2), lotRef varchar2(255 char), client_id number(19,0) not null, primary key (id), unique (lotRef, client_id));
create table JBOSS.los_itemdata_number (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), manufacturerName varchar2(255 char), number varchar2(255 char) not null, index number(10,0) not null, itemData_id number(19,0) not null, client_id number(19,0) not null, primary key (id), unique (number, itemData_id));
create table JBOSS.los_jrxml (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), jrXML raw(255), name varchar2(255 char), client_id number(19,0) not null, primary key (id), unique (name, client_id));
create table JBOSS.los_locationcluster (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), name varchar2(255 char) not null unique, primary key (id));
create table JBOSS.los_order (id number(19,0) not null, primary key (id));
create table JBOSS.los_orderreceipt (id number(19,0) not null, user_ varchar2(255 char) not null, orderType varchar2(255 char) not null, orderReference varchar2(255 char), orderNumber varchar2(255 char) not null, state varchar2(255 char), date timestamp not null, destination varchar2(255 char), primary key (id));
create table JBOSS.los_orderreceiptpos (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), amount number(17,4), lotRef varchar2(255 char), articleRef varchar2(255 char), articleDescr varchar2(255 char), amountordered number(17,4), articleScale number(10,0) not null, pos number(10,0) not null, client_id number(19,0) not null, receipt_id number(19,0), primary key (id));
create table JBOSS.los_orderreq (id number(19,0) not null, orderState varchar2(255 char), delivery date, requestId varchar2(255 char), documentUrl varchar2(255 char), labelUrl varchar2(255 char), orderType varchar2(255 char), destination_id number(19,0) not null, primary key (id));
create table JBOSS.los_orderreqpos (id number(19,0) not null, amount number(17,4), positionIndex number(10,0) not null, partitionAllowed number(1,0) not null, positionState varchar2(255 char), pickedAmount number(17,4), parentRequest_id number(19,0) not null, lot_id number(19,0), itemData_id number(19,0) not null, primary key (id));
create table JBOSS.los_outpos (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), outState varchar2(255 char), source_id number(19,0) not null, goodsOutRequest_id number(19,0) not null, primary key (id), unique (source_id));
create table JBOSS.los_outreq (id number(19,0) not null, outState varchar2(255 char), operator_id number(19,0), parentRequest_id number(19,0) not null, primary key (id));
create table JBOSS.los_pickreceipt (id number(19,0) not null, orderNumber varchar2(255 char), labelID varchar2(255 char), pickNumber varchar2(255 char), state varchar2(255 char), date timestamp, primary key (id));
create table JBOSS.los_pickreceiptpos (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), amount number(17,4), lotRef varchar2(255 char), articleRef varchar2(255 char), articleDescr varchar2(255 char), amountordered number(17,4), receipt_id number(19,0), primary key (id));
create table JBOSS.los_pickreq (id number(19,0) not null, customerNumber varchar2(255 char), state varchar2(255 char), destination_id number(19,0) not null, cart_id number(19,0), parentRequest_id number(19,0) not null, user_id number(19,0), primary key (id));
create table JBOSS.los_pickreqpos (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), amount number(17,4) not null, pickedAmount number(17,4) not null, solved number(1,0) not null, canceled number(1,0) not null, pickingSupplyType varchar2(255 char), pickingDimensionType varchar2(255 char), withdrawalType varchar2(255 char), substitutionType varchar2(255 char), pos_index number(10,0), pickRequest_id number(19,0) not null, parentRequest_id number(19,0) not null, stockUnit_id number(19,0) not null, complementOn_id number(19,0), primary key (id));
create table JBOSS.los_rack (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), numberOfRows number(10,0) not null, numberOfColumns number(10,0) not null, labelOffset number(10,0), rname varchar2(255 char) not null, client_id number(19,0) not null, primary key (id), unique (client_id, rname));
create table JBOSS.los_racklocation (id number(19,0) not null, XPos number(10,0) not null, YPos number(10,0) not null, ZPos number(10,0) not null, rack_id number(19,0) not null, primary key (id));
create table JBOSS.los_replenishreq (id number(19,0) not null, primary key (id));
create table JBOSS.los_replenishreq_los_storloc (los_replenishreq_id number(19,0) not null, allowedSources_id number(19,0) not null);
create table JBOSS.los_sequenceNumber (className varchar2(255 char) not null, sequenceNumber number(19,0) not null, version number(10,0) not null, primary key (className));
create table JBOSS.los_serviceconf (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), subkey varchar2(255 char), servValue varchar2(255 char), servKey varchar2(255 char) not null, service varchar2(255 char) not null, client_id number(19,0) not null, primary key (id), unique (service, client_id, servKey));
create table JBOSS.los_slLabel (id number(19,0) not null, labelId varchar2(255 char), primary key (id));
create table JBOSS.los_stockrecord (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), scale number(10,0) not null, itemData varchar2(255 char), amount number(17,4), lot varchar2(255 char), serialNumber varchar2(255 char), operator varchar2(255 char) not null, toStockUnitIdentity varchar2(255 char), fromStockUnitIdentity varchar2(255 char), activityCode varchar2(255 char) not null, amountStock number(17,4), toUnitLoad varchar2(255 char), toStorageLocation varchar2(255 char) not null, fromUnitLoad varchar2(255 char), fromStorageLocation varchar2(255 char) not null, type varchar2(255 char), client_id number(19,0) not null, primary key (id));
create table JBOSS.los_stocktaking (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), stockTakingNumber varchar2(255 char) not null unique, stockTakingType varchar2(255 char), started timestamp, ended timestamp, primary key (id));
create table JBOSS.los_stocktakingorder (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), operator varchar2(255 char), locationName varchar2(255 char), unitLoadLabel varchar2(255 char), areaName varchar2(255 char), countingDate timestamp, state number(10,0), stockTaking_id number(19,0), primary key (id));
create table JBOSS.los_stocktakingrecord (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), clientNo varchar2(255 char), locationName varchar2(255 char), unitLoadLabel varchar2(255 char), itemNo varchar2(255 char), lotNo varchar2(255 char), plannedQuantity number(19,2), countedQuantity number(19,2), serialNo varchar2(255 char), countedStockId number(19,0), ulTypeNo varchar2(255 char), state number(10,0), stocktakingOrder_id number(19,0), primary key (id));
create table JBOSS.los_storagelocationtype (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), volume number(19,6), liftingCapacity number(16,3), sltname varchar2(255 char) not null, height number(15,2), width number(15,2), depth number(15,2), primary key (id), unique (sltname));
create table JBOSS.los_storagereq (id number(19,0) not null, requestState varchar2(255 char), unitLoad_id number(19,0), destination_id number(19,0), primary key (id));
create table JBOSS.los_storloc (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), reservedCapacity number(10,0) not null, stockTakingDate timestamp, name varchar2(255 char) not null unique, zone_id number(19,0), area_id number(19,0), client_id number(19,0) not null, type_id number(19,0) not null, currentTCC number(19,0), cluster_id number(19,0), primary key (id));
create table JBOSS.los_suLabel (id number(19,0) not null, scale number(10,0) not null, amount number(19,2) not null, itemUnit varchar2(255 char) not null, lotRef varchar2(255 char), labelID varchar2(255 char) not null, clientRef varchar2(255 char) not null, dateRef varchar2(255 char) not null, itemdataRef varchar2(255 char) not null, primary key (id));
create table JBOSS.los_sysprop (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), workstation varchar2(255 char) not null, groupName varchar2(255 char), sysvalue varchar2(255 char) not null, syskey varchar2(255 char) not null, hidden number(1,0) not null, description varchar2(255 char), client_id number(19,0) not null, primary key (id), unique (client_id, syskey, workstation));
create table JBOSS.los_typecapacityconstraint (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), capacity number(10,0) not null, name varchar2(255 char) not null unique, unitLoadType_id number(19,0), storageLocationType_id number(19,0) not null, primary key (id), unique (storageLocationType_id, unitLoadType_id));
create table JBOSS.los_ul_record (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), operator varchar2(255 char), activityCode varchar2(255 char), fromLocation varchar2(255 char) not null, toLocation varchar2(255 char) not null, recordType varchar2(255 char) not null, label varchar2(255 char) not null, client_id number(19,0) not null, primary key (id));
create table JBOSS.los_uladvice (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), labelId varchar2(255 char) not null unique, adviceState varchar2(255 char) not null, externalNumber varchar2(255 char), adviceType varchar2(255 char) not null, reasonForReturn varchar2(255 char), switchStateInfo varchar2(255 char), number varchar2(255 char) not null unique, relatedAdvice_id number(19,0), client_id number(19,0) not null, unitLoadType_id number(19,0), primary key (id));
create table JBOSS.los_uladvicepos (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), notifiedAmount number(17,4) not null, positionNumber varchar2(255 char) not null, itemData_id number(19,0) not null, unitLoadAdvice_id number(19,0) not null, lot_id number(19,0), client_id number(19,0) not null, primary key (id));
create table JBOSS.los_unitload (id number(19,0) not null, stockTakingDate timestamp, packageType varchar2(255 char), weightGross number(16,3), weightNet number(16,3), storageLocation_id number(19,0) not null, primary key (id));
create table JBOSS.mywms_area (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), name varchar2(255 char) not null, client_id number(19,0) not null, primary key (id), unique (client_id, name));
create table JBOSS.mywms_clearingitem (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), user_ varchar2(255 char) not null, messageResourceKey varchar2(255 char) not null, messageParameters raw(255) not null, options blob not null, solver varchar2(255 char), solution blob, shortMessageResourceKey varchar2(255 char) not null, shortMessageParameters raw(255) not null, propertyMap raw(255), bundleResolver blob, host varchar2(255 char) not null, source varchar2(255 char) not null, resourceBundleName varchar2(255 char) not null, client_id number(19,0) not null, primary key (id));
create table JBOSS.mywms_client (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), email varchar2(255 char), phone varchar2(255 char), fax varchar2(255 char), name varchar2(255 char) not null unique, cl_nr varchar2(255 char) not null unique, cl_code varchar2(255 char) unique, primary key (id));
create table JBOSS.mywms_document (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), name varchar2(255 char) not null, type varchar2(255 char) not null, document_size number(10,0), document blob not null, client_id number(19,0) not null, primary key (id), unique (client_id, name));
create table JBOSS.mywms_itemdata (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), safetyStock number(10,0) not null, rest_Usage_GI number(10,0), lotMandatory number(1,0) not null, lotSubstitutionType varchar2(255 char), adviceMandatory number(1,0) not null, serialRecType varchar2(255 char) not null, scale number(10,0) not null, volume number(19,6), tradeGroup varchar2(255 char), name varchar2(255 char) not null, item_nr varchar2(255 char) not null, height number(15,2), width number(15,2), weight number(16,3), descr varchar2(255 char), depth number(15,2), zone_id number(19,0), client_id number(19,0) not null, handlingUnit_id number(19,0) not null, defULType_id number(19,0), primary key (id), unique (client_id, item_nr));
create table JBOSS.mywms_itemunit (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), unitType varchar2(255 char), unitName varchar2(255 char) not null, baseFactor number(10,0) not null, baseUnit_id number(19,0), primary key (id));
create table JBOSS.mywms_logitem (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), user_ varchar2(255 char) not null, messageResourceKey varchar2(255 char) not null, messageParameters raw(255), bundleResolver varchar2(255 char), message varchar2(255 char) not null, type varchar2(255 char) not null, host varchar2(255 char) not null, source varchar2(255 char) not null, resourceBundleName varchar2(255 char) not null, client_id number(19,0) not null, primary key (id));
create table JBOSS.mywms_lot (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), volume number(19,6), useNotBefore date, bestBeforeEnd date, age varchar2(255 char), name varchar2(255 char), lot_date date not null, height number(15,2), width number(15,2), weight number(16,3), depth number(15,2), code varchar2(255 char), client_id number(19,0) not null, itemData_id number(19,0) not null, primary key (id), unique (name, itemData_id));
create table JBOSS.mywms_pluginconfiguration (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), plugin_mode varchar2(255 char) not null, pluginClass varchar2(255 char) not null, pluginName varchar2(255 char) not null, client_id number(19,0) not null, primary key (id), unique (client_id, pluginName, plugin_mode));
create table JBOSS.mywms_request (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), parentRequestNumber varchar2(255 char), request_nr varchar2(255 char) not null unique, client_id number(19,0) not null, primary key (id));
create table JBOSS.mywms_role (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), name varchar2(255 char) not null unique, description varchar2(255 char) not null, primary key (id));
create table JBOSS.mywms_stockunit (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), amount number(17,4) not null, labelId varchar2(255 char) not null, reservedAmount number(17,4), serialNumber varchar2(255 char), unitLoad_id number(19,0) not null, lot_id number(19,0), itemData_id number(19,0) not null, client_id number(19,0) not null, primary key (id), unique (labelId, itemData_id));
create table JBOSS.mywms_unitload (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), labelId varchar2(255 char) not null unique, location_index number(10,0), type_id number(19,0) not null, client_id number(19,0) not null, primary key (id));
create table JBOSS.mywms_unitloadtype (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), volume number(19,6), liftingCapacity number(16,3), name varchar2(255 char) not null, height number(15,2), width number(15,2), weight number(16,3), depth number(15,2), primary key (id), unique (name));
create table JBOSS.mywms_user (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), email varchar2(255 char), phone varchar2(255 char), firstname varchar2(255 char), lastname varchar2(255 char), name varchar2(255 char) not null unique, locale varchar2(255 char), password varchar2(255 char) not null, client_id number(19,0) not null, primary key (id));
create table JBOSS.mywms_user_mywms_role (mywms_user_id number(19,0) not null, roles_id number(19,0) not null);
create table JBOSS.mywms_zone (id number(19,0) not null, created timestamp, modified timestamp, additionalContent varchar2(255 char), version number(10,0) not null, entity_lock number(10,0), name varchar2(255 char) not null, client_id number(19,0) not null, primary key (id), unique (client_id, name));
alter table JBOSS.los_area add constraint FK8C78781C5A8B1214 foreign key (id) references JBOSS.mywms_area;
alter table JBOSS.los_avisreq add constraint FKBE36EA30DFB71F26 foreign key (itemData_id) references JBOSS.mywms_itemdata;
alter table JBOSS.los_avisreq add constraint FKBE36EA30EAD3F22E foreign key (lot_id) references JBOSS.mywms_lot;
alter table JBOSS.los_avisreq add constraint FKBE36EA307576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_bom add constraint FK150C28516FA752E7 foreign key (child_id) references JBOSS.mywms_itemdata;
alter table JBOSS.los_bom add constraint FK150C28518823EC99 foreign key (parent_id) references JBOSS.mywms_itemdata;
alter table JBOSS.los_extinguishreq add constraint FKCB6B2025264B99AA foreign key (id) references JBOSS.los_pickreq;
alter table JBOSS.los_extinguishreq add constraint FKCB6B2025EAD3F22E foreign key (lot_id) references JBOSS.mywms_lot;
alter table JBOSS.los_extorder add constraint FK248471DCF4DCC426 foreign key (id) references JBOSS.los_orderreq;
alter table JBOSS.los_extorder add constraint FK248471DCEAD3F22E foreign key (lot_id) references JBOSS.mywms_lot;
alter table JBOSS.los_extorder add constraint FK248471DCCF4E57F foreign key (authorizedBy_id) references JBOSS.mywms_user;
alter table JBOSS.los_fixassgn add constraint FK78F6EC4297747C46 foreign key (assignedLocation_id) references JBOSS.los_storloc;
alter table JBOSS.los_fixassgn add constraint FK78F6EC42DFB71F26 foreign key (itemData_id) references JBOSS.mywms_itemdata;
alter table JBOSS.los_goodsreceipt add constraint FK81CDB6B162A4F6CD foreign key (operator_id) references JBOSS.mywms_user;
alter table JBOSS.los_goodsreceipt add constraint FK81CDB6B17576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_goodsreceipt add constraint FK81CDB6B1DC91D4F9 foreign key (goodsInLocation_id) references JBOSS.los_storloc;
alter table JBOSS.los_goodsreceipt_los_avisreq add constraint FKDEA77DA2EB5589CF foreign key (assignedAdvices_id) references JBOSS.los_avisreq;
alter table JBOSS.los_goodsreceipt_los_avisreq add constraint FKDEA77DA2ACB9FF15 foreign key (los_goodsreceipt_id) references JBOSS.los_goodsreceipt;
alter table JBOSS.los_grrposition add constraint FKBB47DCA1CBC10219 foreign key (relatedAdvice_id) references JBOSS.los_avisreq;
alter table JBOSS.los_grrposition add constraint FKBB47DCA1BC27A604 foreign key (goodsReceipt_id) references JBOSS.los_goodsreceipt;
alter table JBOSS.los_grrposition add constraint FKBB47DCA1E06B76AE foreign key (stockUnit_id) references JBOSS.mywms_stockunit;
alter table JBOSS.los_grrposition add constraint FKBB47DCA17576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_inventory add constraint FK6DAD32D7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_itemdata_number add constraint FK2CAC199CDFB71F26 foreign key (itemData_id) references JBOSS.mywms_itemdata;
alter table JBOSS.los_itemdata_number add constraint FK2CAC199C7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_jrxml add constraint FK315A8207576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_order add constraint FK35BD19FF4DCC426 foreign key (id) references JBOSS.los_orderreq;
alter table JBOSS.los_orderreceipt add constraint FK2B445FB9F9F1FCA2 foreign key (id) references JBOSS.mywms_document;
alter table JBOSS.los_orderreceiptpos add constraint FK9C10BFB6B21192 foreign key (receipt_id) references JBOSS.los_orderreceipt;
alter table JBOSS.los_orderreceiptpos add constraint FK9C10BFB7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_orderreq add constraint FKDA108E9F5A663BE foreign key (id) references JBOSS.mywms_request;
alter table JBOSS.los_orderreq add constraint FKDA108E9F610EE7DB foreign key (destination_id) references JBOSS.los_storloc;
alter table JBOSS.los_orderreqpos add constraint FK6CC6AB555A663BE foreign key (id) references JBOSS.mywms_request;
alter table JBOSS.los_orderreqpos add constraint FK6CC6AB55DFB71F26 foreign key (itemData_id) references JBOSS.mywms_itemdata;
alter table JBOSS.los_orderreqpos add constraint FK6CC6AB55EAD3F22E foreign key (lot_id) references JBOSS.mywms_lot;
alter table JBOSS.los_orderreqpos add constraint FK6CC6AB552772B400 foreign key (parentRequest_id) references JBOSS.los_orderreq;
alter table JBOSS.los_outpos add constraint FK68501815685FC38A foreign key (source_id) references JBOSS.los_unitload;
alter table JBOSS.los_outpos add constraint FK6850181574799270 foreign key (goodsOutRequest_id) references JBOSS.los_outreq;
alter table JBOSS.los_outreq add constraint FK68501E5F5A663BE foreign key (id) references JBOSS.mywms_request;
alter table JBOSS.los_outreq add constraint FK68501E5F62A4F6CD foreign key (operator_id) references JBOSS.mywms_user;
alter table JBOSS.los_outreq add constraint FK68501E5F2772B400 foreign key (parentRequest_id) references JBOSS.los_orderreq;
alter table JBOSS.los_pickreceipt add constraint FK2FC84488F9F1FCA2 foreign key (id) references JBOSS.mywms_document;
alter table JBOSS.los_pickreceiptpos add constraint FK7A60C0CCD69B7E28 foreign key (receipt_id) references JBOSS.los_pickreceipt;
alter table JBOSS.los_pickreq add constraint FKC12D32EE4BAF7F89 foreign key (cart_id) references JBOSS.los_storloc;
alter table JBOSS.los_pickreq add constraint FKC12D32EE5A663BE foreign key (id) references JBOSS.mywms_request;
alter table JBOSS.los_pickreq add constraint FKC12D32EE51CF0E46 foreign key (user_id) references JBOSS.mywms_user;
alter table JBOSS.los_pickreq add constraint FKC12D32EE610EE7DB foreign key (destination_id) references JBOSS.los_storloc;
alter table JBOSS.los_pickreq add constraint FKC12D32EE2772B400 foreign key (parentRequest_id) references JBOSS.los_orderreq;
alter table JBOSS.los_pickreqpos add constraint FK2ADB70A6291583B foreign key (pickRequest_id) references JBOSS.los_pickreq;
alter table JBOSS.los_pickreqpos add constraint FK2ADB70A6E06B76AE foreign key (stockUnit_id) references JBOSS.mywms_stockunit;
alter table JBOSS.los_pickreqpos add constraint FK2ADB70A66297649 foreign key (parentRequest_id) references JBOSS.los_orderreqpos;
alter table JBOSS.los_pickreqpos add constraint FK2ADB70A68ACB6FED foreign key (complementOn_id) references JBOSS.los_pickreqpos;
alter table JBOSS.los_rack add constraint FK8C7FF2667576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_racklocation add constraint FKD301CC1B2AA3EA0A foreign key (id) references JBOSS.los_storloc;
alter table JBOSS.los_racklocation add constraint FKD301CC1B6E957A1B foreign key (rack_id) references JBOSS.los_rack;
alter table JBOSS.los_replenishreq add constraint FK21BAB647F4DCC426 foreign key (id) references JBOSS.los_orderreq;
alter table JBOSS.los_replenishreq_los_storloc add constraint FKF5EE48D58B688799 foreign key (allowedSources_id) references JBOSS.los_storloc;
alter table JBOSS.los_replenishreq_los_storloc add constraint FKF5EE48D51D030746 foreign key (los_replenishreq_id) references JBOSS.los_replenishreq;
alter table JBOSS.los_serviceconf add constraint FK64999CEA7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_slLabel add constraint FK63B58A2CF9F1FCA2 foreign key (id) references JBOSS.mywms_document;
alter table JBOSS.los_stockrecord add constraint FKC5C8C4787576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_stocktakingorder add constraint FK9D2E768377483CF2 foreign key (stockTaking_id) references JBOSS.los_stocktaking;
alter table JBOSS.los_stocktakingrecord add constraint FKD0763BCD68666C2 foreign key (stocktakingOrder_id) references JBOSS.los_stocktakingorder;
alter table JBOSS.los_storagereq add constraint FK9A96A0925A663BE foreign key (id) references JBOSS.mywms_request;
alter table JBOSS.los_storagereq add constraint FK9A96A092C7DA58DB foreign key (unitLoad_id) references JBOSS.los_unitload;
alter table JBOSS.los_storagereq add constraint FK9A96A092610EE7DB foreign key (destination_id) references JBOSS.los_storloc;
alter table JBOSS.los_storloc add constraint FK735166CD85E165B foreign key (area_id) references JBOSS.los_area;
alter table JBOSS.los_storloc add constraint FK735166CD8BCAF84D foreign key (currentTCC) references JBOSS.los_typecapacityconstraint;
alter table JBOSS.los_storloc add constraint FK735166CD2EB48BA4 foreign key (cluster_id) references JBOSS.los_locationcluster;
alter table JBOSS.los_storloc add constraint FK735166CD53F5F6A6 foreign key (zone_id) references JBOSS.mywms_zone;
alter table JBOSS.los_storloc add constraint FK735166CD7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_storloc add constraint FK735166CD17CCB109 foreign key (type_id) references JBOSS.los_storagelocationtype;
alter table JBOSS.los_suLabel add constraint FK731127C3F9F1FCA2 foreign key (id) references JBOSS.mywms_document;
alter table JBOSS.los_sysprop add constraint FK7C112DC17576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_typecapacityconstraint add constraint FK882380A02C6A1FE6 foreign key (unitLoadType_id) references JBOSS.mywms_unitloadtype;
alter table JBOSS.los_typecapacityconstraint add constraint FK882380A0C34A03D9 foreign key (storageLocationType_id) references JBOSS.los_storagelocationtype;
alter table JBOSS.los_ul_record add constraint FK7F46CCCA7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_uladvice add constraint FK6C7465ECBC10219 foreign key (relatedAdvice_id) references JBOSS.los_avisreq;
alter table JBOSS.los_uladvice add constraint FK6C7465E2C6A1FE6 foreign key (unitLoadType_id) references JBOSS.mywms_unitloadtype;
alter table JBOSS.los_uladvice add constraint FK6C7465E7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_uladvicepos add constraint FKCFD76736DFB71F26 foreign key (itemData_id) references JBOSS.mywms_itemdata;
alter table JBOSS.los_uladvicepos add constraint FKCFD76736EAD3F22E foreign key (lot_id) references JBOSS.mywms_lot;
alter table JBOSS.los_uladvicepos add constraint FKCFD7673629B0FBE4 foreign key (unitLoadAdvice_id) references JBOSS.los_uladvice;
alter table JBOSS.los_uladvicepos add constraint FKCFD767367576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.los_unitload add constraint FK7F1784798CDC8979 foreign key (storageLocation_id) references JBOSS.los_storloc;
alter table JBOSS.los_unitload add constraint FK7F178479B51FDC11 foreign key (id) references JBOSS.mywms_unitload;
alter table JBOSS.mywms_area add constraint FKB0F690DB7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_clearingitem add constraint FKF83EED567576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_document add constraint FKD610BA697576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_itemdata add constraint FKE8EA928B132AA2D4 foreign key (handlingUnit_id) references JBOSS.mywms_itemunit;
alter table JBOSS.mywms_itemdata add constraint FKE8EA928BEA8A22B4 foreign key (defULType_id) references JBOSS.mywms_unitloadtype;
alter table JBOSS.mywms_itemdata add constraint FKE8EA928B53F5F6A6 foreign key (zone_id) references JBOSS.mywms_zone;
alter table JBOSS.mywms_itemdata add constraint FKE8EA928B7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_itemunit add constraint FKE8F27C6521A5FB68 foreign key (baseUnit_id) references JBOSS.mywms_itemunit;
alter table JBOSS.mywms_logitem add constraint FKA60A91497576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_lot add constraint FK9218A143DFB71F26 foreign key (itemData_id) references JBOSS.mywms_itemdata;
alter table JBOSS.mywms_lot add constraint FK9218A1437576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_pluginconfiguration add constraint FKA9F86A957576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_request add constraint FKD2F1A7817576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_stockunit add constraint FKFF6D960CDFB71F26 foreign key (itemData_id) references JBOSS.mywms_itemdata;
alter table JBOSS.mywms_stockunit add constraint FKFF6D960CEAD3F22E foreign key (lot_id) references JBOSS.mywms_lot;
alter table JBOSS.mywms_stockunit add constraint FKFF6D960CC7DA58DB foreign key (unitLoad_id) references JBOSS.los_unitload;
alter table JBOSS.mywms_stockunit add constraint FKFF6D960CAF5EB406 foreign key (unitLoad_id) references JBOSS.mywms_unitload;
alter table JBOSS.mywms_stockunit add constraint FKFF6D960C7576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_unitload add constraint FK914D25B87576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_unitload add constraint FK914D25B83F624790 foreign key (type_id) references JBOSS.mywms_unitloadtype;
alter table JBOSS.mywms_user add constraint FKB0FFAC197576A346 foreign key (client_id) references JBOSS.mywms_client;
alter table JBOSS.mywms_user_mywms_role add constraint FK16F290AA4ABCC2BF foreign key (roles_id) references JBOSS.mywms_role;
alter table JBOSS.mywms_user_mywms_role add constraint FK16F290AAE371C338 foreign key (mywms_user_id) references JBOSS.mywms_user;
alter table JBOSS.mywms_zone add constraint FKB101E3FA7576A346 foreign key (client_id) references JBOSS.mywms_client;
create sequence JBOSS.seqEntities;
