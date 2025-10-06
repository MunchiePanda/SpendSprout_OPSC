# SpendSprout - Personal Budget Management App

## What This App Does

SpendSprout is a personal budget management app that helps people track their money. When you open the app, you see a dashboard that shows your total balance, recent transactions, and spending patterns. The app is built for Android phones using modern programming techniques that make it fast and reliable.

## How The App Is Built

### **The Three-Layer System (MVVM)**
The app is organized into three main parts that work together:

**The Model Layer** - This is where all your data lives. It includes your bank accounts, spending categories, and transaction records. Think of it like a digital filing cabinet that stores everything about your money.

**The View Layer** - This is what you see and touch on your phone screen. It includes all the buttons, lists, and forms that you interact with. The app uses Google's Material Design system, which makes everything look modern and consistent.

**The ViewModel Layer** - This is the smart part that connects your data to what you see on screen. It takes information from the database and prepares it for display, and it handles all the business logic like calculating how much you've spent in each category.

### **The Technologies Used**
- **Kotlin 2.0.21** - This is the programming language used to write the app. It's designed to be safe and easy to read.
- **Room Database 2.7.2** - This is like a digital filing system that stores all your financial data on your phone. It's fast and reliable.
- **Hilt 2.52** - This is a system that helps different parts of the app work together smoothly by automatically connecting them.
- **Material Design 1.13.0** - This is Google's design system that makes the app look professional and easy to use.
- **Coroutines 1.8.1** - This allows the app to do multiple things at once without freezing up.
- **RecyclerView 1.4.0** - This efficiently displays long lists of transactions or categories without slowing down the app.
- **Android Gradle Plugin 8.12.3** - Modern build system for Android development
- **KSP (Kotlin Symbol Processing)** - Fast annotation processing for Room and Hilt

## How Data Is Stored

### **The Six Main Types of Information**
The app stores six different types of information about your finances:

1. **Category_Entity** - These are the main spending categories like "Needs", "Wants", and "Savings". Think of these as the big buckets where you organize your spending.

2. **Subcategory_Entity** - These are smaller, more specific categories that belong under the main categories. For example, under "Needs" you might have "Groceries", "Rent", and "Utilities".

3. **Account_Entity** - These are your actual financial accounts like your bank account, cash, or credit card. Each account has its own balance and transaction history.

4. **Expense_Entity** - These are individual transactions like "Bought groceries for R150" or "Received salary of R5000". Every time you spend or receive money, it creates one of these records.

5. **Contact_Entity** - These are people you might owe money to or who owe you money. This helps you track personal loans or debts.

6. **Budget_Entity** - These are your budget plans and goals. They help you set spending limits and track whether you're staying within your budget.

### **How The Database Works**
The database is designed to be fast and reliable. It uses something called "foreign key constraints" which means that if you delete a main category, all the subcategories under it are automatically handled properly. The database also has special indexes that make searching through your transactions very fast, even if you have thousands of them. The system also includes error handling, so if something goes wrong, the app won't crash and you won't lose your data.

## What The App Can Do

### **1. The Main Dashboard**
When you first open the app, you see a dashboard that gives you a complete picture of your finances. It shows your total balance across all accounts, displays a chart that compares your income to your expenses, lists your most recent transactions, and provides a summary of all your financial accounts. This gives you a quick overview of your financial health at a glance.

### **2. Managing Your Transactions**
The app lets you add new transactions whenever you spend or receive money. You can edit existing transactions if you made a mistake, and you can delete transactions that shouldn't be there. You can filter your transactions by type (income or expenses), by date range, or by amount. Each transaction can be linked to a specific category so you can see where your money is going, and it can be linked to a specific account so you know which account the money came from or went to.

### **3. Organizing Your Spending with Categories**
The app uses a two-level category system to help you organize your spending. The main categories are "Needs", "Wants", and "Savings". Under each main category, you can create subcategories. For example, under "Needs" you might have "Groceries", "Rent", and "Utilities". The app automatically calculates how much you've spent in each category by adding up all the transactions you've assigned to that category. You can set budget limits for each category, and the app will track whether you're staying within those limits.

