package com.dawaluma.desisionmaker.screens

import android.annotation.SuppressLint
import android.speech.tts.UtteranceProgressListener
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.*
import com.dawaluma.desisionmaker.R
import com.dawaluma.desisionmaker.Formating
import com.dawaluma.desisionmaker.SpeakText
import com.dawaluma.desisionmaker.database.AppSettingsContract
import com.dawaluma.desisionmaker.database.DataType
import com.dawaluma.desisionmaker.database.Pill
import com.dawaluma.desisionmaker.database.QuestionAnswer
import com.dawaluma.desisionmaker.database.SharedPrefAppSettings
import com.dawaluma.desisionmaker.database.lookupDrawable
import com.dawaluma.desisionmaker.parseStringWithTags
import com.dawaluma.desisionmaker.viewmodels.StartChatViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StartConversationScreen(conversationID: Long = -1L, speakText: SpeakText, viewModel: StartChatViewModel) {

    val showStartingPrompt by viewModel.showStartingPrompt.observeAsState(initial = true)
    val context = LocalContext.current
    if (conversationID != -1L) {
        viewModel.loadConversation(context, conversationID)
    }

    Box(
        modifier = Modifier
            .padding(top = 0.dp)
            .fillMaxSize()
    ) {
        // Background image covering the whole Box
        Image(
            painter = painterResource(id = lookupDrawable(viewModel.getBackgroundID())),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Scale the image to fill the space
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp)
        )

        var backgroundColor = MaterialTheme.colors.background
        if ( showStartingPrompt) {
            if (isSystemInDarkTheme()) {
                // Dark mode: fade to black if `showStartingPrompt` is true
                Color.Black.copy(alpha = 0.0f)
            } else {
                // Light mode: fade to white if `showStartingPrompt` is true
                Color.White.copy(alpha = 0.10f)
            }
        }else {

            //This is the Fade to Black/White after the 1st question is asked
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }

        val isLoading = viewModel.loading.value // Directly access the value
        if ( isLoading && showStartingPrompt) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center // Center content horizontally and vertically
            ) {
                LottieAnimationView()
            }
        }

        val context = LocalContext.current
        if ( showStartingPrompt) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)), // Semi-transparent dark background,
                contentAlignment = Alignment.Center // Center content horizontally and vertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = context.getString(R.string.startprompt),
                        maxLines = 2,
                        color = Color.White,
                        fontSize = 40.sp // Set the font size to 50sp
                    )

                    if (viewModel.appSettings.hasMapsAPI() && !viewModel.hasMapsPermissions(context)){
                        var isButtonVisible by remember { mutableStateOf(true) } // State to control button visibility
                        if (isButtonVisible) {
                            Button(
                                modifier = Modifier.padding(all = 4.dp),
                                onClick = {
                                    viewModel.requestLocationPermissions()
                                    isButtonVisible = false // Hide the button after clicking
                                }
                            ) {
                                Text(text = context.getString(R.string.askforlocation))
                            }
                        }
                    }
                }
            }
        }

        // Foreground content that scrolls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            // Content that scrolls with weight to take available space
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                QuestionsScreen(viewModel = viewModel, speakText)
            }

            // Input fields pinned at the bottom
            inputFields(
                viewModel,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        if ( isLoading && !showStartingPrompt) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center // Center content horizontally and vertically
            ) {
                LottieAnimationView()
            }
        }
    }
}

@Composable
fun LottieAnimationView(modifier: Modifier = Modifier) {

    // Load the Lottie animation from a JSON file in the raw resource folder
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading1))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Loop the animation forever
    )

    // Display the Lottie animation at 80% of the screen size
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
            .fillMaxSize(1f)
            .scale(2f)
    )
}

