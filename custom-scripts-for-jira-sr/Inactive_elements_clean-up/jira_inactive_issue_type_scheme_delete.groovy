/*
* Inactive Jira/JSM Issue Type Schemes Clean-up
* Created by Dmitrij P @ Sept 2023 / Updated @ Oct 2023
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
// Groovy Field class for internal methods/functions
import groovy.transform.Field
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
@Field def desc = new StringBuilder() // Description
String traceSummary = 'TEST - Inactive Jira/JSM Issue Type Schemes Clean-up'

def log = Logger.getLogger("Inactive ITS clean-up")
log.setLevel(Level.DEBUG)

log.debug(traceSummary +" - Begin")

// Issue Typa Scheme Manager
def itsManager = ComponentAccessor.getIssueTypeSchemeManager()
// Project management
def projectManager = ComponentAccessor.getProjectManager()

int count = 0 //deleted items counter
itsManager.allSchemes.each { its ->
    try {
        if (!itsManager.isDefaultIssueTypeScheme(its)) {
            if (its.associatedProjectIds.size() == 0) {
                log.debug("ITS: ${its.name} - will be deleted\n")                
                //itsManager.deleteScheme(its) // to comment to skip delete action
                count++
                desc.append("ITS: ${its.name} - was deleted\n")
            }
        } else {
            desc.append("<<< ITS: ${its.name} - is a system default item and may not be deleted! >>>\n")
        }
    }
    catch (Exception e) {
        log.error("Error: " + e)
        desc.append("Error: " + e + "\n")
    }
}

desc.append("*${count}* inactive Issue Type Schemes were deleted \n")

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