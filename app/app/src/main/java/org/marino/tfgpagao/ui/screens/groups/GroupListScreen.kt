package org.marino.tfgpagao.ui.screens.groups

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.marino.tfgpagao.R
import org.marino.tfgpagao.domain.model.Group
import org.marino.tfgpagao.ui.screens.common.components.Error

@Composable
fun GroupListScreen(
    goGroupInfo: (Int, String) -> Unit,
    goGroupCreation: () -> Unit,
    logout: () -> Unit,
    viewModel: GroupListViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.handleEvent(GroupListEvent.GetGroups)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            content = {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout_24),
                        contentDescription = "Logout",
                        tint = Color(0xFF9C3636),
                        modifier = Modifier.clickable {
                            showDialog = true
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 22.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline,
                            fontFamily = FontFamily.Serif,
                            fontSize = 50.sp,
                            text = "Pagao"
                        )
                    }
                    if (state.value.isLoading) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    } else {
                        ListGroups(
                            Modifier.weight(2f),
                            groups = state.value.groups,
                            goGroupInfo
                        )
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDialog = false
                            },
                            title = {
                                Text(
                                    "You are about to logout",
                                    Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            text = {
                                Text(
                                    "Are you sure?",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    logout()
                                }) {
                                    Text("Logout", color = Color(0xFFA06E1D))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                }) {
                                    Text("Cancel", color = Color(0xFF9C3636))
                                }
                            }
                        )
                    }
                }

                state.value.error?.let { it1 ->
                    Error(
                        it1
                    ) { viewModel.handleEvent(GroupListEvent.ErrorCatch) }
                }
            },
            floatingActionButton = {
                ButtonCreateGroup(
                    goGroupCreation = goGroupCreation,
                )
            }

        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListGroups(
    modifier: Modifier,
    groups: List<Group>,
    goReceiptList: (Int, String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items = groups, itemContent = { group ->
            ItemsGroup(
                group = group,
                modifier = Modifier.animateItemPlacement(
                    animationSpec = tween(1000)
                ),
                goGroupInfo = goReceiptList
            )
        })
    }
}

@Composable
fun ItemsGroup(
    group: Group,
    modifier: Modifier,
    goGroupInfo: (Int, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { goGroupInfo(group.id, group.name) }
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .padding(start = 6.dp)
        ) {
            Text(
                text = group.name,
                fontSize = 25.sp
            )
        }

    }
}

@Composable
fun ButtonCreateGroup(
    goGroupCreation: () -> Unit
) {
    FloatingActionButton(
        onClick = { goGroupCreation() },
        containerColor = Color(0xFFA06E1D),
        contentColor = Color.Black
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add_group_png),
            modifier = Modifier.size(30.dp),
            contentDescription = "Create new group"
        )
    }
}