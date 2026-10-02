package com.example.calculimc

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculimc.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val historyAdapter = HistoryAdapter()

    // State variables for rotation preservation
    private var lastCalculatedImc: Double? = null
    private var lastCategoryText: String? = null
    private var lastColorRes: Int? = null
    private var isUnitCm: Boolean = false

    companion object {
        private const val KEY_IMC_VALUE = "key_imc_value"
        private const val KEY_CATEGORY_TEXT = "key_category_text"
        private const val KEY_COLOR_RES = "key_color_res"
        private const val KEY_UNIT_CM = "key_unit_cm"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()

        // Restore saved instance state if present (Rotation preservation)
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }

    private fun setupListeners() {
        // Toggle Unit Listener (Meters vs Centimeters)
        binding.toggleUnit.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isUnitCm = (checkedId == R.id.btnUnitCm)
                updateUnitUI()
            }
        }

        // Button Calculer Action
        binding.buttonCalculer.setOnClickListener {
            calculerIMC()
        }

        // Button Effacer Action
        binding.buttonEffacer.setOnClickListener {
            effacerChamps()
        }

        // Button Share Action
        binding.buttonShare.setOnClickListener {
            partagerResultat()
        }

        // Clear History Action
        binding.buttonClearHistory.setOnClickListener {
            historyAdapter.clearHistory()
            updateHistoryVisibility()
        }
    }

    private fun updateUnitUI() {
        if (isUnitCm) {
            binding.layoutTaille.hint = getString(R.string.label_taille) + " (cm)"
            binding.layoutTaille.suffixText = "cm"
            binding.editTextTaille.hint = "Ex: 175"
        } else {
            binding.layoutTaille.hint = getString(R.string.label_taille) + " (m)"
            binding.layoutTaille.suffixText = "m"
            binding.editTextTaille.hint = getString(R.string.hint_taille)
        }
    }

    /**
     * Primary calculation function implementing Section 4.3 & Section 5 requirement
     */
    private fun calculerIMC() {
        // Reset previous errors
        binding.layoutPoids.error = null
        binding.layoutTaille.error = null

        val poidsRaw = binding.editTextPoids.text?.toString()?.trim()
        val tailleRaw = binding.editTextTaille.text?.toString()?.trim()

        // Section 5: Check empty fields
        if (poidsRaw.isNullOrEmpty() && tailleRaw.isNullOrEmpty()) {
            val errMsg = getString(R.string.error_empty_both)
            binding.layoutPoids.error = getString(R.string.error_empty_poids)
            binding.layoutTaille.error = getString(R.string.error_empty_taille)
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            return
        }

        if (poidsRaw.isNullOrEmpty()) {
            val errMsg = getString(R.string.error_empty_poids)
            binding.layoutPoids.error = errMsg
            binding.editTextPoids.requestFocus()
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            return
        }

        if (tailleRaw.isNullOrEmpty()) {
            val errMsg = getString(R.string.error_empty_taille)
            binding.layoutTaille.error = errMsg
            binding.editTextTaille.requestFocus()
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            return
        }

        // Parse numbers safely replacing ',' with '.' for French keyboard input
        val poids = poidsRaw.replace(',', '.').toDoubleOrNull()
        var taille = tailleRaw.replace(',', '.').toDoubleOrNull()

        // Section 5: Check numeric validity
        if (poids == null || poids <= 0) {
            val errMsg = getString(R.string.error_invalid_poids)
            binding.layoutPoids.error = errMsg
            binding.editTextPoids.requestFocus()
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            return
        }

        if (taille == null || taille <= 0) {
            val errMsg = getString(R.string.error_invalid_taille)
            binding.layoutTaille.error = errMsg
            binding.editTextTaille.requestFocus()
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
            return
        }

        // Convert cm to meters if unit selected is cm
        if (isUnitCm) {
            taille /= 100.0
        }

        // Section 2: Calculate IMC formula -> IMC = poids / (taille * taille)
        val imc = poids / (taille * taille)

        // Section 3: Determine Category & Color using if / else if structure
        val (categoryText, colorRes) = meureCategorieEtCouleur(imc)

        // Display results rounded to two decimal places
        afficherResultat(imc, categoryText, colorRes)

        // Save to History (Bonus Feature)
        val record = ImcRecord(
            poidsKg = poids,
            tailleM = taille,
            imcValue = imc,
            categoryText = categoryText,
            categoryColorRes = colorRes
        )
        historyAdapter.addRecord(record)
        updateHistoryVisibility()

        Toast.makeText(this, R.string.toast_calculation_success, Toast.LENGTH_SHORT).show()
    }

    /**
     * Section 3: Determine category and color according to standard ranges
     */
    private fun meureCategorieEtCouleur(imc: Double): Pair<String, Int> {
        return when {
            imc < 18.5 -> Pair(
                getString(R.string.cat_underweight),
                R.color.imc_underweight
            ) // Insuffisance pondérale -> Orange
            imc < 25.0 -> Pair(
                getString(R.string.cat_normal),
                R.color.imc_normal
            ) // Corpulence normale -> Vert
            imc < 30.0 -> Pair(
                getString(R.string.cat_overweight),
                R.color.imc_overweight
            ) // Surpoids -> Orange
            imc < 35.0 -> Pair(
                getString(R.string.cat_obese_1),
                R.color.imc_obese_1
            ) // Obésité modérée -> Rouge
            imc < 40.0 -> Pair(
                getString(R.string.cat_obese_2),
                R.color.imc_obese_2
            ) // Obésité sévère -> Rouge
            else -> Pair(
                getString(R.string.cat_obese_3),
                R.color.imc_obese_3
            ) // Obésité morbide -> Rouge foncé
        }
    }

    /**
     * Updates the UI views with formatted calculation result
     */
    private fun afficherResultat(imc: Double, categoryText: String, colorRes: Int) {
        lastCalculatedImc = imc
        lastCategoryText = categoryText
        lastColorRes = colorRes

        val formattedImc = String.format(Locale.US, "%.2f", imc)

        binding.textViewImc.text = formattedImc
        binding.textViewCategorie.text = categoryText

        // Apply dynamic color styling
        val colorInt = ContextCompat.getColor(this, colorRes)
        binding.textViewImc.setTextColor(colorInt)
        binding.cardCategoryBadge.setCardBackgroundColor(ColorStateList.valueOf(colorInt))
        binding.textViewCategorie.setTextColor(ContextCompat.getColor(this, R.color.white))

        // Show Share button
        binding.buttonShare.visibility = View.VISIBLE
    }

    /**
     * Section 4.4: Reset all fields, results, and place focus on editTextPoids
     */
    private fun effacerChamps() {
        binding.editTextPoids.text?.clear()
        binding.editTextTaille.text?.clear()

        binding.layoutPoids.error = null
        binding.layoutTaille.error = null

        binding.textViewImc.text = getString(R.string.default_imc_value)
        binding.textViewImc.setTextColor(ContextCompat.getColor(this, R.color.primary))

        binding.textViewCategorie.text = getString(R.string.default_category)
        binding.cardCategoryBadge.setCardBackgroundColor(ColorStateList.valueOf(0xFFE0E0E0.toInt()))
        binding.textViewCategorie.setTextColor(ContextCompat.getColor(this, R.color.text_primary))

        binding.buttonShare.visibility = View.GONE

        lastCalculatedImc = null
        lastCategoryText = null
        lastColorRes = null

        // Refocus on weight input as requested by Section 4.4
        binding.editTextPoids.requestFocus()

        Toast.makeText(this, R.string.toast_fields_cleared, Toast.LENGTH_SHORT).show()
    }

    /**
     * Share calculation result via Intent
     */
    private fun partagerResultat() {
        val imc = lastCalculatedImc ?: return
        val cat = lastCategoryText ?: return
        val formattedImc = String.format(Locale.US, "%.2f", imc)

        val shareMessage = getString(R.string.share_content, formattedImc, cat)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title))
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.btn_partager)))
    }

    private fun updateHistoryVisibility() {
        if (historyAdapter.itemCount > 0) {
            binding.recyclerViewHistory.visibility = View.VISIBLE
            binding.textViewEmptyHistory.visibility = View.GONE
        } else {
            binding.recyclerViewHistory.visibility = View.GONE
            binding.textViewEmptyHistory.visibility = View.VISIBLE
        }
    }

    // --- State Retention across Rotation (Section 8) ---

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastCalculatedImc?.let { outState.putDouble(KEY_IMC_VALUE, it) }
        lastCategoryText?.let { outState.putString(KEY_CATEGORY_TEXT, it) }
        lastColorRes?.let { outState.putInt(KEY_COLOR_RES, it) }
        outState.putBoolean(KEY_UNIT_CM, isUnitCm)
    }

    private fun restoreInstanceState(savedInstanceState: Bundle) {
        isUnitCm = savedInstanceState.getBoolean(KEY_UNIT_CM, false)
        if (isUnitCm) {
            binding.toggleUnit.check(R.id.btnUnitCm)
        }

        if (savedInstanceState.containsKey(KEY_IMC_VALUE) && savedInstanceState.containsKey(KEY_CATEGORY_TEXT)) {
            val imc = savedInstanceState.getDouble(KEY_IMC_VALUE)
            val cat = savedInstanceState.getString(KEY_CATEGORY_TEXT) ?: return
            val colorRes = savedInstanceState.getInt(KEY_COLOR_RES, R.color.imc_normal)
            afficherResultat(imc, cat, colorRes)
        }
    }
}
