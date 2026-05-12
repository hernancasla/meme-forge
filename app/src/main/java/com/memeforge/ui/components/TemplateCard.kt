package com.memeforge.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.memeforge.data.model.MemeTemplate

@Composable
fun TemplateCard(
    template: MemeTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(template.imageUrl)
                    .allowHardware(false)
                    .crossfade(true)
                    .build(),
                contentDescription = template.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                contentScale = ContentScale.Fit
            )
            Text(
                text = template.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
