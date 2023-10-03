/*
* Bulk projects delete from selected projects category in Jira/JSM
* All project issues included.
* Created by Dmitrij P @ May 2023
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.ProjectService
// For logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

String categorie = "TO DELETE" // could have any other name
def log = Logger.getLogger("Bulk projects delete action from category <" + categorie + ">")
log.setLevel(Level.DEBUG)
log.debug("Bulk projects delete from projects category - begin")

// Connected user
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
// --- project delete operators 
def projectManager = ComponentAccessor.getProjectManager()
def projectService = ComponentAccessor.getComponent(ProjectService.class)

// Creation of the list of projects from category N
def projectsList = projectManager.getProjectsFromProjectCategory(projectManager.getProjectCategoryObjectByNameIgnoreCase(categorie))

// Validation d'action et la suppression de(s) projet(s)
for(p in projectsList) {
    def projectKey = p.key

    final ProjectService.DeleteProjectValidationResult result = projectService.validateDeleteProject(user, projectKey)
    log.debug("Validation that following project may be deleted: " + result.getReturnedValue().toString())
    
    // Comment the following section with "try-catch", if you need to list the projects without deleting //
    // Prjects delete action from project category N
    if (result.isValid()) {
        try {            
            final ProjectService.DeleteProjectResult projectResult = projectService.deleteProject(user, result)
            log.info("Project <" + projectKey + "> was deleted.")
        } catch (Exception ex) {
            log.error("--- !!! Project <" + projectKey + "> was not deleted !!! ---"  + projectResult.getErrorCollection().getErrors())
            log.error("Exception message for project <"+ projectKey + "> is:\n"+ ex.getMessage())
        }
    } else {
        log.error("--- !!! Project <" + projectKey + "> may not be deleted !!! ---"  + projectResult.getErrorCollection().getErrors())
    }
}
log.debug("Bulk projects delete from projects category - end")
