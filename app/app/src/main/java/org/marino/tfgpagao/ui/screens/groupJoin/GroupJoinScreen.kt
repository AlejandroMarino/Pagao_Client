package org.marino.tfgpagao.ui.screens.groupJoin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.marino.tfgpagao.R
import org.marino.tfgpagao.domain.model.Member
import org.marino.tfgpagao.ui.screens.common.components.Error
import org.marino.tfgpagao.ui.screens.groupDetail.GroupInfo

@Composable
fun GroupJoinScreen(
    groupId: Int,
    goGroups: () -> Unit,
    viewModel: GroupJoinViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.handleEvent(GroupJoinEvent.LoadGroup(groupId))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            content = {
                if (state.value.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Content(
                            state.value.group.name,
                            state.value.group.description,
                            state.value.members,
                            state.value.availableMembers,
                            state.value.selectedMember
                        ) { member -> viewModel.handleEvent(GroupJoinEvent.SelectMember(member)) }
                    }
                }
                state.value.error?.let { it1 ->
                    Error(
                        it1
                    ) { viewModel.handleEvent(GroupJoinEvent.ErrorCatch) }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.handleEvent(GroupJoinEvent.JoinGroup(goGroups)) },
                    containerColor = Color(0xFFA06E1D),
                    contentColor = Color.Black
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_user_24),
                        modifier = Modifier.size(30.dp),
                        contentDescription = "Create group"
                    )
                }
            }
        )
    }
}

@Composable
fun Content(
    groupName: String,
    groupDescription: String?,
    members: List<Member>,
    availableMembers: List<Member>,
    selectedMember: Member,
    selectMember: (Member) -> Unit
) {
    Column {
        GroupInfo(groupName, groupDescription, Modifier.align(Alignment.CenterHorizontally))
        MemberList(members, availableMembers, selectedMember, selectMember)
    }
}


@Composable
fun MemberList(
    members: List<Member>,
    availableMembers: List<Member>,
    selectedMember: Member,
    selectMember: (Member) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 5.dp)
                .padding(top = 15.dp),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            text = "Members"
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
                .padding(horizontal = 20.dp)
                .sizeIn(maxHeight = 700.dp),
        ) {
            items(items = members, itemContent = { member ->

                val backgroundColor = when (member) {
                    selectedMember -> Color(0xFFA06E1D)
                    in availableMembers -> Color.LightGray
                    else -> Color.Gray
                }

                Card(
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 4.dp)
                        .clickable(
                            enabled = member in availableMembers && member != selectedMember
                        ) {
                            selectMember(member)
                        },
                    colors = CardDefaults.cardColors(backgroundColor, contentColor = Color.Black)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(7.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        text = member.name
                    )
                }
            })
        }
    }
}