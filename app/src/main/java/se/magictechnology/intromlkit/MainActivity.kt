package se.magictechnology.intromlkit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import se.magictechnology.intromlkit.ui.theme.IntroMLKitTheme


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
                    Column {
                        Greeting("Android")

                        Button(onClick = {
                            runTextRecognition()
                        }) {
                            Text("Process image")
                        }
                    }
                }
            }
        }
    }

    private fun runTextRecognition() {

        var selectedImage = BitmapFactory.decodeResource(resources, R.drawable.sign1)

        val image = InputImage.fromBitmap(selectedImage, 0)
        var textRecognizerOptions = TextRecognizerOptions.Builder().build()
        val recognizer = TextRecognition.getClient(textRecognizerOptions)
        //mTextButton.setEnabled(false)
        recognizer.process(image)
            .addOnSuccessListener { texts ->
                //mTextButton.setEnabled(true)
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                //mTextButton.setEnabled(true)
                e.printStackTrace()
            }
    }

    private fun processTextRecognitionResult(texts: Text) {
        val blocks: List<Text.TextBlock> = texts.getTextBlocks()
        if (blocks.size == 0) {
            //showToast("No text found")
            return
        }
        //mGraphicOverlay.clear()
        for (i in blocks.indices) {
            val lines: List<Text.Line> = blocks[i].getLines()
            for (j in lines.indices) {
                val elements: List<Text.Element> = lines[j].getElements()
                for (k in elements.indices) {
                    //val textGraphic: Graphic = TextGraphic(mGraphicOverlay, elements[k])
                    //mGraphicOverlay.add(textGraphic)
                    Log.i("MLKITDEBUG",elements[k].text + " " + elements[k].confidence.toString())

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IntroMLKitTheme {
        Greeting("Android")
    }
}