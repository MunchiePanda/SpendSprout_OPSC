# SpendSprout XML Layouts Documentation

## Overview
This document explains the XML layout structure and components used in the SpendSprout financial app. The app follows Material Design principles with a dark theme and uses modern Android layout containers.

## Core Layout Structure

### 1. Main Activity Layouts

#### `activity_transactions.xml`
**Purpose**: Displays a list of financial transactions with timeline visualization
**Structure**:
```xml
DrawerLayout (Root)
├── CoordinatorLayout
│   ├── LinearLayout (Main Content)
│   │   ├── Toolbar
│   │   └── RecyclerView (Transactions List)
│   └── FloatingActionButton (Pink - Add Transaction)
└── NavigationView (Drawer Menu)
```

**Key Features**:
- **DrawerLayout**: Enables side navigation drawer
- **CoordinatorLayout**: Proper FAB positioning and behavior
- **RecyclerView**: Efficient list rendering for transactions
- **FAB**: Pink colored (+ icon) for adding new transactions

#### `activity_categories.xml`
**Purpose**: Shows expense categories with filtering capabilities
**Structure**:
```xml
DrawerLayout (Root)
├── CoordinatorLayout
│   ├── LinearLayout (Main Content)
│   │   ├── Toolbar
│   │   ├── Filter Container (LinearLayout)
│   │   └── RecyclerView (Categories List)
│   └── FloatingActionButton (Green - Add Category)
└── NavigationView (Drawer Menu)
```

**Key Features**:
- **Filter Container**: Interactive filter button with icon and chevron
- **FAB**: Green colored for adding new categories
- **Background Colors**: Uses `@color/ItemBackgroundColor` for visual separation

#### `activity_accounts.xml`
**Purpose**: Displays user accounts with recent transactions
**Structure**:
```xml
DrawerLayout (Root)
├── CoordinatorLayout
│   ├── LinearLayout (Main Content)
│   │   ├── Toolbar
│   │   └── RecyclerView (Accounts List)
│   └── FloatingActionButton (Blue - Add Account)
└── NavigationView (Drawer Menu)
```

**Key Features**:
- **FAB**: Blue colored for adding new accounts
- **Simple Layout**: Focus on account cards with transaction history

#### `activity_wants_category.xml`
**Purpose**: Shows subcategories under a main category (e.g., Wants)
**Structure**:
```xml
DrawerLayout (Root)
├── CoordinatorLayout
│   ├── LinearLayout (Main Content)
│   │   ├── Toolbar
│   │   ├── Summary TextView
│   │   └── RecyclerView (Subcategories List)
│   └── FloatingActionButton (Orange - Add Subcategory)
└── NavigationView (Drawer Menu)
```

**Key Features**:
- **Summary TextView**: Shows category total (e.g., "- R 120")
- **FAB**: Orange colored for adding subcategories
- **Header Styling**: Uses `@style/HeaderTextStyle` for summary

### 2. Settings Layout

#### `activity_settings.xml`
**Purpose**: Modern settings screen with row-based interface
**Structure**:
```xml
ConstraintLayout (Root)
├── Header Bar (Include)
├── ScrollView
│   └── LinearLayout (Settings Rows)
│       ├── Currency Row (ConstraintLayout)
│       ├── Language Row (ConstraintLayout)
│       ├── Theme Row (ConstraintLayout)
│       ├── Security Row (ConstraintLayout)
│       ├── Notification Row (ConstraintLayout)
│       ├── About Row (ConstraintLayout)
│       └── Help Row (ConstraintLayout)
└── Back Button (ImageButton)
```

**Key Features**:
- **Row-based Design**: Each setting is a `ConstraintLayout` with label, value, and chevron
- **Background Drawable**: `@drawable/row_background` for consistent styling
- **Circular Back Button**: Bottom-right positioned with arrow icon
- **Color Scheme**: Uses app's dark theme colors

### 3. List Item Layouts

