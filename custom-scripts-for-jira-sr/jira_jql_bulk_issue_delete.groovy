/*
* Bulk issue delete from JQL filter to override the limits of "Bulk edit" actions in Jira/JSM
* Created by Dmitrij P @ May 2023
*/
// Main objects
import com.atlassian.jira.util.SimpleErrorCollection
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.web.bean.PagerFilter
// For logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("JQL Bulk edit action")
log.setLevel(Level.DEBUG)
log.debug("BULK ISSUES DELETE - Begin")

// Logged-in user
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// --- for issues bulk delete ---
def searchService = ComponentAccessor.getComponentOfType(SearchService)
def issueManager = ComponentAccessor.getIssueManager()

// JQL query
def filter = "< Your JQL filter >"
log.debug("JQL Query for BULK ISSUES DELETE: " + filter)
SearchService.ParseResult parseResult = searchService.parseQuery(user, filter)
def results = searchService.search(user, parseResult.query, PagerFilter.unlimitedFilter)

def issues = results.results 
log.debug("Total issues to be deleted: " + issues.size())
// optional
int count = 0
// Liste of non-deleted issues (optional; normally this script is deleting everithing)
List<String> notDeleted = []
// optional
def totalForDel = issues.size()

issues.each { it ->
    if (issueManager.isEditable(it, user)){
        //  "try-catch" may be commented to simply list the all the issues that might be deleted //
        try {
            def issue = issueManager.getIssueObject(it.key) as MutableIssue
            issueManager.deleteIssueNoEvent(issue)
            log.debug("--- Issue " + it.key + " was deleted.")
            count++
        } catch (Exception ex) {
            log.debug("--- Delete ERROR of " + it.key)
            notDeleted.add(it.key.toString())
            log.debug(ex.getMessage())
        } 
    } else {
        log.debug("You may nod delete issue " + it.key + " with user " + user.getUsername())
        notDeleted.add(it.key.toString())
    } 
}
// optional as notDeleted variable
if (notDeleted.size() > 0) {
    log.debug("--- " + notDeleted.size() + " issues were not deleted.")
    notDeleted.each {
        issueNotDelete ->
        log.debug(issueNotDelete.toString() + " was not deleted.")
    }
}

log.debug("Bulk delete action compleated")
log.debug("--- " + count + " / " + totalForDel + " issues were deleted.") // optional
log.debug("BULK ISSUES DELETE - End")