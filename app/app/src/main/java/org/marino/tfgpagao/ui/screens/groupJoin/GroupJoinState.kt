package org.marino.tfgpagao.ui.screens.groupJoin

import org.marino.tfgpagao.domain.model.Group
import org.marino.tfgpagao.domain.model.Member

data class GroupJoinState(
    val group: Group = Group(0, "", "", emptyList(), emptyList()),
    val members: List<Member> = emptyList(),
    val selectedMember: Member = Member(0, "", 0.0),
    val availableMembers: List<Member> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)