#### `transaction_layout.xml`
**Purpose**: Individual transaction item in the transactions list
**Structure**:
```xml
LinearLayout (Root)
├── Category Image (ImageView)
├── Color Indicator (View)
└── Content LinearLayout
    ├── Date TextView
    └── Details LinearLayout
        ├── Name TextView
        ├── Description TextView
        ├── Amount TextView
        └── Edit Button (ImageButton)
```

**Key Features**:
- **Timeline Visualization**: Color indicator shows transaction type
- **Edit Functionality**: Circular edit button with pencil icon
- **Responsive Layout**: Flexible text views with proper alignment

#### `category_layout.xml`
**Purpose**: Category item showing spent vs allocated amounts
**Structure**:
```xml
LinearLayout (Root)
├── Color Indicator (View)
├── Category Info (LinearLayout)
│   ├── Name TextView
│   └── Amounts LinearLayout
│       ├── Spent TextView
│       └── Allocated TextView
└── Edit Button (ImageButton)
```

**Key Features**:
- **Color Coding**: Visual indicator for category type
- **Amount Display**: Shows both spent and allocated amounts
- **Edit Access**: Direct edit button for category modification

#### `account_layout.xml`
**Purpose**: Account card with recent transactions
**Structure**:
```xml
LinearLayout (Root)
├── Account Header (LinearLayout)
│   ├── Account Name
│   ├── Balance & Limit
│   └── Edit Button
└── Recent Transactions (LinearLayout)
    ├── Transaction 1
    ├── Transaction 2
    └── Transaction 3
```

**Key Features**:
- **Card Design**: Rounded corners with background
- **Transaction History**: Shows up to 3 recent transactions
- **Color Indicators**: Each transaction has a colored dot
- **Comprehensive Info**: Balance, limit, and transaction details

#### `subcategory_layout.xml`
**Purpose**: Subcategory item under main categories
**Structure**:
```xml
LinearLayout (Root)
├── Category Image (ImageView)
├── Color Indicator (View)
└── Content LinearLayout
    ├── Spacer TextView
    └── Details LinearLayout
        ├── Name TextView
        ├── Balance TextView
        ├── Allocation TextView
        ├── Spent TextView
        ├── Allocated TextView
        └── Edit Button (ImageButton)
```

**Key Features**:
- **Hierarchical Display**: Shows subcategory under main category
- **Multiple Amounts**: Balance, allocation, spent, and allocated
- **Edit Access**: Direct editing capability

### 4. Edit Form Layouts

#### `activity_edit_transaction.xml`
**Purpose**: Form for creating/editing transactions
**Structure**:
```xml
DrawerLayout (Root)
├── LinearLayout (Main Content)
│   ├── Toolbar
│   ├── ScrollView
│   │   └── Form LinearLayout
│   │       ├── Description & Amount (Side by side)
│   │       ├── Category Spinner
│   │       ├── Date Button
│   │       ├── Account Spinner
│   │       ├── Repeat Checkbox & Spinner
│   │       ├── Owe Checkbox & Spinner
│   │       └── Notes EditText
│   └── Action Buttons (Cancel & Save)
└── NavigationView
```

**Key Features**:
- **Comprehensive Form**: All transaction fields included
- **Input Validation**: Proper input types for amounts
- **User-Friendly**: Hints and placeholders for guidance
- **Action Buttons**: Clear save/cancel options

#### `activity_edit_category.xml`
**Purpose**: Form for creating/editing categories
**Structure**:
```xml
DrawerLayout (Root)
├── LinearLayout (Main Content)
│   ├── Toolbar
│   ├── ScrollView
│   │   └── Form LinearLayout
│   │       ├── Category Name
│   │       ├── Allocated Amount
│   │       ├── Main Category Spinner
│   │       ├── Color Spinner
│   │       └── Notes EditText
│   └── Action Buttons (Cancel & Save)
└── NavigationView
```

**Key Features**:
- **Subcategory Focus**: Designed for subcategory creation
- **Color Selection**: Dropdown for category colors
- **Parent Reference**: Shows main category relationship

