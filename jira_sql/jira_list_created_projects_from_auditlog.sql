select 
	p.pkey as ProjectKey ,
	ae.ENTITY_TIMESTAMP as Atlassian_AuditLog_TimeStamp ,
	FROM_UNIXTIME(ae.ENTITY_TIMESTAMP/1000) as Create_Date_Time ,
	cast(FROM_UNIXTIME(ae.ENTITY_TIMESTAMP/1000) as date) as Create_Date
from AO_C77861_AUDIT_ENTITY ae
left join project p on p.ID = ae.PRIMARY_RESOURCE_ID 
where ae.`ACTION` = "Project created"
having (cast(FROM_UNIXTIME(ae.ENTITY_TIMESTAMP/1000) as date) > '2023-05-01')
order by 4 desc