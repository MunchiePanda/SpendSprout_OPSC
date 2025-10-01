package com.example.spendsprout_opsc

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * SubcategoryView: A custom view that encapsulates the subcategory_layout.xml.
 * This is similar to a Unity prefab with a script attached to it.
 */
class SubcategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {  // Inherits from LinearLayout, like a Unity GameObject with a RectTransform

    // UI elements from subcategory_layout.xml (like public fields in a Unity script)
    private val txtName: TextView
    private val txtBalance: TextView
    private val imgCategory: ImageView

    init {
        // Inflate the layout (like instantiating a prefab in Unity)
        LayoutInflater.from(context).inflate(R.layout.subcategory_layout, this, true)

        // Get references to views (like GetComponent<> or assigning fields in the Inspector)
        txtName = findViewById(R.id.txt_Name)
        txtBalance = findViewById(R.id.txt_Balance)
        imgCategory = findViewById(R.id.img_Category)
    }

    /**
     * Set the name text.
     * @param name The name to display.
     * Similar to updating a Text component in Unity.
     */
    fun setName(name: String) {
        txtName.text = name
    }

    /**
     * Get the name text.
     * @return The current name text.
     * Similar to retrieving a Text component's value in Unity.
     */
    fun getName(): String {
        return txtName.text.toString()
    }

    /**
     * Set the balance text and update its color based on whether it's positive or negative.
     * @param balance The balance to display.
     * Similar to updating a Text component and its color in Unity.
     */
    fun setBalance(balance: Double) {
        txtBalance.text = "R${"%.2f".format(balance)}"  // Format the balance like "R100.00"
        val balanceColor = if (balance >= 0) {
            R.color.PositiveBalanceColor  // Green for positive balances
        } else {
            R.color.NegativeBalanceColor  // Red for negative balances
        }
        txtBalance.setTextColor(ContextCompat.getColor(context, balanceColor))  // Set the text color
    }

    /**
     * Get the balance value.
     * @return The current balance value.
     * Similar to parsing a Text component's value in Unity.
     */
    fun getBalance(): Double {
        return txtBalance.text.toString().replace("R", "").trim().toDouble()  // Remove "R" and convert to Double
    }

    /**
     * Set the category and tint the category image.
     * @param category The category to set.
     * Similar to changing a sprite's color in Unity.
     */
    fun setCategory(category: String) {
        val categoryColor = when (category) {
            "Needs" -> R.color.NeedsSubCategoryColor
            "Wants" -> R.color.WantsSubCategoryColor
            "Savings" -> R.color.SavingsSubCategoryColor
            else -> R.color.IconColor  // Default color
        }
        imgCategory.setColorFilter(ContextCompat.getColor(context, categoryColor))  // Tint the image
    }
}
