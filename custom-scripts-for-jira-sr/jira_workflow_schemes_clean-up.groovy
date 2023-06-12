/*
* Workflows Schemes management: unused Workflows delete from Jira/JSM instance
* (with SQL query - MySQL/RDS type ) - in progress
* Created by Dmitrij P @ May 2023
*/
// Main object
import com.atlassian.jira.workflow.JiraWorkflowDTO
import com.atlassian.jira.workflow.WorkflowSchemeManager
import com.atlassian.jira.workflow.WorkflowScheme
import com.atlassian.jira.component.ComponentAccessor
// WF management
import com.atlassian.jira.workflow.WorkflowManager
import com.atlassian.jira.workflow.JiraWorkflow
// For SQL
import org.ofbiz.core.entity.ConnectionFactory
import org.ofbiz.core.entity.DelegatorInterface
import groovy.sql.Sql
import java.sql.Connection
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger


def log = Logger.getLogger("Workflow (Schemes) management")
log.setLevel(Level.DEBUG)
log.debug("Unused Workflow Schemes - Begin")

//Utilisateur connecté - utilisé pour les validations et suppressions d'objets jira
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// Workflow Manager
WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager()
WorkflowSchemeManager workflowSchemeManager = ComponentAccessor.getWorkflowSchemeManager()

// List of all WFs of instance Jira/JSM - optional
//def allWFs = workflowManager.getWorkflows()

// SQL query way

// "assistance" pour la connexion à la base de données
def delegator = (DelegatorInterface)ComponentAccessor.getComponent(DelegatorInterface)
String helperName = delegator.getGroupHelperName("default")

// Non-used WFs
def sqlStmt = """
select distinct	
	wfs.id as wfs_id,
	wfs.name as wfs_mane
from workflowscheme wfs
where wfs.id not in (
	select distinct	
		wfs.id
	from workflowscheme wfs
	join nodeassociation na on wfs.id = na.sink_node_id
	join project p on na.source_node_id = p.id 
		and na.sink_node_entity = 'WorkflowScheme'
	group by wfs.id
)
group by wfs.id
order by wfs.name
"""
// Etablissement d'une connexion à la base de données !!! Attention à la clôturer !!!
Connection conn = ConnectionFactory.getConnection(helperName)
Sql sql = new Sql(conn)

// sql result treatment 
List notUsedWFSids = []
List notUsedWFSnames = []
List notUsedWFS = []

try {
	sql.eachRow(sqlStmt) { it->
        Long wfsid = it.getAt("wfs_id")
        log.debug("wfsid value: $wfsid and type " + wfsid.getClass())
        notUsedWFSids.add(wfsid)
        notUsedWFSnames.add(it.getAt("wfs_mane"))
        log.debug("WFS name: "+ it.getAt("wfs_mane"))
        notUsedWFS.addAll(workflowSchemeManager.getWorkflowSchemeObj(wfsid))
        log.debug("WFS object mappings (List of WFs in WFS): "+ workflowSchemeManager.getWorkflowSchemeObj(wfsid).getMappings())
        
	}
    
}
finally {
	sql.close() // <- Cloture de connexion à la base de données est OBLIGATOIRE !!!
}

// variable for unused Workflows
List<JiraWorkflow> notusedWFlist = []
/* for each not used Workflow Scheme

*/
for  (wfs in notUsedWFS) {
    Map wf_list = wfs.getMappings()
    log.debug("worflows list $wf_list and variable type " + wf_list.getClass())
    List wf_list_keys = []
    wfs.each { w->
        log.debug("Variable w values $w and type " + w.getClass())

        //wf_list_keys.addAll(w.key)

    }
    //log.debug("wf_list_keys: " * wf_list_keys.toList())
}


// TO DO: get WF Scheme by Name/ID (hope so) - to test
//def notActiveWFS = workflowSchemeManager.getWorkflowSchemeObj()

// Jira API objects way


log.debug("Unused Workflow Schemes - End")