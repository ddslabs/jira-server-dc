/*
* Inactive (Fields) Screens Clean-up
* Created by Dmitrij P @ Oct 2023
* ATTENTION: May cause an error if "desc" StringBuffer will exceed the character limit set in the Advanced Settings !
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.screen.FieldScreenFactory
import com.atlassian.jira.issue.fields.screen.FieldScreenManager
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager
import com.atlassian.jira.web.action.admin.issuefields.screens.ViewFieldScreens
import com.atlassian.jira.bc.issue.fields.screen.FieldScreenService
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.workflow.WorkflowManager
import com.atlassian.webresource.api.assembler.PageBuilderService
// Logging
import org.apache.log4j.Logger
import org.apache.log4j.Level
// Date & Time management
import groovy.time.TimeCategory 
import groovy.time.TimeDuration

def execStart = new Date()
// trace issue creation variables
def traceProjectKey = 'OPTIMA' // Id of a project to trace the action
String traceIssueType = 'Story' // Issue type of trace issue
def desc = new StringBuilder() // Description
String traceSummary = 'TEST - Inactive (Field) Screens Clean-up (no delete)'

def log = Logger.getLogger(traceSummary)
log.setLevel(Level.DEBUG)

log.debug(traceSummary + "- Begin")

FieldScreenManager fieldScreenManager = ComponentAccessor.getFieldScreenManager()
FieldScreenFactory fieldScreenFactory = ComponentAccessor.getComponent(FieldScreenFactory.class)
FieldScreenSchemeManager fieldScreenSchemeManager = ComponentAccessor.getComponent(FieldScreenSchemeManager.class)
FieldScreenService fieldScreenService = ComponentAccessor.getComponent(FieldScreenService.class)
JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getComponent(JiraAuthenticationContext.class)
PageBuilderService pageBuilderService = ComponentAccessor.getComponent(PageBuilderService.class)
WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
ViewFieldScreens viewFieldScreens = new ViewFieldScreens(fieldScreenManager, 
                                fieldScreenFactory, 
                                fieldScreenSchemeManager, 
                                fieldScreenService, 
                                workflowManager, 
                                jiraAuthenticationContext, 
                                pageBuilderService) 

def fieldScreens = fieldScreenManager.getFieldScreens()
int count = 0
fieldScreens.each { fs ->
    //log.debug(fs.getName())
    if (viewFieldScreens.isDeletable(fs)) {
        desc.append("FS: ${fs.getName()} - may be deleted\n")
        log.debug("FS: ${fs.getName()} - may be deleted")
        // FS DELETE ACTION
        //fieldScreenManager.removeFieldScreen(fs.getId()) // comment to skip the delete action
        //desc.append("FS: ${fs.getName()} - was deleted\n")
        //log.debug("FS: ${fs.getName()} - was deleted")
        count++
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

log.debug(traceSummary + "- End")