### **4. Managing Multiple Accounts**
You can add multiple financial accounts to the app, such as your bank account, cash, or credit card. Each account has its own balance that updates automatically as you add transactions. You can view the complete transaction history for any account, and the app supports different types of accounts like checking accounts, savings accounts, and credit cards.

### **5. Financial Reports and Analytics**
The app includes a comprehensive reports system that provides detailed financial insights and analytics. You can view spending patterns, income trends, and budget performance through interactive charts and visualizations. The reports help you understand your financial behavior and make informed decisions about your spending.

### **6. User Settings and Preferences**
The app includes a settings screen where you can customize your experience. You can adjust display preferences, configure notifications, and manage your account preferences. The settings are designed to be intuitive and provide you with control over how the app behaves.

### **7. User Authentication**
The app includes a login system that allows users to securely access their financial data. This ensures that your personal financial information is protected and only accessible to you.

### **8. Advanced Features**
You can analyze your spending over any time period you choose by setting custom date ranges. The app uses color coding to show you at a glance whether you're over or under budget in each category. There's a consistent navigation menu that appears on every screen, making it easy to move around the app. The app includes comprehensive data validation and error handling to ensure your financial data remains accurate and secure.

## How Users Move Through The App

### **The Main User Journey**
When you use the app, you typically follow this path: You start at the main dashboard, then you can go to the category overview to see all your spending categories, then you can drill down to see specific categories, and finally you can edit or create new categories. This creates a logical flow from overview to detail to action.

### **How Navigation Works**
The app has a consistent side menu that appears on every screen, making it easy to jump to any part of the app. There are floating action buttons (the round buttons with plus signs) that let you quickly add new items like transactions or categories. The navigation is smart - it knows what screen you're on and provides appropriate options. The back button works properly, so you can always go back to where you came from.

### **What Each Screen Does**

#### **The Main Dashboard (OverviewActivity)**
This is the first screen you see when you open the app. It's like your financial command center. It shows your total balance, displays your recent transactions, and provides a summary of all your accounts. From here, you can navigate to any other part of the app using the side menu.

#### **The Category Overview (CategoryOverviewActivity)**
This screen shows you all your spending categories with a summary of how much you've spent in each one. It displays the top 3 subcategories for each main category, so you can see your most important spending areas at a glance. You can filter the data by date range to see spending over different time periods. From here, you can click on a category to see more details or add new categories.

#### **The Category Details (CategoriesActivity)**
This screen shows you the detailed view of either all your main categories or all the subcategories under a specific main category. You can filter to see only the subcategories that belong to one main category. From here, you can click on any category or subcategory to edit it, or you can add new ones.

#### **The Category Editor (EditCategoryActivity)**
This is where you create new categories or edit existing ones. You can enter the category name, choose a color, set a budget limit, and add notes. The form validates your input to make sure you don't make mistakes. When you're done, you save your changes and return to the category list.

#### **The Reports Screen (ReportsActivity)**
This screen provides comprehensive financial analytics and insights. You can view detailed reports on your spending patterns, income trends, and budget performance. The reports include interactive charts and visualizations that help you understand your financial behavior and make informed decisions about your spending.

#### **The Settings Screen (SettingsActivity)**
This screen allows you to customize your app experience. You can adjust display preferences, configure notifications, manage your account settings, and control various app behaviors. The settings are organized in a user-friendly way to make customization easy and intuitive.

#### **The Login Screen (LoginActivity)**
This is the entry point for user authentication. It provides a secure login system that protects your financial data and ensures only you can access your personal financial information. The login system is designed to be simple and secure.

#### **The Transaction Editor (EditTransactionActivity)**
This screen allows you to create new transactions or edit existing ones. You can enter transaction details like amount, description, category, and account. The form includes validation to ensure data accuracy and provides a user-friendly interface for managing your financial transactions.

