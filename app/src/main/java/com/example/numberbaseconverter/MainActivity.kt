package com.example.numberbaseconverter

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.numberbaseconverter.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

const val AllNumbers = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            createListOfBases()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.startBaseSpinner.adapter = adapter
        }

        ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            createListOfBases()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.targetBaseSpinner.adapter = adapter
        }

        binding.startNumber.setOnClickListener{
            binding.finalNumber.text = ""
        }

        binding.calculateButton.setOnClickListener {
            val startBase = binding.startBaseSpinner.selectedItem.toString().toBigInteger()
            val targetBase = binding.targetBaseSpinner.selectedItem.toString().toBigInteger()
            val startNumber = binding.startNumber.text.toString()

            binding.finalNumber.text = doConversion(startBase, targetBase, startNumber)
        }
    }
}

fun createListOfBases(): List<BigInteger> {
    val listOfBases = mutableListOf<BigInteger>()
    for (n in 2..36) {
        listOfBases += n.toBigInteger()
    }
    return listOfBases.toList()
}

fun doConversion(startBase: BigInteger, targetBase: BigInteger, startNumber: String): String {
    val firstPartOfNumber = getFirstPartOfNumber(startNumber)
    val decimalResultFirstPart = commonConversionToDecimal(firstPartOfNumber.uppercase(), startBase)
    val targetResultFirstPart = commonConversionFromDecimal(decimalResultFirstPart, targetBase)
    var targetNumber = targetResultFirstPart

    if (targetResultFirstPart.isEmpty()) {
        targetNumber += "0"
    }

    if (isFractionalNumber(startNumber)) {
        val decimalReminderResult =
            commonConversionReminderToDecimal(startNumber, startBase.toBigDecimal())
        val targetReminder =
            commonConversionReminderFromDecimal(decimalReminderResult, targetBase.toBigDecimal())
        targetNumber += ".$targetReminder"
    }

    return targetNumber.lowercase()
}

fun getFirstPartOfNumber(number: String): String {
    return number.split(".").first()
}

fun getReminderPartOfNumber(number: String): String {
    return number.split('.').last()
}

fun isFractionalNumber(number: String): Boolean {
    return number.contains('.')
}

fun commonConversionToDecimal(sourceNumber: String, sourceBase: BigInteger): BigInteger {
    var dec = BigInteger("0")
    for (index in sourceNumber.indices) {
        val symbol = sourceNumber[index].uppercase()
        val n = AllNumbers.indexOf(symbol)
        val pow = sourceNumber.length - index - 1
        val c = sourceBase.pow(pow)
        dec += (n.toBigInteger() * c)
    }

    return dec
}

fun commonConversionFromDecimal(number: BigInteger, base: BigInteger): String {
    val zero = BigInteger("0")
    var dec = number
    var targetNumber = ""
    while (dec != zero) {
        val rem = dec % base
        val symbol = AllNumbers[rem.toInt()]
        targetNumber += symbol
        dec /= base
    }

    return targetNumber.reversed()
}

fun commonConversionReminderToDecimal(number: String, sourceBase: BigDecimal): BigDecimal {
    var dec = BigDecimal.ZERO
    val one = BigDecimal(1.0)
    val sourceReminderPart = getReminderPartOfNumber(number)
    for (index in sourceReminderPart.indices) {
        val symbol = sourceReminderPart[index].uppercase()
        val n = AllNumbers.indexOf(symbol)
        val pow = index + 1
        val basePow = sourceBase.pow(pow)
        val c = one.divide(basePow, 10, RoundingMode.HALF_UP)
        dec += (n.toBigDecimal() * c)
    }

    return dec.divide(BigDecimal.ONE, 5, RoundingMode.HALF_UP)
}

fun commonConversionReminderFromDecimal(number: BigDecimal, targetBase: BigDecimal): String {
    val intNumber = number.toInt()
    var reminderDec = number - intNumber.toBigDecimal()
    var reminderTarget = "0."

    repeat(5) {
        val n = reminderDec * targetBase
        val firstPart = getFirstPartOfNumber(n.toString())
        val symbolFirstPart = AllNumbers[firstPart.toInt()]
        reminderTarget += symbolFirstPart
        reminderDec = n - firstPart.toBigDecimal()
    }

    return getReminderPartOfNumber(reminderTarget)
}

