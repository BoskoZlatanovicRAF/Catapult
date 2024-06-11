package raf.rs.rma_projekat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import raf.rs.rma_projekat.core.theme.RMAProjekatTheme
import raf.rs.rma_projekat.navigation.Navigation
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RMAProjekatTheme {
                Navigation()
            }
        }
    }
}

