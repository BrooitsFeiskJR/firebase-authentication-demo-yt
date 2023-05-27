package dev.tontech.authentication_firebase_sample_yt.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.tontech.authentication_firebase_sample_yt.R
import dev.tontech.authentication_firebase_sample_yt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}