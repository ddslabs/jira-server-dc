SELECT
    p.pkey as "Project key",
    p.pname as ProjectName,
    p.LEAD as ProjectLead,
    pr.name as "Role name",
    u.lower_user_name as "Username",
    u.active as ActiveAdminUser,
    u.display_name as "Display name",
    u.lower_email_address as "e-Mail",
    null as "Group name"
FROM projectroleactor pra
INNER JOIN projectrole pr ON pr.ID = pra.PROJECTROLEID
INNER JOIN project p ON p.ID = pra.PID
INNER JOIN app_user au ON au.user_key = pra.ROLETYPEPARAMETER
INNER JOIN cwd_user u ON u.lower_user_name = au.lower_user_name
WHERE 
	pra.roletype = 'atlassian-user-role-actor' 
	and pr.NAME = 'Administrators'
	and (p.LEAD = u.lower_user_name and u.active = 0)
UNION
SELECT
    p.pkey as "Project key",
    p.pname as ProjectName,
    p.LEAD as ProjectLead,
    pr.name as "Role name",
    cmem.lower_child_name as "Username",
    u.active as ActiveAdminUser,
    u.display_name as "Display name",
    u.lower_email_address as "e-Mail",
    cmem.lower_parent_name as "Group name"
FROM projectroleactor pra
INNER JOIN projectrole pr ON pr.ID = pra.PROJECTROLEID
INNER JOIN project p ON p.ID = pra.PID
INNER JOIN cwd_membership cmem ON cmem.parent_name = pra.roletypeparameter
INNER JOIN app_user au ON au.lower_user_name = cmem.lower_child_name
INNER JOIN cwd_user u ON u.lower_user_name = au.lower_user_name
WHERE pra.roletype = 'atlassian-group-role-actor' and pr.NAME = 'Administrators'
order by 1, 2, 3