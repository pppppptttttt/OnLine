package ru.hse.online.client.presentation.common

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class NavButtonDrawer {
    @Composable
    fun Draw(from: Activity, to: Class<out Activity>, text: String, modifier: Modifier = Modifier) {
        Button(
            onClick = { from.startActivity(Intent(from, to)) },
            modifier = modifier
        ) {
            Text(text = text)
        }
    }
}
