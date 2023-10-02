/*
* Unused/Inactive Issue Type Screen Schemes Clean-up in Jira/JSM 
* Created by Dmitrij P @ June 2023
*/
// Main objects
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
String traceSummary = 'TEST - Issue Type Screen Schemes Clean-up (sans suppression)'

def log = Logger.getLogger("ITSS Clean-up")
log.setLevel(Level.DEBUG)
log.debug(traceSummary + " - Begin")

// ITSS managements
def itssManager = ComponentAccessor.getIssueTypeScreenSchemeManager()
// Project management
def projectManager = ComponentAccessor.getProjectManager()
// all ITSSs
def allITSS = itssManager.issueTypeScreenSchemes

// inactive ITSS Bulk delete
for (itss in allITSS) {
    //log.debug("ITSS ID: ${itss.getId()} & Name ${itss.getName()} - Projects: ${itss.getProjects()}")
    if (!itss.getProjects()) { // !itss.getProjects() - only inactive ITSSs
        //log.debug("ITSS ID: ${itss.getId()} & Name ${itss.getName()} - Projects: ${itss.getProjects()}")
        try {
            //itssManager.removeIssueTypeScreenScheme(itss) // comment to prevent inactive ITSS delete
            log.debug("ITSS ID: ${itss.getId()} & Name ${itss.getName()} - was REMOVED/DELETED")
            desc.append("ITSS ID: ${itss.getId()} & Name ${itss.getName()} - was REMOVED/DELETED \n")
        } catch (Exception e) {
            log.error(e.getMessage())
        }
    }
}

def execEnd = new Date()
TimeDuration execTotal = TimeCategory.minus(execEnd, execStart)
desc.append("\n *Total execution time: < ${execTotal} >* \n")
// trace issue creation
log.debug("Trace issue creation in " + projectManager.getProjectObjByKey(traceProjectKey))
Issues.create(traceProjectKey, traceIssueType) {
    setSummary(traceSummary)
    setPriority('High')
    setDescription(desc.toString())
}
log.debug("Trace issue was created")

log.debug(traceSummary + " - End")