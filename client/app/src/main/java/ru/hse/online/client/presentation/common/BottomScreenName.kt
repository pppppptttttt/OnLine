package ru.hse.online.client.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class BottomScreenName(private val name: String) {
    @Composable
    fun DisplayNameAndDraw(drawings: @Composable () -> Unit) {
        Scaffold(
            modifier = Modifier.fillMaxWidth(),
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.height(64.dp),
                    containerColor = Color(0x55000000),
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            },
        ) { _ ->
            Box {
                drawings()
            }
        }
    }
}
