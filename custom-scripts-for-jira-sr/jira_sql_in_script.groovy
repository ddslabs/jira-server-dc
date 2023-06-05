/*
* SQL query results treatment in Jira/JSM
* (MySQL/RDS type query)
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

//Enclose the SQL statement in triple quotes so it can span multiple lines for better visibility
def sqlStmt = """

select 
	p.pkey ,
	p.pname ,
	FROM_UNIXTIME(MAX(ae.ENTITY_TIMESTAMP)/1000) as datetime_value,
  	CAST(FROM_UNIXTIME(MAX(ae.ENTITY_TIMESTAMP)/1000) as date) as date_value 
from project p 
left join AO_C77861_AUDIT_ENTITY ae on 
	ae.PRIMARY_RESOURCE_ID = p.ID 
	and ae.CATEGORY = "projects"
group by p.pkey
order by ae.ENTITY_TIMESTAMP, p.pkey asc

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