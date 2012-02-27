create or replace
function md5hash(
  in_string in varchar2)
return varchar2
as
  cln_md5raw raw(2000);
  out_raw raw(16);
begin
  cln_md5raw := utl_raw.cast_to_raw(in_string);
  dbms_obfuscation_toolkit.md5(input=>cln_md5raw,checksum=>out_raw);
  -- return hex version (32 length)
  return lower(rawtohex(out_raw));
end;