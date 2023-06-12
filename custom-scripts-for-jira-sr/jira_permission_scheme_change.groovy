/*
* Change Project Permission scheme in Jira/JSM instance
* Created by Dmitrij P @ May 2023
*/
// Main object
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import org.ofbiz.core.entity.GenericEntityException
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("Project Permission scheme change")
log.setLevel(Level.DEBUG)
log.debug("Project permissions update - Begin")

def permissionSchemeManager = ComponentAccessor.getPermissionSchemeManager()
def projectManager = ComponentAccessor.getProjectManager()

// Permission scheme to Add
def schm = permissionSchemeManager.getSchemeObject(10100)
log.debug("Permission scheme object: " + schm)
// Project to change the Permission scheme
Project proj = projectManager.getProjectObjByKey("ZOIP")
log.debug(proj)

// try-catch
try {
    // Flush all permission schemes of the project to avoid Jira/JSM instance crash
    permissionSchemeManager.removeSchemesFromProject(proj)
    log.debug("Permission scheme was deleted for " + proj)
    // Set new permission scheme to project X
    permissionSchemeManager.addSchemeToProject(proj,schm)
    log.debug("Adding " + schm + " to " + proj)
} catch (GenericEntityException ex) {
    log.debug("Error message: " + ex.getMessage())
}

log.debug("Project permissions update - End")