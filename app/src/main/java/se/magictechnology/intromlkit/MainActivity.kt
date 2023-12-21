package se.magictechnology.intromlkit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import se.magictechnology.intromlkit.ui.theme.IntroMLKitTheme
import java.lang.StringBuilder


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroMLKitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(verticalArrangement =  Arrangement.Center,
                        horizontalAlignment = CenterHorizontally) {
                        SetUpAButton(name = "image 1", imageResourceId = R.drawable.image1)
                        SetUpAButton(name = "image 2", imageResourceId = R.drawable.image2)
                        SetUpAButton(name = "image 3", imageResourceId = R.drawable.image3)
                    }
                }
            }
        }
    }
}
private fun processTextRecognitionResult(texts: Text) {
    val blocks: List<Text.TextBlock> = texts.textBlocks
    if (blocks.isEmpty()) {
        //showToast("No text found")
        return
    }
    //mGraphicOverlay.clear()
    for (i in blocks.indices) {
        val lines: List<Text.Line> = blocks[i].lines
        for (j in lines.indices) {
            val elements: List<Text.Element> = lines[j].elements
            for (k in elements.indices) {
                //val textGraphic: Graphic = TextGraphic(mGraphicOverlay, elements[k])
                //mGraphicOverlay.add(textGraphic)
                Log.i("MLKITDEBUG",elements[k].text + " " + elements[k].confidence.toString())

            }
        }
    }
}
@Composable
fun SetUpAButton(name: String, imageResourceId: Int, modifier: Modifier = Modifier) {
    val selectedImage = BitmapFactory.decodeResource(LocalContext.current.resources, imageResourceId)

    var detectedText: Text? by remember {
        mutableStateOf(null)
    }
    val image = InputImage.fromBitmap(selectedImage, 0)
    var progressBarAlpha by remember {
        mutableFloatStateOf(0f)
    }
    var resultAlpha by remember {
        mutableFloatStateOf(0f)
    }
    Column {
        Row(
            Modifier
                .fillMaxWidth(0.9f)
                .height(200.dp)
                .padding(all = 10.dp)
                .background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {

            Image(
                bitmap = selectedImage.asImageBitmap(),
                contentDescription = "Image 1",
                modifier = modifier
                    .weight(1f)
                    .alpha(resultAlpha)
                    .padding(all = 10.dp)
            )
            val stringBuilder = StringBuilder()
            detectedText?.textBlocks?.mapNotNull { block ->
                stringBuilder.appendLine(block.text)
            }
            CircularProgressIndicator(modifier.alpha(progressBarAlpha))
            Text(stringBuilder.toString(), modifier = modifier
                .weight(1f)
                .alpha(resultAlpha)
                .padding(all = 10.dp),
                textAlign = TextAlign.Center)
        }
        Button(onClick = {
            resultAlpha = 0f
            progressBarAlpha = 1f
            runTextRecognition(image)
                .addOnSuccessListener { texts ->
                    processTextRecognitionResult(texts)
                    detectedText = texts
                    resultAlpha = 1f
                }
                .continueWith {
                    progressBarAlpha = 0f
                }
        }, modifier.align(alignment = CenterHorizontally)) {
            Text(name)
        }
    }
}

private fun runTextRecognition(image: InputImage): Task<Text> {
    val textRecognizerOptions = TextRecognizerOptions.Builder().build()
    val recognizer = TextRecognition.getClient(textRecognizerOptions)
    return recognizer.process(image)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IntroMLKitTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SetUpAButton("Android", R.drawable.image1)
        }
    }
}
