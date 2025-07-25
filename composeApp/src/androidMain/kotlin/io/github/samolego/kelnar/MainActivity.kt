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
import io.github.samolego.kelnar.ui.navigation.ProductsImport
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        AppContext.init(this)

        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data

        Log.d("MainActivity", "appLinkAction: $appLinkAction")
        Log.d("MainActivity", "appLinkData: $appLinkData")

        setContent {
            App(
                    onNavHostReady = { navController ->
                        appLinkData?.let { uri ->
                            val fragment = uri.fragment?.removePrefix("/") ?: ""
                            val path = fragment.split("?")[0]
                            val queryParams = fragment.substringAfter("?", "")

                            Log.d("MainActivity", "Parsed path: $path")
                            Log.d("MainActivity", "Query params: $queryParams")

                            if (path == "products/import") {
                                // Check for data parameter
                                val fragmentUri = Uri.parse("kelnar://$fragment")
                                val importData =
                                        fragmentUri.getQueryParameter("data")?.let { encodedData ->
                                            Log.d("MainActivity", "encoded data: $encodedData")
                                            URLDecoder.decode(
                                                    encodedData,
                                                    StandardCharsets.UTF_8.toString()
                                            )
                                        }
                                                ?: ""

                                Log.d("MainActivity", "Import data: $importData")
                                navController.navigate(ProductsImport(importData))
                            }
                        }
                    }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
