package com.example.ppapb_spinnerdatetime

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Ambil data yang dikirim dari MainActivity
        val username = intent.getStringExtra("username")
        val userTime = intent.getStringExtra("time")
        val userDate = intent.getStringExtra("date")
        val userDestination = intent.getStringExtra("city")

        // Set data ke TextView yang ada di layout
        findViewById<TextView>(R.id.username).text = username
        findViewById<TextView>(R.id.usertime).text = userTime
        findViewById<TextView>(R.id.userdate).text = userDate
        findViewById<TextView>(R.id.userdestination).text = userDestination
    }
}
