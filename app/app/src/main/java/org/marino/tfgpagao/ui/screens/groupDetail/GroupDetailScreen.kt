package org.marino.tfgpagao.ui.screens.groupDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.marino.tfgpagao.R
import org.marino.tfgpagao.domain.model.Member
import org.marino.tfgpagao.ui.screens.common.components.Error
import org.marino.tfgpagao.ui.screens.receiptInfo.TextTitleOfFields

@Composable
fun GroupDetailScreen(
    groupId: Int,
    topBar: @Composable () -> Unit,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.handleEvent(GroupDetailEvent.LoadGroup(groupId))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = topBar,
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
                            state.value.members
                        )
                    }
                }
                state.value.error?.let { it1 ->
                    Error(
                        it1
                    ) { viewModel.handleEvent(GroupDetailEvent.ErrorCatch) }
                }
            },
            floatingActionButton = {

            }
        )
    }
}

@Composable
fun Content(
    groupName: String,
    groupDescription: String?,
    members: List<Member>
) {
    Column {
        GroupInfo(groupName, groupDescription, Modifier.align(Alignment.CenterHorizontally))
        MemberList(members)
    }
}

@Composable
fun GroupInfo(groupName: String, groupDescription: String?, modifier: Modifier) {
    var isDialogOpen by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .padding(horizontal = 40.dp)
            .padding(bottom = 20.dp)
            .then(
                if (!groupDescription.isNullOrBlank()) {
                    Modifier.clickable { isDialogOpen = true }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            TextTitleOfFields(groupName, Modifier.align(Alignment.CenterHorizontally))
            if (!groupDescription.isNullOrBlank()) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_24),
                    contentDescription = "More",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = { isDialogOpen = false },
        ) {
            Box(
                Modifier
                    .padding(bottom = 100.dp)
                    .background(Color(0x6A000000), shape = RoundedCornerShape(16.dp))
            ) {
                Text(
                    groupDescription ?: "",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
fun MemberList(members: List<Member>) {
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
                Card(
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 4.dp)
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