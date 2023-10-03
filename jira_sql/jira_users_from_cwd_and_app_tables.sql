###--- all collumns
select 
    *
from cwd_user cu 
left join app_user au on cu.lower_user_name = au.lower_user_name
left join cwd_directory cd on cd.ID = cu.directory_id