package raf.rs.rma_projekat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import raf.rs.rma_projekat.core.theme.RMAProjekatTheme
import raf.rs.rma_projekat.navigation.Navigation
import raf.rs.rma_projekat.user.ProfileDataStore
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var profileDataStore: ProfileDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RMAProjekatTheme {
                Navigation(profileDataStore)
            }
        }
    }
}