@Composable
private fun inputFields(startChatViewModel: StartChatViewModel, modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    val isLoading = startChatViewModel.loading.value // Directly access the value

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background),
        horizontalArrangement = Arrangement.Center, // Centers the content horizontally
        verticalAlignment = Alignment.CenterVertically // Aligns the content vertically
    ) {

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
                .weight(1f) // Takes available space
                .fillMaxWidth(0.9f),
            keyboardOptions = KeyboardOptions(autoCorrect = false),
            enabled = !isLoading, // Disable when loading
            textStyle = MaterialTheme.typography.body1.copy(fontSize = MaterialTheme.typography.body1.fontSize * 1.5),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                backgroundColor = MaterialTheme.colors.surface,
                focusedIndicatorColor = MaterialTheme.colors.surface,
                unfocusedIndicatorColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                disabledIndicatorColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                cursorColor = MaterialTheme.colors.primaryVariant
            ),
            placeholder = {
                Text(
                    text = context.getString(R.string.getstarted), // Your hint text
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                    fontSize = MaterialTheme.typography.body1.fontSize * 1.5
                )
            }
        )

        // Image aligned to the center
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black),
            contentDescription = null,
            modifier = Modifier
                .padding(0.dp)
                .background(MaterialTheme.colors.surface)
                .size(50.dp)
                .align(Alignment.CenterVertically) // Aligns the image vertically within the Row
                .clickable {
                    if (!isLoading) {
                        // Call fetchAnswer on the ViewModel to perform the network request
                        startChatViewModel.fetchAnswer(inputText.text)
                        inputText = TextFieldValue("")
                    }
                }
        )
    }
}

@Composable
private fun QuestionsScreen(
    viewModel: StartChatViewModel,
    speakText: SpeakText) {
    // Collect LiveData as State in Compose
    val questions by viewModel.questions.observeAsState(initial = emptyList())

    // Create LazyListState to control scrolling
    val listState = rememberLazyListState()

    // Scroll to the last item when `questions` is updated
    LaunchedEffect(questions.size) {
        if (questions.isNotEmpty()) {
            listState.animateScrollToItem(questions.size - 1)
        }
    }

    // Display the list using LazyColumn
    LazyColumn(state = listState) {
        items(questions) { question ->
            QuestionItem(
                questionText = question,
                speakText,
                mapsClicked = {
                    viewModel.openMapWithAddress(it)
                },
                VideoClicked = {
                    viewModel.openVideo(it)
                },
                startChatViewModel = viewModel,
                appSettings = viewModel.appSettings
            )
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun QuestionItem(questionText: QuestionAnswer,
                 speakText: SpeakText,
                 appSettings: AppSettingsContract,
                 mapsClicked: (String) -> Unit,
                 VideoClicked: (DataType.Video) -> Unit,
                 startChatViewModel: StartChatViewModel? = null ) {

    var isPinned by remember { mutableStateOf(questionText.pinned) }
    val clipboardManager = LocalClipboardManager.current // Clipboard manager
    val context = LocalContext.current

    Column() {

        Row() {

            Text(
                text = questionText.question,
                style = MaterialTheme.typography.body1, // Use the app's typography style
                color = MaterialTheme.colors.onBackground, // Text color based on the theme
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f)
                    .border(
                        1.dp,
                        MaterialTheme.colors.onSurface,
                        shape = RoundedCornerShape(8.dp)
                    ) // Border using a color from the theme
                    .clip(RoundedCornerShape(8.dp)) // Clip to match the border shape
                    .background(MaterialTheme.colors.primaryVariant) // Background color based on the theme
                    .padding(8.dp)
                    .clickable {
                        clipboardManager.setText(AnnotatedString(questionText.question))
                    }
            )

            Row(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(16.dp)
            ) {
                if (!questionText.answer.isLocalMessage) {
                    if (isPinned) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pin),
                            colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(0.dp)
                                .size(25.dp)
                                .clickable {
                                    questionText.togglePin(context)
                                    isPinned = !isPinned
                                }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.pin_added),
                            colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(0.dp)
                                .size(25.dp)
                                .clickable {
                                    questionText.togglePin(context)
                                    isPinned = !isPinned
                                }
                        )
                    }
                }
            }
        }
    }

    if ( questionText.answer is DataType.TextType){
        composeDataTypeProfess(questionText,speakText,appSettings, startChatViewModel, )
    }
    else  if ( questionText.answer is DataType.MapsType){
        composeDataTypeMaps(
            questionText = questionText,
            speakText = speakText,
            mapsClicked = mapsClicked,
            appSettings = appSettings,
            startChatViewModel = startChatViewModel )
    }
    else if ( questionText.answer is DataType.VideoType){
        composeDataTypeVideo(
            questionText = questionText,
            speakText = speakText,
            videoClicked = VideoClicked,
            appSettings = appSettings,
            startChatViewModel = startChatViewModel)
    }
}

