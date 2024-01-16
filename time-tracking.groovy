import com.atlassian.jira.component.ComponentAccessorimport 
import com.atlassian.jira.issue.MutableIssueimport 
import com.atlassian.jira.event.type.EventDispatchOption

// Since updating Original Estimate will not trigger an update for the Remaining Estimate field, the logic below helps to achieve such result

// Class
def issue = event.issue as MutableIssue
def changeLog = event.getChangeLog()

// Variables
def originalEstimate = issue.getOriginalEstimate()
def oldRemainingEstimate = issue.getEstimate()
def timeSpent = issue.getTimeSpent()

// Changes to the logic
def remainingEstimate = originalEstimate - timeSpent

// Initial values
log.warn("Starting value (Original): ${originalEstimate}")
log.warn("Starting value (Remaining): ${oldRemainingEstimate}")
log.warn("Diff in seconds (Long): ${remainingEstimate}")

changeLog.getRelated("ChildChangeItem").each { element ->
    // If 'Original Estimate' has been changed in a log    
    if (element.field == "timeoriginalestimate") {
        // Update the 'Remaining Estimate' field        
        issue.setEstimate(remainingEstimate)
        // Final values
        log.warn("Final value (Original): ${originalEstimate}")
        log.warn("Final value (Remaining): ${issue.getEstimate()}")
        // Store the issue        
        ComponentAccessor.getIssueManager().updateIssue(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), issue, EventDispatchOption.ISSUE_UPDATED, false)
    }
}
