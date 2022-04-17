package com.project.myproduction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.myproduction.auth.LoginActivity
import com.project.myproduction.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        Timer().schedule(3500) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}