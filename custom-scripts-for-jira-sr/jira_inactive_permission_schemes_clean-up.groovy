/*
* Inactive Permission scheme in Jira/JSM Clean-up
* Created by Dmitrij P @ Sept 2023
* Fork of : https://github.com/gonchik/cleanup-scripts/blob/master/groovy/jira/issues/schemes/permissionSchemesCleaner.groovy
*/
// Main object
import com.atlassian.jira.component.ComponentAccessor
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger
// Date & Time
import groovy.time.TimeCategory 
import groovy.time.TimeDuration

def execStart = new Date()
// trace issue creation variables
def traceProjectKey = 'OPTIMA' // Id of a project to trace the action
String traceIssueType = 'Story' // Issue type of trace issue
def desc = new StringBuilder() // Description
String traceSummary = 'TEST - Inactive Permission Schemes Sulk Clean-up'

def log = Logger.getLogger("Inactive Permission Schemes Bulk delete")
log.setLevel(Level.DEBUG)
log.debug(traceSummary + " - Begin")

// PermissionSchemeManager
def permissionSchemeManager = ComponentAccessor.getPermissionSchemeManager()
// Project management
def projectManager = ComponentAccessor.getProjectManager()

int count = 0
permissionSchemeManager.getUnassociatedSchemes().each {
    try{
        log.debug("Deleting unused permission scheme: ${it.name}")
        def sId = Long.valueOf("${it.id}")
        //permissionSchemeManager.deleteScheme(sId) // comment to skip the Delete action
        count++
        desc.append("Unused permission scheme: ${it.name} - was deleted \n")
    }
    catch (Exception e) {
        log.debug("Error: " + e )
        desc.append("Error: " + e )
    }
}

desc.append("*${count}* inactive Permission Schemes were deleted \n")

def execEnd = new Date()
TimeDuration execTotal = TimeCategory.minus(execEnd, execStart)
desc.append("\n *Total execution time: < ${execTotal} >* \n")
log.debug("\n *Total execution time: < ${execTotal} >*")

// trace issue creation
log.debug("Trace issue creation in " + projectManager.getProjectObjByKey(traceProjectKey))
Issues.create(traceProjectKey, traceIssueType) {
    setSummary(traceSummary)
    setPriority('High')
    setDescription(desc.toString())
}
log.debug("Trace issue was created")

log.debug(traceSummary + " - End")