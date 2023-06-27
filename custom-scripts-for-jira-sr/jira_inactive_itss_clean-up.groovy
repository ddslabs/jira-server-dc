/*
* Unused/Inactive Issue Type Screen Schemes Clean-up in Jira/JSM 
* Created by Dmitrij P @ June 2023
*/
// Main objects
import com.atlassian.jira.component.ComponentAccessor
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("ITSS Clean-up")
log.setLevel(Level.DEBUG)
log.debug("Issue Type Screen Schemes Clean-up - Begin")

// ITSS managements
def itssManager = ComponentAccessor.getIssueTypeScreenSchemeManager()
// all ITSSs
def allITSS = itssManager.issueTypeScreenSchemes

// inactive ITSS Bulk delete
for (itss in allITSS) {
    //log.debug("ITSS ID: ${itss.getId()} & Name ${itss.getName()} - Projects: ${itss.getProjects()}")
    if (!itss.getProjects()) { // !itss.getProjects() - only inactive ITSSs
        //log.debug("ITSS ID: ${itss.getId()} & Name ${itss.getName()} - Projects: ${itss.getProjects()}")
        try {
            //itssManager.removeIssueTypeScreenScheme(itss) // comment to prevent inactive ITSS delete
            log.debug("ITSS ID: ${itss.getId()} & Name ${itss.getName()} - was REMOVED/DELETED")
        } catch (Exception e) {
            log.error(e.getMessage())
        }
    }
}

/* In progress - begin */
def allFSS = fssManager.getFieldScreenSchemes()
//log.debug(allFSS.toString())

for (fss in allFSS) {
    def fssi = fss.getFieldScreenSchemeItems()
    def fssitr = fss.iterator()
    log.debug("Items: ${fss.fieldScreenSchemeItems.findAll()}")
    log.debug("FSS ID: ${fss.getId()} & Name: ${fss.getName()} & FieldScreenSchemeItems size: ${fssi.size()} ")
    
    
}
/* In progress - end */

// inactive Screens & Screen Schemes are not treated by this scripts (for now)
log.debug("Issue Type Screen Schemes Clean-up - End")
