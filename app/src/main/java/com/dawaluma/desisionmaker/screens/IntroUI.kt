package com.dawaluma.desisionmaker.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.viewmodels.APIKeyTest
import com.dawaluma.desisionmaker.viewmodels.APITestState
import com.dawaluma.desisionmaker.viewmodels.IntroViewModel
import com.dawaluma.desisionmaker.viewmodels.LLM

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun IntroUI(
    navController: NavController, viewModel: IntroViewModel) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize() // Make the Box full screen
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.b),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.4f)
        )

        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectChatLLM(viewModel)

            var showYouTubeAPI by remember { mutableStateOf(viewModel.appSettings.isYouTubeAPIOffered()) }
            if (showYouTubeAPI) {
                SmallCard() {
                    var apiKey by remember { mutableStateOf("") }
                    var gptAPIState = viewModel.youttubeapiKeyValid
                    RowCentered() {
                        iconRounded(R.drawable.ic_youtube, ignoreTheme = true)
                        TitleText(text = context.getString(R.string.youtubekey))
                        iconPlain(R.drawable.ic_delete, onClick = {
                            showYouTubeAPI = false
                            viewModel.appSettings.setYouTubeOffered(false)
                        })
                    }
                    RowCentered() {
                        SimpleTextField(value = apiKey, onValueChange = {
                            apiKey = it
                            viewModel.updateYoutubeAIKey(it)
                        })
                    }
                    RowCentered() {
                         when (gptAPIState.value) {
                            APIKeyTest.KeyNotRequired -> {
                                Text(context.getString(R.string.apikey_not_required))
                            }

                            APIKeyTest.KeyFailedTesting -> {
                                ErrorText(context.getString(R.string.apikey_not_valid))
                            }

                            APIKeyTest.KeyRequired -> {
                                Text(context.getString(R.string.apikey_required))
                            }
                             APIKeyTest.KeyPassedTesting -> {
                                 Text(context.getString(R.string.keyvalid))
                             }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            var showMapsAPI by remember { mutableStateOf(viewModel.appSettings.isMapsAPIOffered()) }
            if (showMapsAPI) {
                SmallCard() {
                    var apiKey by remember { mutableStateOf("") }
                    var gptAPIState = viewModel.mapsapiKeyValid
                    RowCentered() {
                        iconRounded(R.drawable.ic_maps, ignoreTheme = true)
                        TitleText(text = context.getString(R.string.mapskey))
                        iconPlain(R.drawable.ic_delete, onClick = {
                            showMapsAPI = false
                            viewModel.appSettings.setMapsOffered(false)
                        })
                    }
                    RowCentered() {
                        SimpleTextField(
                            value = apiKey,
                            onValueChange =
                            {
                                apiKey = it
                                viewModel.updateMapsAIKey(it)
                            })
                    }
                    RowCentered() {
                        when (gptAPIState.value) {
                            APIKeyTest.KeyNotRequired -> {
                                Text(context.getString(R.string.apikey_not_required))
                            }

                            APIKeyTest.KeyFailedTesting -> {
                                ErrorText(context.getString(R.string.apikey_not_valid))
                            }

                            APIKeyTest.KeyRequired -> {
                                Text(context.getString(R.string.apikey_required))
                            }
                            APIKeyTest.KeyPassedTesting -> {
                                Text(context.getString(R.string.keyvalid))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Row() {
                val testingAPIKeys = viewModel.testingAPIKeys
                when (testingAPIKeys.value) {

                    APITestState.NOT_TESTED -> {
                        buttonsRow(navController, viewModel)
                    }

                    APITestState.TESTING -> {
                        Text("Testing API Keys")
                    }

                    APITestState.TESTING_PASSED -> {
                        viewModel.appSettings.setAppTermsAgreed(true)
                        navController.navigate("initialChat")
                    }

                    APITestState.TESTING_FAILED -> {
                        buttonsRow(navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun buttonsRow(navController: NavController,viewModel: IntroViewModel){

    val context = LocalContext.current
    Column(){
        PrimaryButton(
            text = context.getString(R.string.starttalking),
            onClick = {
                if ( viewModel.isConfigured()){
                    viewModel.appSettings.setAppTermsAgreed(true)
                    navController.navigate("initialChat") {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") { inclusive = true }
                    }
                }
                else {
                    viewModel.testUserApiKeys()
                }
            })
        PrimaryButton(
            text = context.getString(R.string.demomode),
            onClick = {
                viewModel.appSettings.setAppTermsAgreed(true)
                viewModel.demoMode()
                navController.navigate("initialChat") {
                    popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") { inclusive = true }
                }

            })
    }
}

@Composable
fun SelectChatLLM(viewModel: IntroViewModel){

    val appSettings = viewModel.appSettings
    if ( appSettings.hasValidLLMKey()){
        appSettings.getLLMFromDefault()?.let{
            SmallCard() {
                TitleText(text = it)
            }
        }
    } else {
        LLConfigCard(viewModel)
    }
}

@Composable
fun LLConfigCard(viewModel: IntroViewModel ){

    SmallCard() {
        var apiKey by remember { mutableStateOf("") }  // Managed state for the text field input
        val gptAPIState = viewModel.llmApiKeyValid

        RowCentered() {
            var selectedButton by remember { mutableStateOf(LLM.GEMINI) }
            val backgroundColor1 = if (selectedButton == LLM.GEMINI) Color.Gray else Color.Transparent
            val backgroundColor2 = if (selectedButton == LLM.CHATGPT) Color.Gray else Color.Transparent

            RowCentered {
                // Button 1 (Image 1)
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            selectedButton = LLM.GEMINI
                            viewModel.selectedLLM = selectedButton
                        }
                        .background(backgroundColor1)
                        .padding(8.dp)
                ) {
                    Text("Gemini")
                    Image(
                        painter = painterResource(id = R.drawable.geminilogo), // Replace with your first image
                        contentDescription = LLM.GEMINI.name,
                        modifier = Modifier.size(80.dp)
                    )
                }

                // Button 2 (Image 2)
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            selectedButton = LLM.CHATGPT
                            viewModel.selectedLLM = selectedButton
                        }
                        .background(backgroundColor2)
                        .padding(8.dp)
                ) {
                    Text("ChatGPT")
                    Image(
                        painter = painterResource(id = R.drawable.openai_logomark), // Replace with your second image
                        contentDescription = LLM.CHATGPT.name,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

        }

        RowCentered() {
            SimpleTextField(
                value = apiKey,
                onValueChange = {
                    apiKey = it
                    viewModel.updateLLMKey(it)
                })
        }

        RowCentered() {
            when (gptAPIState.value) {
                APIKeyTest.KeyNotRequired -> {
                    Text(LocalContext.current.getString(R.string.apikey_not_required))
                }

                APIKeyTest.KeyFailedTesting -> {
                    ErrorText(LocalContext.current.getString(R.string.apikey_not_valid))
                }

                APIKeyTest.KeyRequired -> {
                    Text(LocalContext.current.getString(R.string.apikey_required))
                }

                APIKeyTest.KeyPassedTesting -> {
                    Text(LocalContext.current.getString(R.string.keyvalid))
                }
            }
        }
    }
}
