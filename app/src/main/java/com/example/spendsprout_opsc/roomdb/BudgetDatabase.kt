package com.example.spendsprout_opsc.roomdb
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(  entities = [Category_Entity::class,
                        Subcategory_Entity::class,
                        Expense_Entity::class,
                        Contact_Entity::class,
                        Account_Entity::class,
                        Budget_Entity::class],
                    version = 4,
                    exportSchema = false)
abstract class BudgetDatabase : RoomDatabase()
{
    abstract fun categoryDao(): Category_DAO
    abstract fun subcategoryDao(): Subcategory_DAO
    abstract fun expenseDao(): Expense_DAO
    abstract fun contactDao(): Contact_DAO
    abstract fun accountDao(): Account_DAO
    abstract fun budgetDao(): Budget_DAO
}

//Create an instance of this database:
//  val db = Room.databaseBuilder(applicationContext, BudgetDatabase::class.java, "database-name").build()

//Use the DAOs:
//  val categoryDao = db.categoryDao()
//  val categories: List<Category_Entity> = categoryDao.getAll()