package com.nrkei.project.dialog.unit

import com.nrkei.project.issue.Issue.State.RESOLVED
import com.nrkei.project.issue.IssueParty
import com.nrkei.project.template.util.TestConversation.testConversation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Purpose: Ensures the Conversation API works as expected
internal class ConversationApiTest {
    companion object {
        private val SYSTEM = IssueParty("SYSTEM")
    }

    @Test fun `respond to login without ConversationId with open issues`() {
        testConversation.clone().openIssues().also { issues ->
            assertEquals(5, issues.size)
        }
    }

    @Test fun `respond to login with ConversationId with remaining open issues`() {
        testConversation.clone().also { conversation ->
            conversation.openIssues().first().be(RESOLVED, SYSTEM)
        }.also { priorConversation ->
            assertEquals(4, priorConversation.openIssues().size)
        }
    }
}