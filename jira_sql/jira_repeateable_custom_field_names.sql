###--- Nb of repeateable names of (custom) fields 
with fields as (
	select 
		fsli.ID as fid,
		if(fsli.fieldidentifier is null, 'EMPTY FIELD NAME', fsli.fieldidentifier) as ident,
		if(concat('customfield_', cf.id) = fsli.fieldidentifier, cf.cfname, fsli.fieldidentifier) as fname, 
		cf.ID as cfid,
		cf.cfname as cfname,
		LOWER(if(concat('customfield_', cf.id) = fsli.fieldidentifier, cf.cfname, fsli.fieldidentifier)) as low_name
	from fieldscreenlayoutitem fsli
	left join customfield cf on concat('customfield_', cf.id) = fsli.fieldidentifier
	group by fsli.fieldidentifier
)
select  
	f.fname as FieldName,
	f.low_name as LowFieldName,
	count(f.fid) NbOfSameCFs
from fields f
group by f.low_name
order by count(f.fid) desc