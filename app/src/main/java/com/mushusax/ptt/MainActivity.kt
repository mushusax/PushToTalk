package com.mushusax.ptt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.time.Duration

class MainActivity : ComponentActivity() {

    private val client = HttpClient(OkHttp) {
        install(WebSockets) {
            pingInterval = 500
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PTTButton()
        }
    }

    @Preview
    @Composable
    fun PTTButton() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { sendHello() })
            { Text("PTT") }
            Button(onClick = { close() })
            { Text("close") }
        }
    }

    private fun sendHello() {
        runBlocking {
            client.ws(
                method = HttpMethod.Get,
                host = "10.0.0.46",
                port = 8080,
                path = "/chat"
            ) {
                while(true) {
                    val othersMessage = incoming.receive() as? Frame.Text ?: continue
                    println(othersMessage.readText())
                    val myMessage = "Hello"
                    if(myMessage != null) {
                        send(myMessage)
                    }
                }
            }
        }
        client.close()
    }

    private fun close() {
        client.close()
    }
}