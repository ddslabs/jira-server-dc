/*
* Inactive Notification Schemes Bulk delete in Jira/JSM instance
* Created by Dmitrij P @ July 2023
* With help of the following script:
* https://github.com/gonchik/cleanup-scripts/blob/master/groovy/jira/issues/schemes/notificationSchemesCleaner.groovy
*/
// Main object
import com.atlassian.jira.component.ComponentAccessor
// Logging
import org.apache.log4j.Level
import org.apache.log4j.Logger

def log = Logger.getLogger("Notification Scheme Bulk delete")
log.setLevel(Level.DEBUG)
log.debug("Inactive Notification Scheme Clean-up - Begin")

def notificationShcemeManager = ComponentAccessor.getNotificationSchemeManager()

def inactNotSchms = notificationShcemeManager.getUnassociatedSchemes()
log.debug('<<< Inactive Notification Schemes delete action: >>>')
inactNotSchms.forEach() { nSchmForDelete ->
    try {
        log.debug("Notification Scheme ID: <${nSchmForDelete.getId()}> and Label: <${nSchmForDelete.getName()}> - will be deleted")
        Long sId = Long.valueOf(nSchmForDelete.getId())
        //log.debug('sId type/class: ' + sId.class)
        //notificationShcemeManager.deleteScheme(sId) // to comment if you need to list Notification Schemes without delete action
    } catch (Exception e) {
        log.debug("Error: " + e + "\n")
    }
}

log.debug("Inactive Notification Scheme Clean-up - End")