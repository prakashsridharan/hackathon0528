declare
flag varchar2(1);
cursor c1 is select * from INPUT_DATA_REQUEST;
sum_cnt number:=0;
CNT_VALUE number:=0;
begin
for var in c1 loop
sum_cnt:=0;
for KAR in ( select
INPUT_PARSED_DATA_oid, 
TOKEN_STRING,
NEGATION,
TENSE
from INPUT_PARSED_DATA where INPUT_DATA_REQUEST_oid=VAR.INPUT_DATA_REQUEST_oid ORDER BY INPUT_PARSED_DATA_oid ) 
loop
CNT_VALUE:=0;
begin
--if (kar.NEGATION)='PAST'
if (kar.TENSE)=-1
THEN
SELECT RANK INTO CNT_VALUE from lookup_words WHERE word=kar.TOKEN_STRING;
IF (kar.NEGATION)='T' then
CNT_VALUE:=CNT_VALUE*(-1);
END IF;
sum_cnt:=sum_cnt+CNT_VALUE;
END IF;
EXCEPTION
WHEN no_data_found THEN
CNT_VALUE:=0;
sum_cnt:=sum_cnt+CNT_VALUE;
end;
begin
--if (kar.NEGATION)='FUTURE'
if (kar.TENSE)=1
THEN
SELECT RANK INTO CNT_VALUE from lookup_words WHERE word=kar.TOKEN_STRING;
IF (kar.NEGATION)='T' then
CNT_VALUE:=CNT_VALUE*(-1);
END IF;
sum_cnt:=sum_cnt+CNT_VALUE;
END IF;
EXCEPTION
WHEN no_data_found THEN
CNT_VALUE:=0;
sum_cnt:=sum_cnt+CNT_VALUE;
end;
begin
--if (kar.NEGATION)='PRESENT'
if (kar.TENSE)=0
THEN
SELECT RANK INTO CNT_VALUE from lookup_words WHERE word=kar.TOKEN_STRING;
IF (kar.NEGATION)='T' then
CNT_VALUE:=CNT_VALUE*(-1);
END IF;
if SIGN(CNT_VALUE)=-1 then
CNT_VALUE:=CNT_VALUE-2;
elsif SIGN(CNT_VALUE)=1 then
CNT_VALUE:=CNT_VALUE+2;
else
CNT_VALUE:=CNT_VALUE+1;
end if;
sum_cnt:=sum_cnt+CNT_VALUE;
END IF;
EXCEPTION
WHEN no_data_found THEN
CNT_VALUE:=0;
sum_cnt:=sum_cnt+CNT_VALUE;
end;
begin
if (kar.TENSE)IS NULL THEN
SELECT RANK INTO CNT_VALUE from lookup_words WHERE word=kar.TOKEN_STRING;
IF (kar.NEGATION)='T' then
CNT_VALUE:=CNT_VALUE*(-1);
END IF;
sum_cnt:=sum_cnt+CNT_VALUE;
END IF;
EXCEPTION
WHEN no_data_found THEN
CNT_VALUE:=0;
sum_cnt:=sum_cnt+CNT_VALUE;
end;
update INPUT_PARSED_DATA 
SET RANK = CNT_VALUE
WHERE INPUT_PARSED_DATA_OID=kar.INPUT_PARSED_DATA_oid;
end loop;
if SIGN(sum_cnt)=-1 then
flag:='N';
elsif SIGN(sum_cnt)=1 then
flag:='P';
else
flag:='O';
end if;
update INPUT_DATA_REQUEST 
SET OUTPUT_RESPONSE = flag
WHERE INPUT_DATA_REQUEST_OID=var.INPUT_DATA_REQUEST_OID;
end loop;
COMMIT;
end;
