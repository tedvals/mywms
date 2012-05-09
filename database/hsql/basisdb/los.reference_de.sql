--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: los.reference; Type: DATABASE; Schema: -; Owner: jboss
--

CREATE DATABASE "los.reference" WITH TEMPLATE = template0 ENCODING = 'UTF8';


ALTER DATABASE "los.reference" OWNER TO jboss;

\connect "los.reference"

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: jms_messages; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE jms_messages (
    messageid integer NOT NULL,
    destination character varying(150) NOT NULL,
    txid integer,
    txop character(1),
    messageblob bytea
);


ALTER TABLE public.jms_messages OWNER TO jboss;

--
-- Name: jms_roles; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE jms_roles (
    roleid character varying(32) NOT NULL,
    userid character varying(32) NOT NULL
);


ALTER TABLE public.jms_roles OWNER TO jboss;

--
-- Name: jms_subscriptions; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE jms_subscriptions (
    clientid character varying(128) NOT NULL,
    subname character varying(128) NOT NULL,
    topic character varying(255) NOT NULL,
    selector character varying(255)
);


ALTER TABLE public.jms_subscriptions OWNER TO jboss;

--
-- Name: jms_transactions; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE jms_transactions (
    txid integer NOT NULL
);


ALTER TABLE public.jms_transactions OWNER TO jboss;

--
-- Name: jms_users; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE jms_users (
    userid character varying(32) NOT NULL,
    passwd character varying(32) NOT NULL,
    clientid character varying(128)
);


ALTER TABLE public.jms_users OWNER TO jboss;

--
-- Name: los_area; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_area (
    id bigint NOT NULL,
    areatype character varying(255)
);


ALTER TABLE public.los_area OWNER TO jboss;

--
-- Name: los_avisreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_avisreq (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    advicenumber character varying(255) NOT NULL,
    advicestate character varying(255),
    expecteddelivery date,
    expirebatch boolean NOT NULL,
    externalno character varying(255),
    externalid character varying(255),
    notifiedamount numeric(17,4),
    receiptamount numeric(17,4),
    itemdata_id bigint NOT NULL,
    lot_id bigint,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_avisreq OWNER TO jboss;

--
-- Name: los_bom; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_bom (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4) NOT NULL,
    index integer NOT NULL,
    pickable boolean NOT NULL,
    parent_id bigint NOT NULL,
    child_id bigint NOT NULL
);


ALTER TABLE public.los_bom OWNER TO jboss;

--
-- Name: los_extinguishreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_extinguishreq (
    id bigint NOT NULL,
    lot_id bigint
);


ALTER TABLE public.los_extinguishreq OWNER TO jboss;

--
-- Name: los_extorder; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_extorder (
    id bigint NOT NULL,
    authorizedby_id bigint,
    lot_id bigint
);


ALTER TABLE public.los_extorder OWNER TO jboss;

--
-- Name: los_fixassgn; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_fixassgn (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    desiredamount numeric(17,4) NOT NULL,
    itemdata_id bigint NOT NULL,
    assignedlocation_id bigint NOT NULL
);


ALTER TABLE public.los_fixassgn OWNER TO jboss;

--
-- Name: los_goodsreceipt; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_goodsreceipt (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    delnote character varying(255),
    drivername character varying(255),
    forwarder character varying(255),
    gr_number character varying(255) NOT NULL,
    licenceplate character varying(255),
    receiptdate date,
    receiptstate character varying(255),
    referenceno character varying(255),
    client_id bigint NOT NULL,
    goodsinlocation_id bigint,
    operator_id bigint NOT NULL
);


ALTER TABLE public.los_goodsreceipt OWNER TO jboss;

--
-- Name: los_goodsreceipt_los_avisreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_goodsreceipt_los_avisreq (
    los_goodsreceipt_id bigint NOT NULL,
    assignedadvices_id bigint NOT NULL
);


ALTER TABLE public.los_goodsreceipt_los_avisreq OWNER TO jboss;

--
-- Name: los_grrposition; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_grrposition (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4),
    itemdata character varying(255),
    lot character varying(255),
    orderreference character varying(255),
    positionnumber character varying(255),
    qafault character varying(1024),
    receipttype character varying(255),
    scale integer NOT NULL,
    state integer,
    stockunitstr character varying(255),
    unitload character varying(255),
    relatedadvice_id bigint,
    stockunit_id bigint,
    client_id bigint NOT NULL,
    goodsreceipt_id bigint NOT NULL
);


ALTER TABLE public.los_grrposition OWNER TO jboss;

--
-- Name: los_inventory; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_inventory (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    advised numeric(19,2),
    available numeric(19,2),
    instock numeric(19,2),
    itemdataref character varying(255),
    lastamount numeric(19,2),
    lastincoming timestamp without time zone,
    locked numeric(19,2),
    lotref character varying(255),
    reserved numeric(19,2),
    client_id bigint NOT NULL
);


ALTER TABLE public.los_inventory OWNER TO jboss;

--
-- Name: los_itemdata_number; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_itemdata_number (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    index integer NOT NULL,
    manufacturername character varying(255),
    number character varying(255) NOT NULL,
    itemdata_id bigint NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_itemdata_number OWNER TO jboss;

--
-- Name: los_jrxml; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_jrxml (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    jrxml bytea,
    name character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.los_jrxml OWNER TO jboss;

--
-- Name: los_locationcluster; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_locationcluster (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.los_locationcluster OWNER TO jboss;

--
-- Name: los_order; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_order (
    id bigint NOT NULL
);


ALTER TABLE public.los_order OWNER TO jboss;

--
-- Name: los_orderreceipt; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_orderreceipt (
    id bigint NOT NULL,
    date timestamp without time zone NOT NULL,
    destination character varying(255),
    ordernumber character varying(255) NOT NULL,
    orderreference character varying(255),
    ordertype character varying(255) NOT NULL,
    state character varying(255),
    user_ character varying(255) NOT NULL
);


ALTER TABLE public.los_orderreceipt OWNER TO jboss;

--
-- Name: los_orderreceiptpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_orderreceiptpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4),
    amountordered numeric(17,4),
    articledescr character varying(255),
    articleref character varying(255),
    articlescale integer NOT NULL,
    lotref character varying(255),
    pos integer NOT NULL,
    client_id bigint NOT NULL,
    receipt_id bigint
);


ALTER TABLE public.los_orderreceiptpos OWNER TO jboss;

--
-- Name: los_orderreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_orderreq (
    id bigint NOT NULL,
    delivery date,
    documenturl character varying(255),
    labelurl character varying(255),
    orderstate character varying(255),
    ordertype character varying(255),
    requestid character varying(255),
    destination_id bigint NOT NULL
);


ALTER TABLE public.los_orderreq OWNER TO jboss;

--
-- Name: los_orderreqpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_orderreqpos (
    id bigint NOT NULL,
    amount numeric(17,4),
    partitionallowed boolean NOT NULL,
    pickedamount numeric(17,4),
    positionindex integer NOT NULL,
    positionstate character varying(255),
    itemdata_id bigint NOT NULL,
    parentrequest_id bigint NOT NULL,
    lot_id bigint
);


ALTER TABLE public.los_orderreqpos OWNER TO jboss;

--
-- Name: los_outpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_outpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    outstate character varying(255),
    source_id bigint NOT NULL,
    goodsoutrequest_id bigint NOT NULL
);


ALTER TABLE public.los_outpos OWNER TO jboss;

--
-- Name: los_outreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_outreq (
    id bigint NOT NULL,
    outstate character varying(255),
    parentrequest_id bigint NOT NULL,
    operator_id bigint
);


ALTER TABLE public.los_outreq OWNER TO jboss;

--
-- Name: los_pickreceipt; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickreceipt (
    id bigint NOT NULL,
    date timestamp without time zone,
    labelid character varying(255),
    ordernumber character varying(255),
    picknumber character varying(255),
    state character varying(255)
);


ALTER TABLE public.los_pickreceipt OWNER TO jboss;

--
-- Name: los_pickreceiptpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickreceiptpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4),
    amountordered numeric(17,4),
    articledescr character varying(255),
    articleref character varying(255),
    lotref character varying(255),
    receipt_id bigint
);


ALTER TABLE public.los_pickreceiptpos OWNER TO jboss;

--
-- Name: los_pickreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickreq (
    id bigint NOT NULL,
    customernumber character varying(255),
    state character varying(255),
    destination_id bigint NOT NULL,
    cart_id bigint,
    user_id bigint,
    parentrequest_id bigint NOT NULL
);


ALTER TABLE public.los_pickreq OWNER TO jboss;

--
-- Name: los_pickreqpos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_pickreqpos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4) NOT NULL,
    canceled boolean NOT NULL,
    pos_index integer,
    pickedamount numeric(17,4) NOT NULL,
    pickingdimensiontype character varying(255),
    pickingsupplytype character varying(255),
    solved boolean NOT NULL,
    substitutiontype character varying(255),
    withdrawaltype character varying(255),
    parentrequest_id bigint NOT NULL,
    complementon_id bigint,
    stockunit_id bigint NOT NULL,
    pickrequest_id bigint NOT NULL
);


ALTER TABLE public.los_pickreqpos OWNER TO jboss;

