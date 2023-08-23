/*
Code snippets for Jira/JSM
Some from documentation of Script Runner.
And some, found in the other sources.
*/

/* Create issue: */
Issues.create('ProjKey', 'Story') {
    setSummary('Permission scheme change for ' + ProjName)
    setPriority('High')
    setDescription('Permission scheme change for ' + ProjName + '\n' +
        'from: <' + oldps + '> to: <' + newps + '> .')
}


log.debug('<<< Project Permission scheme: >>>')
def allPS = permissionSchemeManager.getSchemes()
allPS.forEach() { permSchm ->
    log.debug(permSchm.id + ' - ' + permSchm.name)
}

log.debug('<<< Project Categories: >>>')
def allCats = projectManager.getAllProjectCategories()
allCats.forEach() { category ->
    log.debug(category.getId() + ' - ' + category.getName())
}

