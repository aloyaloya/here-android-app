package ru.aloyaloya.here

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.here.ui.HereApp

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (application as HereApplication).appComponent

        viewModel = appComponent.viewModelFactory.create(MainViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            HereTheme {
                HereApp()
            }
        }
    }
}