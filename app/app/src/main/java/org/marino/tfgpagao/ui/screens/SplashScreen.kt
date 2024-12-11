package org.marino.tfgpagao.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.marino.tfgpagao.ui.screens.common.components.Logo

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        Alignment.Center
    ) {
        Logo()
    }
}