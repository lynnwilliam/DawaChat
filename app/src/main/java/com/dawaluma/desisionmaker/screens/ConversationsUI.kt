package com.dawaluma.desisionmaker.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.database.Conversation
import com.dawaluma.desisionmaker.database.lookupDrawable
import com.dawaluma.desisionmaker.viewmodels.ConversationListViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ConversationsUI(navController: NavController, viewModel: ConversationListViewModel) {

    val conversations = viewModel.conversations.observeAsState(initial = emptyList<Conversation>())

    if ( conversations.value.isEmpty()){
        val context = LocalContext.current
        emptyScreen(
            titleText = context.getString(R.string.nochats),
            buttonText = context.getString(R.string.starttalking),
            navController = navController)
    }else{
        // Display the list using LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(conversations.value) { conversation ->
                ConversationItem(conversation = conversation,
                    openConversationClick = { loadConversation(it, navController) },
                    deleteConversationClick = { viewModel.deleteConversation(it) }
                )
                Divider(
                    color = Color.LightGray, // Set the color of the divider
                    thickness = 2.dp // Set the thickness of the divider
                )
            }
        }
    }

}

fun loadConversation(conversation: Conversation, navController: NavController){
    navController.navigate("currentConversation/${conversation.conversationID}")
}


@Composable
fun ConversationItem(
    conversation: Conversation,
    openConversationClick: (Conversation) -> Unit,
    deleteConversationClick: (Conversation) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        iconRounded(lookupDrawable(conversation.iconID), ignoreTheme = true, onClick = {openConversationClick(conversation)})
        Spacer(modifier = Modifier.width(4.dp)) // Add space between image and tex
        iconRounded(R.drawable.ic_delete, onClick = {
            deleteConversationClick(conversation)
        })

        Text(
            modifier = Modifier.clickable { openConversationClick(conversation) }
                .padding(start = 4.dp, end = 4.dp)
                .border(
                    1.dp,
                    MaterialTheme.colors.onSurface,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.primaryVariant)
                .padding(start = 4.dp, end = 4.dp),
            text = conversation.conversationLabel,
            fontSize = 20.sp) // Replace with your UI elements
        }
}
