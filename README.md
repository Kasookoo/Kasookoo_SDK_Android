# 📱 Kasookoo SDK (Android)

📘 Overview
This SDK enables real-time voice calling between users (e.g., Customer ↔ Driver) using webrtc server for audio and FCM for push notifications. It handles authentication, user filtering, call setup, and room connection — providing a complete ready-to-use solution for in-app voice communication.
## What’s included



- Session management
- Webrtc integration with resilient audio setup (caller and callee symmetric)
- FCM-based incoming call notifications with strict routing rules
- In-call UI (mute, speaker), Ringing UI, and automatic state transitions
- SIP-based Support calls (make/end) with dynamic `room_name`

## Requirements

- Android 7.0+ (API 24), Target SDK 34
- Kotlin 1.9+
- A Firebase project with valid `google-services.json`

## Setup (summary)

1) Dependencies (Gradle): Webrtc, Firebase Messaging, Retrofit/OkHttp, Coroutines.
2) Permissions: INTERNET, ACCESS_NETWORK_STATE, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS, POST_NOTIFICATIONS.
3) Firebase: add `google-services.json`, apply `com.google.gms.google-services` plugin.

## Key modules/files

- `ui/MainActivity.kt`: Role-based entry, call initiation, global call type
- `ui/RingingActivity.kt`: Incoming/outgoing ringing, token fetch for callee, auto-accept when launched from notification action, no accept UI in that path
- `ui/CallActivity.kt`: In-call controls (mute, speaker), end-call UX
- `service/KasookooFirebaseMessagingService` (file name `FirebaseMessagingService.kt`): Receives FCM and routes notifications per rules
- `service/CallActionReceiver.kt`: Accept/Decline from notification; passes `auto_accept=true` for instant connect path
- `data/ApiService.kt` + `data/ApiClient.kt`: Retrofit endpoints for caller/called tokens, registration and update, support make/end
- `data/UserDataManager.kt` + `data/FirebaseTokenManager.kt`: Local user data and robust FCM token generation with retry/backoff

## Call flows
Setup
1. Initialize the SDK
   VoiceCallSDK.initialize(context, Key = "")


📞 Start a Call
To initiate a call:
VoiceCallSDK.startCall(userId) { callSession ->
// callSession contains webrtc server URL and token
}

This automatically:
Requests a caller token:


Connects to webrtc server room


Sends an FCM notification to the recipient



📲 Call Lifecycle
End or Reject Call:
VoiceCallSDK.endCall(roomId)
VoiceCallSDK.rejectCall()


🛎️ Support Call (Optional)
If the logged-in user is a customer, support calls are also available:
VoiceCallSDK.connectToSupport()



## Notification routing rules

- `customer_incoming_call` → shown on Driver devices only
- `driver_incoming_call` → shown on Customer devices only
- We inspect local stored role to decide whether to show/suppress
- Accept from notification sets `auto_accept=true`: the driver’s accept UI is hidden; the screen shows Connecting, then Connected

## Authentication and FCM token registration




## UI/UX notes

- Ringing (driver, accepted via notification): accept UI removed automatically
- Background updated to white/green theme; driver’s Support card hidden; logout icon updated
- Speaker control: real toggle with icons; default speaker ON in-call and restored on exit

### Handling Incoming Calls


## Disconnect behavior

- End call sets local state to `IDLE` immediately and disconnects the  room; the remote peer is notified by server
- Callee path ensures global call type is set so the End button always works
- If call type is already cleared, the in-call screen finishes gracefully



## Troubleshooting

- Notifications go to the wrong device: verify FCM type and local stored role; routing suppresses mismatched roles
- Called token failure due to lifecycle cancellation: Ringing uses a SupervisorJob scope and suppresses finish until token arrives
- “Call already ended” when ending from callee: fixed by setting global call type on join; screen now finishes even if type is cleared

## Version

- Version: 1.1.0
- Last Updated: August 2025
- Min Android: API 24
- Target Android: API 34

## 🧪 Testing

### Single Device Testing

### Common Issues

**Audio Not Working**
```kotlin
// Check audio permissions and setup
private fun verifyAudioSetup(): Boolean {
    val hasPermission = ContextCompat.checkSelfPermission(
        this, 
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
    
    val audioMode = audioManager.mode == AudioManager.MODE_IN_COMMUNICATION
    
    return hasPermission && audioMode
}
```

**Connection Failures**
```kotlin
// Monitor connection status
Webrtc.roomConnectionStatus.collect { status ->
    when (status) {
        RoomConnectionStatus.ERROR -> {
            showError("Connection failed. Check internet connection.")
        }
        RoomConnectionStatus.DISCONNECTED -> {
            showError("Call disconnected. Attempting to reconnect...")
            retryConnection()
        }
    }
}
```

**Notifications Not Received**
```kotlin
// Verify notification setup
private fun checkNotificationPermissions(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
}
```



**Version**: 1.0.0  
**Last Updated**: July 2025  
**Minimum Android Version**: API 24 (Android 7.0)  
**Target Android Version**: API 34 (Android 14) 