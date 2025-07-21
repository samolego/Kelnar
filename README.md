# Kelnar - Restaurant Invoice Management App

A mobile-first Kotlin Multiplatform application for managing restaurant orders and invoices. Built with Compose Multiplatform, this app works on Android, Desktop, and Web platforms with local-only data storage.

## Features

### 🍽️ Order Management
- **Landing Page**: View all orders with a clean, card-based interface
- **New Order Creation**: Easy-to-use interface for creating new orders
- **Table Number Input**: Track which table each order belongs to
- **Product Selection**: Searchable dropdown to quickly find and add products
- **Quantity Management**: Adjust item quantities with intuitive +/- buttons
- **Item Customization**: Add special instructions (e.g., "no ketchup", "extra cheese")
- **Swipe-to-Delete**: Remove items from orders with a simple swipe gesture
- **Real-time Calculation**: Automatic price calculation as you build orders
- **Order Completion**: Mark orders as completed when served

### 🛍️ Product Management
- **Product Catalog**: Manage your restaurant's menu items
- **Add/Edit Products**: Simple forms to add new products or modify existing ones
- **Product Details**: Name, price, and optional description for each item
- **Default Menu**: Comes pre-loaded with sample restaurant items

### 📱 User Interface
- **Mobile-First Design**: Optimized for mobile use with touch-friendly interfaces
- **Material Design 3**: Modern, clean UI following Google's design guidelines
- **Navigation Drawer**: Easy access to different sections of the app
- **Floating Action Buttons**: Quick access to create new orders and products
- **Responsive Layout**: Works great on phones, tablets, and desktop screens

### 💾 Data Storage
- **Local-Only Storage**: No internet connection required
- **Platform-Specific Storage**:
  - Android: SharedPreferences
  - Desktop: Local file system (`.kelnar` folder in user home)
  - Web: Browser localStorage
- **JSON Serialization**: Human-readable data format for easy backup/recovery
- **Persistent State**: All data is automatically saved and restored

## Getting Started

### Prerequisites
- Android Studio or IntelliJ IDEA with Kotlin Multiplatform plugin
- JDK 11 or higher
- For Android: Android SDK with API level 24+

### Building and Running

#### Android
```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug  # Requires connected device/emulator
```

#### Desktop
```bash
./gradlew :composeApp:run
```

#### Web (WASM)
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## App Structure

### Main Screens

1. **Orders Screen** (Landing Page)
   - Shows list of all orders
   - FAB to create new orders
   - Order cards showing table number, items count, total price
   - Mark orders as completed or delete them

2. **New Order Screen**
   - Table number input (required)
   - Product search and selection
   - Order items list with quantity controls
   - Customization options for each item
   - Real-time total calculation
   - Save order functionality

3. **Products Screen**
   - List of all available products
   - Add new products with name, price, description
   - Edit existing products
   - Delete products (with confirmation)

### Navigation
- **Drawer Menu**: Access Orders and Products screens
- **Back Navigation**: Navigate between screens
- **Deep Linking**: Support for navigation between screens

## Data Models

### Order
- ID, table number, creation timestamp
- List of order items
- Total price calculation
- Completion status

### OrderItem
- Product reference
- Quantity
- Customizations (special instructions)
- Subtotal calculation

### Product
- ID, name, price
- Optional description
- Used for building orders

## Technical Details

### Architecture
- **MVVM Pattern**: ViewModels manage state and business logic
- **Repository Pattern**: Centralized data management
- **Dependency Injection**: Manual DI for simplicity
- **Reactive UI**: StateFlow and Compose for reactive updates

### Technologies Used
- **Kotlin Multiplatform**: Share code across platforms
- **Compose Multiplatform**: Modern declarative UI framework
- **Material Design 3**: Google's latest design system
- **Navigation Compose**: Type-safe navigation
- **Kotlinx Serialization**: JSON serialization for data persistence
- **Kotlinx DateTime**: Cross-platform date/time handling
- **Kotlinx Coroutines**: Asynchronous programming

### Key Dependencies
```kotlin
implementation("org.jetbrains.androidx.navigation:navigation-compose")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
implementation("org.jetbrains.kotlinx:kotlinx-datetime")
implementation("org.jetbrains.compose.material:material-icons-extended")
```

## Usage Guide

### Creating Your First Order

1. **Open the app** - You'll see the Orders screen (initially empty)
2. **Tap the + button** to create a new order
3. **Enter table number** - This is required (e.g., "5", "A1", "Patio 3")
4. **Add products**:
   - Tap "Add Product" 
   - Search for items in the dropdown
   - Tap a product to add it to the order
5. **Customize items** (optional):
   - Tap "Customize" on any item
   - Add special instructions like "no onions"
6. **Adjust quantities** using the +/- buttons
7. **Review the total** at the bottom
8. **Tap "Save"** to create the order

### Managing Products

1. **Open the drawer menu** (☰ icon)
2. **Select "Products"**
3. **Add new products**:
   - Tap the + button
   - Enter name (required)
   - Enter price (required)
   - Add description (optional)
   - Tap "Save"
4. **Edit existing products** by tapping the edit icon
5. **Delete products** by tapping the delete icon

### Managing Orders

- **View all orders** on the main screen
- **Mark orders complete** by tapping "Complete"
- **Delete orders** by tapping the delete icon
- **Orders show**:
  - Table number
  - Number of items
  - Preview of first few items
  - Total price
  - Creation date
  - Completion status

## Sample Data

The app comes with sample products to get you started:
- Burger ($12.99)
- Hot Dog ($8.50)
- French Fries ($4.99)
- Coca Cola ($2.50)
- Pizza Margherita ($15.99)

## Customization

### Adding New Features
The app is built with extensibility in mind. You can easily:
- Add new fields to orders (customer name, phone number)
- Implement different pricing models (discounts, taxes)
- Add receipt printing functionality
- Integrate with POS systems
- Add cloud synchronization

### Modifying UI
- Colors and themes can be changed in the Material Design 3 color scheme
- Layout and spacing can be adjusted in individual screen composables
- Icons can be replaced with custom designs

## Data Location

### Android
```
/data/data/io.github.samolego.kelnar/shared_prefs/kelnar_prefs.xml
```

### Desktop
```
~/.kelnar/data.properties
```

### Web
```
Browser localStorage (accessible via browser dev tools)
```

## Troubleshooting

### Common Issues

1. **App crashes on startup**
   - Check if storage permissions are granted (Android)
   - Verify JDK version is 11+

2. **Data not persisting**
   - Check storage permissions
   - Verify localStorage is enabled (Web)

3. **Build errors**
   - Clean and rebuild: `./gradlew clean build`
   - Check Kotlin and Compose versions compatibility

### Performance Tips

- The app is optimized for local storage and should handle hundreds of orders efficiently
- Large numbers of products (1000+) may cause slower search performance
- Regular cleanup of completed orders can improve performance

## Contributing

This app is designed to be easily extensible. Key areas for contribution:
- Additional customization options for orders
- Receipt/invoice printing
- Data export functionality
- Cloud synchronization
- Multi-language support
- Advanced reporting features

## License

This project is part of a personal portfolio and demonstration of Kotlin Multiplatform capabilities.