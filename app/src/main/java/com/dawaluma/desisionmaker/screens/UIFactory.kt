package com.dawaluma.desisionmaker.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawaluma.desisionmaker.R

@Composable
fun iconRounded(iconID: Int, onClick: () -> Unit = {}, ignoreTheme : Boolean = false){

    var colorFilter : ColorFilter? = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black)
    if ( ignoreTheme){
        colorFilter = null
    }

    Image(
        painter = painterResource(id = iconID),
        colorFilter = colorFilter,
        contentDescription = null,
        modifier = Modifier
            .clickable {
                onClick()
            }
            .size(50.dp) // Set the size to 100 x 100 pixels
            .clip(CircleShape) // Clip the image to make it rounded
            .background(MaterialTheme.colors.surface) // Optional background color for contrast
            .border(2.dp, Color.Gray, CircleShape), // Optional border
        contentScale = ContentScale.Crop // Scale the image to fill the rounded shape
    )
}

@Composable
fun iconPlain(iconID: Int, onClick: () -> Unit = {}, ignoreTheme : Boolean = false){

    var colorFilter : ColorFilter? = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black)
    if ( ignoreTheme){
        colorFilter = null
    }

    Image(
        painter = painterResource(id = iconID),
        colorFilter = colorFilter,
        contentDescription = null,
        modifier = Modifier
            .clickable {
                onClick()
            }
            .size(25.dp) // Set the size to 100 x 100 pixels
            .background(MaterialTheme.colors.surface) // Optional background color for contrast
    )
}

@Composable
fun PillButton(
    text: String,
    backgroundColor: Color = MaterialTheme.colors.primary,
    textColor: Color = MaterialTheme.colors.onPrimary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50), // Pill shape
        color = backgroundColor,
        modifier = modifier
            .wrapContentWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp) // Padding for pill shape
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TitleText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(0.8f).padding(start = 4.dp, end = 4.dp),
        text = text,
        color = MaterialTheme.colors.onSecondary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun MinorText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(0.8f).padding(start = 8.dp),
        text = text,
        color = MaterialTheme.colors.onSecondary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SimpleTextField(value: String,onValueChange: (String) -> Unit) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.Transparent,
            cursorColor = Color.Blue,
            focusedIndicatorColor = Color.Blue,
            unfocusedIndicatorColor = Color.Gray
        )
    )
}

@Composable
fun RowCentered(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun SmallCard(
    content: @Composable () -> Unit){
    Card( modifier = Modifier.fillMaxWidth().padding(all = 8.dp)){
        Column(modifier = Modifier.fillMaxWidth().padding(all = 8.dp)) {
            content()
        }
    }
}

@Composable
fun PrimaryButton(text: String,onClick: () -> Unit){
    Button(
        onClick = onClick,
        modifier = Modifier.padding(8.dp).fillMaxWidth(0.8f),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onSecondary
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Composable
fun ErrorText(text: String){
    Text(text, color = Color.Red)
}

