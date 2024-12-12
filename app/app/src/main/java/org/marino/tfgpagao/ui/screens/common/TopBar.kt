package org.marino.tfgpagao.ui.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.marino.tfgpagao.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    goBack: () -> Unit,
    title: String,
    rightAction: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        end =
                        if (rightAction == null) {
                            48.dp
                        } else {
                            0.dp
                        }
                    )
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { goBack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "go back"
                )
            }
        },
        actions = {
            if (rightAction != null) {
                IconButton(onClick = rightAction) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info_24),
                        contentDescription = "info",
                        tint = Color.White
                    )
                }
            }
        }

    )
}