--
-- Name: los_rack; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_rack (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    labeloffset integer,
    rname character varying(255) NOT NULL,
    numberofcolumns integer NOT NULL,
    numberofrows integer NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_rack OWNER TO jboss;

--
-- Name: los_racklocation; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_racklocation (
    id bigint NOT NULL,
    xpos integer NOT NULL,
    ypos integer NOT NULL,
    zpos integer NOT NULL,
    rack_id bigint NOT NULL
);


ALTER TABLE public.los_racklocation OWNER TO jboss;

--
-- Name: los_replenishreq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_replenishreq (
    id bigint NOT NULL
);


ALTER TABLE public.los_replenishreq OWNER TO jboss;

--
-- Name: los_replenishreq_los_storloc; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_replenishreq_los_storloc (
    los_replenishreq_id bigint NOT NULL,
    allowedsources_id bigint NOT NULL
);


ALTER TABLE public.los_replenishreq_los_storloc OWNER TO jboss;

--
-- Name: los_sequencenumber; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sequencenumber (
    classname character varying(255) NOT NULL,
    sequencenumber bigint NOT NULL,
    version integer NOT NULL
);


ALTER TABLE public.los_sequencenumber OWNER TO jboss;

--
-- Name: los_serviceconf; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_serviceconf (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    servkey character varying(255) NOT NULL,
    service character varying(255) NOT NULL,
    subkey character varying(255),
    servvalue character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.los_serviceconf OWNER TO jboss;

--
-- Name: los_sllabel; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sllabel (
    id bigint NOT NULL,
    labelid character varying(255)
);


ALTER TABLE public.los_sllabel OWNER TO jboss;

--
-- Name: los_stockrecord; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stockrecord (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    activitycode character varying(255) NOT NULL,
    amount numeric(17,4),
    amountstock numeric(17,4),
    fromstockunitidentity character varying(255),
    fromstoragelocation character varying(255) NOT NULL,
    fromunitload character varying(255),
    itemdata character varying(255),
    lot character varying(255),
    operator character varying(255) NOT NULL,
    scale integer NOT NULL,
    serialnumber character varying(255),
    tostockunitidentity character varying(255),
    tostoragelocation character varying(255) NOT NULL,
    tounitload character varying(255),
    type character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.los_stockrecord OWNER TO jboss;

--
-- Name: los_stocktaking; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stocktaking (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    ended timestamp without time zone,
    started timestamp without time zone,
    stocktakingnumber character varying(255) NOT NULL,
    stocktakingtype character varying(255)
);


ALTER TABLE public.los_stocktaking OWNER TO jboss;

--
-- Name: los_stocktakingorder; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stocktakingorder (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    areaname character varying(255),
    countingdate timestamp without time zone,
    locationname character varying(255),
    operator character varying(255),
    state integer,
    unitloadlabel character varying(255),
    stocktaking_id bigint
);


ALTER TABLE public.los_stocktakingorder OWNER TO jboss;

--
-- Name: los_stocktakingrecord; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_stocktakingrecord (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    clientno character varying(255),
    countedquantity numeric(19,2),
    countedstockid bigint,
    itemno character varying(255),
    locationname character varying(255),
    lotno character varying(255),
    plannedquantity numeric(19,2),
    serialno character varying(255),
    state integer,
    ultypeno character varying(255),
    unitloadlabel character varying(255),
    stocktakingorder_id bigint
);


ALTER TABLE public.los_stocktakingrecord OWNER TO jboss;

--
-- Name: los_storagelocationtype; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_storagelocationtype (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    depth numeric(15,2),
    height numeric(15,2),
    liftingcapacity numeric(16,3),
    sltname character varying(255) NOT NULL,
    volume numeric(19,6),
    width numeric(15,2)
);


ALTER TABLE public.los_storagelocationtype OWNER TO jboss;

--
-- Name: los_storagereq; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_storagereq (
    id bigint NOT NULL,
    requeststate character varying(255),
    destination_id bigint,
    unitload_id bigint
);


ALTER TABLE public.los_storagereq OWNER TO jboss;

--
-- Name: los_storloc; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_storloc (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    name character varying(255) NOT NULL,
    reservedcapacity integer NOT NULL,
    stocktakingdate timestamp without time zone,
    client_id bigint NOT NULL,
    cluster_id bigint,
    area_id bigint,
    currenttcc bigint,
    type_id bigint NOT NULL,
    zone_id bigint
);


ALTER TABLE public.los_storloc OWNER TO jboss;

--
-- Name: los_sulabel; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sulabel (
    id bigint NOT NULL,
    amount numeric(19,2) NOT NULL,
    clientref character varying(255) NOT NULL,
    dateref character varying(255) NOT NULL,
    itemunit character varying(255) NOT NULL,
    itemdataref character varying(255) NOT NULL,
    labelid character varying(255) NOT NULL,
    lotref character varying(255),
    scale integer NOT NULL
);


ALTER TABLE public.los_sulabel OWNER TO jboss;

--
-- Name: los_sysprop; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_sysprop (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    description character varying(255),
    groupname character varying(255),
    hidden boolean NOT NULL,
    syskey character varying(255) NOT NULL,
    sysvalue character varying(255) NOT NULL,
    workstation character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_sysprop OWNER TO jboss;

--
-- Name: los_typecapacityconstraint; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_typecapacityconstraint (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    capacity integer NOT NULL,
    name character varying(255) NOT NULL,
    unitloadtype_id bigint,
    storagelocationtype_id bigint NOT NULL
);


ALTER TABLE public.los_typecapacityconstraint OWNER TO jboss;

--
-- Name: los_ul_record; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_ul_record (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    activitycode character varying(255),
    fromlocation character varying(255) NOT NULL,
    label character varying(255) NOT NULL,
    operator character varying(255),
    recordtype character varying(255) NOT NULL,
    tolocation character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_ul_record OWNER TO jboss;

--
-- Name: los_uladvice; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_uladvice (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    advicestate character varying(255) NOT NULL,
    advicetype character varying(255) NOT NULL,
    externalnumber character varying(255),
    labelid character varying(255) NOT NULL,
    number character varying(255) NOT NULL,
    reasonforreturn character varying(255),
    switchstateinfo character varying(255),
    client_id bigint NOT NULL,
    unitloadtype_id bigint,
    relatedadvice_id bigint
);


ALTER TABLE public.los_uladvice OWNER TO jboss;

--
-- Name: los_uladvicepos; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_uladvicepos (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    notifiedamount numeric(17,4) NOT NULL,
    positionnumber character varying(255) NOT NULL,
    lot_id bigint,
    itemdata_id bigint NOT NULL,
    unitloadadvice_id bigint NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.los_uladvicepos OWNER TO jboss;

--
-- Name: los_unitload; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE los_unitload (
    id bigint NOT NULL,
    packagetype character varying(255),
    stocktakingdate timestamp without time zone,
    weightgross numeric(16,3),
    weightnet numeric(16,3),
    storagelocation_id bigint NOT NULL
);


ALTER TABLE public.los_unitload OWNER TO jboss;

--
-- Name: mywms_area; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_area (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    name character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_area OWNER TO jboss;

--
-- Name: mywms_clearingitem; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_clearingitem (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    bundleresolver oid,
    host character varying(255) NOT NULL,
    messageparameters bytea NOT NULL,
    messageresourcekey character varying(255) NOT NULL,
    options oid NOT NULL,
    propertymap bytea,
    resourcebundlename character varying(255) NOT NULL,
    shortmessageparameters bytea NOT NULL,
    shortmessageresourcekey character varying(255) NOT NULL,
    solution oid,
    solver character varying(255),
    source character varying(255) NOT NULL,
    user_ character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_clearingitem OWNER TO jboss;

--
-- Name: mywms_client; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_client (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    cl_code character varying(255),
    email character varying(255),
    fax character varying(255),
    name character varying(255) NOT NULL,
    cl_nr character varying(255) NOT NULL,
    phone character varying(255)
);


ALTER TABLE public.mywms_client OWNER TO jboss;

--
-- Name: mywms_document; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_document (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    document oid NOT NULL,
    name character varying(255) NOT NULL,
    document_size integer,
    type character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_document OWNER TO jboss;

--
-- Name: mywms_itemdata; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_itemdata (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    advicemandatory boolean NOT NULL,
    depth numeric(15,2),
    descr character varying(255),
    height numeric(15,2),
    lotmandatory boolean NOT NULL,
    lotsubstitutiontype character varying(255),
    name character varying(255) NOT NULL,
    item_nr character varying(255) NOT NULL,
    rest_usage_gi integer,
    safetystock integer NOT NULL,
    scale integer NOT NULL,
    serialrectype character varying(255) NOT NULL,
    tradegroup character varying(255),
    volume numeric(19,6),
    weight numeric(16,3),
    width numeric(15,2),
    defultype_id bigint,
    handlingunit_id bigint NOT NULL,
    client_id bigint NOT NULL,
    zone_id bigint
);


ALTER TABLE public.mywms_itemdata OWNER TO jboss;

--
-- Name: mywms_itemunit; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_itemunit (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    basefactor integer NOT NULL,
    unitname character varying(255) NOT NULL,
    unittype character varying(255),
    baseunit_id bigint
);


ALTER TABLE public.mywms_itemunit OWNER TO jboss;

--
-- Name: mywms_logitem; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_logitem (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    bundleresolver character varying(255),
    host character varying(255) NOT NULL,
    message character varying(255) NOT NULL,
    messageparameters bytea,
    messageresourcekey character varying(255) NOT NULL,
    resourcebundlename character varying(255) NOT NULL,
    source character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    user_ character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_logitem OWNER TO jboss;

--
-- Name: mywms_lot; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_lot (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    age character varying(255),
    bestbeforeend date,
    code character varying(255),
    lot_date date NOT NULL,
    depth numeric(15,2),
    height numeric(15,2),
    name character varying(255),
    usenotbefore date,
    volume numeric(19,6),
    weight numeric(16,3),
    width numeric(15,2),
    itemdata_id bigint NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_lot OWNER TO jboss;

--
-- Name: mywms_pluginconfiguration; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_pluginconfiguration (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    plugin_mode character varying(255) NOT NULL,
    pluginclass character varying(255) NOT NULL,
    pluginname character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_pluginconfiguration OWNER TO jboss;

--
-- Name: mywms_request; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_request (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    request_nr character varying(255) NOT NULL,
    parentrequestnumber character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_request OWNER TO jboss;

--
-- Name: mywms_role; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_role (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    description character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.mywms_role OWNER TO jboss;

--
-- Name: mywms_stockunit; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_stockunit (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    amount numeric(17,4) NOT NULL,
    labelid character varying(255) NOT NULL,
    reservedamount numeric(17,4),
    serialnumber character varying(255),
    lot_id bigint,
    client_id bigint NOT NULL,
    itemdata_id bigint NOT NULL,
    unitload_id bigint NOT NULL
);


ALTER TABLE public.mywms_stockunit OWNER TO jboss;

--
-- Name: mywms_unitload; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_unitload (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    location_index integer,
    labelid character varying(255) NOT NULL,
    type_id bigint NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_unitload OWNER TO jboss;

--
-- Name: mywms_unitloadtype; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_unitloadtype (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    depth numeric(15,2),
    height numeric(15,2),
    liftingcapacity numeric(16,3),
    name character varying(255) NOT NULL,
    volume numeric(19,6),
    weight numeric(16,3),
    width numeric(15,2)
);


ALTER TABLE public.mywms_unitloadtype OWNER TO jboss;

--
-- Name: mywms_user; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_user (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    locale character varying(255),
    name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    phone character varying(255),
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_user OWNER TO jboss;

--
-- Name: mywms_user_mywms_role; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_user_mywms_role (
    mywms_user_id bigint NOT NULL,
    roles_id bigint NOT NULL
);


ALTER TABLE public.mywms_user_mywms_role OWNER TO jboss;

--
-- Name: mywms_zone; Type: TABLE; Schema: public; Owner: jboss; Tablespace: 
--

CREATE TABLE mywms_zone (
    id bigint NOT NULL,
    additionalcontent character varying(255),
    created timestamp without time zone,
    entity_lock integer,
    modified timestamp without time zone,
    version integer NOT NULL,
    name character varying(255) NOT NULL,
    client_id bigint NOT NULL
);


ALTER TABLE public.mywms_zone OWNER TO jboss;

--
-- Name: seqentities; Type: SEQUENCE; Schema: public; Owner: jboss
--

CREATE SEQUENCE seqentities
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.seqentities OWNER TO jboss;

--
-- Name: seqentities; Type: SEQUENCE SET; Schema: public; Owner: jboss
--

SELECT pg_catalog.setval('seqentities', 23, true);


--
-- Data for Name: jms_messages; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY jms_messages (messageid, destination, txid, txop, messageblob) FROM stdin;
\.


--
-- Data for Name: jms_roles; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY jms_roles (roleid, userid) FROM stdin;
controller	LOSListenerBean
\.


--
-- Data for Name: jms_subscriptions; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY jms_subscriptions (clientid, subname, topic, selector) FROM stdin;
\.


--
-- Data for Name: jms_transactions; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY jms_transactions (txid) FROM stdin;
\.


--
-- Data for Name: jms_users; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY jms_users (userid, passwd, clientid) FROM stdin;
LOSListenerBean	lino_lib	\N
\.


--
-- Data for Name: los_area; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_area (id, areatype) FROM stdin;
250	GOODS_IN
251	GOODS_OUT
252	STORE
253	QUARANTINE
254	PICKING
\.


--
-- Data for Name: los_avisreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_avisreq (id, additionalcontent, created, entity_lock, modified, version, advicenumber, advicestate, expecteddelivery, expirebatch, externalno, externalid, notifiedamount, receiptamount, itemdata_id, lot_id, client_id) FROM stdin;
950	\N	2010-11-30 16:19:58.825	0	2010-11-30 16:19:58.915	1	AVIS 000001	RAW	2010-11-30	f	\N	\N	16.0000	0.0000	600	\N	0
951	\N	2010-11-30 16:19:58.844	0	2010-11-30 16:19:58.915	1	AVIS 000002	RAW	2010-11-30	f	\N	\N	2000.0000	0.0000	602	\N	0
952	\N	2010-11-30 16:19:58.867	0	2010-11-30 16:19:58.915	1	AVIS 000003	RAW	2010-11-30	f	\N	\N	572.0000	0.0000	604	\N	0
\.


--
-- Data for Name: los_bom; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_bom (id, additionalcontent, created, entity_lock, modified, version, amount, index, pickable, parent_id, child_id) FROM stdin;
\.


--
-- Data for Name: los_extinguishreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_extinguishreq (id, lot_id) FROM stdin;
\.


--
-- Data for Name: los_extorder; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_extorder (id, authorizedby_id, lot_id) FROM stdin;
\.


--
-- Data for Name: los_fixassgn; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_fixassgn (id, additionalcontent, created, entity_lock, modified, version, desiredamount, itemdata_id, assignedlocation_id) FROM stdin;
700	\N	2010-11-30 16:19:57.739	0	2010-11-30 16:19:57.739	0	400.0000	602	570
701	\N	2010-11-30 16:19:57.786	0	2010-11-30 16:19:57.786	0	400.0000	603	572
\.


--
-- Data for Name: los_goodsreceipt; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_goodsreceipt (id, additionalcontent, created, entity_lock, modified, version, delnote, drivername, forwarder, gr_number, licenceplate, receiptdate, receiptstate, referenceno, client_id, goodsinlocation_id, operator_id) FROM stdin;
\.


--
-- Data for Name: los_goodsreceipt_los_avisreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_goodsreceipt_los_avisreq (los_goodsreceipt_id, assignedadvices_id) FROM stdin;
\.


--
-- Data for Name: los_grrposition; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_grrposition (id, additionalcontent, created, entity_lock, modified, version, amount, itemdata, lot, orderreference, positionnumber, qafault, receipttype, scale, state, stockunitstr, unitload, relatedadvice_id, stockunit_id, client_id, goodsreceipt_id) FROM stdin;
\.


--
-- Data for Name: los_inventory; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_inventory (id, additionalcontent, created, entity_lock, modified, version, advised, available, instock, itemdataref, lastamount, lastincoming, locked, lotref, reserved, client_id) FROM stdin;
\.


--
-- Data for Name: los_itemdata_number; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_itemdata_number (id, additionalcontent, created, entity_lock, modified, version, index, manufacturername, number, itemdata_id, client_id) FROM stdin;
650	\N	2010-11-30 16:19:57.473	0	2010-11-30 16:19:57.477	0	0	\N	12345678	602	0
651	\N	2010-11-30 16:19:57.563	0	2010-11-30 16:19:57.563	0	0	\N	12312312	604	0
\.


--
-- Data for Name: los_jrxml; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_jrxml (id, additionalcontent, created, entity_lock, modified, version, jrxml, name, client_id) FROM stdin;
\.


--
-- Data for Name: los_locationcluster; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_locationcluster (id, additionalcontent, created, entity_lock, modified, version, name) FROM stdin;
\.


--
-- Data for Name: los_order; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_order (id) FROM stdin;
1000
1004
1007
1011
1015
\.


--
-- Data for Name: los_orderreceipt; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_orderreceipt (id, date, destination, ordernumber, orderreference, ordertype, state, user_) FROM stdin;
\.


--
-- Data for Name: los_orderreceiptpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_orderreceiptpos (id, additionalcontent, created, entity_lock, modified, version, amount, amountordered, articledescr, articleref, articlescale, lotref, pos, client_id, receipt_id) FROM stdin;
\.


--
-- Data for Name: los_orderreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_orderreq (id, delivery, documenturl, labelurl, orderstate, ordertype, requestid, destination_id) FROM stdin;
1000	2010-11-30	\N	\N	PROCESSING	TO_CUSTOMER	ORDER 000001	451
1004	2010-11-30	\N	\N	PROCESSING	TO_CUSTOMER	ORDER 000002	451
1007	2010-11-30	\N	\N	PROCESSING	TO_CUSTOMER	ORDER 000003	451
1011	2010-11-30	\N	\N	PROCESSING	TO_CUSTOMER	ORDER 000004	451
1015	2010-11-30	\N	\N	PROCESSING	TO_CUSTOMER	ORDER 000005	451
\.


--
-- Data for Name: los_orderreqpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_orderreqpos (id, amount, partitionallowed, pickedamount, positionindex, positionstate, itemdata_id, parentrequest_id, lot_id) FROM stdin;
1001	1.0000	t	0.0000	0	PROCESSING	601	1000	\N
1002	1.0000	t	0.0000	1	PROCESSING	602	1000	\N
1005	1.0000	t	0.0000	0	PROCESSING	600	1004	\N
1008	1.0000	t	0.0000	0	PROCESSING	600	1007	\N
1009	1.0000	t	0.0000	1	PROCESSING	602	1007	\N
1012	10.0000	t	0.0000	0	PROCESSING	602	1011	\N
1013	1.0000	t	0.0000	1	PROCESSING	604	1011	\N
1016	10.0000	t	0.0000	0	PROCESSING	602	1015	\N
1017	1.0000	t	0.0000	1	PROCESSING	604	1015	\N
\.


--
-- Data for Name: los_outpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_outpos (id, additionalcontent, created, entity_lock, modified, version, outstate, source_id, goodsoutrequest_id) FROM stdin;
\.


--
-- Data for Name: los_outreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_outreq (id, outstate, parentrequest_id, operator_id) FROM stdin;
\.


--
-- Data for Name: los_pickreceipt; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickreceipt (id, date, labelid, ordernumber, picknumber, state) FROM stdin;
\.


--
-- Data for Name: los_pickreceiptpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickreceiptpos (id, additionalcontent, created, entity_lock, modified, version, amount, amountordered, articledescr, articleref, lotref, receipt_id) FROM stdin;
\.


--
-- Data for Name: los_pickreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickreq (id, customernumber, state, destination_id, cart_id, user_id, parentrequest_id) FROM stdin;
1003	System	RAW	451	582	1	1000
1006	System	RAW	451	583	1	1004
1010	System	RAW	451	584	1	1007
1014	System	RAW	451	585	1	1011
1018	System	RAW	451	586	1	1015
\.


--
-- Data for Name: los_pickreqpos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_pickreqpos (id, additionalcontent, created, entity_lock, modified, version, amount, canceled, pos_index, pickedamount, pickingdimensiontype, pickingsupplytype, solved, substitutiontype, withdrawaltype, parentrequest_id, complementon_id, stockunit_id, pickrequest_id) FROM stdin;
1050	\N	2010-11-30 16:19:59.033	0	2010-11-30 16:19:59.034	0	1.0000	f	0	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1001	\N	807	1003
1051	\N	2010-11-30 16:19:59.053	0	2010-11-30 16:19:59.089	1	1.0000	f	1	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1002	\N	800	1003
1052	\N	2010-11-30 16:19:59.145	0	2010-11-30 16:19:59.145	0	1.0000	f	0	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1005	\N	805	1006
1053	\N	2010-11-30 16:19:59.248	0	2010-11-30 16:19:59.248	0	1.0000	f	0	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1008	\N	805	1010
1054	\N	2010-11-30 16:19:59.265	0	2010-11-30 16:19:59.297	1	1.0000	f	1	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1009	\N	800	1010
1056	\N	2010-11-30 16:19:59.463	0	2010-11-30 16:19:59.463	0	1.0000	f	0	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1013	\N	808	1014
1055	\N	2010-11-30 16:19:59.425	0	2010-11-30 16:19:59.499	1	10.0000	f	1	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1012	\N	800	1014
1058	\N	2010-11-30 16:19:59.577	0	2010-11-30 16:19:59.577	0	1.0000	f	0	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1017	\N	808	1018
1057	\N	2010-11-30 16:19:59.558	0	2010-11-30 16:19:59.633	1	10.0000	f	1	0.0000	ONE_DIMENSIONAL	STATIC_DECENTRALIZED	f	SUBSTITUTION_SAME_LOT	UNORDERED_FROM_STOCKUNIT	1016	\N	800	1018
\.


--
-- Data for Name: los_rack; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_rack (id, additionalcontent, created, entity_lock, modified, version, labeloffset, rname, numberofcolumns, numberofrows, client_id) FROM stdin;
500	\N	2010-11-30 16:19:56.651	0	2010-11-30 16:19:56.661	1	\N	Palette A1	5	2	0
501	\N	2010-11-30 16:19:56.874	0	2010-11-30 16:19:56.881	1	\N	Fachboden A2	3	2	0
502	\N	2010-11-30 16:19:57.193	0	2010-11-30 16:19:57.207	1	\N	Pick Festplatz 01	2	2	0
\.


--
-- Data for Name: los_racklocation; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_racklocation (id, xpos, ypos, zpos, rack_id) FROM stdin;
454	0	0	0	500
455	0	0	0	500
456	0	0	0	500
457	0	0	0	500
458	0	0	0	500
459	0	0	0	500
460	0	0	0	500
461	0	0	0	500
462	0	0	0	500
463	0	0	0	500
464	0	0	0	500
465	0	0	0	500
466	0	0	0	500
467	0	0	0	500
468	0	0	0	500
469	0	0	0	500
470	0	0	0	500
471	0	0	0	500
472	0	0	0	500
473	0	0	0	500
474	0	0	0	500
475	0	0	0	500
476	0	0	0	500
477	0	0	0	500
478	0	0	0	500
479	0	0	0	500
480	0	0	0	500
481	0	0	0	500
482	0	0	0	500
483	0	0	0	500
484	0	0	0	501
485	0	0	0	501
486	0	0	0	501
487	0	0	0	501
488	0	0	0	501
489	0	0	0	501
490	0	0	0	501
491	0	0	0	501
492	0	0	0	501
493	0	0	0	501
494	0	0	0	501
495	0	0	0	501
496	0	0	0	501
497	0	0	0	501
498	0	0	0	501
499	0	0	0	501
550	0	0	0	501
551	0	0	0	501
552	0	0	0	501
553	0	0	0	501
554	0	0	0	501
555	0	0	0	501
556	0	0	0	501
557	0	0	0	501
558	0	0	0	501
559	0	0	0	501
560	0	0	0	501
561	0	0	0	501
562	0	0	0	501
563	0	0	0	501
564	0	0	0	501
565	0	0	0	501
566	0	0	0	501
567	0	0	0	501
568	0	0	0	501
569	0	0	0	501
570	0	0	0	502
571	0	0	0	502
572	0	0	0	502
573	0	0	0	502
574	0	0	0	502
575	0	0	0	502
576	0	0	0	502
577	0	0	0	502
578	0	0	0	502
579	0	0	0	502
580	0	0	0	502
581	0	0	0	502
\.


--
-- Data for Name: los_replenishreq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_replenishreq (id) FROM stdin;
\.


--
-- Data for Name: los_replenishreq_los_storloc; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_replenishreq_los_storloc (los_replenishreq_id, allowedsources_id) FROM stdin;
\.


--
-- Data for Name: los_sequencenumber; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sequencenumber (classname, sequencenumber, version) FROM stdin;
de.linogistix.los.inventory.ws.ManageInventory	13	25
de.linogistix.los.inventory.model.LOSAdvice	3	5
de.linogistix.los.inventory.model.LOSOrderRequest	5	9
de.linogistix.los.inventory.pick.model.LOSPickRequest	5	9
de.linogistix.los.stocktaking.model.LOSStockTaking	1	1
\.


--
-- Data for Name: los_serviceconf; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_serviceconf (id, additionalcontent, created, entity_lock, modified, version, servkey, service, subkey, servvalue, client_id) FROM stdin;
\.


--
-- Data for Name: los_sllabel; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sllabel (id, labelid) FROM stdin;
\.


--
-- Data for Name: los_stockrecord; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stockrecord (id, additionalcontent, created, entity_lock, modified, version, activitycode, amount, amountstock, fromstockunitidentity, fromstoragelocation, fromunitload, itemdata, lot, operator, scale, serialnumber, tostockunitidentity, tostoragelocation, tounitload, type, client_id) FROM stdin;
850	\N	2010-11-30 16:19:57.893	0	2010-11-30 16:19:57.899	0	IMAN 000001	200.0000	200.0000	800	P1-011-1	P1-011-1	14009724	\N	deutsch	0	\N	800	P1-011-1	P1-011-1	STOCK_CREATED	0
851	\N	2010-11-30 16:19:57.986	0	2010-11-30 16:19:57.989	0	IMAN 000002	200.0000	200.0000	801	A1-011-1	000001	14009724	\N	deutsch	0	\N	801	A1-011-1	000001	STOCK_CREATED	0
852	\N	2010-11-30 16:19:58.049	0	2010-11-30 16:19:58.052	0	IMAN 000003	200.0000	200.0000	802	A1-012-1	000002	14009724	\N	deutsch	0	\N	802	A1-012-1	000002	STOCK_CREATED	0
853	\N	2010-11-30 16:19:58.117	0	2010-11-30 16:19:58.12	0	IMAN 000004	200.0000	200.0000	803	A1-021-1	000003	14009725	\N	deutsch	0	\N	803	A1-021-1	000003	STOCK_CREATED	0
854	\N	2010-11-30 16:19:58.172	0	2010-11-30 16:19:58.176	0	IMAN 000005	200.0000	200.0000	804	A1-022-1	000004	14009725	\N	deutsch	0	\N	804	A1-022-1	000004	STOCK_CREATED	0
855	\N	2010-11-30 16:19:58.226	0	2010-11-30 16:19:58.23	0	IMAN 000006	8.0000	8.0000	805	A1-011-2	000005	14008712	\N	deutsch	0	\N	805	A1-011-2	000005	STOCK_CREATED	0
856	\N	2010-11-30 16:19:58.304	0	2010-11-30 16:19:58.307	0	IMAN 000007	8.0000	8.0000	806	A1-012-2	000006	14008712	\N	deutsch	0	\N	806	A1-012-2	000006	STOCK_CREATED	0
857	\N	2010-11-30 16:19:58.354	0	2010-11-30 16:19:58.36	0	IMAN 000008	8.0000	8.0000	807	A1-013-2	000007	14008715	\N	deutsch	0	\N	807	A1-013-2	000007	STOCK_CREATED	0
858	\N	2010-11-30 16:19:58.448	0	2010-11-30 16:19:58.451	0	IMAN 000009	144.0000	144.0000	808	A1-021-2	000008	14006411	1582 (14006411)	deutsch	0	\N	808	A1-021-2	000008	STOCK_CREATED	0
859	\N	2010-11-30 16:19:58.551	0	2010-11-30 16:19:58.554	0	IMAN 000010	144.0000	144.0000	809	A1-022-2	000009	14006411	1582 (14006411)	deutsch	0	\N	809	A1-022-2	000009	STOCK_CREATED	0
860	\N	2010-11-30 16:19:58.684	0	2010-11-30 16:19:58.688	0	IMAN 000011	144.0000	144.0000	810	A1-023-2	000010	14006411	1582 (14006411)	deutsch	0	\N	810	A1-023-2	000010	STOCK_CREATED	0
861	\N	2010-11-30 16:19:58.745	0	2010-11-30 16:19:58.748	0	IMAN 000012	72.0000	72.0000	811	A1-031-2	000011	14006413	\N	deutsch	0	\N	811	A1-031-2	000011	STOCK_CREATED	0
862	\N	2010-11-30 16:19:58.796	0	2010-11-30 16:19:58.799	0	IMAN 000013	15000.0000	15000.0000	812	A2-011-1	000012	10002830	\N	deutsch	0	\N	812	A2-011-1	000012	STOCK_CREATED	0
\.


--
-- Data for Name: los_stocktaking; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stocktaking (id, additionalcontent, created, entity_lock, modified, version, ended, started, stocktakingnumber, stocktakingtype) FROM stdin;
1100	\N	2010-11-30 16:19:59.602	0	2010-11-30 16:19:59.62	0	\N	2010-11-30 16:19:59.619	IV 00000001	END_OF_PERIOD_INVENTORY
\.


--
-- Data for Name: los_stocktakingorder; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stocktakingorder (id, additionalcontent, created, entity_lock, modified, version, areaname, countingdate, locationname, operator, state, unitloadlabel, stocktaking_id) FROM stdin;
1150	\N	2010-11-30 16:19:59.629	0	2010-11-30 16:19:59.63	0	\N	\N	A1-011-1	\N	0	\N	1100
1151	\N	2010-11-30 16:19:59.639	0	2010-11-30 16:19:59.639	0	\N	\N	A1-012-1	\N	0	\N	1100
1152	\N	2010-11-30 16:19:59.641	0	2010-11-30 16:19:59.641	0	\N	\N	A1-013-1	\N	0	\N	1100
\.


--
-- Data for Name: los_stocktakingrecord; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_stocktakingrecord (id, additionalcontent, created, entity_lock, modified, version, clientno, countedquantity, countedstockid, itemno, locationname, lotno, plannedquantity, serialno, state, ultypeno, unitloadlabel, stocktakingorder_id) FROM stdin;
\.


--
-- Data for Name: los_storagelocationtype; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_storagelocationtype (id, additionalcontent, created, entity_lock, modified, version, depth, height, liftingcapacity, sltname, volume, width) FROM stdin;
1	Lagerplatztyp für Systemplätze. \n\nDies ist ein System-Datensatz. NICHT ENTFERNEN ODER SPERREN! Er wird von einigen Prozessen verwendet.	2010-11-30 16:19:56.152	0	2010-11-30 16:19:56.154	1	\N	\N	\N	System	\N	\N
0	Defaultwert für Lagerplätze.\n\nDies ist ein System-Datensatz. NICHT ENTFERNEN ODER SPERREN! Er wird von einigen Prozessen verwendet.	2010-11-30 16:19:56.113	0	2010-11-30 16:19:56.529	2	\N	\N	\N	Palette	\N	\N
2	Lagerplatztyp für fest zugeordnete Kommissionierplätze. \n\nDies ist ein System-Datensatz. NICHT ENTFERNEN ODER SPERREN! Er wird von einigen Prozessen verwendet.	2010-11-30 16:19:56.18	0	2010-11-30 16:19:56.549	2	\N	\N	\N	Kommissionierung	\N	\N
303	\N	2010-11-30 16:19:56.551	0	2010-11-30 16:19:56.551	0	\N	\N	\N	Fachboden	\N	\N
\.


--
-- Data for Name: los_storagereq; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_storagereq (id, requeststate, destination_id, unitload_id) FROM stdin;
\.


--
-- Data for Name: los_storloc; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_storloc (id, additionalcontent, created, entity_lock, modified, version, name, reservedcapacity, stocktakingdate, client_id, cluster_id, area_id, currenttcc, type_id, zone_id) FROM stdin;
450	\N	2010-11-30 16:19:56.317	0	2010-11-30 16:19:56.324	1	Wareneingang	0	\N	0	\N	250	\N	1	\N
451	\N	2010-11-30 16:19:56.335	0	2010-11-30 16:19:56.356	1	Warenausgang	0	\N	0	\N	251	\N	1	\N
1	Dies ist ein System-Datensatz. NICHT ENTFERNEN ODER SPERREN! Er wird von einigen Prozessen verwendet.	2010-11-30 16:19:56.365	0	2010-11-30 16:19:56.368	1	Klärung	0	\N	0	\N	\N	\N	1	\N
0	Dies ist ein System-Datensatz. NICHT ENTFERNEN ODER SPERREN! Er wird von einigen Prozessen verwendet.	2010-11-30 16:19:56.421	0	2010-11-30 16:19:56.427	1	Nirwana	0	\N	0	\N	\N	\N	1	\N
454	\N	2010-11-30 16:19:56.659	0	2010-11-30 16:19:56.661	1	A1-011-1	0	\N	0	\N	252	\N	0	200
455	\N	2010-11-30 16:19:56.68	0	2010-11-30 16:19:56.682	1	A1-011-2	0	\N	0	\N	252	\N	0	200
456	\N	2010-11-30 16:19:56.689	0	2010-11-30 16:19:56.692	1	A1-012-1	0	\N	0	\N	252	\N	0	200
457	\N	2010-11-30 16:19:56.695	0	2010-11-30 16:19:56.697	1	A1-012-2	0	\N	0	\N	252	\N	0	200
458	\N	2010-11-30 16:19:56.701	0	2010-11-30 16:19:56.703	1	A1-013-1	0	\N	0	\N	252	\N	0	200
459	\N	2010-11-30 16:19:56.707	0	2010-11-30 16:19:56.711	1	A1-013-2	0	\N	0	\N	252	\N	0	200
460	\N	2010-11-30 16:19:56.714	0	2010-11-30 16:19:56.716	1	A1-021-1	0	\N	0	\N	252	\N	0	200
461	\N	2010-11-30 16:19:56.719	0	2010-11-30 16:19:56.722	1	A1-021-2	0	\N	0	\N	252	\N	0	200
462	\N	2010-11-30 16:19:56.726	0	2010-11-30 16:19:56.728	1	A1-022-1	0	\N	0	\N	252	\N	0	200
463	\N	2010-11-30 16:19:56.731	0	2010-11-30 16:19:56.733	1	A1-022-2	0	\N	0	\N	252	\N	0	200
464	\N	2010-11-30 16:19:56.736	0	2010-11-30 16:19:56.739	1	A1-023-1	0	\N	0	\N	252	\N	0	200
465	\N	2010-11-30 16:19:56.741	0	2010-11-30 16:19:56.744	1	A1-023-2	0	\N	0	\N	252	\N	0	200
466	\N	2010-11-30 16:19:56.747	0	2010-11-30 16:19:56.749	1	A1-031-1	0	\N	0	\N	252	\N	0	200
467	\N	2010-11-30 16:19:56.752	0	2010-11-30 16:19:56.755	1	A1-031-2	0	\N	0	\N	252	\N	0	200
468	\N	2010-11-30 16:19:56.758	0	2010-11-30 16:19:56.761	1	A1-032-1	0	\N	0	\N	252	\N	0	200
469	\N	2010-11-30 16:19:56.764	0	2010-11-30 16:19:56.767	1	A1-032-2	0	\N	0	\N	252	\N	0	200
470	\N	2010-11-30 16:19:56.771	0	2010-11-30 16:19:56.773	1	A1-033-1	0	\N	0	\N	252	\N	0	200
471	\N	2010-11-30 16:19:56.776	0	2010-11-30 16:19:56.779	1	A1-033-2	0	\N	0	\N	252	\N	0	200
472	\N	2010-11-30 16:19:56.782	0	2010-11-30 16:19:56.785	1	A1-041-1	0	\N	0	\N	252	\N	0	200
473	\N	2010-11-30 16:19:56.788	0	2010-11-30 16:19:56.794	1	A1-041-2	0	\N	0	\N	252	\N	0	200
474	\N	2010-11-30 16:19:56.798	0	2010-11-30 16:19:56.80	1	A1-042-1	0	\N	0	\N	252	\N	0	200
475	\N	2010-11-30 16:19:56.803	0	2010-11-30 16:19:56.806	1	A1-042-2	0	\N	0	\N	252	\N	0	200
476	\N	2010-11-30 16:19:56.809	0	2010-11-30 16:19:56.812	1	A1-043-1	0	\N	0	\N	252	\N	0	200
477	\N	2010-11-30 16:19:56.815	0	2010-11-30 16:19:56.818	1	A1-043-2	0	\N	0	\N	252	\N	0	200
478	\N	2010-11-30 16:19:56.823	0	2010-11-30 16:19:56.826	1	A1-051-1	0	\N	0	\N	252	\N	0	200
479	\N	2010-11-30 16:19:56.83	0	2010-11-30 16:19:56.833	1	A1-051-2	0	\N	0	\N	252	\N	0	200
480	\N	2010-11-30 16:19:56.836	0	2010-11-30 16:19:56.844	1	A1-052-1	0	\N	0	\N	252	\N	0	200
481	\N	2010-11-30 16:19:56.848	0	2010-11-30 16:19:56.851	1	A1-052-2	0	\N	0	\N	252	\N	0	200
482	\N	2010-11-30 16:19:56.855	0	2010-11-30 16:19:56.858	1	A1-053-1	0	\N	0	\N	252	\N	0	200
483	\N	2010-11-30 16:19:56.862	0	2010-11-30 16:19:56.864	1	A1-053-2	0	\N	0	\N	252	\N	0	200
484	\N	2010-11-30 16:19:56.878	0	2010-11-30 16:19:56.881	1	A2-011-1	0	\N	0	\N	252	\N	303	200
485	\N	2010-11-30 16:19:56.886	0	2010-11-30 16:19:56.893	1	A2-011-2	0	\N	0	\N	252	\N	303	200
486	\N	2010-11-30 16:19:56.896	0	2010-11-30 16:19:56.899	1	A2-012-1	0	\N	0	\N	252	\N	303	200
487	\N	2010-11-30 16:19:56.903	0	2010-11-30 16:19:56.905	1	A2-012-2	0	\N	0	\N	252	\N	303	200
488	\N	2010-11-30 16:19:56.909	0	2010-11-30 16:19:56.911	1	A2-013-1	0	\N	0	\N	252	\N	303	200
489	\N	2010-11-30 16:19:56.914	0	2010-11-30 16:19:56.919	1	A2-013-2	0	\N	0	\N	252	\N	303	200
490	\N	2010-11-30 16:19:56.923	0	2010-11-30 16:19:56.926	1	A2-014-1	0	\N	0	\N	252	\N	303	200
491	\N	2010-11-30 16:19:56.929	0	2010-11-30 16:19:56.932	1	A2-014-2	0	\N	0	\N	252	\N	303	200
492	\N	2010-11-30 16:19:56.935	0	2010-11-30 16:19:56.938	1	A2-015-1	0	\N	0	\N	252	\N	303	200
493	\N	2010-11-30 16:19:56.941	0	2010-11-30 16:19:56.944	1	A2-015-2	0	\N	0	\N	252	\N	303	200
494	\N	2010-11-30 16:19:56.947	0	2010-11-30 16:19:56.951	1	A2-016-1	0	\N	0	\N	252	\N	303	200
495	\N	2010-11-30 16:19:56.954	0	2010-11-30 16:19:56.957	1	A2-016-2	0	\N	0	\N	252	\N	303	200
496	\N	2010-11-30 16:19:56.961	0	2010-11-30 16:19:56.964	1	A2-021-1	0	\N	0	\N	252	\N	303	200
497	\N	2010-11-30 16:19:56.968	0	2010-11-30 16:19:56.971	1	A2-021-2	0	\N	0	\N	252	\N	303	200
498	\N	2010-11-30 16:19:56.975	0	2010-11-30 16:19:56.978	1	A2-022-1	0	\N	0	\N	252	\N	303	200
499	\N	2010-11-30 16:19:56.981	0	2010-11-30 16:19:56.984	1	A2-022-2	0	\N	0	\N	252	\N	303	200
550	\N	2010-11-30 16:19:56.987	0	2010-11-30 16:19:56.991	1	A2-023-1	0	\N	0	\N	252	\N	303	200
551	\N	2010-11-30 16:19:56.994	0	2010-11-30 16:19:56.997	1	A2-023-2	0	\N	0	\N	252	\N	303	200
552	\N	2010-11-30 16:19:57	0	2010-11-30 16:19:57.003	1	A2-024-1	0	\N	0	\N	252	\N	303	200
553	\N	2010-11-30 16:19:57.007	0	2010-11-30 16:19:57.01	1	A2-024-2	0	\N	0	\N	252	\N	303	200
554	\N	2010-11-30 16:19:57.014	0	2010-11-30 16:19:57.017	1	A2-025-1	0	\N	0	\N	252	\N	303	200
555	\N	2010-11-30 16:19:57.02	0	2010-11-30 16:19:57.023	1	A2-025-2	0	\N	0	\N	252	\N	303	200
556	\N	2010-11-30 16:19:57.027	0	2010-11-30 16:19:57.034	1	A2-026-1	0	\N	0	\N	252	\N	303	200
557	\N	2010-11-30 16:19:57.038	0	2010-11-30 16:19:57.041	1	A2-026-2	0	\N	0	\N	252	\N	303	200
558	\N	2010-11-30 16:19:57.044	0	2010-11-30 16:19:57.047	1	A2-031-1	0	\N	0	\N	252	\N	303	200
559	\N	2010-11-30 16:19:57.05	0	2010-11-30 16:19:57.053	1	A2-031-2	0	\N	0	\N	252	\N	303	200
560	\N	2010-11-30 16:19:57.057	0	2010-11-30 16:19:57.06	1	A2-032-1	0	\N	0	\N	252	\N	303	200
561	\N	2010-11-30 16:19:57.063	0	2010-11-30 16:19:57.066	1	A2-032-2	0	\N	0	\N	252	\N	303	200
562	\N	2010-11-30 16:19:57.069	0	2010-11-30 16:19:57.073	1	A2-033-1	0	\N	0	\N	252	\N	303	200
563	\N	2010-11-30 16:19:57.076	0	2010-11-30 16:19:57.079	1	A2-033-2	0	\N	0	\N	252	\N	303	200
564	\N	2010-11-30 16:19:57.082	0	2010-11-30 16:19:57.085	1	A2-034-1	0	\N	0	\N	252	\N	303	200
565	\N	2010-11-30 16:19:57.088	0	2010-11-30 16:19:57.093	1	A2-034-2	0	\N	0	\N	252	\N	303	200
566	\N	2010-11-30 16:19:57.105	0	2010-11-30 16:19:57.11	1	A2-035-1	0	\N	0	\N	252	\N	303	200
567	\N	2010-11-30 16:19:57.115	0	2010-11-30 16:19:57.119	1	A2-035-2	0	\N	0	\N	252	\N	303	200
568	\N	2010-11-30 16:19:57.124	0	2010-11-30 16:19:57.137	1	A2-036-1	0	\N	0	\N	252	\N	303	200
569	\N	2010-11-30 16:19:57.153	0	2010-11-30 16:19:57.158	1	A2-036-2	0	\N	0	\N	252	\N	303	200
571	\N	2010-11-30 16:19:57.212	0	2010-11-30 16:19:57.22	1	P1-011-2	0	\N	0	\N	254	\N	2	200
573	\N	2010-11-30 16:19:57.23	0	2010-11-30 16:19:57.236	1	P1-012-2	0	\N	0	\N	254	\N	2	200
574	\N	2010-11-30 16:19:57.239	0	2010-11-30 16:19:57.243	1	P1-013-1	0	\N	0	\N	254	\N	2	200
575	\N	2010-11-30 16:19:57.246	0	2010-11-30 16:19:57.255	1	P1-013-2	0	\N	0	\N	254	\N	2	200
576	\N	2010-11-30 16:19:57.259	0	2010-11-30 16:19:57.282	1	P1-021-1	0	\N	0	\N	254	\N	2	200
577	\N	2010-11-30 16:19:57.288	0	2010-11-30 16:19:57.296	1	P1-021-2	0	\N	0	\N	254	\N	2	200
578	\N	2010-11-30 16:19:57.31	0	2010-11-30 16:19:57.317	1	P1-022-1	0	\N	0	\N	254	\N	2	200
579	\N	2010-11-30 16:19:57.325	0	2010-11-30 16:19:57.333	1	P1-022-2	0	\N	0	\N	254	\N	2	200
580	\N	2010-11-30 16:19:57.34	0	2010-11-30 16:19:57.343	1	P1-023-1	0	\N	0	\N	254	\N	2	200
581	\N	2010-11-30 16:19:57.354	0	2010-11-30 16:19:57.356	1	P1-023-2	0	\N	0	\N	254	\N	2	200
570	\N	2010-11-30 16:19:57.199	0	2010-11-30 16:19:57.766	2	P1-011-1	0	\N	0	\N	254	\N	1	200
572	\N	2010-11-30 16:19:57.223	0	2010-11-30 16:19:57.813	2	P1-012-1	0	\N	0	\N	254	\N	1	200
582	\N	2010-11-30 16:19:58.959	0	2010-11-30 16:19:58.959	0	PICK 000001	0	\N	0	\N	\N	\N	1	\N
583	\N	2010-11-30 16:19:59.115	0	2010-11-30 16:19:59.115	0	PICK 000002	0	\N	0	\N	\N	\N	1	\N
584	\N	2010-11-30 16:19:59.209	0	2010-11-30 16:19:59.209	0	PICK 000003	0	\N	0	\N	\N	\N	1	\N
585	\N	2010-11-30 16:19:59.395	0	2010-11-30 16:19:59.395	0	PICK 000004	0	\N	0	\N	\N	\N	1	\N
586	\N	2010-11-30 16:19:59.528	0	2010-11-30 16:19:59.528	0	PICK 000005	0	\N	0	\N	\N	\N	1	\N
\.


--
-- Data for Name: los_sulabel; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sulabel (id, amount, clientref, dateref, itemunit, itemdataref, labelid, lotref, scale) FROM stdin;
\.


--
-- Data for Name: los_sysprop; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_sysprop (id, additionalcontent, created, entity_lock, modified, version, description, groupname, hidden, syskey, sysvalue, workstation, client_id) FROM stdin;
100	\N	2010-11-30 16:19:47.865	0	2010-11-30 16:19:47.865	0	\N	\N	f	CREATE_DEMO_TOPOLOGY	true	DEFAULT	0
\.


--
-- Data for Name: los_typecapacityconstraint; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_typecapacityconstraint (id, additionalcontent, created, entity_lock, modified, version, capacity, name, unitloadtype_id, storagelocationtype_id) FROM stdin;
400	\N	2010-11-30 16:19:56.287	0	2010-11-30 16:19:56.288	0	1	1 EURO / Palettenfach	0	0
401	\N	2010-11-30 16:19:56.604	0	2010-11-30 16:19:56.604	0	1	1 Box 60x40 / Fachboden	351	303
402	\N	2010-11-30 16:19:56.612	0	2010-11-30 16:19:56.616	1	2	2 Box 30x40 / Fachboden	352	303
403	\N	2010-11-30 16:19:56.62	0	2010-11-30 16:19:56.62	0	1	1 Komm-LHM / Kommfach	1	2
\.


--
-- Data for Name: los_ul_record; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_ul_record (id, additionalcontent, created, entity_lock, modified, version, activitycode, fromlocation, label, operator, recordtype, tolocation, client_id) FROM stdin;
\.


--
-- Data for Name: los_uladvice; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_uladvice (id, additionalcontent, created, entity_lock, modified, version, advicestate, advicetype, externalnumber, labelid, number, reasonforreturn, switchstateinfo, client_id, unitloadtype_id, relatedadvice_id) FROM stdin;
\.


--
-- Data for Name: los_uladvicepos; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_uladvicepos (id, additionalcontent, created, entity_lock, modified, version, notifiedamount, positionnumber, lot_id, itemdata_id, unitloadadvice_id, client_id) FROM stdin;
\.


--
-- Data for Name: los_unitload; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY los_unitload (id, packagetype, stocktakingdate, weightgross, weightnet, storagelocation_id) FROM stdin;
750	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	570
751	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	572
752	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	454
753	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	456
754	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	460
755	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	462
756	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	455
757	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	457
758	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	459
759	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	461
760	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	463
761	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	465
762	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	467
763	OF_SAME_LOT_CONSOLIDATE	\N	\N	\N	484
\.


--
-- Data for Name: mywms_area; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_area (id, additionalcontent, created, entity_lock, modified, version, name, client_id) FROM stdin;
250	\N	2010-11-30 16:19:56.05	0	2010-11-30 16:19:56.055	1	Wareneingang	0
251	\N	2010-11-30 16:19:56.076	0	2010-11-30 16:19:56.08	1	Warenausgang	0
252	\N	2010-11-30 16:19:56.084	0	2010-11-30 16:19:56.087	1	Lager	0
253	\N	2010-11-30 16:19:56.09	0	2010-11-30 16:19:56.115	1	Klärung	0
254	\N	2010-11-30 16:19:56.521	0	2010-11-30 16:19:56.53	1	Kommissionierung	0
\.


--
-- Data for Name: mywms_clearingitem; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_clearingitem (id, additionalcontent, created, entity_lock, modified, version, bundleresolver, host, messageparameters, messageresourcekey, options, propertymap, resourcebundlename, shortmessageparameters, shortmessageresourcekey, solution, solver, source, user_, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_client; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_client (id, additionalcontent, created, entity_lock, modified, version, cl_code, email, fax, name, cl_nr, phone) FROM stdin;
0	This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name.	2010-11-30 16:18:59.47633	0	2010-11-30 16:19:55.964	1	01	\N	\N	System-Mandant	System	\N
\.


--
-- Data for Name: mywms_document; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_document (id, additionalcontent, created, entity_lock, modified, version, document, name, document_size, type, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_itemdata; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_itemdata (id, additionalcontent, created, entity_lock, modified, version, advicemandatory, depth, descr, height, lotmandatory, lotsubstitutiontype, name, item_nr, rest_usage_gi, safetystock, scale, serialrectype, tradegroup, volume, weight, width, defultype_id, handlingunit_id, client_id, zone_id) FROM stdin;
600	\N	2010-11-30 16:19:57.382	0	2010-11-30 16:19:57.405	1	f	\N	Laserdrucker, Farbe, 200 Blatt pro Minute	\N	f	NOT_ALLOWED	Drucker Laser 8712	14008712	0	0	0	NO_RECORD	Demoartikel	\N	\N	\N	0	0	0	200
601	\N	2010-11-30 16:19:57.413	0	2010-11-30 16:19:57.432	1	f	\N	Laserdrucker, Schwarz-Weiss	\N	f	NOT_ALLOWED	Drucker Laser MLD-914	14008715	0	0	0	GOODS_OUT_RECORD	Demoartikel	\N	\N	\N	0	0	0	200
602	\N	2010-11-30 16:19:57.435	0	2010-11-30 16:19:57.479	1	f	\N	Papier DIN A4, 80 mg/qm	\N	f	NOT_ALLOWED	Papier A4 (80g)	14009724	0	0	0	NO_RECORD	Demoartikel	\N	\N	\N	0	53	0	200
603	\N	2010-11-30 16:19:57.519	0	2010-11-30 16:19:57.544	1	f	\N	Papier DIN A4, 100 mg/qm	\N	f	NOT_ALLOWED	Papier A4 (120g)	14009725	0	0	0	NO_RECORD	Demoartikel	\N	\N	\N	0	53	0	200
604	\N	2010-11-30 16:19:57.547	0	2010-11-30 16:19:57.565	1	f	\N	Toner, Farbe Schwarz	\N	t	NOT_ALLOWED	Toner schwarz	14006411	0	0	0	NO_RECORD	Demoartikel	\N	\N	\N	0	0	0	200
605	\N	2010-11-30 16:19:57.589	0	2010-11-30 16:19:57.649	1	f	\N	Toner, Farbe Bunt	\N	f	NOT_ALLOWED	Toner bunt	14006413	0	0	0	NO_RECORD	Demoartikel	\N	\N	\N	0	0	0	200
606	\N	2010-11-30 16:19:57.653	0	2010-11-30 16:19:57.69	1	f	\N	Schraube 6x20 mm Kreuz	\N	f	NOT_ALLOWED	Schraube 6x20 mm Kreuz 	10002830	0	0	0	NO_RECORD	Demoartikel	\N	\N	\N	351	51	0	200
607	\N	2010-11-30 16:19:57.701	0	2010-11-30 16:19:57.736	1	f	\N	Schraube 6x30 mm	\N	f	NOT_ALLOWED	Schraube 6x30 mm 	10002841	0	0	0	NO_RECORD	Demoartikel	\N	\N	\N	351	51	0	200
\.


--
-- Data for Name: mywms_itemunit; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_itemunit (id, additionalcontent, created, entity_lock, modified, version, basefactor, unitname, unittype, baseunit_id) FROM stdin;
0	This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it.	2010-11-30 16:19:42.334	0	2010-11-30 16:19:55.979	1	1	Stk	PIECE	\N
51	\N	2010-11-30 16:19:56.467	0	2010-11-30 16:19:56.475	1	1	g	WEIGHT	\N
52	\N	2010-11-30 16:19:56.479	0	2010-11-30 16:19:56.489	1	1000	kg	WEIGHT	51
53	\N	2010-11-30 16:19:56.494	0	2010-11-30 16:19:56.529	1	1	Pack	PIECE	\N
\.


--
-- Data for Name: mywms_logitem; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_logitem (id, additionalcontent, created, entity_lock, modified, version, bundleresolver, host, message, messageparameters, messageresourcekey, resourcebundlename, source, type, user_, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_lot; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_lot (id, additionalcontent, created, entity_lock, modified, version, age, bestbeforeend, code, lot_date, depth, height, name, usenotbefore, volume, weight, width, itemdata_id, client_id) FROM stdin;
900	\N	2010-11-30 16:19:58.407	0	2010-11-30 16:19:58.408	0	\N	\N	\N	2010-11-30	\N	\N	1582	\N	\N	\N	\N	604	0
\.


--
-- Data for Name: mywms_pluginconfiguration; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_pluginconfiguration (id, additionalcontent, created, entity_lock, modified, version, plugin_mode, pluginclass, pluginname, client_id) FROM stdin;
\.


--
-- Data for Name: mywms_request; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_request (id, additionalcontent, created, entity_lock, modified, version, request_nr, parentrequestnumber, client_id) FROM stdin;
1003	\N	2010-11-30 16:19:58.966	0	2010-11-30 16:19:58.992	1	PICK 000001	ORDER 000001	0
1001	\N	2010-11-30 16:19:58.929	0	2010-11-30 16:19:59.056	1	ORDER 000001_0	ORDER 000001	0
1000		2010-11-30 16:19:58.908	0	2010-11-30 16:19:59.089	3	ORDER 000001	\N	0
1002	\N	2010-11-30 16:19:58.94	0	2010-11-30 16:19:59.089	2	ORDER 000001_1	ORDER 000001	0
1006	\N	2010-11-30 16:19:59.119	0	2010-11-30 16:19:59.132	1	PICK 000002	ORDER 000002	0
1004		2010-11-30 16:19:59.083	0	2010-11-30 16:19:59.176	2	ORDER 000002	\N	0
1005	\N	2010-11-30 16:19:59.097	0	2010-11-30 16:19:59.178	1	ORDER 000002_0	ORDER 000002	0
1010	\N	2010-11-30 16:19:59.215	0	2010-11-30 16:19:59.227	1	PICK 000003	ORDER 000003	0
1008	\N	2010-11-30 16:19:59.19	0	2010-11-30 16:19:59.267	1	ORDER 000003_0	ORDER 000003	0
1007		2010-11-30 16:19:59.168	0	2010-11-30 16:19:59.297	3	ORDER 000003	\N	0
1009	\N	2010-11-30 16:19:59.197	0	2010-11-30 16:19:59.297	2	ORDER 000003_1	ORDER 000003	0
1014	\N	2010-11-30 16:19:59.401	0	2010-11-30 16:19:59.41	1	PICK 000004	ORDER 000004	0
1012	\N	2010-11-30 16:19:59.37	0	2010-11-30 16:19:59.466	1	ORDER 000004_0	ORDER 000004	0
1011		2010-11-30 16:19:59.29	0	2010-11-30 16:19:59.499	3	ORDER 000004	\N	0
1013	\N	2010-11-30 16:19:59.377	0	2010-11-30 16:19:59.499	2	ORDER 000004_1	ORDER 000004	0
1018	\N	2010-11-30 16:19:59.534	0	2010-11-30 16:19:59.544	1	PICK 000005	ORDER 000005	0
1016	\N	2010-11-30 16:19:59.508	0	2010-11-30 16:19:59.579	1	ORDER 000005_0	ORDER 000005	0
1015		2010-11-30 16:19:59.494	0	2010-11-30 16:19:59.633	3	ORDER 000005	\N	0
1017	\N	2010-11-30 16:19:59.515	0	2010-11-30 16:19:59.633	2	ORDER 000005_1	ORDER 000005	0
\.


--
-- Data for Name: mywms_role; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_role (id, additionalcontent, created, entity_lock, modified, version, description, name) FROM stdin;
0	This is a system used entity. DO NOT REMOVE, LOCK OR RENAME IT! Some processes may use it.	2010-11-30 16:18:59.47633	0	2010-11-30 16:18:59.47633	0	System Administrator	Admin
150	\N	2010-11-30 16:19:55.963	0	2010-11-30 16:19:55.964	0		Operator
151	\N	2010-11-30 16:19:55.969	0	2010-11-30 16:19:55.969	0		Foreman
152	\N	2010-11-30 16:19:55.972	0	2010-11-30 16:19:55.972	0		Inventory
\.


--
-- Data for Name: mywms_stockunit; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_stockunit (id, additionalcontent, created, entity_lock, modified, version, amount, labelid, reservedamount, serialnumber, lot_id, client_id, itemdata_id, unitload_id) FROM stdin;
801	\N	2010-11-30 16:19:57.981	0	2010-11-30 16:19:57.981	0	200.0000	801	0.0000	\N	\N	0	602	752
802	\N	2010-11-30 16:19:58.046	0	2010-11-30 16:19:58.046	0	200.0000	802	0.0000	\N	\N	0	602	753
803	\N	2010-11-30 16:19:58.113	0	2010-11-30 16:19:58.113	0	200.0000	803	0.0000	\N	\N	0	603	754
804	\N	2010-11-30 16:19:58.168	0	2010-11-30 16:19:58.168	0	200.0000	804	0.0000	\N	\N	0	603	755
806	\N	2010-11-30 16:19:58.30	0	2010-11-30 16:19:58.30	0	8.0000	806	0.0000	\N	\N	0	600	757
809	\N	2010-11-30 16:19:58.547	0	2010-11-30 16:19:58.556	1	144.0000	809	0.0000	\N	900	0	604	760
810	\N	2010-11-30 16:19:58.681	0	2010-11-30 16:19:58.691	1	144.0000	810	0.0000	\N	900	0	604	761
811	\N	2010-11-30 16:19:58.74	0	2010-11-30 16:19:58.74	0	72.0000	811	0.0000	\N	\N	0	605	762
812	\N	2010-11-30 16:19:58.792	0	2010-11-30 16:19:58.792	0	15000.0000	812	0.0000	\N	\N	0	606	763
807	\N	2010-11-30 16:19:58.349	0	2010-11-30 16:19:59.036	1	8.0000	807	1.0000	\N	\N	0	601	758
805	\N	2010-11-30 16:19:58.222	0	2010-11-30 16:19:59.25	2	8.0000	805	2.0000	\N	\N	0	600	756
800	\N	2010-11-30 16:19:57.887	0	2010-11-30 16:19:59.56	4	200.0000	800	22.0000	\N	\N	0	602	750
808	\N	2010-11-30 16:19:58.445	0	2010-11-30 16:19:59.579	3	144.0000	808	2.0000	\N	900	0	604	759
\.


--
-- Data for Name: mywms_unitload; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_unitload (id, additionalcontent, created, entity_lock, modified, version, location_index, labelid, type_id, client_id) FROM stdin;
751	\N	2010-11-30 16:19:57.804	0	2010-11-30 16:19:57.807	0	-1	P1-012-1	1	0
750	\N	2010-11-30 16:19:57.759	0	2010-11-30 16:19:57.917	2	-1	P1-011-1	1	0
752	\N	2010-11-30 16:19:57.977	0	2010-11-30 16:19:58.012	2	-1	000001	0	0
753	\N	2010-11-30 16:19:58.04	0	2010-11-30 16:19:58.061	2	-1	000002	0	0
754	\N	2010-11-30 16:19:58.109	0	2010-11-30 16:19:58.133	2	-1	000003	0	0
755	\N	2010-11-30 16:19:58.162	0	2010-11-30 16:19:58.189	2	-1	000004	0	0
756	\N	2010-11-30 16:19:58.218	0	2010-11-30 16:19:58.24	2	-1	000005	0	0
757	\N	2010-11-30 16:19:58.296	0	2010-11-30 16:19:58.318	2	-1	000006	0	0
758	\N	2010-11-30 16:19:58.345	0	2010-11-30 16:19:58.37	2	-1	000007	0	0
759	\N	2010-11-30 16:19:58.436	0	2010-11-30 16:19:58.468	2	-1	000008	0	0
760	\N	2010-11-30 16:19:58.536	0	2010-11-30 16:19:58.564	2	-1	000009	0	0
761	\N	2010-11-30 16:19:58.672	0	2010-11-30 16:19:58.70	2	-1	000010	0	0
762	\N	2010-11-30 16:19:58.736	0	2010-11-30 16:19:58.759	2	-1	000011	0	0
763	\N	2010-11-30 16:19:58.788	0	2010-11-30 16:19:58.915	2	-1	000012	351	0
\.


--
-- Data for Name: mywms_unitloadtype; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_unitloadtype (id, additionalcontent, created, entity_lock, modified, version, depth, height, liftingcapacity, name, volume, weight, width) FROM stdin;
0	Defaultwert für LHM-Typen.\n\nDies ist ein System-Datensatz. NICHT ENTFERNEN ODER SPERREN! Er wird von einigen Prozessen verwendet.	2010-11-30 16:19:56.248	0	2010-11-30 16:19:56.562	2	1.20	1.40	500.000	EURO	\N	25.000	0.80
351	\N	2010-11-30 16:19:56.565	0	2010-11-30 16:19:56.57	1	0.40	0.25	20.000	Box 60x40	\N	0.700	0.60
352	\N	2010-11-30 16:19:56.573	0	2010-11-30 16:19:56.576	1	0.40	0.25	20.000	Box 30x40	\N	0.500	0.30
1	LHM-Typ für Kommissionierplätze.\n\nDies ist ein System-Datensatz. NICHT ENTFERNEN ODER SPERREN! Er wird von einigen Prozessen verwendet.	2010-11-30 16:19:56.582	0	2010-11-30 16:19:56.609	2	\N	\N	\N	Spezial LHM-Typ fuer Kommfach	\N	\N	\N
\.


--
-- Data for Name: mywms_user; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_user (id, additionalcontent, created, entity_lock, modified, version, email, firstname, lastname, locale, name, password, phone, client_id) FROM stdin;
0	This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name and password.	2010-11-30 16:18:59.47633	0	2010-11-30 16:18:59.47633	0	\N	\N	\N	en	admin	21232f297a57a5a743894a0e4a801fc3	\N	0
1	\N	2010-11-30 16:18:59.47633	0	2010-11-30 16:19:55.992	1	\N	\N	Deutsch	de	deutsch	09c438e63455e3e1b3deabe65fdbc087	\N	0
2	\N	2010-11-30 16:18:59.47633	0	2010-11-30 16:19:56	1	\N	\N	English	en	english	ba0a6ddd94c73698a3658f92ac222f8a	\N	0
3	\N	2010-11-30 16:18:59.47633	0	2010-11-30 16:19:56.007	1	\N	\N	Français	fr	français	616e89b4d5aa15b04a9606ad8b194760	\N	0
4	\N	2010-11-30 16:18:59.47633	0	2010-11-30 16:19:56.031	1	\N	\N	Français	fr	francais	cd015c1b092fbf168b20770e3b8f0e49	\N	0
\.


--
-- Data for Name: mywms_user_mywms_role; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_user_mywms_role (mywms_user_id, roles_id) FROM stdin;
0	0
1	0
2	0
3	0
4	0
\.


--
-- Data for Name: mywms_zone; Type: TABLE DATA; Schema: public; Owner: jboss
--

COPY mywms_zone (id, additionalcontent, created, entity_lock, modified, version, name, client_id) FROM stdin;
200	\N	2010-11-30 16:19:56.03	0	2010-11-30 16:19:56.03	0	A	0
201	\N	2010-11-30 16:19:56.036	0	2010-11-30 16:19:56.037	0	B	0
202	\N	2010-11-30 16:19:56.039	0	2010-11-30 16:19:56.039	0	C	0
\.


--
-- Name: jms_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY jms_messages
    ADD CONSTRAINT jms_messages_pkey PRIMARY KEY (messageid, destination);


--
-- Name: jms_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY jms_roles
    ADD CONSTRAINT jms_roles_pkey PRIMARY KEY (userid, roleid);


--
-- Name: jms_subscriptions_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY jms_subscriptions
    ADD CONSTRAINT jms_subscriptions_pkey PRIMARY KEY (clientid, subname);


--
-- Name: jms_transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY jms_transactions
    ADD CONSTRAINT jms_transactions_pkey PRIMARY KEY (txid);


--
-- Name: jms_users_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY jms_users
    ADD CONSTRAINT jms_users_pkey PRIMARY KEY (userid);


--
-- Name: los_area_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_area
    ADD CONSTRAINT los_area_pkey PRIMARY KEY (id);


--
-- Name: los_avisreq_advicenumber_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT los_avisreq_advicenumber_key UNIQUE (advicenumber);


--
-- Name: los_avisreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT los_avisreq_pkey PRIMARY KEY (id);


--
-- Name: los_bom_parent_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT los_bom_parent_id_key UNIQUE (parent_id, child_id);


--
-- Name: los_bom_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT los_bom_pkey PRIMARY KEY (id);


--
-- Name: los_extinguishreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_extinguishreq
    ADD CONSTRAINT los_extinguishreq_pkey PRIMARY KEY (id);


--
-- Name: los_extorder_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_extorder
    ADD CONSTRAINT los_extorder_pkey PRIMARY KEY (id);


--
-- Name: los_fixassgn_assignedlocation_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT los_fixassgn_assignedlocation_id_key UNIQUE (assignedlocation_id);


--
-- Name: los_fixassgn_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT los_fixassgn_pkey PRIMARY KEY (id);


--
-- Name: los_goodsreceipt_gr_number_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT los_goodsreceipt_gr_number_key UNIQUE (gr_number);


--
-- Name: los_goodsreceipt_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT los_goodsreceipt_pkey PRIMARY KEY (id);


--
-- Name: los_grrposition_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT los_grrposition_pkey PRIMARY KEY (id);


--
-- Name: los_grrposition_positionnumber_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT los_grrposition_positionnumber_key UNIQUE (positionnumber);


--
-- Name: los_inventory_lotref_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_inventory
    ADD CONSTRAINT los_inventory_lotref_key UNIQUE (lotref, client_id);


--
-- Name: los_inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_inventory
    ADD CONSTRAINT los_inventory_pkey PRIMARY KEY (id);


--
-- Name: los_itemdata_number_number_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT los_itemdata_number_number_key UNIQUE (number, itemdata_id);


--
-- Name: los_itemdata_number_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT los_itemdata_number_pkey PRIMARY KEY (id);


--
-- Name: los_jrxml_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_jrxml
    ADD CONSTRAINT los_jrxml_name_key UNIQUE (name, client_id);


--
-- Name: los_jrxml_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_jrxml
    ADD CONSTRAINT los_jrxml_pkey PRIMARY KEY (id);


--
-- Name: los_locationcluster_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_locationcluster
    ADD CONSTRAINT los_locationcluster_name_key UNIQUE (name);


--
-- Name: los_locationcluster_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_locationcluster
    ADD CONSTRAINT los_locationcluster_pkey PRIMARY KEY (id);


--
-- Name: los_order_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_order
    ADD CONSTRAINT los_order_pkey PRIMARY KEY (id);


--
-- Name: los_orderreceipt_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderreceipt
    ADD CONSTRAINT los_orderreceipt_pkey PRIMARY KEY (id);


--
-- Name: los_orderreceiptpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderreceiptpos
    ADD CONSTRAINT los_orderreceiptpos_pkey PRIMARY KEY (id);


--
-- Name: los_orderreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderreq
    ADD CONSTRAINT los_orderreq_pkey PRIMARY KEY (id);


--
-- Name: los_orderreqpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_orderreqpos
    ADD CONSTRAINT los_orderreqpos_pkey PRIMARY KEY (id);


--
-- Name: los_outpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_outpos
    ADD CONSTRAINT los_outpos_pkey PRIMARY KEY (id);


--
-- Name: los_outpos_source_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_outpos
    ADD CONSTRAINT los_outpos_source_id_key UNIQUE (source_id);


--
-- Name: los_outreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT los_outreq_pkey PRIMARY KEY (id);


--
-- Name: los_pickreceipt_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickreceipt
    ADD CONSTRAINT los_pickreceipt_pkey PRIMARY KEY (id);


--
-- Name: los_pickreceiptpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickreceiptpos
    ADD CONSTRAINT los_pickreceiptpos_pkey PRIMARY KEY (id);


--
-- Name: los_pickreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickreq
    ADD CONSTRAINT los_pickreq_pkey PRIMARY KEY (id);


--
-- Name: los_pickreqpos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_pickreqpos
    ADD CONSTRAINT los_pickreqpos_pkey PRIMARY KEY (id);


--
-- Name: los_rack_client_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_rack
    ADD CONSTRAINT los_rack_client_id_key UNIQUE (client_id, rname);


--
-- Name: los_rack_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_rack
    ADD CONSTRAINT los_rack_pkey PRIMARY KEY (id);


--
-- Name: los_racklocation_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_racklocation
    ADD CONSTRAINT los_racklocation_pkey PRIMARY KEY (id);


--
-- Name: los_replenishreq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_replenishreq
    ADD CONSTRAINT los_replenishreq_pkey PRIMARY KEY (id);


--
-- Name: los_sequencenumber_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sequencenumber
    ADD CONSTRAINT los_sequencenumber_pkey PRIMARY KEY (classname);


--
-- Name: los_serviceconf_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_serviceconf
    ADD CONSTRAINT los_serviceconf_pkey PRIMARY KEY (id);


--
-- Name: los_serviceconf_service_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_serviceconf
    ADD CONSTRAINT los_serviceconf_service_key UNIQUE (service, client_id, servkey);


--
-- Name: los_sllabel_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sllabel
    ADD CONSTRAINT los_sllabel_pkey PRIMARY KEY (id);


--
-- Name: los_stockrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stockrecord
    ADD CONSTRAINT los_stockrecord_pkey PRIMARY KEY (id);


--
-- Name: los_stocktaking_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktaking
    ADD CONSTRAINT los_stocktaking_pkey PRIMARY KEY (id);


--
-- Name: los_stocktaking_stocktakingnumber_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktaking
    ADD CONSTRAINT los_stocktaking_stocktakingnumber_key UNIQUE (stocktakingnumber);


--
-- Name: los_stocktakingorder_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktakingorder
    ADD CONSTRAINT los_stocktakingorder_pkey PRIMARY KEY (id);


--
-- Name: los_stocktakingrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_stocktakingrecord
    ADD CONSTRAINT los_stocktakingrecord_pkey PRIMARY KEY (id);


--
-- Name: los_storagelocationtype_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagelocationtype
    ADD CONSTRAINT los_storagelocationtype_pkey PRIMARY KEY (id);


--
-- Name: los_storagelocationtype_sltname_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagelocationtype
    ADD CONSTRAINT los_storagelocationtype_sltname_key UNIQUE (sltname);


--
-- Name: los_storagereq_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT los_storagereq_pkey PRIMARY KEY (id);


--
-- Name: los_storloc_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT los_storloc_name_key UNIQUE (name);


--
-- Name: los_storloc_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT los_storloc_pkey PRIMARY KEY (id);


--
-- Name: los_sulabel_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sulabel
    ADD CONSTRAINT los_sulabel_pkey PRIMARY KEY (id);


--
-- Name: los_sysprop_client_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sysprop
    ADD CONSTRAINT los_sysprop_client_id_key UNIQUE (client_id, syskey, workstation);


--
-- Name: los_sysprop_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_sysprop
    ADD CONSTRAINT los_sysprop_pkey PRIMARY KEY (id);


--
-- Name: los_typecapacityconstraint_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT los_typecapacityconstraint_name_key UNIQUE (name);


--
-- Name: los_typecapacityconstraint_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT los_typecapacityconstraint_pkey PRIMARY KEY (id);


--
-- Name: los_typecapacityconstraint_storagelocationtype_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT los_typecapacityconstraint_storagelocationtype_id_key UNIQUE (storagelocationtype_id, unitloadtype_id);


--
-- Name: los_ul_record_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_ul_record
    ADD CONSTRAINT los_ul_record_pkey PRIMARY KEY (id);


--
-- Name: los_uladvice_labelid_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT los_uladvice_labelid_key UNIQUE (labelid);


--
-- Name: los_uladvice_number_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT los_uladvice_number_key UNIQUE (number);


--
-- Name: los_uladvice_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT los_uladvice_pkey PRIMARY KEY (id);


--
-- Name: los_uladvicepos_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT los_uladvicepos_pkey PRIMARY KEY (id);


--
-- Name: los_unitload_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY los_unitload
    ADD CONSTRAINT los_unitload_pkey PRIMARY KEY (id);


--
-- Name: mywms_area_client_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_area
    ADD CONSTRAINT mywms_area_client_id_key UNIQUE (client_id, name);


--
-- Name: mywms_area_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_area
    ADD CONSTRAINT mywms_area_pkey PRIMARY KEY (id);


--
-- Name: mywms_clearingitem_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_clearingitem
    ADD CONSTRAINT mywms_clearingitem_pkey PRIMARY KEY (id);


--
-- Name: mywms_client_cl_code_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_client
    ADD CONSTRAINT mywms_client_cl_code_key UNIQUE (cl_code);


--
-- Name: mywms_client_cl_nr_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_client
    ADD CONSTRAINT mywms_client_cl_nr_key UNIQUE (cl_nr);


--
-- Name: mywms_client_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_client
    ADD CONSTRAINT mywms_client_name_key UNIQUE (name);


--
-- Name: mywms_client_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_client
    ADD CONSTRAINT mywms_client_pkey PRIMARY KEY (id);


--
-- Name: mywms_document_client_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_document
    ADD CONSTRAINT mywms_document_client_id_key UNIQUE (client_id, name);


--
-- Name: mywms_document_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_document
    ADD CONSTRAINT mywms_document_pkey PRIMARY KEY (id);


--
-- Name: mywms_itemdata_client_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT mywms_itemdata_client_id_key UNIQUE (client_id, item_nr);


--
-- Name: mywms_itemdata_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT mywms_itemdata_pkey PRIMARY KEY (id);


--
-- Name: mywms_itemunit_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_itemunit
    ADD CONSTRAINT mywms_itemunit_pkey PRIMARY KEY (id);


--
-- Name: mywms_logitem_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_logitem
    ADD CONSTRAINT mywms_logitem_pkey PRIMARY KEY (id);


--
-- Name: mywms_lot_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT mywms_lot_name_key UNIQUE (name, itemdata_id);


--
-- Name: mywms_lot_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT mywms_lot_pkey PRIMARY KEY (id);


--
-- Name: mywms_pluginconfiguration_client_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_pluginconfiguration
    ADD CONSTRAINT mywms_pluginconfiguration_client_id_key UNIQUE (client_id, pluginname, plugin_mode);


--
-- Name: mywms_pluginconfiguration_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_pluginconfiguration
    ADD CONSTRAINT mywms_pluginconfiguration_pkey PRIMARY KEY (id);


--
-- Name: mywms_request_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_request
    ADD CONSTRAINT mywms_request_pkey PRIMARY KEY (id);


--
-- Name: mywms_request_request_nr_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_request
    ADD CONSTRAINT mywms_request_request_nr_key UNIQUE (request_nr);


--
-- Name: mywms_role_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_role
    ADD CONSTRAINT mywms_role_name_key UNIQUE (name);


--
-- Name: mywms_role_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_role
    ADD CONSTRAINT mywms_role_pkey PRIMARY KEY (id);


--
-- Name: mywms_stockunit_labelid_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT mywms_stockunit_labelid_key UNIQUE (labelid, itemdata_id);


--
-- Name: mywms_stockunit_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT mywms_stockunit_pkey PRIMARY KEY (id);


--
-- Name: mywms_unitload_labelid_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT mywms_unitload_labelid_key UNIQUE (labelid);


--
-- Name: mywms_unitload_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT mywms_unitload_pkey PRIMARY KEY (id);


--
-- Name: mywms_unitloadtype_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitloadtype
    ADD CONSTRAINT mywms_unitloadtype_name_key UNIQUE (name);


--
-- Name: mywms_unitloadtype_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_unitloadtype
    ADD CONSTRAINT mywms_unitloadtype_pkey PRIMARY KEY (id);


--
-- Name: mywms_user_name_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_user
    ADD CONSTRAINT mywms_user_name_key UNIQUE (name);


--
-- Name: mywms_user_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_user
    ADD CONSTRAINT mywms_user_pkey PRIMARY KEY (id);


--
-- Name: mywms_zone_client_id_key; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_zone
    ADD CONSTRAINT mywms_zone_client_id_key UNIQUE (client_id, name);


--
-- Name: mywms_zone_pkey; Type: CONSTRAINT; Schema: public; Owner: jboss; Tablespace: 
--

ALTER TABLE ONLY mywms_zone
    ADD CONSTRAINT mywms_zone_pkey PRIMARY KEY (id);


--
-- Name: jms_messages_destination; Type: INDEX; Schema: public; Owner: jboss; Tablespace: 
--

CREATE INDEX jms_messages_destination ON jms_messages USING btree (destination);


--
-- Name: jms_messages_txop_txid; Type: INDEX; Schema: public; Owner: jboss; Tablespace: 
--

CREATE INDEX jms_messages_txop_txid ON jms_messages USING btree (txop, txid);


--
-- Name: fk150c28516fa752e7; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT fk150c28516fa752e7 FOREIGN KEY (child_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk150c28518823ec99; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_bom
    ADD CONSTRAINT fk150c28518823ec99 FOREIGN KEY (parent_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk16f290aa4abcc2bf; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_user_mywms_role
    ADD CONSTRAINT fk16f290aa4abcc2bf FOREIGN KEY (roles_id) REFERENCES mywms_role(id);


--
-- Name: fk16f290aae371c338; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_user_mywms_role
    ADD CONSTRAINT fk16f290aae371c338 FOREIGN KEY (mywms_user_id) REFERENCES mywms_user(id);


--
-- Name: fk21bab647f4dcc426; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishreq
    ADD CONSTRAINT fk21bab647f4dcc426 FOREIGN KEY (id) REFERENCES los_orderreq(id);


--
-- Name: fk248471dccf4e57f; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_extorder
    ADD CONSTRAINT fk248471dccf4e57f FOREIGN KEY (authorizedby_id) REFERENCES mywms_user(id);


--
-- Name: fk248471dcead3f22e; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_extorder
    ADD CONSTRAINT fk248471dcead3f22e FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fk248471dcf4dcc426; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_extorder
    ADD CONSTRAINT fk248471dcf4dcc426 FOREIGN KEY (id) REFERENCES los_orderreq(id);


--
-- Name: fk2adb70a6291583b; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreqpos
    ADD CONSTRAINT fk2adb70a6291583b FOREIGN KEY (pickrequest_id) REFERENCES los_pickreq(id);


--
-- Name: fk2adb70a66297649; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreqpos
    ADD CONSTRAINT fk2adb70a66297649 FOREIGN KEY (parentrequest_id) REFERENCES los_orderreqpos(id);


--
-- Name: fk2adb70a68acb6fed; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreqpos
    ADD CONSTRAINT fk2adb70a68acb6fed FOREIGN KEY (complementon_id) REFERENCES los_pickreqpos(id);


--
-- Name: fk2adb70a6e06b76ae; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreqpos
    ADD CONSTRAINT fk2adb70a6e06b76ae FOREIGN KEY (stockunit_id) REFERENCES mywms_stockunit(id);


--
-- Name: fk2b445fb9f9f1fca2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreceipt
    ADD CONSTRAINT fk2b445fb9f9f1fca2 FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: fk2cac199c7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT fk2cac199c7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk2cac199cdfb71f26; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_itemdata_number
    ADD CONSTRAINT fk2cac199cdfb71f26 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk2fc84488f9f1fca2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreceipt
    ADD CONSTRAINT fk2fc84488f9f1fca2 FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: fk315a8207576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_jrxml
    ADD CONSTRAINT fk315a8207576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk35bd19ff4dcc426; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_order
    ADD CONSTRAINT fk35bd19ff4dcc426 FOREIGN KEY (id) REFERENCES los_orderreq(id);


--
-- Name: fk63b58a2cf9f1fca2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_sllabel
    ADD CONSTRAINT fk63b58a2cf9f1fca2 FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: fk64999cea7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_serviceconf
    ADD CONSTRAINT fk64999cea7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk68501815685fc38a; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outpos
    ADD CONSTRAINT fk68501815685fc38a FOREIGN KEY (source_id) REFERENCES los_unitload(id);


--
-- Name: fk6850181574799270; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outpos
    ADD CONSTRAINT fk6850181574799270 FOREIGN KEY (goodsoutrequest_id) REFERENCES los_outreq(id);


--
-- Name: fk68501e5f2772b400; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT fk68501e5f2772b400 FOREIGN KEY (parentrequest_id) REFERENCES los_orderreq(id);


--
-- Name: fk68501e5f5a663be; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT fk68501e5f5a663be FOREIGN KEY (id) REFERENCES mywms_request(id);


--
-- Name: fk68501e5f62a4f6cd; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_outreq
    ADD CONSTRAINT fk68501e5f62a4f6cd FOREIGN KEY (operator_id) REFERENCES mywms_user(id);


--
-- Name: fk6c7465e2c6a1fe6; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT fk6c7465e2c6a1fe6 FOREIGN KEY (unitloadtype_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fk6c7465e7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT fk6c7465e7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk6c7465ecbc10219; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvice
    ADD CONSTRAINT fk6c7465ecbc10219 FOREIGN KEY (relatedadvice_id) REFERENCES los_avisreq(id);


--
-- Name: fk6cc6ab552772b400; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreqpos
    ADD CONSTRAINT fk6cc6ab552772b400 FOREIGN KEY (parentrequest_id) REFERENCES los_orderreq(id);


--
-- Name: fk6cc6ab555a663be; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreqpos
    ADD CONSTRAINT fk6cc6ab555a663be FOREIGN KEY (id) REFERENCES mywms_request(id);


--
-- Name: fk6cc6ab55dfb71f26; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreqpos
    ADD CONSTRAINT fk6cc6ab55dfb71f26 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk6cc6ab55ead3f22e; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreqpos
    ADD CONSTRAINT fk6cc6ab55ead3f22e FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fk6dad32d7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_inventory
    ADD CONSTRAINT fk6dad32d7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk731127c3f9f1fca2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_sulabel
    ADD CONSTRAINT fk731127c3f9f1fca2 FOREIGN KEY (id) REFERENCES mywms_document(id);


--
-- Name: fk735166cd17ccb109; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk735166cd17ccb109 FOREIGN KEY (type_id) REFERENCES los_storagelocationtype(id);


--
-- Name: fk735166cd2eb48ba4; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk735166cd2eb48ba4 FOREIGN KEY (cluster_id) REFERENCES los_locationcluster(id);


--
-- Name: fk735166cd53f5f6a6; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk735166cd53f5f6a6 FOREIGN KEY (zone_id) REFERENCES mywms_zone(id);


--
-- Name: fk735166cd7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk735166cd7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk735166cd85e165b; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk735166cd85e165b FOREIGN KEY (area_id) REFERENCES los_area(id);


--
-- Name: fk735166cd8bcaf84d; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storloc
    ADD CONSTRAINT fk735166cd8bcaf84d FOREIGN KEY (currenttcc) REFERENCES los_typecapacityconstraint(id);


--
-- Name: fk78f6ec4297747c46; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT fk78f6ec4297747c46 FOREIGN KEY (assignedlocation_id) REFERENCES los_storloc(id);


--
-- Name: fk78f6ec42dfb71f26; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_fixassgn
    ADD CONSTRAINT fk78f6ec42dfb71f26 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk7a60c0ccd69b7e28; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreceiptpos
    ADD CONSTRAINT fk7a60c0ccd69b7e28 FOREIGN KEY (receipt_id) REFERENCES los_pickreceipt(id);


--
-- Name: fk7c112dc17576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_sysprop
    ADD CONSTRAINT fk7c112dc17576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk7f1784798cdc8979; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_unitload
    ADD CONSTRAINT fk7f1784798cdc8979 FOREIGN KEY (storagelocation_id) REFERENCES los_storloc(id);


--
-- Name: fk7f178479b51fdc11; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_unitload
    ADD CONSTRAINT fk7f178479b51fdc11 FOREIGN KEY (id) REFERENCES mywms_unitload(id);


--
-- Name: fk7f46ccca7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_ul_record
    ADD CONSTRAINT fk7f46ccca7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk81cdb6b162a4f6cd; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT fk81cdb6b162a4f6cd FOREIGN KEY (operator_id) REFERENCES mywms_user(id);


--
-- Name: fk81cdb6b17576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT fk81cdb6b17576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk81cdb6b1dc91d4f9; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt
    ADD CONSTRAINT fk81cdb6b1dc91d4f9 FOREIGN KEY (goodsinlocation_id) REFERENCES los_storloc(id);


--
-- Name: fk882380a02c6a1fe6; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT fk882380a02c6a1fe6 FOREIGN KEY (unitloadtype_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fk882380a0c34a03d9; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_typecapacityconstraint
    ADD CONSTRAINT fk882380a0c34a03d9 FOREIGN KEY (storagelocationtype_id) REFERENCES los_storagelocationtype(id);


--
-- Name: fk8c78781c5a8b1214; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_area
    ADD CONSTRAINT fk8c78781c5a8b1214 FOREIGN KEY (id) REFERENCES mywms_area(id);


--
-- Name: fk8c7ff2667576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_rack
    ADD CONSTRAINT fk8c7ff2667576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk914d25b83f624790; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT fk914d25b83f624790 FOREIGN KEY (type_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fk914d25b87576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_unitload
    ADD CONSTRAINT fk914d25b87576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk9218a1437576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT fk9218a1437576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk9218a143dfb71f26; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_lot
    ADD CONSTRAINT fk9218a143dfb71f26 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fk9a96a0925a663be; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT fk9a96a0925a663be FOREIGN KEY (id) REFERENCES mywms_request(id);


--
-- Name: fk9a96a092610ee7db; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT fk9a96a092610ee7db FOREIGN KEY (destination_id) REFERENCES los_storloc(id);


--
-- Name: fk9a96a092c7da58db; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_storagereq
    ADD CONSTRAINT fk9a96a092c7da58db FOREIGN KEY (unitload_id) REFERENCES los_unitload(id);


--
-- Name: fk9c10bfb6b21192; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreceiptpos
    ADD CONSTRAINT fk9c10bfb6b21192 FOREIGN KEY (receipt_id) REFERENCES los_orderreceipt(id);


--
-- Name: fk9c10bfb7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreceiptpos
    ADD CONSTRAINT fk9c10bfb7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fk9d2e768377483cf2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_stocktakingorder
    ADD CONSTRAINT fk9d2e768377483cf2 FOREIGN KEY (stocktaking_id) REFERENCES los_stocktaking(id);


--
-- Name: fka60a91497576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_logitem
    ADD CONSTRAINT fka60a91497576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fka9f86a957576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_pluginconfiguration
    ADD CONSTRAINT fka9f86a957576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkb0f690db7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_area
    ADD CONSTRAINT fkb0f690db7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkb0ffac197576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_user
    ADD CONSTRAINT fkb0ffac197576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkb101e3fa7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_zone
    ADD CONSTRAINT fkb101e3fa7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkbb47dca17576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fkbb47dca17576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkbb47dca1bc27a604; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fkbb47dca1bc27a604 FOREIGN KEY (goodsreceipt_id) REFERENCES los_goodsreceipt(id);


--
-- Name: fkbb47dca1cbc10219; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fkbb47dca1cbc10219 FOREIGN KEY (relatedadvice_id) REFERENCES los_avisreq(id);


--
-- Name: fkbb47dca1e06b76ae; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_grrposition
    ADD CONSTRAINT fkbb47dca1e06b76ae FOREIGN KEY (stockunit_id) REFERENCES mywms_stockunit(id);


--
-- Name: fkbe36ea307576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT fkbe36ea307576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkbe36ea30dfb71f26; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT fkbe36ea30dfb71f26 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fkbe36ea30ead3f22e; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_avisreq
    ADD CONSTRAINT fkbe36ea30ead3f22e FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fkc12d32ee2772b400; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreq
    ADD CONSTRAINT fkc12d32ee2772b400 FOREIGN KEY (parentrequest_id) REFERENCES los_orderreq(id);


--
-- Name: fkc12d32ee4baf7f89; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreq
    ADD CONSTRAINT fkc12d32ee4baf7f89 FOREIGN KEY (cart_id) REFERENCES los_storloc(id);


--
-- Name: fkc12d32ee51cf0e46; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreq
    ADD CONSTRAINT fkc12d32ee51cf0e46 FOREIGN KEY (user_id) REFERENCES mywms_user(id);


--
-- Name: fkc12d32ee5a663be; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreq
    ADD CONSTRAINT fkc12d32ee5a663be FOREIGN KEY (id) REFERENCES mywms_request(id);


--
-- Name: fkc12d32ee610ee7db; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_pickreq
    ADD CONSTRAINT fkc12d32ee610ee7db FOREIGN KEY (destination_id) REFERENCES los_storloc(id);


--
-- Name: fkc5c8c4787576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_stockrecord
    ADD CONSTRAINT fkc5c8c4787576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkcb6b2025264b99aa; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_extinguishreq
    ADD CONSTRAINT fkcb6b2025264b99aa FOREIGN KEY (id) REFERENCES los_pickreq(id);


--
-- Name: fkcb6b2025ead3f22e; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_extinguishreq
    ADD CONSTRAINT fkcb6b2025ead3f22e FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fkcfd7673629b0fbe4; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fkcfd7673629b0fbe4 FOREIGN KEY (unitloadadvice_id) REFERENCES los_uladvice(id);


--
-- Name: fkcfd767367576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fkcfd767367576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkcfd76736dfb71f26; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fkcfd76736dfb71f26 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fkcfd76736ead3f22e; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_uladvicepos
    ADD CONSTRAINT fkcfd76736ead3f22e FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: fkd0763bcd68666c2; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_stocktakingrecord
    ADD CONSTRAINT fkd0763bcd68666c2 FOREIGN KEY (stocktakingorder_id) REFERENCES los_stocktakingorder(id);


--
-- Name: fkd2f1a7817576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_request
    ADD CONSTRAINT fkd2f1a7817576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkd301cc1b2aa3ea0a; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_racklocation
    ADD CONSTRAINT fkd301cc1b2aa3ea0a FOREIGN KEY (id) REFERENCES los_storloc(id);


--
-- Name: fkd301cc1b6e957a1b; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_racklocation
    ADD CONSTRAINT fkd301cc1b6e957a1b FOREIGN KEY (rack_id) REFERENCES los_rack(id);


--
-- Name: fkd610ba697576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_document
    ADD CONSTRAINT fkd610ba697576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkda108e9f5a663be; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreq
    ADD CONSTRAINT fkda108e9f5a663be FOREIGN KEY (id) REFERENCES mywms_request(id);


--
-- Name: fkda108e9f610ee7db; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_orderreq
    ADD CONSTRAINT fkda108e9f610ee7db FOREIGN KEY (destination_id) REFERENCES los_storloc(id);


--
-- Name: fkdea77da2acb9ff15; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt_los_avisreq
    ADD CONSTRAINT fkdea77da2acb9ff15 FOREIGN KEY (los_goodsreceipt_id) REFERENCES los_goodsreceipt(id);


--
-- Name: fkdea77da2eb5589cf; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_goodsreceipt_los_avisreq
    ADD CONSTRAINT fkdea77da2eb5589cf FOREIGN KEY (assignedadvices_id) REFERENCES los_avisreq(id);


--
-- Name: fke8ea928b132aa2d4; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fke8ea928b132aa2d4 FOREIGN KEY (handlingunit_id) REFERENCES mywms_itemunit(id);


--
-- Name: fke8ea928b53f5f6a6; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fke8ea928b53f5f6a6 FOREIGN KEY (zone_id) REFERENCES mywms_zone(id);


--
-- Name: fke8ea928b7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fke8ea928b7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fke8ea928bea8a22b4; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemdata
    ADD CONSTRAINT fke8ea928bea8a22b4 FOREIGN KEY (defultype_id) REFERENCES mywms_unitloadtype(id);


--
-- Name: fke8f27c6521a5fb68; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_itemunit
    ADD CONSTRAINT fke8f27c6521a5fb68 FOREIGN KEY (baseunit_id) REFERENCES mywms_itemunit(id);


--
-- Name: fkf5ee48d51d030746; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishreq_los_storloc
    ADD CONSTRAINT fkf5ee48d51d030746 FOREIGN KEY (los_replenishreq_id) REFERENCES los_replenishreq(id);


--
-- Name: fkf5ee48d58b688799; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY los_replenishreq_los_storloc
    ADD CONSTRAINT fkf5ee48d58b688799 FOREIGN KEY (allowedsources_id) REFERENCES los_storloc(id);


--
-- Name: fkf83eed567576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_clearingitem
    ADD CONSTRAINT fkf83eed567576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkff6d960c7576a346; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fkff6d960c7576a346 FOREIGN KEY (client_id) REFERENCES mywms_client(id);


--
-- Name: fkff6d960caf5eb406; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fkff6d960caf5eb406 FOREIGN KEY (unitload_id) REFERENCES mywms_unitload(id);


--
-- Name: fkff6d960cc7da58db; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fkff6d960cc7da58db FOREIGN KEY (unitload_id) REFERENCES los_unitload(id);


--
-- Name: fkff6d960cdfb71f26; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fkff6d960cdfb71f26 FOREIGN KEY (itemdata_id) REFERENCES mywms_itemdata(id);


--
-- Name: fkff6d960cead3f22e; Type: FK CONSTRAINT; Schema: public; Owner: jboss
--

ALTER TABLE ONLY mywms_stockunit
    ADD CONSTRAINT fkff6d960cead3f22e FOREIGN KEY (lot_id) REFERENCES mywms_lot(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

