###--- nb of issues updated before <date> --- 
with count_issues as (
	select 
		project, 
		count(*) as total 
	from jiraissue j group by project
), issue_updated_before_date as (
	select 
		p.ID as ProjectId,
		p.pkey as ProjectKey,
		count(*) as total 
	from project p
	left outer join jiraissue j on j.project = p.id
	where j.UPDATED < '2022-01-01'
	group by p.pkey 
)
select 
	p.pkey as ProjectKey,
	p.pname as ProjectName,
	p.PROJECTTYPE as ProjectType,
	p.LEAD as ProjectLead,
	au.lower_user_name as RealLeadUserName,
	cu.active as ActiveLeadUser,
	cu.email_address as ProjectLeadEmail,
	p.pcounter as IssuesCreated,
	ci.total as TotalIssuesInProject,
	(p.pcounter - ci.total) as IssuesDifference,
	round(((ci.total/p.pcounter)*100),2) as PresentIssuesPourCent,
	max(j.UPDATED) as LastUpdated,
	up.total as NbOfIssuesUpdatedAvantDate,
	(ci.total - up.total) as IssuesUpdatedApresDate
from project p 
left join jiraissue j on j.PROJECT = p.ID 
left join issue_updated_before_date up on up.ProjectId = p.ID 
left join count_issues ci on ci.project = j.PROJECT 
left join app_user au on au.user_key = p.LEAD 
left join cwd_user cu on cu.lower_user_name = au.lower_user_name 
group by p.pkey 
order by 12, 1