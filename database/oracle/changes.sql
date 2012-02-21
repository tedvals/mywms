/* 
 * Add your changes to database here as postgres SQL command
 * Changes since release 1.2.0
 */

15.09.2010
create table public.los_bom (id int8 not null, additionalContent varchar(255), created timestamp, entity_lock int4, modified timestamp, version int4 not null, amount numeric(19, 2), index int4 not null, pickable bool not null, parent_id int8 not null, child_id int8 not null, primary key (id), unique (parent_id, child_id));

alter table los_advice add column externalNumber varchar(255);
alter table los_advice add column externalId varchar(255);


24.09.2010
alter table los_sysprop add column workstation varchar(255);
alter table los_sysprop add column description varchar(255);
alter table los_sysprop add column groupname varchar(255);
alter table los_sysprop add column client_id int8;
alter table los_sysprop add column hidden boolean;

update los_sysprop set workstation = 'DEFAULT' where workstation is null;
update los_sysprop set client_id = 0 where client_id is null;
update los_sysprop set hidden = false where hidden is null;

alter table los_sysprop alter column client_id set not null;
alter table los_sysprop alter column workstation set not null;

alter table los_sysprop DROP CONSTRAINT los_sysprop_syskey_key;
CREATE UNIQUE INDEX los_sysprop_key ON los_sysprop  USING btree ("syskey", "client_id", "workstation");

alter table public.los_sysprop add constraint FK7C112DC17576A346 foreign key (client_id) references public.mywms_client;

11.11.2010
alter table los_unitload add column weightGross numeric(16, 3);
alter table los_unitload add column weightNet numeric(16, 3);

alter table mywms_lot add column height numeric(15, 2);
alter table mywms_lot add column width numeric(15, 2);
alter table mywms_lot add column depth numeric(15, 2);
alter table mywms_lot add column volume numeric(19,6);
alter table mywms_lot add column weight numeric(16, 3);
alter table mywms_lot add column code varchar(255);
alter table mywms_lot add column age varchar(255);

alter table mywms_unitloadtype add column volume numeric(19,6);
alter table mywms_unitloadtype alter column weight type numeric(16, 3);
alter table mywms_unitloadtype alter column liftingCapacity type numeric(16, 3);

alter table mywms_itemdata add column volume numeric(19,6);
alter table mywms_itemdata alter column weight type numeric(16, 3);

alter table los_storagelocationtype add column volume numeric(19,6);
alter table los_storagelocationtype alter column liftingCapacity type numeric(16, 3);

alter table los_bom alter column amount type numeric(17, 4);
alter table los_fixassgn alter column desiredAmount type numeric(17, 4);
alter table los_grrposition alter column amount type numeric(17, 4);
alter table los_stockrecord alter column amount type numeric(17, 4);
alter table los_stockrecord alter column amountStock type numeric(17, 4);
alter table los_uladvicepos alter column notifiedAmount type numeric(17, 4);



