package com.thebugslayyers.kisanlensai.feature.chat

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.thebugslayyers.kisanlensai.R
import com.thebugslayyers.kisanlensai.domain.model.ChatMessage
import com.thebugslayyers.kisanlensai.domain.model.ChatSeed
import com.thebugslayyers.kisanlensai.domain.model.CropRegion
import com.thebugslayyers.kisanlensai.domain.model.CropSeason
import com.thebugslayyers.kisanlensai.domain.model.Language
import com.thebugslayyers.kisanlensai.ui.theme.KisanLensTheme

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    currentLanguage: Language,
    isSending: Boolean,
    isListening: Boolean,
    speakingMessageId: String?,
    chatSeed: ChatSeed?,
    errorMessage: String?,
    onSendMessage: (String) -> Unit,
    onStartVoiceInput: () -> Unit,
    onStopVoiceInput: () -> Unit,
    onSpeakMessage: (ChatMessage) -> Unit,
    onStopSpeaking: () -> Unit,
    onNewChat: () -> Unit,
    onDismissError: () -> Unit,
    onError: (String) -> Unit
) {
    var draft by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            onStartVoiceInput()
        } else {
            onError("Microphone permission is needed for voice input.")
        }
    }

    // Keep the newest message (or the typing row) in view.
    val itemCount = messages.size + if (isSending) 1 else 0
    LaunchedEffect(itemCount) {
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 20.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.chat_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.chat_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (messages.isNotEmpty()) {
                IconButton(onClick = onNewChat) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.chat_new_chat),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        chatSeed?.let { seed ->
            val bannerText = when (seed) {
                is ChatSeed.Scan -> stringResource(id = R.string.chat_seeded_banner)
                is ChatSeed.Season -> stringResource(
                    id = R.string.chat_seeded_season_banner,
                    seed.season.getLocalizedName(currentLanguage)
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = bannerText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        if (messages.isEmpty()) {
            EmptyChatContent(
                onSuggestionClick = { suggestion ->
                    draft = ""
                    onSendMessage(suggestion)
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(
                        message = message,
                        isSpeaking = message.id == speakingMessageId,
                        onSpeak = { onSpeakMessage(message) },
                        onStopSpeaking = onStopSpeaking
                    )
                }

                if (isSending) {
                    item(key = "typing") { TypingIndicator() }
                }
            }
        }

        errorMessage?.let { message ->
            ErrorBanner(message = message, onDismiss = onDismissError)
        }

        if (isListening) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = stringResource(id = R.string.chat_listening),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }

        ChatInputBar(
            draft = draft,
            onDraftChange = { draft = it },
            isSending = isSending,
            isListening = isListening,
            onSend = {
                val toSend = draft.trim()
                if (toSend.isNotEmpty()) {
                    draft = ""
                    onSendMessage(toSend)
                }
            },
            onMicClick = {
                if (isListening) {
                    onStopVoiceInput()
                } else if (hasMicPermission) {
                    onStartVoiceInput()
                } else {
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        )
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onStopSpeaking: () -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.88f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                color = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }

            // Only the assistant's answers can be read aloud.
            if (!isUser) {
                TextButton(onClick = { if (isSpeaking) onStopSpeaking() else onSpeak() }) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSpeaking) {
                            stringResource(id = R.string.chat_stop_audio)
                        } else {
                            stringResource(id = R.string.chat_read_aloud)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = R.string.chat_thinking),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyChatContent(
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.chat_empty_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.chat_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        val suggestions = listOf(
            stringResource(id = R.string.chat_suggestion_1),
            stringResource(id = R.string.chat_suggestion_2),
            stringResource(id = R.string.chat_suggestion_3),
            stringResource(id = R.string.chat_suggestion_4)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { suggestion ->
                AssistChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.chat_dismiss),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    draft: String,
    onDraftChange: (String) -> Unit,
    isSending: Boolean,
    isListening: Boolean,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = onMicClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isListening) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
                    contentColor = if (isListening) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = stringResource(id = R.string.chat_mic)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.chat_input_hint),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                enabled = !isSending
            )

            Spacer(modifier = Modifier.width(10.dp))

            FilledIconButton(
                onClick = onSend,
                enabled = draft.isNotBlank() && !isSending,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(id = R.string.chat_send)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenEmptyPreview() {
    KisanLensTheme {
        ChatScreen(
            messages = emptyList(),
            currentLanguage = Language.HINDI,
            isSending = false,
            isListening = false,
            speakingMessageId = null,
            chatSeed = null,
            errorMessage = null,
            onSendMessage = {},
            onStartVoiceInput = {},
            onStopVoiceInput = {},
            onSpeakMessage = {},
            onStopSpeaking = {},
            onNewChat = {},
            onDismissError = {},
            onError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenConversationPreview() {
    KisanLensTheme {
        ChatScreen(
            messages = listOf(
                ChatMessage(text = "मेरे टमाटर की पत्तियों पर काले धब्बे हैं, क्या करूं?", isUser = true),
                ChatMessage(
                    text = "यह अगेती झुलसा हो सकता है। प्रभावित पत्तियां तोड़कर नष्ट करें और पौधों के बीच हवा का आवागमन बढ़ाएं।",
                    isUser = false
                )
            ),
            currentLanguage = Language.HINDI,
            isSending = false,
            isListening = false,
            speakingMessageId = null,
            chatSeed = ChatSeed.Season(CropSeason.RABI, CropRegion.PENINSULAR_INDIA),
            errorMessage = null,
            onSendMessage = {},
            onStartVoiceInput = {},
            onStopVoiceInput = {},
            onSpeakMessage = {},
            onStopSpeaking = {},
            onNewChat = {},
            onDismissError = {},
            onError = {}
        )
    }
}
