with count_issues as (
	select project, count(*) as total 
	from jiraissue j group by project
)
select 
	p.pkey as ProjectKey,
	p.pname as ProjectName,
	p.PROJECTTYPE,
	max(j.UPDATED) as LastUpdated,
	p.LEAD as ProjectLead,
	au.lower_user_name as RealLeadUserName,
	cu.active as ActiveUser,
	p.pcounter as NbOfCreatedIssues,
	ci.total as PresentIssues,
	(p.pcounter - ci.total) as diff
from project p 
left join jiraissue j on j.PROJECT = p.ID 
left join app_user au on au.user_key = p.LEAD 
left join cwd_user cu on cu.lower_user_name = au.lower_user_name 
left join count_issues ci on ci.project = j.PROJECT 
group by p.pkey 
order by max(j.UPDATED) desc