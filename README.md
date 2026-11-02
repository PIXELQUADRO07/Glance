# Glance

<img width="150" height="150" alt="Glance logo" src="https://github.com/user-attachments/assets/c1db22b7-0125-4fb6-8365-ccd160c660e2" />

Glance è un'app Android dedicata a Smart TV e Google TV per visualizzare in modo immediato le condizioni meteo, la previsione del tempo e informazioni utili in una UI pensata per il controllo remoto e la fruizione da distanza.

## Funzionalità

- Meteo in tempo reale della località associata al dispositivo
- Ricerca manuale di città e cambio rapido della posizione
- Previsioni orarie e giornaliere
- Indicazione della qualità dell'aria
- Fase lunare e informazioni sul sole
- Interfaccia ottimizzata per TV con focus e navigazione remota
- Supporto a cache/offline con salvataggio dati locali
- Aggiornamento automatico periodico

## Screenshot

> In futuro puoi aggiungere immagini reali dell'app per mostrare la UI su TV.

## Stack tecnologico

- Kotlin
- Android Jetpack
- Jetpack Compose
- Material 3
- Android TV / TV Foundation
- DataStore Preferences
- Open-Meteo API
- IPWho API
- Kotlin Serialization

## Requisiti

- Android Studio latest
- JDK 11+
- Android SDK 37
- Dispositivo o emulator Android TV / Google TV

## Configurazione

1. Clona il repository:

```bash
git clone https://github.com/PIXELQUADRO07/Glance.git
cd Glance
```

2. Apri il progetto in Android Studio.

3. Sincronizza le dipendenze Gradle.

4. Avvia l'app su un emulatore TV o su un dispositivo Android TV/Google TV.

## Esecuzione

```bash
./gradlew assembleDebug
```

oppure avvia direttamente da Android Studio.

## Struttura del progetto

```text
Glance/
├── app/
│   ├── src/main/java/com/damiano/glance/
│   │   ├── data/
│   │   ├── domain/
│   │   ├── ui/
│   │   ├── MainActivity.kt
│   │   └── ...
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── gradle.properties
├── README.md
└── gradle/
```

## Fonti dati

- Open-Meteo per previsione meteo, previsioni orarie/giornaliere e qualità dell'aria
- IPWho per individuare la posizione in base all'IP del dispositivo
- Geocoding API di Open-Meteo per la ricerca delle città

## Note

L'app usa un design pensato per Smart TV: layout ampi, card con focus visibile, pulsanti grandi e navigazione semplice da remoto.

## Licenza

Questo progetto non include ancora un file di licenza. Se vuoi, puoi aggiungere una licenza come MIT o Apache 2.0.

## Autore

Damiano

## Contributi

Se vuoi contribuire:

1. Fai un fork del progetto
2. Crea un branch per la tua modifica
3. Apri una pull request con una descrizione chiara

---
