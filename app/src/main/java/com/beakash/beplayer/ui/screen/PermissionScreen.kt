package com.beakash.beplayer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionScreen(
    onGrantPermission: () -> Unit,
    onPickWithDocumentPicker: () -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Grant video access to load your device library.")

        Button(
            onClick = onGrantPermission,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Grant Video Permission")
        }

        Button(
            onClick = onPickWithDocumentPicker,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Pick Single Video Instead")
        }

        errorMessage?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}