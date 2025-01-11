package com.dawaluma.desisionmaker.screens

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dawaluma.desisionmaker.IntentLauncher
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.SpeakText
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.viewmodels.AboutViewModelFactory
import com.dawaluma.desisionmaker.viewmodels.ConversationListViewModelFactory
import com.dawaluma.desisionmaker.viewmodels.IntroViewModelFactory
import com.dawaluma.desisionmaker.viewmodels.PinsListViewModelFactory
import com.dawaluma.desisionmaker.viewmodels.StartChatViewModelFactory
import kotlinx.coroutines.launch

data class AppBarIcon(var iconID: Int, var ignoreTheme: Boolean = false, var clickStartsNewChat : Boolean = false)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainLayout(speakText: SpeakText,
               intentLauncher: IntentLauncher,
               appSettings: AppSettingsContract,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val appBarTitle = remember { mutableStateOf("Initial Chat") }
    val appBarIcon = remember { mutableStateOf( AppBarIcon(R.mipmap.professor_round, ignoreTheme=true, clickStartsNewChat = true) )}

    val context = LocalContext.current
    val startConversation = context.getString(R.string.hamburger_conv)
    val chats = context.getString(R.string.hamburger_chats)
    val pinned = context.getString(R.string.hamburger_pinned)
    val about = context.getString(R.string.about)

    //the 3 drawer items
    val professorItem = DrawerData(startConversation, iconID = R.mipmap.professor_round, ignoreTheme = true)
    val chatsItem = DrawerData(chats, iconID = R.drawable.ic_chats)
    val pinnedItem = DrawerData(pinned, iconID = R.drawable.ic_pin)
    val aboutItem = DrawerData(context.getString(R.string.about), iconID = R.drawable.ic_info)

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                items = listOf(
                    professorItem,
                    chatsItem,
                    pinnedItem,
                    aboutItem)
                ,
                onItemClick = { item ->
                    scope.launch { drawerState.close() }
                    when (item) {
                        startConversation -> navController.navigate("initialChat")
                        chats -> navController.navigate("allchats")
                        pinned -> navController.navigate("pins")
                        about -> navController.navigate("about")
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .background(MaterialTheme.colors.surface) // Set the drawer itself to 50% of the screen width
    ) {
        Scaffold(
            topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .background(Color.Transparent),
                                    verticalAlignment = Alignment.CenterVertically,
                            ) {

                                var colorFilter : ColorFilter ?= null
                                if (!appBarIcon.value.ignoreTheme){
                                    colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black)
                                }

                                Image(
                                    painter = painterResource(id = appBarIcon.value.iconID),
                                    contentDescription = null,
                                    colorFilter = colorFilter,
                                    modifier = Modifier
                                        .size(45.dp) // Set the size to 100 x 100 pixels
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colors.surface)
                                        .border(2.dp, Color.Gray, CircleShape)
                                        .clickable {
                                            if (appBarIcon.value.clickStartsNewChat){
                                                navController.navigate("initialChat")
                                            }
                                        }
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop // Scale the image to fill the rounded shape
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = appBarTitle.value,
                                    style = MaterialTheme.typography.h6, // Use Material theme typography for the title
                                    color = MaterialTheme.colors.onPrimary // Use a color that contrasts with the app bar background
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    Icons.Filled.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colors.onPrimary // Icon color to match the theme
                                )
                            }
                        },
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp,
                        modifier = Modifier.zIndex(1f),
                    )
            },
            content = {
                val context = LocalContext.current

                NavHost(navController = navController,
                    startDestination = if (appSettings.isAppTermsAgreed()) "initialChat" else "intro",
                    modifier = Modifier.padding(top = 0.dp)) {
                    composable("currentConversation/{conversationID}") { backStackEntry ->
                        appBarTitle.value = ""
                        appBarIcon.value = AppBarIcon(professorItem.iconID, ignoreTheme = true, clickStartsNewChat = true)
                        val conversationId = backStackEntry.arguments?.getString("conversationID")?.toLongOrNull() ?: -1L
                        val application = context.applicationContext as Application

                        StartConversationScreen(conversationID = conversationId, speakText, viewModel(
                            factory = StartChatViewModelFactory(application, intentLauncher)
                        ))
                    }
                    composable("allchats") {
                        appBarTitle.value = chatsItem.text
                        appBarIcon.value = AppBarIcon(chatsItem.iconID)

                        val application = context.applicationContext as Application
                        ConversationsUI(navController, viewModel(
                            factory = ConversationListViewModelFactory(application)
                        )   ) }
                    composable("initialChat") {
                        appBarTitle.value = ""
                        appBarIcon.value = AppBarIcon(professorItem.iconID, ignoreTheme = true, clickStartsNewChat = true)


                        val application = context.applicationContext as Application
                        StartConversationScreen(speakText = speakText, viewModel =
                            viewModel(
                                factory = StartChatViewModelFactory(application, intentLauncher)
                            )
                        ) }
                    composable("pins") {
                        appBarTitle.value = pinnedItem.text
                        appBarIcon.value = AppBarIcon(pinnedItem.iconID)

                        val application = context.applicationContext as Application
                        PinsUI(
                            navController, speakText,
                            viewModel(
                                factory = PinsListViewModelFactory(application, intentLauncher)
                            )
                        )
                    }
                    composable("intro") {
                        val application = context.applicationContext as Application

                        IntroUI(navController, viewModel(
                            factory = IntroViewModelFactory(application)
                        ))
                    }
                    composable("about") {

                        val application = context.applicationContext as Application
                        AboutUI(viewModel(
                            factory = AboutViewModelFactory(application)
                        ), navController, intentLauncher)

                    }
                }
            }
        )
    }
}


data class DrawerData(val text: String, val iconID: Int, val ignoreTheme: Boolean = false)

@Composable
fun DrawerContent(items: List<DrawerData>, onItemClick: (String) -> Unit) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(MaterialTheme.colors.primary)
        .padding(16.dp)) {
        items.forEach { item ->

            Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {

                var colorFilter :ColorFilter? = null
                if (!item.ignoreTheme) {
                    colorFilter =
                        ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black)
                }

                Image(
                    painter = painterResource(id = item.iconID),
                    colorFilter = colorFilter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp) // Set the size to 100 x 100 pixels
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.surface)
                        .border(2.dp, Color.Gray, CircleShape)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop // Scale the image to fill the rounded shape
                )

                Text(
                    text = item.text,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item.text) }
                        .padding(vertical = 8.dp)
                        .padding(start = 10.dp),
                )
            }

            Divider(
                color = Color.LightGray, // Set the color of the divider
                thickness = 2.dp // Set the thickness of the divider
            )
        }
    }
}
