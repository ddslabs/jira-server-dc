import groovy.time.TimeCategory 
import groovy.time.TimeDuration

def execStart = new Date()
/*
 // do something here
*/
def execEnd = new Date()
 
TimeDuration execTotal = TimeCategory.minus(execEnd, execStart)
println execTotal // or log.gebug("Total execution time $execTotal")
