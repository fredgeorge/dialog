package com.nrkei.project.dialog.unit

import com.nrkei.project.issue.Issue.State.DISMISSED
import com.nrkei.project.issue.IssueParty
import com.nrkei.project.template.util.TestConversation.testConversation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Purpose: Ensures Conversation manages it's Issues correctly
internal class ConversationIssueTest {
    companion object {
        private val TEST = IssueParty("TEST")
    }

    @Test
    fun `Closed Issues not shown`() {
        testConversation.clone().also { conversation ->
            conversation.openIssues().size.also { originalCount ->
                conversation.openIssues().first().be(DISMISSED, TEST)
                assertEquals(originalCount - 1, conversation.openIssues().size)
            }
        }
    }
}