# Android-PictureDiary

This app uses a long running background service and the Camera2 API to periodically take pictures when the phone is in use. It then analyzes the picture using the Microsoft Cognitive Face API and categorizes it based on Emotion and Time. It uses Room for the Database Management which is an interface built on top of SQLite.
