/*
MIT License

Copyright (c) 2022 Stɑrry Shivɑm

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.starry.greenstash.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.starry.greenstash.R
import com.starry.greenstash.database.Transaction
import com.starry.greenstash.utils.AppConstants
import com.starry.greenstash.utils.formatCurrency
import com.starry.greenstash.utils.visible

class InfoLVAdapter(private val context: Context, private val transactions: List<Transaction>) :
    BaseAdapter() {

    private val settingPerf = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getCount(): Int {
        return transactions.size
    }

    override fun getItem(p0: Int): Any {
        return transactions[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val defCurrency = settingPerf.getString("currency", "")
        val transaction = transactions[position]
        val transactionView =
            LayoutInflater.from(context).inflate(R.layout.transaction_row, parent, false)
        val transactionText = transactionView.findViewById<TextView>(R.id.transactionText)
        val transactionDate = transactionView.findViewById<TextView>(R.id.transactionDate)
        val transactionNotes = transactionView.findViewById<TextView>(R.id.transactionNotes)
        // set transaction details and notes (if available)
        if (transaction.transactionType == AppConstants.TRANSACTION_DEPOSIT) {
            transactionText.text = "${context.getString(R.string.info_deposited)} | $defCurrency${
                formatCurrency(transaction.amount)
            }"
        } else {
            transactionText.text = "${context.getString(R.string.info_withdrawn)} | $defCurrency${
                formatCurrency(transaction.amount)
            }"
        }
        if (!transaction.notes.isNullOrEmpty()) {
            transactionNotes.text = transaction.notes
            transactionNotes.visible()
        }
        transactionDate.text = transaction.date
        return transactionView

    }

}
