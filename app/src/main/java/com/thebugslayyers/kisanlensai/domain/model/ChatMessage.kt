package com.thebugslayyers.kisanlensai.domain.model

import java.util.UUID

/**
 * A single turn in the farmer's chat with Kisan AI.
 *
 * [id] uses a UUID rather than a timestamp because a user turn and the assistant's reply can be
 * created within the same millisecond, and the id is used as a stable list key.
 *
 * Chat is session-only and deliberately not persisted, so this model has no serialization
 * annotations.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean
)
