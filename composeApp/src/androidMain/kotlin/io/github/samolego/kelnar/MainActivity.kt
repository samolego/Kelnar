package io.github.samolego.kelnar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        AppContext.init(this)

        val originalIntent: Intent = intent
        val originalUri: Uri? = originalIntent.data

        // Since we use # in our app due to SPA routing,
        // navcontroller cannot really follow the deeplinks normally.
        originalUri?.toString()?.let { uriString ->
            // Check if this is a hash-based URL that needs to be transformed.
            if (uriString.contains("/#")) {
                // Replace "/#/" with a standard path separator "/".
                val newUriString = uriString.replace("/#", "/")
                val newUri = Uri.parse(newUriString)

                // Create a new Intent with the same action but the new URI.
                val newIntent =
                        Intent(originalIntent.action, newUri).apply {
                            // It's good practice to ensure the BROWSABLE category is present.
                            addCategory(Intent.CATEGORY_BROWSABLE)
                        }

                Log.d("MainActivity", "replaced link: $newUri")

                // CRITICAL STEP: Replace the Activity's intent with our new one.
                // The NavController will now use this new intent for deep linking.
                intent = newIntent
            }
        }

        setContent { App() }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
