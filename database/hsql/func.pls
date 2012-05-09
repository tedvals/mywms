CREATE OR REPLACE FUNCTION md5hash (str IN VARCHAR2)
RETURN VARCHAR2
IS v_checksum VARCHAR2
BEGIN
v_checksum := LOWER( RAWTOHEX( UTL_RAW.CAST_TO_RAW( sys.dbms_obfuscation_toolkit.md5(input_string => str) ) ) );
RETURN v_checksum;
EXCEPTION
WHEN NO_DATA_FOUND THEN
NULL;
WHEN OTHERS THEN
RAISE;
END md5hash;