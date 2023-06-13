/*
* Unused/Inactive Jira/JSM Workflows (Schemes) Clean-up
* fork of https://community.atlassian.com/t5/Jira-Core-Server-articles/Cleaning-up-Inactive-Workflow-Schemes-and-Workflows/ba-p/2029884
* Created by Dmitrij P @ June 2023
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

def log = Logger.getLogger("Workflow (Schemes) management")
log.setLevel(Level.DEBUG)

log.debug("Unused/Inactive Workflow (Schemes) Clean-up - Begin")

// Object managers
@Field WorkflowSchemeManager workflowSchemeManager = ComponentAccessor.getWorkflowSchemeManager()
@Field WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
@Field WorkflowSchemeService workflowSchemeService = ComponentAccessor.getComponent(WorkflowSchemeService)

// Delete all inactive Workflow Schemes
def deleteInactiveWorkflowSchemes() {
    def log = Logger.getLogger("deleteInactiveWorkflowSchemes()")
    log.setLevel(Level.DEBUG)
    // Connected user
    def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
    // inactiveWorkflowSchemes
    Collection<WorkflowScheme> inactiveWorkflowSchemes = workflowSchemeManager.getAssignableSchemes()
        .findAll{!workflowSchemeService.isActive((WorkflowScheme) it)} // class java.util.ArrayList 
    log.debug("deleteInactiveWorkflowSchemes() - Begin")
    try {
        log.debug("${inactiveWorkflowSchemes.size()} inactive workflow schemes will be deleted")
        for (iws in inactiveWorkflowSchemes) {
            workflowSchemeService.validateUpdateWorkflowScheme(user,iws)
            log.debug("Delete is possible of <${iws.getName()}>") 
            //workflowSchemeService.deleteWorkflowScheme(user, iws) //comment to skip WF Scheme delete action
            log.debug("${iws.getName()} was deleted")        
        }
    } catch (WorkflowException we) {
        log.error("WorkflowException Error message: " + we.getMessage())
    }
    log.debug("deleteInactiveWorkflowSchemes() - End")
}

// Delete all inactive Workflows
def deleteInactiveWorkflows() {
    def log = Logger.getLogger("deleteInactiveWorkflows()")
    log.setLevel(Level.DEBUG)
    log.debug("deleteInactiveWorkflows() - Begin")
    // inactiveWorkflows
    def allWorkflows = workflowManager.getWorkflows()// Default(System)/Configurable JiraWorkflow objects Collection ; class java.util.ArrayList
    def activeWorkflows = workflowManager.getActiveWorkflows() // Configurable JiraWorkflow objects Collection ; class java.util.HashSet
    def inactiveWorkflows = allWorkflows.findAll{!(it in activeWorkflows)} // class java.util.ArrayList
    try {
        log.debug("${inactiveWorkflows.size()} inactive workflows may be deleted")
        for (iw in inactiveWorkflows) {
            //log.debug("is iw ${iw.getName()} - editable: ${iw.editable} & default: ${workflowManager.isSystemWorkflow(iw)}")
            if(iw.editable) {
                //log.debug("${iw.getDisplayName()} will be deleted")
                //workflowManager.deleteWorkflow(iw) //comment to skip WF delete action
                log.debug("${iw.getDisplayName()} was deleted")                
            } else {log.warn("${iw.getDisplayName()} is default system workflow and may not be deleted")}
        }
    } catch (WorkflowException we) {
        log.error("Unable to delete ${iw.getDisplayName()}")
        log.error("Error message: " + we.getMessage())
    }
    log.debug("deleteInactiveWorkflows() - End")
}

// ATTENTION: First delete the inactive schemes, then delete the inactive workflows
// First action
deleteInactiveWorkflowSchemes()
// Second action
deleteInactiveWorkflows()

log.debug("Unused/Inactive Workflow (Schemes) Clean-up - End")