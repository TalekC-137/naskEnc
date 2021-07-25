# naskEnc
encryption, decryption, biometrics, bluetooth

Przy pierszym uruchomieniu generowany jest klucz AES który następnie jest zapisywany w "shared preferences".
Po wprowadzeniu danych do zapisania i kliknięciu "encrypt" wprowadzone dane są szyfrowane za pomocą wygenerowanego klucza 
i zapisywane w pliku bazy danych SQLite za pomocą biblioteki SQLCipher która pomaga w kryptografii. Jeśli chcemy odczytać dane wystarczy nacisnąć przycisk "decrypt",
zostanie wyświetlona prośba o potwierdzenie odciskiem palca (odcisk musi się zgadzać z tymi zapisanymi w danych systemowych np: do odblokowania ekranu.
aplikacja nie przechowuje danych biometrycznych) a te zostaną odszyfrowane przy pomocy klucza wcześniej zapisanego w "shared preferences" 
i wyświetlone na liście recyclerview przy pomocy biblioteki "Groupie". Funkcja przesyłania danych za pomocą bluetooth kryje się za guzikiem 
z logiem bluetooth ale niestety nie miałem jak sprawdzić poprawności działania tej funkcji.
