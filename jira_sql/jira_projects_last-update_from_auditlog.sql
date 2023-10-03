###--- all registered in AuditLog projects with last update date/time
with max_update as (
	select 
        ae.PRIMARY_RESOURCE_ID as pid,
        max(ae.ENTITY_TIMESTAMP) as maxts
    from AO_C77861_AUDIT_ENTITY ae
    where ae.CATEGORY = "projects"
    group by ae.PRIMARY_RESOURCE_ID 
)
select 
    p.pkey ,
    p.pname ,
    FROM_UNIXTIME(MAX(mu.maxts)/1000) as valeur_datetime,
    CAST(FROM_UNIXTIME(MAX(mu.maxts)/1000) as date) as valeur_date 
from project p 
join max_update mu on 
    mu.pid = p.ID 
group by p.pkey
order by mu.maxts desc