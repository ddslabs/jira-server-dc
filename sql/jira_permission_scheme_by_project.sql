select 
	na.SOURCE_NODE_ID , 
	p.pkey ,
	na.SINK_NODE_ID , 
	ps.NAME 
from nodeassociation na 
left join permissionscheme ps on na.SINK_NODE_ID = ps.ID 
left join project p on p.ID = na.SOURCE_NODE_ID 
where na.SINK_NODE_ENTITY = 'PermissionScheme'
order by 2