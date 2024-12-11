package org.marino.tfgpagao.ui.screens.common.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun Error(error: String, errorCaught: () -> Unit) {
    val context = LocalContext.current
    if (error.isNotEmpty()) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        errorCaught()
    }
}