#### `activity_edit_account.xml`
**Purpose**: Form for creating/editing accounts
**Structure**:
```xml
DrawerLayout (Root)
├── LinearLayout (Main Content)
│   ├── Toolbar
│   ├── ScrollView
│   │   └── Form LinearLayout
│   │       ├── Account Name
│   │       ├── Account Type Spinner
│   │       ├── Balance
│   │       └── Notes EditText
│   └── Action Buttons (Cancel & Save)
└── NavigationView
```

**Key Features**:
- **Simple Form**: Essential account information only
- **Type Selection**: Dropdown for account types (Cash, Bank, etc.)
- **Balance Input**: Numeric input for initial balance

### 5. Shared Components

#### `header_bar_layout.xml`
**Purpose**: Reusable header component
**Structure**:
```xml
ConstraintLayout (Root)
└── Title TextView (Centered)
```

**Usage**: Included in all activities for consistent header display

#### `drawer_menu.xml`
**Purpose**: Navigation menu items
**Structure**:
```xml
Menu (Root)
├── Overview
├── Categories
├── Transactions
├── Accounts
├── Reports
├── Settings
└── Exit
```

### 6. Drawable Resources

#### Background Drawables
- **`row_background.xml`**: Rounded rectangle for settings rows
- **`edit_text_background.xml`**: Input field styling with borders
- **`circle_drawable.xml`**: Circular backgrounds for buttons

#### Icons
- **`ic_add.xml`**: Plus icon for FABs
- **`ic_edit.xml`**: Pencil icon for edit buttons
- **`ic_chevron_right.xml`**: Right arrow for navigation
- **`ic_arrow_left.xml`**: Left arrow for back navigation

## Color Scheme

### Primary Colors
- **Background**: `@color/BackgroundColor` (#18181A)
- **Top Bar**: `@color/TopBarColor` (#161618)
- **Items**: `@color/ItemBackgroundColor` (#212327)
- **Sub Items**: `@color/SubItemBackgroundColor` (#2E2F34)

### Text Colors
- **Main Text**: `@color/MainTextColor` (#FFFFFF)
- **Sub Text**: `@color/SubTextColor` (#505359)
- **Highlight**: `@color/HighlightTextColor` (#BAE655)

### Category Colors
- **Needs**: `@color/NeedsCategoryColor` (Green)
- **Wants**: `@color/WantsCategoryColor` (Purple)
- **Savings**: `@color/SavingsCategoryColor` (Teal)

## Layout Best Practices

### 1. Container Hierarchy
- Use `DrawerLayout` as root for navigation
- Wrap main content in `CoordinatorLayout` for FABs
- Use `ScrollView` for forms to handle keyboard

### 2. Responsive Design
- Use `layout_weight` for flexible layouts
- Implement proper margins and padding
- Consider different screen sizes

### 3. Accessibility
- Include proper content descriptions
- Use semantic color names
- Ensure touch targets are adequate size

### 4. Performance
- Use `RecyclerView` for lists
- Minimize layout nesting
- Optimize drawable resources

## Common Patterns

### 1. FAB Implementation
```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp"
    android:elevation="8dp"
    android:src="@drawable/ic_add"
    app:backgroundTint="@color/CategoryColor"
    app:tint="@android:color/white" />
```

### 2. Edit Button Pattern
```xml
<ImageButton
    android:id="@+id/btn_Edit"
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:background="@drawable/circle_drawable"
    android:src="@drawable/ic_edit"
    android:tint="@color/MainTextColor"
    android:padding="4dp" />
```

### 3. Row Layout Pattern
```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:background="@drawable/row_background"
    android:padding="16dp">
    <TextView android:id="@+id/label" />
    <TextView android:id="@+id/value" />
    <ImageView android:id="@+id/chevron" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

This layout structure provides a solid foundation for the SpendSprout app with consistent design patterns, proper navigation, and efficient list rendering.
