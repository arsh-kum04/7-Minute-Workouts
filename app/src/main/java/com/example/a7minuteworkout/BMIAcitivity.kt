package com.example.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minuteworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIAcitivity : AppCompatActivity() {
    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIW = "US_UNIT_VIEW"
    }
    private var currentVisibleView: String = "METRIC_UNIT_VIEW"
    private var binding: ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }
        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId: Int ->
            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUsUnitsView()
            }
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }
    }

    private fun calculateUnits() {
        if(currentVisibleView == METRIC_UNITS_VIEW) {
            if(validateMetricUnits()){
                val heightValue: Double = binding?.etMetricUnitHeight?.text.toString().toDouble() / 100
                val weightValue: Double = binding?.etMetricUnitWeight?.text.toString().toDouble()
                val bmi = weightValue / (heightValue * heightValue)

                displayBMIResult(bmi)
            }
            else{
                Toast.makeText(
                    this ,
                    "Please enter the valid values" ,
                    Toast.LENGTH_LONG
                ).show()
            }
        }else {
            if(validateUsUnits()) {
                val feet: Float = binding?.etUsUnitsHeightFeet?.text.toString().toFloat()
                val inch: Float = binding?.etUsUnitsHeightInch?.text.toString().toFloat()

                val heightValue : Double = (((feet * 12) + inch) * 2.54) / 100
                val weightValue: Double = binding?.etMetricUnitWeight?.text.toString().toDouble()
                val bmi = weightValue / (heightValue * heightValue)

                displayBMIResult(bmi)
            }else {
                Toast.makeText(
                    this ,
                    "Please enter the valid values" ,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE

        binding?.tilUsUnitsHeightFeet?.visibility = View.INVISIBLE
        binding?.tilUsUnitsHeightInch?.visibility = View.INVISIBLE

        binding?.etMetricUnitWeight?.text?.clear()
        binding?.etMetricUnitHeight?.text?.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun makeVisibleUsUnitsView() {
        currentVisibleView = US_UNITS_VIW
        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE

        binding?.tilUsUnitsHeightFeet?.visibility = View.VISIBLE
        binding?.tilUsUnitsHeightInch?.visibility = View.VISIBLE

        binding?.etMetricUnitWeight?.text?.clear()
        binding?.etUsUnitsHeightFeet?.text?.clear()
        binding?.etUsUnitsHeightInch?.text?.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun displayBMIResult(bmi: Double){

        val bmiLabel : String
        val bmiDescription: String

        if(bmi.compareTo(15f) <= 0){
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }
        else if(bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0){
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }
        else if(bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0){
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        }
        else if(bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0){
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        }
        else if(bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0){
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout maybe!"
        }
        else if(bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0){
            bmiLabel = "Obese class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout maybe!"
        }
        else if(bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0){
            bmiLabel = "Obese class (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }
        else {
            bmiLabel = "Obese class (Very severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        val bmiValue = BigDecimal(bmi).setScale(2 , RoundingMode.HALF_EVEN).toString()

        binding?.llDisplayBMIResult?.visibility = View.VISIBLE
        binding?.BMIValue?.text = bmiValue
        binding?.BMIType?.text = bmiLabel
        binding?.BMIDescription?.text = bmiDescription
    }

    private fun validateMetricUnits() : Boolean {
        var isValid = true

        if(binding?.etMetricUnitWeight?.text.toString().isEmpty()){
            isValid = false
        }
        else if(binding?.etMetricUnitHeight?.text.toString().isEmpty()){
            isValid = false
        }

        return isValid
    }

    private fun validateUsUnits() : Boolean {
        var isValid = true

       when{
           binding?.etMetricUnitWeight?.text.toString().isEmpty()->{
               isValid = false
           }
           binding?.etUsUnitsHeightFeet?.text.toString().isEmpty()->{
               isValid = false
           }
           binding?.etUsUnitsHeightInch?.text.toString().isEmpty()->{
               isValid = false
           }
       }

        return isValid
    }
}