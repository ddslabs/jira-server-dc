/*
* Unused/Inactive Issue Type Screen Schemes Clean-up in Jira/JSM 
* Created by Dmitrij P @ Sept 2023
* With some idieas from:
* https://github.com/gonchik/cleanup-scripts/blob/master/groovy/jira/issues/schemes/issueTypeSchemesCleaner.groovy
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
String traceSummary = 'TEST - Issue Type Screen Schemes Clean-up (check action)'

def log = Logger.getLogger("ITSS Clean-up")
log.setLevel(Level.DEBUG)
log.debug(traceSummary + " - Begin")

// ITSS managements
def itssManager = ComponentAccessor.getIssueTypeScreenSchemeManager()
// Project management
def projectManager = ComponentAccessor.getProjectManager()

int count = 0
itssManager.issueTypeScreenSchemes.each { itss ->
    try {
        if (!itss.isDefault()) {
            if (itssManager.getProjects(itss).size() < 1) {
                log.debug("Inactive ITSS: <${itss.getName()}> will be deleted") 
                //itssManager.removeIssueTypeScreenScheme(itss) //to comment, if you want to skip delete action
                count++
                desc.append("Inactive ITSS: <${itss.getName()}> was be deleted \n")
            }
        } else {
            log.debug("Else case <${itss.getName()}> is a default Issue Type Screen Scheme")
            desc.append("Else case <${itss.getName()}> is a default Issue Type Screen Scheme and may not be deleted. \n")
        }
        
    }
    catch (Exception e) {
        log.error("Error: " + e )
    }
}
desc.append("*${count}* inactive Issue Type Screen Schemes were deleted \n")

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