#### **The Account Editor (EditAccountActivity)**
This screen allows you to create new financial accounts or edit existing ones. You can set account details like name, type, and initial balance. This helps you organize your different financial accounts and track their individual balances.

#### **The Budget Editor (EditBudgetActivity)**
This screen allows you to create and manage budget plans. You can set spending limits for different categories, track your progress, and receive alerts when you're approaching or exceeding your budget limits.

## How The App Manages Your Data

### **The Repository System**
The app uses a special system called the "Repository Pattern" to handle all database operations. This means that instead of each screen talking directly to the database, they go through special classes called repositories. There are different repositories for different types of data: one for accounts, one for categories, one for transactions, and one for budgets. This makes the code more organized and easier to maintain.

### **How Data Flows Through The App**
When you interact with the app, here's what happens: You tap something on the screen (the UI), which tells the ViewModel what you want to do. The ViewModel then asks the appropriate Repository to get or save data. The Repository talks to the DAO (Data Access Object), which actually communicates with the Room database. This creates a clean, organized flow that makes the app reliable and easy to debug.

### **What The App Does With Your Data**
The app keeps all your data synchronized in real-time, so when you add a transaction, it immediately updates all the relevant calculations. It automatically calculates how much you've spent in each category by adding up all the transactions you've assigned to that category. The app validates all your input to make sure you don't accidentally enter invalid data, and it stores everything reliably in the Room database so you won't lose your information.

## How The App Looks And Feels

### **The Design System**
The app uses Google's Material Design system, which means it looks modern and professional. It has a consistent dark theme throughout, which is easier on the eyes and looks sleek. The app uses color coding to help you understand your finances at a glance - red colors indicate expenses or overspending, while green colors show positive values or staying within budget. The layout adapts to different screen sizes, so it works well on both phones and tablets. The app is also designed with accessibility in mind, so it works well for users with different needs.

### **The Main Interface Components**
The app uses several key components to display information efficiently. The RecyclerView is used to display long lists of transactions, categories, or accounts without slowing down the app. The floating action buttons (those round buttons with plus signs) provide quick access to add new items. The navigation drawer provides a consistent menu that appears on every screen. The buttons use modern Material Design styling with proper touch feedback, so you know when you've tapped something. The app also includes custom chart components that help visualize your financial data.

## How The App Is Built Technically

### **Dependency Injection with Hilt**
The app uses Hilt 2.52 for dependency injection, which automatically manages connections between different parts of the app. The system includes:
- **DatabaseModule** - Provides database instances and DAOs
- **DataFlowModule** - Manages data flow and repository connections
- **Repository Pattern** - Centralized data access through dedicated repository classes
- **KSP Integration** - Fast annotation processing for Hilt and Room

### **Room Database Configuration**
The app uses Room Database 2.7.2 to store all your financial data with the following entities:
- **Account_Entity** - Financial accounts (bank, cash, credit cards)
- **Category_Entity** - Main spending categories (Needs, Wants, Savings)
- **Subcategory_Entity** - Specific subcategories under main categories
- **Expense_Entity** - Individual expense transactions
- **Income_Entity** - Income transactions
- **Budget_Entity** - Budget plans and limits
- **Contact_Entity** - Personal contacts for loans/debts

The database includes comprehensive error handling, data validation, and migration support.

### **The ViewModel Pattern**
The app uses ViewModels to handle the business logic and data management. For example, the CategoryViewModel has methods to load categories from the database, calculate how much has been spent in each category, and format amounts for display. This separates the business logic from the user interface, making the code more organized and easier to maintain.

## How The App Calculates Your Finances

### **Spending Calculations**
The app tracks your spending by treating expenses as negative values and income as positive values. This makes it easy to see at a glance whether you're spending or earning money. The app automatically calculates the total spending for each category by adding up all the transactions you've assigned to that category. When you have subcategories, the app rolls up the spending from all subcategories into the main category, so you can see the total impact. All these calculations happen in real-time, so when you add a new transaction, all the relevant totals update immediately.

