/*
* Jira/JSM project name change
* fork of https://github.com/sparxsys/ScriptRunner-Scripts/blob/master/jira_server/projectRename.groovy
* Created by Dmitrij P @ May 2023
*/
// Main object
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.UpdateProjectParameters
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger


def log = Logger.getLogger("Project name change")
log.setLevel(Level.DEBUG)
log.debug("Project name change - Begin")

def projectManager = ComponentAccessor.getProjectManager()
def project = projectManager.getProjectObjByKey("OIP")

if(project) {
    def updateProjectParameters = UpdateProjectParameters.forProject(project.id).name("ZZZ Old issues project")
    projectManager.updateProject(updateProjectParameters)
    log.debug("Project renamed")
} else {
    log.debug("Project doesn't exist")
}

log.debug("Project name change - Begin")