package com.example.spendsprout_opsc.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.SBMH.SpendSprout.model.Category
import com.SBMH.SpendSprout.model.Expense
import com.example.spendsprout_opsc.overview.model.ChartDataPoint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ReportsViewModel : ViewModel() {

    private val _monthlySpent = MutableLiveData<Double>()
    val monthlySpent: LiveData<Double> = _monthlySpent

    private val _dailySpendSeries = MutableLiveData<List<ChartDataPoint>>()
    val dailySpendSeries: LiveData<List<ChartDataPoint>> = _dailySpendSeries

    private val _categoryTotals = MutableLiveData<List<Pair<Category, Double>>>()
    val categoryTotals: LiveData<List<Pair<Category, Double>>> = _categoryTotals

    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadMonthlySpent(startDate: Long, endDate: Long) {
        if (userId == null) return

        val expensesRef = database.getReference("users/$userId/expenses")
        expensesRef.orderByChild("date").startAt(startDate.toDouble()).endAt(endDate.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val total = snapshot.children.sumOf { it.child("amount").getValue(Double::class.java) ?: 0.0 }
                    _monthlySpent.postValue(total)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun loadDailySpendSeries(startDate: Long, endDate: Long, monthlyTarget: Double) {
        if (userId == null) return

        val expensesRef = database.getReference("users/$userId/expenses")
        expensesRef.orderByChild("date").startAt(startDate.toDouble()).endAt(endDate.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expenses = snapshot.children.mapNotNull { it.getValue(Expense::class.java) }
                    val days = getDaysInRange(startDate, endDate)
                    val perDayTarget = if (days.isNotEmpty() && monthlyTarget > 0) monthlyTarget / days.size else 0.0

                    val dailySum = expenses.groupBy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.date)) }
                        .mapValues { (_, list) -> list.sumOf { it.amount } }

                    val series = days.map { dayMillis ->
                        val key = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dayMillis))
                        val spent = dailySum[key] ?: 0.0
                        ChartDataPoint(
                            month = key,
                            revenue = spent,
                            target = perDayTarget
                        )
                    }
                    _dailySpendSeries.postValue(series)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun loadCategoryTotals(startDate: Long, endDate: Long) {
        if (userId == null) return

        val expensesRef = database.getReference("users/$userId/expenses")
        val categoriesRef = database.getReference("users/$userId/categories")

        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(categorySnapshot: DataSnapshot) {
                val categories = categorySnapshot.children.mapNotNull { it.getValue(Category::class.java) }
                expensesRef.orderByChild("date").startAt(startDate.toDouble()).endAt(endDate.toDouble())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(expenseSnapshot: DataSnapshot) {
                            val expenses = expenseSnapshot.children.mapNotNull { it.getValue(Expense::class.java) }
                            val categoryTotals = categories.map { category ->
                                val total = expenses.filter { it.category == category.name }.sumOf { it.amount }
                                Pair(category, total)
                            }
                            _categoryTotals.postValue(categoryTotals)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun getDaysInRange(start: Long, end: Long): List<Long> {
        val days = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.timeInMillis = start
        while (cal.timeInMillis <= end) {
            // normalize to midnight
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            days.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
}
