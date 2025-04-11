package ru.hse.online.client.presentation.routelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hse.online.client.presentation.common.BottomScreenName
import ru.hse.online.client.presentation.common.SearchBar
import ru.hse.online.client.ui.theme.ClientTheme

class RouteListView : ComponentActivity() {
    private val bottomScreenName = BottomScreenName("Route List")
    private val searchBar = SearchBar()

    companion object {
        private val cardHeight = 135.dp
        private val cardWidth = 410.dp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClientTheme {
                val textFieldState = rememberTextFieldState()
                val items = listOf("")

                val filteredItems by remember {
                    derivedStateOf {
                        val searchText = textFieldState.text.toString()
                        if (searchText.isEmpty()) {
                            emptyList()
                        } else {
                            items.filter { it.contains(searchText, ignoreCase = true) }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    bottomScreenName.DisplayNameAndDraw {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            searchBar.Draw(
                                textFieldState = textFieldState,
                                onSearch = {},
                                searchResults = filteredItems
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Draw()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Draw() {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
        ) {
            for (i in 1..10) {
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier
                        .size(width = cardWidth, height = cardHeight)
                        .padding(10.dp)
                        .clickable {},
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column {
                        Text(
                            text = "Route №${i}",
                            modifier = Modifier
                                .padding(5.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize = 16.sp
                        )

                        Text(
                            text = """
                                from: point A
                                to:    point B
                            """.trimIndent(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(cardHeight))
        }
    }
}
