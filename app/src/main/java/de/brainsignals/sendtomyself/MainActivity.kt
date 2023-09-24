//This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

package de.brainsignals.sendtomyself

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var editTextEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextEmail = findViewById(R.id.editTextEmail)

        loadEmail()
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            handleSendText(intent)
        }
    }

    private fun loadEmail() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("UserEmail", "") // Default to an empty string if not set

        editTextEmail.setText(savedEmail)
    }

    fun sendEmail(view: View) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("UserEmail", "") ?: ""

        if (userEmail.isNotEmpty()) {
            sendEmail(userEmail, "Your shared content", "Here's some content you shared.")
        } else {
            Toast.makeText(this, "No email apps available", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendEmail(to: String, subject: String, content: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, content)
        }

        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(this, "No email apps available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        val title = (intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: "")+(intent.getStringExtra(Intent.EXTRA_TITLE) ?: "")

        val subject = "${title} ${sharedText.take(96)}..." // Take first 30 characters for a summary

        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("UserEmail", "") ?: ""

        if (userEmail.isNotEmpty()) {
            sendEmail(userEmail, subject, sharedText)
        } else {
            Toast.makeText(this, "No email apps available", Toast.LENGTH_SHORT).show()
        }
    }


    fun saveEmail(view: View) {
        val email = findViewById<EditText>(R.id.editTextEmail).text.toString()

        if (email.isNotEmpty()) {  // You can add more validation checks if needed
            val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("UserEmail", email)
            editor.apply()

            Toast.makeText(this, "Email saved successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter a valid email!", Toast.LENGTH_SHORT).show()
        }
    }
}