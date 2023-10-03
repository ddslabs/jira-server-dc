/*
* Inactive Notification Scheme Bulk delete action in Jira/JSM instance
* Created by Dmitrij P @ Jully 2023 / Updated @ Sept 2023
*/
// Main object
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import org.ofbiz.core.entity.GenericEntityException
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
String traceSummary = 'TEST - Inactive Notification Scheme Clean-up (no delete)'

def log = Logger.getLogger(traceSummary)
log.setLevel(Level.DEBUG)
log.debug(traceSummary + " - Begin")

def notificationShcemeManager = ComponentAccessor.getNotificationSchemeManager()
def projectManager = ComponentAccessor.getProjectManager()

log.debug('<<< Inactive Notificaiton Schemes: >>>')
def inactNotSchms = notificationShcemeManager.getUnassociatedSchemes()

log.debug('<<< Inactive Notification Schemes delete: >>>')
int count = 0
inactNotSchms.forEach() { nSchmForDelete ->
    try {
        log.debug("Notification Scheme ID: <${nSchmForDelete.getId()}> & Label: <${nSchmForDelete.getName()}> - will be deleted")
        Long sId = Long.valueOf(nSchmForDelete.getId())
        //log.debug('sId type/class: ' + sId.class)
        //notificationShcemeManager.deleteScheme(sId) // to comment if you need to list Notification Schemes without delete action
        count++
        desc.append("Notification Scheme ID: <${nSchmForDelete.getId()}> & Label: <${nSchmForDelete.getName()}> - was deleted \n")
    } catch (Exception e) {
        log.debug("Error: " + e + "\n")
        desc.append("Error: " + e + "\n")
    }
}
desc.append("*${count} inactive Notification scheme were deleted*\n")
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