CREATE OR REPLACE FUNCTION MD5 (
    CADENA IN VARCHAR2
) RETURN DBMS_OBFUSCATION_TOOLKIT.VARCHAR2_CHECKSUM
AS
BEGIN
    RETURN LOWER(
        RAWTOHEX(
            UTL_RAW.CAST_TO_RAW(
                DBMS_OBFUSCATION_TOOLKIT.MD5(INPUT_STRING => CADENA)
            )
        )
    );
END;

insert into mywms_client(id, version, entity_lock, created, modified, 
name, CL_NR, cl_code, 
additionalcontent) 
values(0, 0, 0, current_timestamp, current_timestamp, 
'System-Client', 'System', 'System-Client',
'This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name.');

insert into mywms_role(id, version, entity_lock, created, modified,
name, description,
additionalcontent)
values(0, 0, 0, current_timestamp, current_timestamp, 
'Admin', 'System Administrator',
'This is a system used entity. DO NOT REMOVE, LOCK OR RENAME IT! Some processes may use it.');

insert into mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id, 
additionalcontent) 
values(0, 0, 0, current_timestamp, current_timestamp, 
'admin', 'en', md5hash('admin'), 0,
'This is a system used entity. DO NOT REMOVE OR LOCK IT! Some processes may use it. But feel free to choose a suitable name and password.');

insert into mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id) 
values(1, 0, 0, current_timestamp, current_timestamp, 
'deutsch', 'de', md5hash('deutsch'), 0);

insert into mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id) 
values(2, 0, 0, current_timestamp, current_timestamp, 
'english', 'en', md5hash('english'), 0);

insert into mywms_user(id, version, entity_lock, created, modified, 
name, locale, password, client_id) 
values(4, 0, 0, current_timestamp, current_timestamp, 
'francais', 'fr', md5hash('francais'), 0);

insert into mywms_user_mywms_role(mywms_user_id, roles_id)
(select u.id, r.id 
from mywms_user u , mywms_role r
where u.name in ('admin','deutsch','english','francais') and
r.name='Admin');