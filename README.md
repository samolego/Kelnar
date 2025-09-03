# Kelnar 🍽️

A cross-platform restaurant order management app built with Kotlin Multiplatform and Compose Multiplatform.

*[Slovenščina](README.sl.md) | English*

[**DEMO**](https://samolego.github.io/Kelnar/#menu/import?data=%5BFanta%3B2.5%3B0.25%20l%7CBurger%3B6.3%3BBeef%2C%20onions%2C%20tomato%7CIce%20Tea%3B2.5%3B0.5%20l%5D)

## Overview

The Kelnar application allows you to keep track of guest orders. All data is stored on the device - no internet connection is required. It also supports sharing the menu with other waiters via QR codes or links.
The application allows you to add products (e.g., food that you sell) and then create bills. The total price automatically updates when adding products.

## App helps you with
* Creating and managing orders with table numbers
* Adding items from the searchable menu
* Tracking active and completed orders
* Adding, deleting, and editing items in the menu
* Sharing the menu via QR codes and links

## Screenshots 📱

| <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_orders.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_new-order.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_order-detail.png"> |
|:---:|:---:|:---:|
| Orders | New Order | Order Details |
| <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_products.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_share.png"> | <img width="256" src="metadata/en-US/images/phoneScreenshots/screenshot_kelnar_complete-order.png"> |
| Products | Share | Complete Order |

## Platforms

- **Android** (API 24+) 🤖
- **Desktop** (JVM) 💻
- **Web** (WebAssembly) 🌐

## Development

### Prerequisites

- JDK 11+
- Android Studio (recommended) or IntelliJ IDEA with Kotlin Multiplatform plugin

### Running the App

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
