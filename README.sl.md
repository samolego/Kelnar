# Kelnar 🍽️

Aplikacija za upravljanje naročil na (gasilskih) veselicah, v restavracijah, ipd. zgrajena s Kotlin Multiplatform in Compose Multiplatform.

## Pregled

Kelnar pomaga osebju upravljati naročila in jedi na napravah Android, računalniku ali preko spleta. Vsi podatki so shranjeni lokalno - internetna povezava ni potrebna. Podpira tudi deljenje menija z ostalimi natakarji preko QR kod ali povezav.

## Zaslonske slike 📱

| <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_orders.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_new-order.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_order-detail.png"> |
|:---:|:---:|:---:|
| Naročila | Novo naročilo | Podatki naročila |
| <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_products.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_share.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_complete-order.png"> |
| Produkti | Deljenje | Končanje naročila |

## Platforme 📱

- **Android** (API 24+) 🤖
- **Desktop** (JVM) 💻
- **Web** (WebAssembly) 🌐

## Ključne Funkcionalnosti

- Ustvarjanje in upravljanje naročil z številkami miz
- Dodajanje izdelkov iz iskalnega menija
- Sledenje aktivnim in končanim naročilom
- Upravljanje jedi restavracije
- Deljenje menija preko QR kod in povezav 📲
- Uvoz/izvoz izdelkov med napravami 🔄
- Lokalno shranjevanje podatkov (brez odvisnosti od oblaka) 💾

## Začetek Dela 🚀

### Predpogoji

- JDK 11+ ☕
- Android Studio ali IntelliJ IDEA z Kotlin Multiplatform vtičnikom

### Zagon Aplikacije

**Android:**
```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

**Desktop:**
```bash
./gradlew :composeApp:run
```

**Web:**
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## Uporaba 📖

1. **Zavihek Naročila** - Pregled aktivnih in končanih naročil
2. **Ustvari Naročilo** - Dodaj številko mize in izberi jedi
3. **Zavihek Izdelki** - Upravljaj jedi in cene
4. **Deli Meni** - Generiraj QR kode in deljive povezave
5. **Uvozi Izdelke** - Skeniraj QR kode ali uporabi povezave za uvoz jedi
6. **Spletne Poti** - Neposreden dostop do različnih razdelkov

## Shranjevanje Podatkov

- **Android**: SharedPreferences 📱
- **Desktop**: Lokalne datoteke v `~/.kelnar/` 🗂️
- **Web**: Browser localStorage 🌐

## Tehnološki Sklop ⚡

- Kotlin Multiplatform
- Compose Multiplatform
- Material Design 3
- Navigation Compose
- Kotlinx Serialization
- Generiranje QR Kod

## Struktura Projekta 🏗️

```
composeApp/src/
├── commonMain/        # Skupna koda 🔗
├── androidMain/       # Android-specifična 🤖
├── desktopMain/       # Desktop-specifična 💻
└── wasmJsMain/        # Web-specifična 🌐
```

## Licenca 📝

Osebni projekt za predstavitev Kotlin Multiplatform razvoja.
