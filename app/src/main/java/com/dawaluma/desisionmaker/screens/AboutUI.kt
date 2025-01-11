package com.dawaluma.desisionmaker.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.dawaluma.desisionmaker.AppInfoProvider
import com.dawaluma.desisionmaker.IntentLauncher
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.viewmodels.AboutViewModel

@Composable
fun AboutUI(
    viewModel: AboutViewModel,
    navController: NavController,
    intentLauncher: IntentLauncher){

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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SmallCard() {
                RowCentered() {
                    TitleText(text = context.getString(R.string.created))
                }
                RowCentered {
                    Text(text = AppInfoProvider.getAppVersionDetails(context))
                }
                RowCentered() {
                    Text(text = context.getString(R.string.reportbugs))
                }
                RowCentered() {
                    PrimaryButton(text = context.getString(R.string.openrepo)) {
                       intentLauncher.openBrowser(context.getString(R.string.openrepo))
                    }
                }
            }

            if( viewModel.appSettings.isDemoMode()){
                SmallCard() {
                    RowCentered() {
                        TitleText(text = context.getString(R.string.demomode))
                    }
                    RowCentered() {
                        PrimaryButton(text = context.getString(R.string.demomodeoff), onClick = {
                            viewModel.disableDemoMode()
                            navController.navigate("intro")

                        })
                    }
                }
            } else {
                SmallCard() {
                    RowCentered() {
                        TitleText(text = context.getString(R.string.resetAPI))
                    }
                    RowCentered() {
                        PrimaryButton(text = context.getString(R.string.resetAPIOFF), onClick = {
                            viewModel.disableDemoMode()
                            navController.navigate("intro")
                        })
                    }
                }
            }
        }
    }

}