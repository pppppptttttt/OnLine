package ru.hse.online.client

import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = text)
    }
}
