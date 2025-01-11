package com.dawaluma.desisionmaker.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.SpeakText
import com.dawaluma.desisionmaker.database.QuestionAnswer
import com.dawaluma.desisionmaker.viewmodels.PinsViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PinsUI(navController: NavController, speakText: SpeakText, viewModel: PinsViewModel) {

    val conversations = viewModel.conversations.observeAsState(initial = emptyList<QuestionAnswer>())

    if (conversations.value.isEmpty()){
        val context = LocalContext.current
        emptyScreen(
            titleText = context.getString(R.string.nopins),
            buttonText = context.getString(R.string.starttalking),
            navController = navController)
    }else{
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(conversations.value) { conversation ->
                QuestionItem(conversation,speakText,
                    mapsClicked = {
                        viewModel.openMapWithAddress(it)
                    },
                    VideoClicked = {
                        viewModel.openVideo(it)
                    },
                    appSettings = viewModel.appSettings
                )
                Divider(
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color.LightGray, // Set the color of the divider
                    thickness = 2.dp // Set the thickness of the divider
                )
            }
        }
    }
}

@Composable
fun emptyScreen(titleText: String, buttonText: String,navController: NavController ){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = titleText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGray,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
        )

        RowCentered {
            PrimaryButton(buttonText, onClick = {
                navController.navigate("initialChat")
            })
        }

    }
}

