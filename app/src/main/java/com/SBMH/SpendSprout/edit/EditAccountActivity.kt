package com.SBMH.SpendSprout.edit

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.SBMH.SpendSprout.R
import com.SBMH.SpendSprout.model.Account
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditAccountActivity : AppCompatActivity() {

    private lateinit var viewModel: EditAccountViewModel
    private lateinit var accountNameEditText: TextInputEditText
    private lateinit var accountBalanceEditText: TextInputEditText
    private lateinit var saveButton: MaterialButton

    private var accountId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        viewModel = ViewModelProvider(this).get(EditAccountViewModel::class.java)

        accountNameEditText = findViewById(R.id.edt_AccountName)
        accountBalanceEditText = findViewById(R.id.edt_AccountBalance)
        saveButton = findViewById(R.id.btn_SaveAccount)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        accountId = intent.getStringExtra("accountId")

        if (accountId != null) {
            supportActionBar?.title = "Edit Account"
            loadAccountDetails(accountId!!)
        } else {
            supportActionBar?.title = "Add Account"
        }

        saveButton.setOnClickListener {
            val accountName = accountNameEditText.text.toString()
            val accountBalance = accountBalanceEditText.text.toString().toDoubleOrNull() ?: 0.0

            if (accountName.isNotBlank()) {
                if (accountId != null) {
                    viewModel.updateAccount(accountId!!, accountName, accountBalance)
                    Toast.makeText(this, "Account updated", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.saveAccount(accountName, accountBalance)
                    Toast.makeText(this, "Account saved", Toast.LENGTH_SHORT).show()
                }
                finish()
            } else {
                Toast.makeText(this, "Please enter an account name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAccountDetails(accountId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val accountRef = FirebaseDatabase.getInstance().getReference("users/${currentUser.uid}/accounts/$accountId")
            accountRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val account = snapshot.getValue(Account::class.java)
                    if (account != null) {
                        accountNameEditText.setText(account.accountName)
                        accountBalanceEditText.setText(account.balance.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditAccountActivity, "Failed to load account details", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
