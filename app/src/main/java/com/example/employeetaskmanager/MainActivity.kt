package com.example.employeetaskmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        val buttonTaskDetails = findViewById<Button>(R.id.buttonTaskDetails)

        buttonTaskDetails.setOnClickListener {
            val intent = Intent(this, TaskDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}