@Composable
fun composeDataTypeVideo(
    questionText: QuestionAnswer,
    speakText: SpeakText,
    appSettings: AppSettingsContract,
    videoClicked: (DataType.Video) -> Unit,
    startChatViewModel: StartChatViewModel? = null){

    //here we pass in the list of Videos
    val videoDataType: DataType.VideoType = questionText.answer as DataType.VideoType
    Column(modifier =
    Modifier
        .padding(2.dp)
        .background(MaterialTheme.colors.surface)
        .border(
            1.dp,
            MaterialTheme.colors.onSurface,
            shape = RoundedCornerShape(8.dp)
        ) // Border color from the theme
        .clip(RoundedCornerShape(8.dp))
    ) {
        videoDataType.videoList.forEach { video ->
            VideoItem(video, videoClicked)
            Spacer(modifier = Modifier.height(8.dp)) // Optional spacing between items
            Divider(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.8f),
                color = Color.LightGray, // Set the color of the divider
                thickness = 2.dp // Set the thickness of the divider
            )
        }
        //do we add pills
        startChatViewModel?.let {
            if (questionText.hasPills()) {
                PillsRow(questionText.getPills(
                    LocalContext.current,
                    videoAPIEnabled = appSettings.hasYouTubeAPI(),
                    mapsAPIEnabled = appSettings.hasMapsAPI()
                ), startChatViewModel = startChatViewModel)
            }
        }
    }
}


