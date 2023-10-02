/*
Code snippets for Jira/JSM
Some from documentation of Script Runner.
And some, found in the other sources.
*/

/* execution time counter - begin */
import groovy.time.TimeCategory 
import groovy.time.TimeDuration

def execStart = new Date()
/*
 main script execution
*/
def execEnd = new Date()
 
TimeDuration execTotal = TimeCategory.minus(execEnd, execStart)
println execTotal // or log.gebug("Total execution time $execTotal")
/* - end - */

/* Create issue: */
Issues.create('ProjKey', 'Story') {
    setSummary('Permission scheme change for ' + ProjName)
    setPriority('High')
    setDescription('Permission scheme change for ' + ProjName + '\n' +
        'from: <' + oldps + '> to: <' + newps + '> .')
}


log.debug('<<< All Project Permission scheme: >>>')
def allPS = permissionSchemeManager.getSchemes()
allPS.forEach() { permSchm ->
    log.debug(permSchm.id + ' - ' + permSchm.name)
}

log.debug('<<< All Project Categories: >>>')
def allCats = projectManager.getAllProjectCategories()
allCats.forEach() { category ->
    log.debug(category.getId() + ' - ' + category.getName())
}

log.debug('<<< All Notificaiton Schemes: >>>')
def allNotifSchms = notificationShcemeManager.getSchemes()
allNotifSchms.forEach() { notifSchm ->
    log.debug(notifSchm.id + ' - ' + notifSchm.name)
}

log.debug('<<< Inactive Notificaiton Schemes: >>>')
def inactNotSchms = notificationShcemeManager.getUnassociatedSchemes()
inactNotSchms.forEach() { inNSchm ->
    log.debug(inNSchm.getId() + ' - ' + inNSchm.getName())
}

log.debug("<<< All Projects: >>>")
def allProjs = projectManager.getProjects()
allProjs.forEach() { p ->
    log.debug("Project Id <${p.getId()}> - Key <${p.getKey()}> - Name ${p.getName()}")
}