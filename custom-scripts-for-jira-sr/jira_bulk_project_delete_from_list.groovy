/*
* Jira/JSM project(s) delete from list
* Created by Dmitrij P @June 2023
*/
// Les objets du base
import com.atlassian.jira.bc.project.ProjectService
import com.atlassian.jira.component.ComponentAccessor
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("Jira/JSM project(s) delete from list")
log.setLevel(Level.DEBUG)
log.debug("Actions log - Begin")

// Utilisateur connecté - utilisé pour les validations et suppressions d'objets jira
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
// Objets basic de traitement des operations de supprion projets
def projectService = ComponentAccessor.getComponent(ProjectService.class)

List<String> projList = ["EP","ZOIP"] // List with any project keys; could be filled by SQL query

// Validation d'action et la suppression de(s) projet(s)
for(projectKey in projList) {
    final ProjectService.DeleteProjectValidationResult result = projectService.validateDeleteProject(user, projectKey)
    log.debug("Validation de la possibilité de suppression: " + result.getReturnedValue().toString())
                
    if (result.isValid()) {
        try {            
            final ProjectService.DeleteProjectResult projectResult = projectService.deleteProject(user, result)
            //log.debug("Rédultat de suppression dans l'objet: " + projectResult.getProperties().toPrettyString())
            log.info("Projet <" + projectKey + "> a été supprimé")
        } catch (Exception e) {
            log.error("--- !!! Projet <" + projectKey + "> n'a pas été supprimé !!! ---")
            log.error("Exception pour projet <"+ projectKey + ">\n"+ e)
        }
    } else {
        log.error("--- !!! Projet <" + projectKey + "> n'existe pas ou ne peut pas être supprimé !!! ---" )
    }
}

log.debug("Action log - End")