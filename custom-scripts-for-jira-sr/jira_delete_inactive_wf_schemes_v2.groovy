// Main objects
import com.onresolve.scriptrunner.exception.GenericException
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.workflow.JiraWorkflow // optional Interface
import com.atlassian.jira.workflow.ConfigurableJiraWorkflow // optional Class
import com.atlassian.jira.workflow.WorkflowScheme
import com.atlassian.jira.bc.workflow.WorkflowSchemeService
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("Workflow (Schemes) management")
log.setLevel(Level.DEBUG)
log.debug("Unused Workflow Schemes - Begin")

//Utilisateur connecté - utilisé pour les validations et suppressions d'objets jira
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// Object managers
def workflowSchemeManager = ComponentAccessor.getWorkflowSchemeManager()
def workflowManager = ComponentAccessor.getWorkflowManager()
def workflowSchemeService = ComponentAccessor.getComponent(WorkflowSchemeService)
// optional
def confWorkflow = ComponentAccessor.getComponent(ConfigurableJiraWorkflow) // single object operations Class
def jiraWorkflow = ComponentAccessor.getComponent(JiraWorkflow) // single object operations Interface

// workflows anc workflow schemes
def wfSchemeObjs = workflowSchemeManager.getSchemeObjects() // All Workflow Scheme objects
//log.debug("wfSchemeObjs size " + wfSchemeObjs.size() + " - " + wfSchemeObjs)
def wfSchemes = workflowSchemeManager.getSchemes() // All Workflow Schemes with "[name/description/id]" values 
//log.debug("wfSchemes size " + wfSchemes.size() + " - " + wfSchemes)
def wfActive = workflowSchemeManager.getActiveWorkflowNames() // List of active WF names
//log.debug("wfSchemesActive size " + wfActive.size() + " - " + wfActive)
def wfAll = workflowManager.getWorkflows()// Default JiraWorkflow objects Collection
log.debug("wfAll size " + wfAll.size() + " - " + wfAll)
def wfAllActive = workflowManager.getActiveWorkflows() // Configurable JiraWorkflow objects Collection
log.debug("wfAllActive size " + wfAllActive.size() + " - " + wfAllActive)
def inactiveWorkflows = wfAll.findAll{!(it in wfAllActive)}
log.debug("inactiveWorkflows size " + inactiveWorkflows.size() + " - $inactiveWorkflows") 
Collection<WorkflowScheme> inactiveWorkflowSchemes = workflowSchemeManager.getAssignableSchemes()
    .findAll{! workflowSchemeService.isActive((WorkflowScheme) it)}
log.debug("inactiveWorkflowSchemes size " + inactiveWorkflowSchemes.size() + " - " + inactiveWorkflowSchemes)

/*
for (wfscheme in wfSchemeObjs) {
    log.debug("wfscheme = $wfscheme")
    def wfs = wfscheme.getEntities()
    for (wf in wfs) {
        log.debug("wf class " + wf.getClass())
        //JiraWorkflow wfent = workflowManager.get
        //def wfobj = workflowManager.getAt(wf.getEntityTypeId())
        //def wfobj = workflowManager.getAt(wf.getEntityTypeId())
        //log.debug("wfobj = $wfobj")
        def isactive = wfActive.contains(wf)
        log.debug("isactive = $isactive")
        
    }
}

// try - catch
try {
    log.debug("TRY - CATCH")
} catch (GenericException ex) {
    log.debug("Error message: " + ex.getMessage())
}
*/

log.debug("Unused Workflow Schemes - End")