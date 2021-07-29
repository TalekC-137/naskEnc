# naskEnc
encryption, decryption, biometrics, bluetooth

On the first app launch, an AES key is generated and stored in shared preferences.
After entering the data and tapping "encrypt", the entered data is encrypted using the generated key
and saved to a SQLite database file using the SQLCipher library which helps with cryptography. To read the data, just press the "decrypt" button,
a fingerprint confirmation request will be displayed (the fingerprint must match those saved in the system data, e.g. to unlock the screen.
the application does not store biometric data) and these will be decrypted using the key previously saved in shared preferences
and displayed in the recyclerview list using "Groupie" library. The data transfer function via bluetooth is hidden behind the button
with bluetooth logo, but unfortunately I had no way to check if it works.
