CREATE OR REPLACE function jboss.md5hash(in_string in varchar2)
return varchar2 as cln_md5raw raw(2000); out_raw raw(16); begin cln_md5raw := utl_raw.cast_to_raw(in_string);dbms_obfuscation_toolkit.md5(input=>cln_md5raw,checksum=>out_raw);return lower(rawtohex(out_raw));end;;

insert into jboss.mywms_client(id, version, entity_lock, created, modified, 
name, CL_NR, cl_code, 
additionalcontent) 
values(0, 0, 0, current_timestamp, current_timestamp, 
'System-Client', 'System', 'System-Client',
'This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name.');

insert into jboss.mywms_role(id, version, entity_lock, created, modified,
name, description,
additionalcontent)
values(0, 0, 0, current_timestamp, current_timestamp, 
'Admin', 'System Administrator',
'This is a system used entity. DO NOT REMOVE, LOCK OR RENAME IT! Some processes may use it.');

insert into jboss.mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id, 
additionalcontent) 
values(0, 0, 0, current_timestamp, current_timestamp, 
'admin', 'en', jboss.md5hash('admin'), 0,
'This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name and password.');

insert into jboss.mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id) 
values(1, 0, 0, current_timestamp, current_timestamp, 
'deutsch', 'de', jboss.md5hash('deutsch'), 0);

insert into jboss.mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id) 
values(2, 0, 0, current_timestamp, current_timestamp, 
'english', 'en', jboss.md5hash('english'), 0);

insert into jboss.mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id) 
values(4, 0, 0, current_timestamp, current_timestamp, 
'francais', 'fr', jboss.md5hash('francais'), 0);

insert into jboss.mywms_user_jboss.mywms_role(jboss.mywms_user_id, roles_id)
(select u.id, r.id 
from jboss.mywms_user u , jboss.mywms_role r
where u.name in ('admin','deutsch','english','francais') and
r.name='Admin');