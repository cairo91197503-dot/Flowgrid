package com.flowgrid.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Sand
import com.flowgrid.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    val slides = listOf(
        Triple("🚰", "Conecte os tubos", "Toque nas peças para rotacioná-las e conectar a fonte ao destino."),
        Triple("📅", "Um desafio por dia", "O Nível do Dia é igual para todo mundo. Compare seu resultado."),
        Triple("👑", "FlowGrid Pro", "Remova os anúncios e tenha dicas ilimitadas por uma única vez.")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        // Pular button (except last slide)
        if (pagerState.currentPage < slides.size - 1) {
            TextButton(
                onClick = {
                    viewModel.setOnboardingCompleted()
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Pular", color = DarkText.copy(alpha = 0.6f))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = slide.first,
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Text(
                        text = slide.second,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = slide.third,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkText.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Page Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(slides.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Jade else Earth
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(10.dp)
                    )
                }
            }

            // Bottom Button
            Button(
                onClick = {
                    if (pagerState.currentPage < slides.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.setOnboardingCompleted()
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Jade),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == slides.size - 1) "Começar" else "Próximo",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