### **Data Validation**
The app includes several layers of validation to make sure your data is accurate and consistent. It checks that required fields are filled in, validates that numeric values are entered correctly, and ensures that data formats are consistent. The app enforces business rules like budget limits and spending alerts, and it maintains data consistency across all your financial records. If something goes wrong, the app handles errors gracefully and shows you user-friendly messages instead of crashing.

## How To Get Started

### **What You Need**
To run this app, you need:
- **Android Studio** - Latest version (2024.1 or newer)
- **Android SDK** - API level 26 (Android 8.0) or higher
- **Kotlin** - Version 2.0.21 or later
- **Java** - Version 11 or higher
- **Gradle** - Version 8.12.3 or higher
- **Android Device** - Android 8.0 (API 26) or higher for running the app

### **How To Install And Run The App**
1. **Clone the Repository**: First, clone this repository to your local machine using Git.
2. **Open in Android Studio**: Open the project in Android Studio. The IDE will automatically detect the project structure and sync the Gradle files.
3. **Sync Dependencies**: Android Studio will automatically download all necessary dependencies including Room Database, Hilt, Material Design components, and Coroutines.
4. **Build the Project**: Use the "Build" menu to compile the project and ensure all dependencies are properly resolved.
5. **Run the App**: You can run the app on either:
   - A physical Android device (Android 8.0 or higher) connected via USB
   - An Android emulator with API level 26 or higher
6. **First Launch**: The app will automatically initialize the database with sample data to help you get started.

### **How The App Starts Up**
When you first run the app, it automatically sets up the database with sample data to help you get started. It creates three main categories (Needs, Wants, and Savings), adds sample subcategories under each main category, creates sample accounts and transactions, and populates everything with realistic financial data so you can see how the app works right away.

## Recent Features and Improvements

### **New Features Added**
- **Comprehensive Reports System** - Interactive charts and analytics for spending patterns and budget performance
- **User Authentication** - Secure login system to protect financial data
- **Settings Management** - Customizable app preferences and user controls
- **Enhanced Data Validation** - Improved error handling and data consistency
- **Modern Architecture** - Updated to latest Android development practices with Hilt 2.52 and Room 2.7.2

### **Technical Improvements**
- **KSP Integration** - Faster annotation processing for Room and Hilt
- **Updated Dependencies** - Latest versions of all libraries for better performance and security
- **Enhanced Database Schema** - Improved data structure with better relationships and constraints
- **Modern UI Components** - Updated Material Design components for better user experience

## What's Coming Next

### **Planned New Features**
The app is designed to grow with more features in the future. Budget alerts will notify you when you're approaching or exceeding your spending limits. You'll be able to export your transactions to CSV or PDF files for external analysis. Cloud sync will allow you to backup your data and access it across multiple devices. More advanced charts will provide even better visualizations of your financial data.

### **Technical Improvements**
The app will continue to improve technically with comprehensive unit testing to ensure reliability, performance optimizations to make database queries faster, enhanced accessibility features for users with different needs, and multi-language support for international users.

## How The Code Is Organized

### **Code Structure**
The code is organized with clear separation by feature - accounts, categories, and transactions each have their own packages. The code follows consistent Kotlin naming conventions, includes comprehensive documentation, and has robust error handling throughout. This makes the code easy to read, maintain, and extend.

### **Key Design Decisions**
The app uses MVVM architecture for clean separation of concerns, the Repository pattern for centralized data access, dependency injection with Hilt for loose coupling, and Material Design for a consistent user experience. These decisions make the app reliable, maintainable, and user-friendly.

## About This Project

This is a personal project that demonstrates modern Android development practices. The codebase serves as a reference for Room database implementation, MVVM architecture, Material Design principles, Kotlin best practices, and Android development patterns. It shows proficiency in building real-world Android applications with proper architecture and user experience design.

## License

This project is for educational and portfolio purposes, demonstrating proficiency in Android development with Kotlin, Room database, and modern Android architecture patterns.

---

**SpendSprout** - Your personal financial companion for better money management!
