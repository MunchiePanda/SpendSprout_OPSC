package com.example.spendsprout_opsc

import androidx.lifecycle.MutableLiveData

object Repository {
    // Data classes
    data class Transaction(
        val description: String,
        val amount: Double,
        val date: String,
        val category: String,
        val account: String,
        val repeat: String = "None",
        val owe: String = "",
        val notes: String = ""
    )

    data class SubCategory(
        val name: String,
        val amount: Double
    )

    data class Category(
        val name: String,
        val current: Double,
        val limit: Double,
        val subCategories: List<SubCategory>
    )

    data class Account(
        val name: String,
        val balance: Double,
        val limit: Double,
        val recentTransactions: List<Transaction>
    )

    // Dummy data
    val transactions: MutableLiveData<List<Transaction>> = MutableLiveData(
        listOf(
            Transaction("Petrol", -1500.0, "10 August 2025", "Needs", "Cash"),
            Transaction("Mug n Bean", -360.0, "08 August 2025", "Wants", "FNB Next Transact"),
            Transaction("Salary", 20000.0, "25 July 2025", "Income", "FNB Next Transact"),
            Transaction("Groceries", -850.0, "05 August 2025", "Needs", "FNB Next Transact"),
            Transaction("McDonalds", -110.0, "08 August 2025", "Wants", "Cash"),
            Transaction("Birthday Gift", -500.0, "12 August 2025", "Wants", "FNB Next Transact"),
            Transaction("Electricity", -1200.0, "01 August 2025", "Needs", "FNB Next Transact"),
            Transaction("Movie Tickets", -300.0, "15 August 2025", "Wants", "Cash")
        )
    )

    val categories: MutableLiveData<Map<String, Category>> = MutableLiveData(
        mapOf(
            "Needs" to Category(
                "Needs",
                8900.0,
                10000.0,
                listOf(
                    SubCategory("Rent", 3500.0),
                    SubCategory("Gas", 1500.0),
                    SubCategory("Groceries", 360.0),
                    SubCategory("Utilities", 1200.0)
                )
            ),
            "Wants" to Category(
                "Wants",
                -120.0,
                6000.0,
                listOf(
                    SubCategory("Eating Out", 110.0),
                    SubCategory("Entertainment", 6010.0),
                    SubCategory("Shopping", 500.0)
                )
            ),
            "Savings" to Category(
                "Savings",
                4000.0,
                4000.0,
                listOf(
                    SubCategory("Rainy Day", 2500.0),
                    SubCategory("Investments", 1500.0)
                )
            ),
            "Income" to Category(
                "Income",
                20000.0,
                0.0,
                listOf()
            )
        )
    )

    val accounts: MutableLiveData<List<Account>> = MutableLiveData(
        listOf(
            Account(
                "Cash",
                160.0,
                1900.0,
                listOf(
                    Transaction("Recent Transaction 1", -3500.0, "", "", ""),
                    Transaction("Recent Transaction 2", -1500.0, "", "", ""),
                    Transaction("Recent Transaction 3", 360.0, "", "", "")
                )
            ),
            Account(
                "FNB Next Transact",
                1720.0,
                15000.0,
                listOf(
                    Transaction("Recent Transaction 1", -3500.0, "", "", ""),
                    Transaction("Recent Transaction 2", -1500.0, "", "", ""),
                    Transaction("Recent Transaction 3", -400.0, "", "", "")
                )
            )
        )
    )

    // Helper functions
    fun addTransaction(transaction: Transaction) {
        val currentList = transactions.value?.toMutableList() ?: mutableListOf()
        currentList.add(0, transaction)
        transactions.value = currentList

        // Update category totals
        updateCategoryTotals()
    }

    fun updateTransaction(index: Int, transaction: Transaction) {
        val currentList = transactions.value?.toMutableList() ?: mutableListOf()
        if (index in currentList.indices) {
            currentList[index] = transaction
            transactions.value = currentList
            updateCategoryTotals()
        }
    }

    fun addAccount(account: Account) {
        val currentList = accounts.value?.toMutableList() ?: mutableListOf()
        currentList.add(account)
        accounts.value = currentList
    }

    fun updateAccount(index: Int, account: Account) {
        val currentList = accounts.value?.toMutableList() ?: mutableListOf()
        if (index in currentList.indices) {
            currentList[index] = account
            accounts.value = currentList
        }
    }

    fun addCategory(category: Category) {
        val currentMap = categories.value?.toMutableMap() ?: mutableMapOf()
        currentMap[category.name] = category
        categories.value = currentMap
    }

    fun updateCategory(categoryName: String, category: Category) {
        val currentMap = categories.value?.toMutableMap() ?: mutableMapOf()
        currentMap[categoryName] = category
        categories.value = currentMap
    }

    private fun updateCategoryTotals() {
        val currentTransactions = transactions.value ?: return
        val currentCategories = categories.value?.toMutableMap() ?: return

        // Reset all category totals
        for (category in currentCategories.values) {
            category.current = 0.0
        }

        // Calculate totals based on transactions
        for (transaction in currentTransactions) {
            if (currentCategories.containsKey(transaction.category)) {
                val category = currentCategories[transaction.category]!!
                category.current += transaction.amount
            }
        }

        categories.value = currentCategories
    }
}
