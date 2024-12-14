package org.marino.tfgpagao.ui.screens.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.marino.tfgpagao.data.local.DataStoreManager
import org.marino.tfgpagao.ui.screens.SplashScreen
import org.marino.tfgpagao.ui.screens.groupCreation.GroupCreationScreen
import org.marino.tfgpagao.ui.screens.groupDetail.GroupDetailScreen
import org.marino.tfgpagao.ui.screens.groupJoin.GroupJoinScreen
import org.marino.tfgpagao.ui.screens.groups.GroupListScreen
import org.marino.tfgpagao.ui.screens.insideGroup.GroupInfoScreen
import org.marino.tfgpagao.ui.screens.login.LoginScreen
import org.marino.tfgpagao.ui.screens.receipCreation.ReceiptCreationScreen
import org.marino.tfgpagao.ui.screens.receiptInfo.ReceiptInfoScreen
import org.marino.tfgpagao.ui.screens.register.RegisterScreen

@Composable
fun Navigation(context: Context, isOpenedByLink: Boolean) {
    val navController = rememberNavController()
    val dataStoreManager = DataStoreManager(context)
    val authToken by dataStoreManager.getAuthToken.collectAsState(initial = null)
    var isFirstLoad by remember { mutableStateOf(true) }
    var previousTokenWasValid by remember { mutableStateOf(false) }
    var openedLinkHasNotLoaded by remember { mutableStateOf(isOpenedByLink) }

    LaunchedEffect(authToken) {
        if (!authToken.isNullOrBlank() != previousTokenWasValid || isFirstLoad) {
            if (isFirstLoad) {
                delay(1000)
            }
            isFirstLoad = false
            if (authToken.isNullOrBlank()) {
                previousTokenWasValid = false
                navController.popBackStack()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                previousTokenWasValid = true
                if (openedLinkHasNotLoaded) {
                    openedLinkHasNotLoaded = false
                } else {
                    navController.popBackStack()
                    navController.navigate("groupList") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash",
    ) {
        composable(
            "splash"
        ) {
            SplashScreen()
        }
        composable(
            "login"
        ) {
            LoginScreen(
                goRegister = {
                    navController.navigate("register")
                },
                goGroups = {
                    navController.navigate("groupList") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            "register"
        ) {
            RegisterScreen(
                {
                    TopBar(
                        goBack = {
                            navController.popBackStack()
                        },
                        title = "Register"
                    )
                },
                goLogin = {
                    navController.navigate("login") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            "groupList"
        ) {
            GroupListScreen(
                goGroupInfo = { id, groupName -> navController.navigate("groupInfo/$id/${groupName}") },
                goGroupCreation = { navController.navigate("groupCreation") },
                logout = {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStoreManager.clearAuthToken()
                    }
                }
            )
        }
        composable(
            "groupCreation"
        ) {
            GroupCreationScreen(
                {
                    TopBar(
                        goBack = {
                            navController.popBackStack()
                        },
                        title = "Create a new group"
                    )
                },
                goGroupList = {
                    navController.navigate("groupList") {
                        popUpTo("groupList") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            "groupDetail/{groupId}",
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            val groupId = it.arguments?.getInt("groupId") ?: 0
            GroupDetailScreen(
                groupId,
                {
                    TopBar(
                        goBack = {
                            navController.popBackStack()
                        },
                        title = "Group Info",
                    )
                }
            )
        }
        composable(
            "groupJoin/{groupId}",
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "https://pagao/invite/{groupId}" })
        ) {
            val groupId = it.arguments?.getInt("groupId") ?: 0
            GroupJoinScreen(
                groupId,
                {
                    navController.navigate("groupList") {
                        popUpTo("groupJoin/{groupId}") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            "groupInfo/{groupId}/{groupName}",
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument("groupName") {
                    type = NavType.StringType
                }
            )
        ) {
            val groupId = it.arguments?.getInt("groupId") ?: 0
            val groupName = it.arguments?.getString("groupName") ?: ""
            GroupInfoScreen(
                groupId = groupId,
                goReceiptCreation = { id ->
                    navController.navigate("receiptCreation/$id")
                },
                goReceiptCreationPredefined = { id, idPayer, idReceiver, amount ->
                    navController.navigate("receiptCreation/$id?payerId=$idPayer&receiverId=$idReceiver&amount=$amount")
                },
                goReceiptInfo = { id ->
                    navController.navigate("receiptInfo/$id")
                }
            ) {
                TopBar(
                    goBack = {
                        navController.popBackStack()
                    },
                    title = groupName,
                    rightAction = {
                        navController.navigate("groupDetail/$groupId")
                    }
                )
            }
        }
        composable(
            "receiptCreation/{groupId}?payerId={payerId}&receiverId={receiverId}&amount={amount}",
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument("payerId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("receiverId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("amount") {
                    type = NavType.FloatType
                    defaultValue = -1
                }
            )
        ) {
            val groupId = it.arguments?.getInt("groupId") ?: 0
            val payerId = it.arguments?.getInt("payerId") ?: -1
            val receiverId = it.arguments?.getInt("receiverId") ?: -1
            val amount = it.arguments?.getFloat("amount") ?: -1
            ReceiptCreationScreen(
                {
                    TopBar(
                        goBack = {
                            navController.popBackStack()
                        },
                        title = "Create a new receipt"
                    )
                },
                goGroupInfo = {
                    navController.popBackStack()
                },
                groupId = groupId,
                payerId = payerId,
                receiverId = receiverId,
                amount = amount.toDouble()
            )
        }
        composable(
            "receiptInfo/{receiptId}",
            arguments = listOf(
                navArgument("receiptId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) {
            val receiptId = it.arguments?.getInt("receiptId") ?: 0
            ReceiptInfoScreen(
                receiptId = receiptId,
                {
                    TopBar(
                        goBack = {
                            navController.popBackStack()
                        },
                        title = ""
                    )
                }
            )
        }
    }
}