@Composable
fun VideoItem(video: DataType.Video, videoClicked: (DataType.Video) -> Unit ) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { videoClicked(video) }
            .padding(8.dp)
    ) {
        Image(
            painter = rememberImagePainter(video.thumbnailURL),
            contentDescription = "",
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(12.dp)) // Apply rounded corners
                .padding(end = 4.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Text(
                text = video.text,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}



@SuppressLint("SuspiciousIndentation")
@Composable
private fun composeDataTypeMaps(questionText: QuestionAnswer,
                                speakText: SpeakText,
                                appSettings: AppSettingsContract,
                                mapsClicked: (String) -> Unit,
                                startChatViewModel: StartChatViewModel? = null){

    val clipboardManager = LocalClipboardManager.current // Clipboard manager
    var isSpeaking by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.4f else 1f,
        animationSpec = if (isSpeaking) {
            infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(durationMillis = 0) // Instantaneously set back to normal when not speaking
        }
    )

    if (!questionText.answer.getAnswerAsString().isNullOrBlank()){
        val mapsDataType: DataType.MapsType = questionText.answer as DataType.MapsType

        Column(modifier =
        Modifier
            .padding(2.dp)
            .background(MaterialTheme.colors.surface)
            .border(
                1.dp,
                MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(8.dp)
            ) // Border color from the theme
            .clip(RoundedCornerShape(8.dp))
        ){
            Row(){

                Image(
                    painter = painterResource(id = R.mipmap.professor),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp) // Set the size to 100 x 100 pixels
                        .clip(CircleShape) // Clip the image to make it rounded
                        .background(Color.Transparent) // Optional background color for contrast
                )

                // Spacer to push the next image to the right
                Spacer(modifier = Modifier.weight(1f))

                if ( speakText.isSpeakSupported()) {
                    Image(
                        painter = painterResource(id = R.drawable.talkback),
                        colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(5.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            )
                            .background(Color.Transparent)
                            .clickable {

                                questionText.answer?.let {

                                    isSpeaking = !isSpeaking
                                    class speechListener : UtteranceProgressListener() {
                                        override fun onStart(utteranceId: String?) {
                                            isSpeaking = true
                                        }

                                        override fun onDone(utteranceId: String?) {
                                            isSpeaking = false
                                        }

                                        override fun onError(utteranceId: String?) {
                                            isSpeaking = false
                                        }
                                    }
                                    speakText.speakText(
                                        questionText.answer.getAnswerAsString(),
                                        speechListener()
                                    )
                                }
                            }
                    )
                }
            }

            //Make formatted text for the business
            //Create a good Formatted String !
            var mapString : String = "Closest location:\n\n" + Formating.startBold + mapsDataType.text + Formating.endBold + "\n" + mapsDataType.mapsLocation
            val annotatedString = parseStringWithTags(mapString)
                    Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.body1, // Apply Material typography
                    color = MaterialTheme.colors.onBackground, // Text color matching the theme
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            clipboardManager.setText(annotatedString)
                        }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                iconRounded(iconID = R.drawable.ic_location, onClick = {
                    mapsClicked(mapsDataType.mapsLocation)
                })
                //do we add pills
                startChatViewModel?.let {
                    if (questionText.hasPills()) {
                        PillsRow(questionText.getPills(
                            LocalContext.current,
                            videoAPIEnabled = appSettings.hasYouTubeAPI(),
                            mapsAPIEnabled = appSettings.hasMapsAPI()
                        ), startChatViewModel = startChatViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun composeDataTypeProfess(questionText: QuestionAnswer,
                                   speakText: SpeakText,
                                   appSettingsContract: AppSettingsContract,
                                   startChatViewModel: StartChatViewModel? = null) {

    val clipboardManager = LocalClipboardManager.current // Clipboard manager
    var isSpeaking by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.4f else 1f,
        animationSpec = if (isSpeaking) {
            infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(durationMillis = 0) // Instantaneously set back to normal when not speaking
        }
    )

    if (!questionText.answer.getAnswerAsString().isNullOrBlank()){

        Column(modifier =
        Modifier
            .padding(2.dp)
            .background(MaterialTheme.colors.surface)
            .border(
                1.dp,
                MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(8.dp)
            ) // Border color from the theme
            .clip(RoundedCornerShape(8.dp))
        ){
            Row(){

                Image(
                    painter = painterResource(id = R.mipmap.professor),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp) // Set the size to 100 x 100 pixels
                        .clip(CircleShape) // Clip the image to make it rounded
                        .background(Color.Transparent) // Optional background color for contrast
                )

                // Spacer to push the next image to the right
                Spacer(modifier = Modifier.weight(1f))

                if ( speakText.isSpeakSupported()) {
                    Image(
                        painter = painterResource(id = R.drawable.talkback),
                        colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(5.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            )
                            .background(Color.Transparent)
                            .clickable {

                                questionText.answer?.let {

                                    isSpeaking = !isSpeaking
                                    class speechListener : UtteranceProgressListener() {
                                        override fun onStart(utteranceId: String?) {
                                            isSpeaking = true
                                        }

                                        override fun onDone(utteranceId: String?) {
                                            isSpeaking = false
                                        }

                                        override fun onError(utteranceId: String?) {
                                            isSpeaking = false
                                        }
                                    }
                                    speakText.speakText(
                                        questionText.answer.getAnswerAsString(),
                                        speechListener()
                                    )
                                }
                            }
                    )
                }
            }

            Text(
                text = parseStringWithTags(questionText.answer.getAnswerAsString()),
                style = MaterialTheme.typography.body1, // Apply Material typography
                color = MaterialTheme.colors.onSecondary, // Text color matching the theme
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        clipboardManager.setText(AnnotatedString(questionText.answer.getAnswerAsString()))
                    }
            )

            //do we add pills
            startChatViewModel?.let {
                if (questionText.hasPills()) {
                    PillsRow(questionText.getPills(
                        LocalContext.current,
                        videoAPIEnabled = appSettingsContract.hasYouTubeAPI(),
                        mapsAPIEnabled = appSettingsContract.hasMapsAPI()), startChatViewModel = startChatViewModel)
                }
            }
        }
    }
}

@Composable
fun PillsRow(pillsList : List<Pill>, startChatViewModel: StartChatViewModel) {
    Row(modifier = Modifier.padding(8.dp)) {
        pillsList.forEach { pill ->
            PillButton(
                text = pill.display,
                backgroundColor = MaterialTheme.colors.primaryVariant,
                onClick = {
                        //here we inject a new question
                          startChatViewModel.fetchAnswer(pill.question)
                    },
                modifier = Modifier.padding(end = 8.dp) // Add space between pills
            )
        }
    }
}


