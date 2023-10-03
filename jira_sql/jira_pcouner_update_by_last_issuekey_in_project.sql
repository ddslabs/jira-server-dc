## --- GLOBAL pcounter update/set to last MAX issue key
update 
	project p 
left join (
	select 
		PROJECT as pid,
		max(issuenum) as val
	from jiraissue 
	group by PROJECT
) ji on p.ID = ji.pid
set p.pcounter = ji.val