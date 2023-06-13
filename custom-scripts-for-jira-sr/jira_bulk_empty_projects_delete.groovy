/*
* Bulk empty projects delete in Jira/JSM
* Created by Dmitrij P @ May 2023
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.ProjectService
// For logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("Bulk empty projects delete action")
log.setLevel(Level.DEBUG)
log.debug("Bulk empty projects delete - begin")

// --- project delete operators 
def projectManager = ComponentAccessor.getProjectManager()
def projectService = ComponentAccessor.getComponent(ProjectService.class)

log.debug("Creation of the list of empty projects")
def empty_projects = projectManager.getProjects().findAll() {
    issueManager.getIssueCountForProject(it.id) == 0}.collect {it.key}
log.debug("Empty projets count: " + empty_projects.size())

int pnum = 1 // optional
empty_projects.forEach() { project ->
    log.debug("Empty project Nr " + pnum + " : " + project.getChars())
    pnum++
    
    // Validation of a possibility to delete an empty project N
    def delValRes = projService.validateDeleteProject(user, project.toString())
    log.debug("Validation: project <" + project.toString() + "> may be deleted.")
    
    // Comment "try-catch" section if you need to list the projects without deleting //
    // Empty prjects delete action
    log.debug("Attempt to delete " + project.toString())
    try {
        projService.deleteProject(user, delValRes)
        log.debug("--- Project " + project.toString() + " wad deleted successfully.")
    } catch (Exception ex) {
        log.debug("--- ERROR: impossible to delete " + project.toString())
        log.debug(ex.getMessage())
    }

}

log.debug("Bulk empty projects delete - end")