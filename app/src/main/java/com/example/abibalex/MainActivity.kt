package com.example.abibalex

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.abibalex.ui.theme.ABIBALEXTheme
import com.google.accompanist.permissions.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ABIBALEXTheme {
                var phoneNo by remember { mutableStateOf("") }
                var message by remember { mutableStateOf("") }
                val context = LocalContext.current

                GetPermission() // Request necessary permissions

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = "SMS APP",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Blue // Changed to Blue
                        )
                    )

                    OutlinedTextField(
                        value = phoneNo,
                        onValueChange = { phoneNo = it },
                        label = { Text(text = "Phone No.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black,
                            errorBorderColor = if (isValidPhoneNumber(phoneNo)) Color.White else MaterialTheme.colorScheme.error, // Added error color
                            errorLabelColor = if (isValidPhoneNumber(phoneNo)) Color.Black else MaterialTheme.colorScheme.error,   // Added error color
                            errorSupportingTextColor = MaterialTheme.colorScheme.error // Added
                        ),
                        textStyle = TextStyle(color = Color.Black),
                        isError = !isValidPhoneNumber(phoneNo), // Set error state
                        supportingText = {  //Added
                            if (!isValidPhoneNumber(phoneNo)) {
                                Text(
                                    text = "Invalid phone number format",
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }

                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text(text = "Message") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle(color = Color.Black)
                    )

                    Button(
                        onClick = {
                            if (isValidPhoneNumber(phoneNo) && message.isNotBlank()) { //check for valid phone number
                                sendSms(phoneNo, message, context = context)
                            } else {
                                Toast.makeText(context, "Please enter a valid phone number and message.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(top = 5.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                    ) {
                        Text(
                            text = "Send",
                            style = TextStyle(fontSize = 20.sp, color = Color.White)
                        )
                    }
                }
            }
        }
    }
}

fun sendSms(phoneNo: String, message: String, context: Context) {
    try {
        val smsManager = context.getSystemService(SmsManager::class.java)
            ?: throw IllegalStateException("SmsManager not available")

        smsManager.sendTextMessage(phoneNo, null, message, null, null)
        Toast.makeText(context, "Message Sent Successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error sending message: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetPermission() {
    val permissions = mutableListOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.SEND_SMS
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        permissions.add(Manifest.permission.READ_PHONE_NUMBERS)
    }

    val multiplePermissions = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        multiplePermissions.launchMultiplePermissionRequest()
    }
}

fun isValidPhoneNumber(phoneNo: String): Boolean {
    return phoneNo.isEmpty() || phoneNo.matches(Regex("^\\+?[0-9]{4,13}$"))
}
