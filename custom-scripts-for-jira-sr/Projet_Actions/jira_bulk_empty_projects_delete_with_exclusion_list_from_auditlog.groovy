/*
* Jira empty projects bulk delete with
* SQL query exclusion list from AuditLog
* ( MySQL/RDS type query )
* Created by Dmitrij P @ June 2023
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.ProjectService
// For SQL
import org.ofbiz.core.entity.ConnectionFactory
import org.ofbiz.core.entity.DelegatorInterface
import groovy.sql.Sql
import java.sql.Connection
// For logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("Bulk empty projects delete")
log.setLevel(Level.DEBUG)
log.debug("Bulk empty projects delete with exclusions list from DB - Begin")

def delegator = (DelegatorInterface)ComponentAccessor.getComponent(DelegatorInterface)
String helperName = delegator.getGroupHelperName("default")

// --- Empty projects "filtered" bulk delete

// Logged-in user
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// --- BULK projects delete operators
def projectManager = ComponentAccessor.getProjectManager()
def projService = ComponentAccessor.getComponent(ProjectService.class)

// Enclose the SQL statement in triple quotes so it can span multiple lines for better visibility
def sqlStmt = """

select 
	p.pkey as pkey ,
	ae.ENTITY_TIMESTAMP as Atlassian_AuditLog_TimeStamp ,
	FROM_UNIXTIME(ae.ENTITY_TIMESTAMP/1000) as Create_Date_Time ,
	cast(FROM_UNIXTIME(ae.ENTITY_TIMESTAMP/1000) as date) as Create_Date
from AO_C77861_AUDIT_ENTITY ae
left join project p on p.ID = ae.PRIMARY_RESOURCE_ID 
where ae.`ACTION` = 'Project created'
having (cast(FROM_UNIXTIME(ae.ENTITY_TIMESTAMP/1000) as date) > '2023-05-01')
order by 4 desc

"""

Connection conn = ConnectionFactory.getConnection(helperName)
Sql sql = new Sql(conn)

// Use "try" and "finally" blocks for database connections, if something will go wrong with the connectio to 
// database connection, it will be able to closed. Block "finally" always gets executed.
List projectList = [] // - filter list
try {
	//StringBuffer sb = new StringBuffer()
	sql.eachRow(sqlStmt){ it->
        //sb << "${it.pkey} \t- ${it.Create_Date}\n"
        projectList.add(it.getAt("pkey"))       
	}
	//log.debug("List of Projects with Last Update dates from AuditLog \n" + sb.toString())   
    log.debug("Exclusions list of projects to filter bulk delete: " + projectList.toListString())   
}
finally {
	sql.close() // Every connection MUST BE CLOSED!
}

log.debug("ALL empty projects.")
def empty_projects = projectManager.getProjects().findAll() {
    issueManager.getIssueCountForProject(it.id) == 0}.collect {it.key}
log.debug("Empty projects count: " + empty_projects.size())

int pnum = 1 // optional; deleted projects counter
empty_projects.forEach() { project ->
    
    if(!projectList.contains(project)) { // to skip projects from Exception list
        log.debug("Empry project Nr " + pnum + " : " + project)
        pnum++
        
        // Validation of a possibility to delete project X
        def delValRes = projService.validateDeleteProject(user, project.toString())
        log.debug("Validation: Project " + project.toString() + " could be deleted.")
        
        // Empty projects delete (try-catch)
        log.debug("Attempt to delete project: " + project.toString())
        try {
            //projService.deleteProject(user, delValRes) // to comment to skip project delete action
            log.debug("--- Project " + project.toString() + " was deleted.")
        } catch (Exception ex) {
            log.debug("--- Error to delete project: " + project.toString())
            log.debug(ex.getMessage())
        } 
    } else { // project(s) that was/were not deleted
        log.debug("<<< --- Empty project ${project} was not deleted. --- >>>")
    }   
}

log.debug("Bulk empty projects delete with exclusions list from DB - End")