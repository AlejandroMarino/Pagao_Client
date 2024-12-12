package org.marino.tfgpagao.ui.screens.groupDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.marino.tfgpagao.domain.usecases.group.GetGroup
import org.marino.tfgpagao.domain.usecases.member.GetMembersOfGroup
import org.marino.tfgpagao.utils.NetworkResult
import org.marino.tfgpagao.utils.StringProvider
import org.marino.tfgpagao.utils.Utils
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val getGroup: GetGroup,
    private val getMembersOfGroup: GetMembersOfGroup,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _state = MutableStateFlow(GroupDetailState())
    val state: StateFlow<GroupDetailState> = _state.asStateFlow()

    fun handleEvent(event: GroupDetailEvent) {
        when (event) {
            GroupDetailEvent.ErrorCatch -> {
                errorCatch()
            }

            is GroupDetailEvent.LoadGroup -> {
                loadGroup(event.groupId)
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

    private fun loadGroup(idGroup: Int) {
        viewModelScope.launch {
            getGroup.invoke(idGroup).collect { result ->
                if (Utils.hasInternetConnection(stringProvider.context)) {
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
                            }
                        }

                        is NetworkResult.SuccessNoData -> _state.update {
                            it.copy(isLoading = false)
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

    private fun setupMembers(idGroup: Int) {
        viewModelScope.launch {
            getMembersOfGroup.invoke(idGroup).collect { result ->
                if (Utils.hasInternetConnection(stringProvider.context)) {
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
                } else {
                    _state.update {
                        it.copy(isLoading = false, error = "No internet connection")
                    }
                }
            }
        }
    }
}