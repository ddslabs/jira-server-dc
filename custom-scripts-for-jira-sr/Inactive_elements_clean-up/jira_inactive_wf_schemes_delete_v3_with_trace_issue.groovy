/*
* Unused/Inactive Jira/JSM Workflows (Schemes) Clean-up
* fork of https://community.atlassian.com/t5/Jira-Core-Server-articles/Cleaning-up-Inactive-Workflow-Schemes-and-Workflows/ba-p/2029884
* Created by Dmitrij P @ June 2023 / Updated @ Sept 2023
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.workflow.WorkflowSchemeService
import com.atlassian.jira.workflow.WorkflowSchemeManager
import com.atlassian.jira.workflow.WorkflowManager
import com.atlassian.jira.workflow.WorkflowScheme
import com.atlassian.jira.workflow.WorkflowException
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
String traceSummary = 'TEST - Unused/Inactive Workflow (Schemes) Clean-up (no delete)'

def log = Logger.getLogger("Workflow (Schemes) management")
log.setLevel(Level.DEBUG)

log.debug(traceSummary +" - Begin")

// Object managers
@Field WorkflowSchemeManager workflowSchemeManager = ComponentAccessor.getWorkflowSchemeManager()
@Field WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
@Field WorkflowSchemeService workflowSchemeService = ComponentAccessor.getComponent(WorkflowSchemeService)
// Project management
def projectManager = ComponentAccessor.getProjectManager()

// Delete all inactive Workflow Schemes
def deleteInactiveWorkflowSchemes() {
    def log = Logger.getLogger("deleteInactiveWorkflowSchemes()")
    desc.append("Delete of inactive Workflow Schemes - Begin \n")
    log.setLevel(Level.DEBUG)
    // Connected user
    def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
    // inactiveWorkflowSchemes
    Collection<WorkflowScheme> inactiveWorkflowSchemes = workflowSchemeManager.getAssignableSchemes()
        .findAll{!workflowSchemeService.isActive((WorkflowScheme) it)} // class java.util.ArrayList 
    log.debug("deleteInactiveWorkflowSchemes() - Begin")
    
    log.debug("${inactiveWorkflowSchemes.size()} inactive workflow schemes will be deleted")
    int count = 0
    for (iws in inactiveWorkflowSchemes) {
        try {
            workflowSchemeService.validateUpdateWorkflowScheme(user,iws)
            //log.debug("Delete is possible of <${iws.getName()}>") 
            //workflowSchemeService.deleteWorkflowScheme(user, iws) //comment to skip WF Scheme delete action
            log.debug("${iws.getName()} - was deleted")   
            desc.append("* ${iws.getName()} - was deleted \n")
            count++      
        } catch (WorkflowException we) {
            log.error("Unable to delete ${iws.getDisplayName()}")
            log.error("WorkflowException Error message: " + we.getMessage())
        }
    }
    log.debug("deleteInactiveWorkflowSchemes() - End")
    desc.append("\n*${count} inactive Workflow Schemes were deleted* \n")
    desc.append("Delete of inactive Workflow Schemes - End \n\n")
}

// Delete all inactive Workflows
def deleteInactiveWorkflows() {
    def log = Logger.getLogger("deleteInactiveWorkflows()")
    desc.append("Delete of inactive Workflows - Begin \n")
    log.setLevel(Level.DEBUG)
    log.debug("deleteInactiveWorkflows() - Begin")
    // inactiveWorkflows
    def allWorkflows = workflowManager.getWorkflows()// Default(System)/Configurable JiraWorkflow objects Collection ; class java.util.ArrayList
    def activeWorkflows = workflowManager.getActiveWorkflows() // Configurable JiraWorkflow objects Collection ; class java.util.HashSet
    def inactiveWorkflows = allWorkflows.findAll{!(it in activeWorkflows)} // class java.util.ArrayList
    
    log.debug("${inactiveWorkflows.size()} inactive workflows may be deleted")
    int count = 0
    for (iw in inactiveWorkflows) {
        try {
            //log.debug("is iw ${iw.getName()} - editable: ${iw.editable} & default: ${workflowManager.isSystemWorkflow(iw)}")
            if(iw.editable) {
                //log.debug("${iw.getDisplayName()} will be deleted")
                //workflowManager.deleteWorkflow(iw) //comment to skip WF delete action
                log.debug("${iw.getDisplayName()} - was deleted")  
                desc.append("* ${iw.getDisplayName()} - was deleted \n")
                count++              
            } else {
                log.warn("${iw.getDisplayName()} is default system workflow and may not be deleted")
                desc.append("*ELSE CASE:* ${iw.getDisplayName()} is default system workflow and may not be deleted \n")
            }
        } catch (WorkflowException we) {
            log.error("Unable to delete ${iw.getDisplayName()}")
            log.error("Error message: " + we.getMessage())
        }
    }
    log.debug("deleteInactiveWorkflows() - End")
    desc.append("\n*${count} inactive Workflows were deleted* \n")
    desc.append("Delete of inactive Workflows - End \n")
}

// ATTENTION: First delete the inactive schemes, then delete the inactive workflows
// First action
deleteInactiveWorkflowSchemes()
// Second action
deleteInactiveWorkflows()

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