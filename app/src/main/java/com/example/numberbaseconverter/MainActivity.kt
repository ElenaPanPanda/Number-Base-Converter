package com.example.numberbaseconverter

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.numberbaseconverter.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

const val AllNumbers = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

class Bases() {
    fun createListOfBases(): List<BigInteger> {
        val listOfBases = mutableListOf<BigInteger>()
        for (n in 2..36) {
            listOfBases += n.toBigInteger()
        }
        return listOfBases.toList()
    }
}

class SymbolsOfNumber() {

    fun getAllCorrectSymbols(): List<Char> {
        val listSymbols = mutableListOf<Char>()

        for (c in '0'..'9') listSymbols.add(c)
        for (c in 'A'..'Z') listSymbols.add(c)
        for (c in 'a'..'z') listSymbols.add(c)
        listSymbols.add('.')

        return listSymbols
    }

    fun getListNumbersInARow(): List<Char> {
        val listNumbers = mutableListOf<Char>()

        for (c in '0'..'9') listNumbers.add(c)
        for (c in 'A'..'Z') listNumbers.add(c)

        return listNumbers
    }

    fun getListOfSymbolsThisBase(base: Int): List<Char> {
        val listNumbers = getListNumbersInARow()
        val symbolsToBase = mutableListOf<Char>()

        for (i in 0 until base) {
            symbolsToBase.add(listNumbers[i])
        }

        return symbolsToBase
    }
}


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listOfBases = Bases().createListOfBases()

        ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            listOfBases
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.startBaseSpinner.adapter = adapter
            val selection = 10
            val spinnerPosition: Int = adapter.getPosition(selection.toBigInteger())
            binding.startBaseSpinner.setSelection(spinnerPosition)
        }

        ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            listOfBases
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.targetBaseSpinner.adapter = adapter
        }

        binding.startNumber.setOnClickListener {
            binding.finalNumber.text = ""
        }

        // final number disappear when start number is edited

        // add check first number: correct symbols, number of dots, correspond to base

        // change button and targetNumber in xml

        // create pretty spinners

        // change app icon

        // change app theme

        // add chose to upper or lower letters

        // add share button

        binding.swap.setOnClickListener {
            val positionStartBaseBefore = binding.startBaseSpinner.selectedItemPosition
            val positionTargetBaseBefore = binding.targetBaseSpinner.selectedItemPosition
            binding.startBaseSpinner.setSelection(positionTargetBaseBefore)
            binding.targetBaseSpinner.setSelection(positionStartBaseBefore)
        }

        binding.calculateButton.setOnClickListener {
            val startBase = binding.startBaseSpinner.selectedItem.toString().toBigInteger()
            val targetBase = binding.targetBaseSpinner.selectedItem.toString().toBigInteger()
            val startNumber = binding.startNumber.text.toString()

            binding.finalNumber.text = doConversion(startBase, targetBase, startNumber)
        }
    }
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

fun getFirstPartOfNumber(number: String): String = number.split(".").first()

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

fun checkStartNumber(number: String, base: Int, listOfBases: List<BigInteger>): Boolean {


    if (!isAllSymbolsAreCorrect(number)) {
        //show toast
        //show validation
        //return false
    }

    if (!isNullOrOneDots(number)) {
        //show toast
        //show validation
        //return false
    }

    if (!isNumberCorrespondsBase(number, base, listOfBases)) {
        //show toast
        //show validation
        //return false
    }

    return true
}

fun isAllSymbolsAreCorrect(number: String): Boolean {
    val listOfCorrectSymbols = SymbolsOfNumber().getAllCorrectSymbols()

    for (symbol in number) {
        if (!listOfCorrectSymbols.contains(symbol)) return false
    }

    return true
}

fun isNullOrOneDots(number: String): Boolean {
    val numberOfDots = number.count { it == '.' }
    if (numberOfDots == 0 || numberOfDots == 1) return true

    return false
}

fun isNumberCorrespondsBase(number: String, base: Int, listOfBases: List<BigInteger>): Boolean {
    val listCorrectSymbolsThisBase = SymbolsOfNumber().getListOfSymbolsThisBase(base)

    for (symbol in number) {
        if (symbol != '.') {
            if (!listCorrectSymbolsThisBase.contains(symbol.uppercaseChar())) return false
        }
    }

    return true
}


