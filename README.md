# 🎬 Android Video Player (Personal Project)

A private, MX Player–like Android video player built for personal use.

This app focuses on:

* 🔒 Privacy-first video storage
* 📱 Smooth offline playback
* 🧠 Clean architecture (MVVM)
* ⚡ High performance with Media3 (ExoPlayer)

---

# 🚀 Features

## 🎥 Playback

* Play local videos
* Pause / Resume
* Seek forward / backward
* Playback speed (0.5x – 2x)
* Resume from last position

## 🎛️ Controls

* Fullscreen mode
* Show / hide controls
* Progress bar

## ✋ Gestures (Planned / WIP)

* Swipe left/right → seek
* Swipe up/down (left) → brightness
* Swipe up/down (right) → volume

## 📝 Subtitles (Planned)

* Load external `.srt` files
* Toggle subtitles

---

# 📚 Video Library

* Load all device videos using MediaStore
* Show thumbnail, title, duration
* Sort (name, date, duration)
* Search videos
* Folder grouping (optional)

---

# 🔐 Private Vault

Secure video storage using **app-internal storage**.

### Features:

* Import video (copy into app storage)
* Not accessible by other apps
* Not visible in gallery
* No media scanning
* Separate vault UI
* Secure playback

---

# 🧱 Architecture

## Tech Stack

* Kotlin
* Android Studio
* Jetpack Media3 (ExoPlayer)
* Room Database
* MVVM Architecture
* Scoped Storage (MediaStore + filesDir)

---

## 🏗️ Project Structure

```
app/
 ├── ui/                # Activities / Compose Screens
 ├── viewmodel/        # ViewModels
 ├── repository/       # Data logic
 ├── data/
 │    ├── local/       # Room DB
 │    ├── storage/     # File handling (vault)
 ├── player/           # ExoPlayer logic
```

---

# 📂 Storage Model (IMPORTANT)

## 1. Shared Videos

* Loaded using MediaStore
* All device videos
* Public (other apps may access)

## 2. Private Vault

* Stored in `filesDir`
* App-private storage
* Not accessible by other apps
* Not scanned by media scanner

---

# 🔐 Privacy Rules

* ✅ Vault videos stored in internal storage only
* ❌ No public directories for private files
* ❌ No exposing file paths via intents
* ❌ No media scanning
* ❌ No automatic sharing/export

---

# 🧠 Database (Room)

### VideoEntity

| Field              | Description     |
| ------------------ | --------------- |
| id                 | Unique ID       |
| title              | Video title     |
| filePath           | File path       |
| duration           | Video duration  |
| lastPlayedPosition | Resume position |
| isPrivate          | Vault or shared |
| dateAdded          | Timestamp       |

---

# 🔁 Playback State

* Save playback position
* Resume automatically
* Periodic position updates

---

# 📲 Permissions

* `READ_MEDIA_VIDEO` (Android 13+)
* Scoped storage compliant
* File picker for importing videos

---

# 🗺️ Development Roadmap

## Phase 1

* [ ] Basic player (ExoPlayer)
* [ ] Play local video

## Phase 2

* [ ] Load videos (MediaStore)
* [ ] Video list UI

## Phase 3

* [ ] Private vault (copy + storage)

## Phase 4

* [ ] Resume playback (Room)

## Phase 5

* [ ] Gestures
* [ ] Subtitles
* [ ] Playback speed

## Phase 6

* [ ] UI polish
* [ ] Performance optimization

---

# 📊 Current Status

👉 **Current Phase:** ___

## ✅ Completed

* [ ]

## 🚧 In Progress

* [ ]

## ❌ Issues

* [ ]

---

# 🧪 Future Features

* Picture-in-Picture (PiP)
* Background playback
* Playlist support
* Video locking (PIN / biometric)
* Thumbnail caching
* Streaming (HLS)

---

# ⚠️ Notes

* This app is for **personal use only**
* Focus is on **privacy + performance**
* Minimal permissions, no overengineering

---

# 🤝 Contribution

Not open for public contribution (personal project).

---

# 🧠 Developer Notes

* Follow MVVM strictly
* Keep functions small & modular
* Avoid exposing private file paths
* Always respect scoped storage rules
