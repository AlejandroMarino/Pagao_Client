package org.marino.tfgpagao.ui.screens.groupJoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.marino.tfgpagao.domain.model.Member
import org.marino.tfgpagao.domain.usecases.group.GetGroup
import org.marino.tfgpagao.domain.usecases.member.GetMembersAvailableOfGroup
import org.marino.tfgpagao.domain.usecases.member.GetMembersOfGroup
import org.marino.tfgpagao.domain.usecases.member.SetUserToMember
import org.marino.tfgpagao.utils.NetworkResult
import org.marino.tfgpagao.utils.StringProvider
import org.marino.tfgpagao.utils.Utils
import javax.inject.Inject

@HiltViewModel
class GroupJoinViewModel @Inject constructor(
    private val getGroup: GetGroup,
    private val getMembersOfGroup: GetMembersOfGroup,
    private val getMembersAvailableOfGroup: GetMembersAvailableOfGroup,
    private val setUserToMember: SetUserToMember,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _state = MutableStateFlow(GroupJoinState())
    val state: StateFlow<GroupJoinState> = _state.asStateFlow()

    fun handleEvent(event: GroupJoinEvent) {
        when (event) {
            GroupJoinEvent.ErrorCatch -> {
                errorCatch()
            }

            is GroupJoinEvent.JoinGroup -> {
                joinGroup(event.goGroup)
            }

            is GroupJoinEvent.LoadGroup -> {
                loadGroup(event.groupId)
            }

            is GroupJoinEvent.SelectMember -> {
                selectMember(event.member.id)
            }
        }
    }

    private fun errorCatch() {
        _state.update {
            it.copy(
                error = "",
            )
        }
    }

    private fun selectMember(idMember: Int) {
        val selectedMember = _state.value.availableMembers.find { member -> member.id == idMember }
        _state.update {
            it.copy(selectedMember = selectedMember ?: Member(0, "", 0.0))
        }
    }

    private fun joinGroup(goGroup: () -> Unit) {
        viewModelScope.launch {
            val member = _state.value.selectedMember
            if (member.id != 0) {
                if (Utils.hasInternetConnection(stringProvider.context)) {
                    setUserToMember.invoke(member.id).collect { result ->
                        when (result) {
                            is NetworkResult.Error -> {
                                _state.update {
                                    it.copy(
                                        error = result.message ?: "",
                                        isLoading = false
                                    )
                                }
                            }

                            is NetworkResult.Loading -> _state.update { it.copy(isLoading = true) }
                            is NetworkResult.Success -> {
                                _state.update { it.copy(isLoading = false) }
                                goGroup()
                            }

                            is NetworkResult.SuccessNoData -> {
                                _state.update { it.copy(isLoading = false) }
                                goGroup()
                            }
                        }
                    }
                } else {
                    _state.update { it.copy(error = "Internet connection needed") }
                }
            } else {
                _state.update { it.copy(error = "Please select a valid member") }
            }
        }
    }

    private fun loadGroup(idGroup: Int) {
        viewModelScope.launch {
            if (Utils.hasInternetConnection(stringProvider.context)) {
                getGroup.invoke(idGroup).collect { result ->
                    when (result) {
                        is NetworkResult.Error -> {
                            _state.update {
                                it.copy(
                                    error = result.message ?: "",
                                    isLoading = false
                                )
                            }
                        }

                        is NetworkResult.Loading -> _state.update { it.copy(isLoading = true) }
                        is NetworkResult.Success -> {
                            val group = result.data
                            if (group == null) {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        error = "Error while loading group"
                                    )
                                }
                            } else {
                                _state.update {
                                    it.copy(isLoading = false, group = group)
                                }
                                setupMembers(group.id)
                                setupAvailableMembers(group.id)
                            }
                        }

                        is NetworkResult.SuccessNoData -> _state.update {
                            it.copy(isLoading = false)
                        }
                    }
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = "No internet connection")
                }
            }
        }
    }

    private fun setupMembers(idGroup: Int) {
        viewModelScope.launch {

            if (Utils.hasInternetConnection(stringProvider.context)) {
                getMembersOfGroup.invoke(idGroup).collect { result ->
                    when (result) {
                        is NetworkResult.Error -> {
                            _state.update {
                                it.copy(
                                    error = result.message ?: "",
                                    isLoading = false
                                )
                            }
                        }

                        is NetworkResult.Loading -> _state.update { it.copy(isLoading = true) }
                        is NetworkResult.Success -> {
                            val members = result.data
                            if (!members.isNullOrEmpty()) {
                                _state.update {
                                    it.copy(isLoading = false, members = members)
                                }
                            }
                        }

                        is NetworkResult.SuccessNoData -> _state.update {
                            it.copy(isLoading = false)
                        }
                    }
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = "No internet connection")
                }
            }
        }
    }

    private fun setupAvailableMembers(idGroup: Int) {
        viewModelScope.launch {

            if (Utils.hasInternetConnection(stringProvider.context)) {
                getMembersAvailableOfGroup.invoke(idGroup).collect { result ->
                    when (result) {
                        is NetworkResult.Error -> {
                            _state.update {
                                it.copy(
                                    error = result.message ?: "",
                                    isLoading = false
                                )
                            }
                        }

                        is NetworkResult.Loading -> _state.update { it.copy(isLoading = true) }
                        is NetworkResult.Success -> {
                            val availableMembers = result.data
                            if (!availableMembers.isNullOrEmpty()) {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        availableMembers = availableMembers
                                    )
                                }
                            }
                        }

                        is NetworkResult.SuccessNoData -> _state.update {
                            it.copy(isLoading = false)
                        }
                    }
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = "No internet connection")
                }
            }
        }
    }
}