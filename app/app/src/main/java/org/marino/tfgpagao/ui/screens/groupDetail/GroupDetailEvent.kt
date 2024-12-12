package org.marino.tfgpagao.ui.screens.groupDetail

sealed class GroupDetailEvent {
    object ErrorCatch : GroupDetailEvent()
    class LoadGroup(val groupId: Int) : GroupDetailEvent()
}