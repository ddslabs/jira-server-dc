/*
* SQL query results treatment in Jira/JSM
* ( MySQL/RDS type query )
* Created by Dmitrij P @ May 2023
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
// For SQL
import org.ofbiz.core.entity.ConnectionFactory
import org.ofbiz.core.entity.DelegatorInterface
import groovy.sql.Sql
import java.sql.Connection
// For logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("SQL in script")
log.setLevel(Level.DEBUG)
log.debug("SQL query results - Begin")

def delegator = (DelegatorInterface)ComponentAccessor.getComponent(DelegatorInterface)
String helperName = delegator.getGroupHelperName( "default")

// Enclose the SQL statement in triple quotes so it can span multiple lines for better visibility
def sqlStmt = """

with max_maj as (
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
    FROM_UNIXTIME(MAX(mm.maxts)/1000) as valeur_datetime,
    CAST(FROM_UNIXTIME(MAX(mm.maxts)/1000) as date) as valeur_date 
from project p 
join max_maj mm on 
    mm.pid = p.ID 
group by p.pkey
order by mm.maxts desc

"""

Connection conn = ConnectionFactory.getConnection(helperName)
Sql sql = new Sql(conn)

// Use try and finally blocks for database connections because if something is wrong with the database connection
// the connection can atleast be closed. finally block always gets executed.
//def projectsMap = [:] // TO DO
List projectList = []
try {
	StringBuffer sb = new StringBuffer()
	sql.eachRow(sqlStmt) { it->
        def dateVal = it.getAt("date_value")
        //log.debug("dateVal value: " + dateVal)
        if(it.getAt("date_value") != null) {
            sb.append(it.getAt("pkey") + " : " + it.getAt("date_value") + " - " + dateVal.getClass().getName())
            sb.append("\n")

            //projectsMap.add(it.getAt("pkey"),it.getAt("date_value"))
            //projectsMap.add("maj",dateVal.toString())
            projectsMap.putAll([it.getAt("pkey"):it.getAt("date_value")])
            projectList.add(it.getAt("pkey"))
            
        }
	}

	log.debug("List of Projects with Last Update dates from AuditLog \n" + sb.toString())
    //log.debug("Map of valid values: " + projectsMap.toMapString())
    log.debug("Simple list of projects: " + projectList.toListString())
}
finally {
	sql.close() // Every connection MUST BE CLOSED!
}

log.debug("SQL query results - Begin")