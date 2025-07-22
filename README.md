# Kelnar - Restaurant Invoice Management App

A mobile-first Kotlin Multiplatform application for managing restaurant orders and invoices. Built with Compose Multiplatform, this app works on Android, Desktop, and Web platforms with local-only data storage.

## Features

### 🍽️ Order Management
- **Landing Page**: View all orders with tabs for Active and Completed orders
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

### 🌐 Web Features
- **URL Routing**: Direct access to pages via URLs (e.g., `/orders`, `/products`, `/menu`)
- **Bookmarkable URLs**: All pages can be bookmarked and accessed directly
- **SPA Routing**: Single Page Application with client-side routing
- **History API**: Full browser back/forward button support
- **Guest Menu**: Public read-only menu at `/menu` for customers
- **Shareable Links**: Direct links to specific sections of the app
- **Dynamic Page Titles**: Browser tab titles update based on current page

### 📱 User Interface
- **Mobile-First Design**: Optimized for mobile use with touch-friendly interfaces
- **Material Design 3**: Modern, clean UI with custom styled navigation bars
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

### Web URLs and Routing
When running the web version, you can access different sections directly via URLs. All routes are bookmarkable and work with browser navigation:

- **`/`** or **`/orders`** - Orders management (staff only)
- **`/new-order`** - Create new order (staff only)
- **`/products`** - Product management (staff only)
- **`/menu`** - Public guest menu (read-only for customers)

#### Routing Features:
- **Direct Access**: Bookmark any URL and return to that exact page
- **Browser Navigation**: Back/forward buttons work as expected
- **Page Refresh**: URLs persist through page refreshes
- **Deep Linking**: Share specific pages with others
- **SPA Fallback**: All unknown routes redirect to the orders page

## App Structure

### Main Screens

1. **Orders Screen** (Landing Page) - `/orders`
   - Two tabs: "Active" and "Completed" orders
   - Active tab is selected by default
   - Tab badges show count of orders in each category
   - FAB to create new orders
   - Enhanced order cards with better styling and layout
   - Mark orders as completed or delete them
   - Styled navigation bar with primary color theme

2. **New Order Screen** - `/new-order`
   - Table number input (required first field)
   - Product search and selection
   - Order items list with quantity controls
   - Item customization options for each item
   - Real-time total calculation
   - Save order functionality

3. **Products Screen** - `/products`
   - List of all available products
   - Add new products with name, price, description
   - Edit existing products
   - Delete products (with confirmation)

4. **Guest Menu Screen** - `/menu` (Web Only)
   - Public-facing read-only menu for customers
   - Beautiful card-based layout showcasing menu items
   - No edit controls or management features
   - Perfect for sharing with customers or displaying on tablets

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

1. **Open the app** - You'll see the Orders screen with two tabs
   - "Active" tab (default) - shows pending orders
   - "Completed" tab - shows finished orders
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

- **View orders** organized in two tabs:
  - **Active Orders**: Current pending orders (default view)
  - **Completed Orders**: Finished orders
- **Tab badges** show the count of orders in each category
- **Mark orders complete** by tapping the "Complete" button
- **Delete orders** by tapping the delete icon
- **Enhanced order cards** show:
  - Table number with prominent styling
  - Number of items and creation date
  - Preview of first few items
  - Total price in highlighted badge
  - Completion status with visual indicators
  - Improved card elevation and spacing

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
- Navigation bar styling is customized with primary color theme
- Layout and spacing can be adjusted in individual screen composables
- Icons can be replaced with custom designs
- Tab styling and badges can be customized in OrdersScreen

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

3. **Web routing not working**
   - Ensure you're accessing the development server URL (usually http://localhost:8080)
   - Check that JavaScript is enabled in your browser
   - Clear browser cache and refresh
   - For bookmarked URLs, make sure the development server is running

4. **Build errors**
   - Clean and rebuild: `./gradlew clean build`
   - Check Kotlin and Compose versions compatibility
   - Delete `.gradle` and `build` directories if cache issues persist

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
