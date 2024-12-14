package org.marino.tfgpagao.ui.screens.groupJoin

import org.marino.tfgpagao.domain.model.Member

sealed class GroupJoinEvent {
    object ErrorCatch : GroupJoinEvent()
    class LoadGroup(val groupId: Int) : GroupJoinEvent()
    class SelectMember(val member: Member) : GroupJoinEvent()
    class JoinGroup(val goGroup: () -> Unit) : GroupJoinEvent()
}