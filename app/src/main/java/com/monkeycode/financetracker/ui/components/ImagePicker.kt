package com.monkeycode.financetracker.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ImagePicker(
    selectedImagePath: String?,
    onImageSelected: (String) -> Unit,
    onImageRemoved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = saveUriToFiles(context, it)
            onImageSelected(path)
        }
    }
    
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "图片",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row {
                    if (selectedImagePath != null) {
                        AsyncImage(
                            model = selectedImagePath,
                            contentDescription = "已上传图片",
                            modifier = Modifier
                                .size(80.dp)
                                .border(1.dp, Color.Gray)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(onClick = onImageRemoved) {
                            Text(
                                text = "删除",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        Button(
                            onClick = { galleryLauncher.launch("image/*") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("拍照")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = { galleryLauncher.launch("image/*") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("相册")
                        }
                    }
                }
            }
            
            if (selectedImagePath == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .border(
                            1.dp,
                            Color.Gray.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "点击拍照或从相册选择",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun saveUriToFiles(context: Context, uri: Uri): String {
    val fileName = "IMG_${System.currentTimeMillis()}.jpg"
    val imagesDir = File(context.filesDir, "images")
    if (!imagesDir.exists()) {
        imagesDir.mkdirs()
    }
    val outputFile = File(imagesDir, fileName)
    
    context.contentResolver.openInputStream(uri)?.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    
    return outputFile.absolutePath
}
