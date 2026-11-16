package com.example

import com.example.model.ArgusViewModel
import com.example.model.PlanStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `test initial groups and group registration`() {
        val viewModel = ArgusViewModel()
        val initialSize = viewModel.groups.value.size
        
        viewModel.addGroup("New Test Group", "@new_test_group")
        assertEquals(initialSize + 1, viewModel.groups.value.size)
        
        val addedGroup = viewModel.groups.value.last()
        assertEquals("New Test Group", addedGroup.title)
        assertEquals(PlanStatus.TRIAL, addedGroup.planStatus)
    }

    @Test
    fun `test blacklist word management`() {
        val viewModel = ArgusViewModel()
        val groupId = viewModel.groups.value.first().id
        
        viewModel.addBlacklistWord(groupId, "test_spam_word")
        val group = viewModel.groups.value.find { it.id == groupId }
        assertTrue(group?.blacklistWords?.contains("test_spam_word") == true)
        
        viewModel.removeBlacklistWord(groupId, "test_spam_word")
        assertTrue(group?.blacklistWords?.contains("test_spam_word") == false)
    }

    @Test
    fun `test telegram stars subscription activation`() {
        val viewModel = ArgusViewModel()
        val group = viewModel.groups.value.find { it.planStatus == PlanStatus.TRIAL }
        
        if (group != null) {
            assertEquals(PlanStatus.TRIAL, group.planStatus)
            viewModel.subscribeGroupWithStars(group.id)
            
            val updatedGroup = viewModel.groups.value.find { it.id == group.id }
            assertEquals(PlanStatus.PRO_ACTIVE, updatedGroup?.planStatus)
            assertEquals(0, updatedGroup?.trialDaysLeft)
        }
    }
}
