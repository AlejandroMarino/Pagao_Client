package org.marino.tfgpagao.ui.screens.groupDetail

import org.marino.tfgpagao.domain.model.Group
import org.marino.tfgpagao.domain.model.Member

data class GroupDetailState(
    val group: Group = Group(0, "", "", emptyList(), emptyList()),
    val members: List<Member> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)