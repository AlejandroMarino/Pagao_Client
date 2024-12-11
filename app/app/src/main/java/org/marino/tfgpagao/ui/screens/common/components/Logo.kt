package org.marino.tfgpagao.ui.screens.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.marino.tfgpagao.R

@Composable
fun Logo() {
    Image(
        painter = painterResource(R.drawable.iconpagao),
        contentDescription = "Logo",
        modifier = Modifier.padding(vertical = 50.dp)
    )
}