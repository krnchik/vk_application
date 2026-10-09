package com.example.vk_application

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.vk_application.ui.theme.Vk_applicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Vk_applicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    var message by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Поле для текста ---
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Введите текст") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // --- Поле для телефона ---
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Введите номер телефона") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // =============================================
        // 1. Явный Intent — открыть вторую Activity
        // =============================================
        Button(
            onClick = {
                val trimmed = message.trim()
                if (trimmed.isEmpty()) {
                    Toast.makeText(context, "Введите текст", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val intent = Intent(context, SecondActivity::class.java).apply {
                    putExtra("EXTRA_MESSAGE", trimmed)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Открыть вторую Activity")
        }

        // =============================================
        // 2. Неявный Intent — звонок (ACTION_DIAL)
        // =============================================
        Button(
            onClick = {
                val trimmed = phone.trim()
                if (trimmed.isEmpty()) {
                    Toast.makeText(context, "Введите номер телефона", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!Patterns.PHONE.matcher(trimmed).matches()) {
                    Toast.makeText(context, "Неверный формат номера", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:$trimmed".toUri()
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Приложение для звонков не найдено", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Позвонить другу")
        }

        // =============================================
        // 3. Системный Intent — поделиться (ACTION_SEND)
        // =============================================
        Button(
            onClick = {
                val trimmed = message.trim()
                if (trimmed.isEmpty()) {
                    Toast.makeText(context, "Введите текст для отправки", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, trimmed)
                }
                val chooser = Intent.createChooser(intent, "Поделиться через")
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(chooser)
                } else {
                    Toast.makeText(context, "Нет приложений для отправки", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Поделиться текстом")
        }
    }
}