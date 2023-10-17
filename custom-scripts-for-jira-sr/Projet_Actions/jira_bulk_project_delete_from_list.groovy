/*
* Jira/JSM project(s) delete from list
* Created by Dmitrij P @June 2023
*/
// Main libraries
import com.atlassian.jira.bc.project.ProjectService
import com.atlassian.jira.component.ComponentAccessor
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("Jira/JSM project(s) delete from list")
log.setLevel(Level.DEBUG)
log.debug("Actions log - Begin")

// Connected/Logged-in user
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
// Main object for project operations
def projectService = ComponentAccessor.getComponent(ProjectService.class)

List<String> projList = ["EP","ZOIP"] // List with any project keys; could be filled by SQL query

// Delete action validator and project delete action
for(projectKey in projList) {
    final ProjectService.DeleteProjectValidationResult result = projectService.validateDeleteProject(user, projectKey)
    log.debug("Possible delete valitator: " + result.getReturnedValue().toString())
                
    if (result.isValid()) {
        try {            
            final ProjectService.DeleteProjectResult projectResult = projectService.deleteProject(user, result)
            //log.debug("Delete result in object: " + projectResult.getProperties().toPrettyString())
            log.info("Project <" + projectKey + "> was deleted")
        } catch (Exception e) {
            log.error("--- !!! Project <" + projectKey + "> was not deleted !!! ---")
            log.error("Exception for project <"+ projectKey + ">\n"+ e)
        }
    } else {
        log.error("--- !!! Project <" + projectKey + "> does not exist or/and may not be deleted !!! ---" )
    }
}

log.debug("Action log - End")