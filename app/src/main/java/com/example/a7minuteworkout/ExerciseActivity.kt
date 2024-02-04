package com.example.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkout.databinding.ActivityExerciseBinding
import com.example.a7minuteworkout.databinding.CustomDialogBackConfirmationBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosittion = -1
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarExercise)

        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolBarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this , this)



        setupRestView()
        setUpExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = CustomDialogBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }

    private fun setUpExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false)
            exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setupRestView(){

        try {
            val soundURI = Uri.parse("android.resource://com.example.a7minuteworkout/" +
                    R.raw.app_src_main_res_raw_press_start)
            player = MediaPlayer.create(applicationContext , soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flprogressBar?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.upcomingExercise?.visibility = View.VISIBLE
        binding?.exerciseName?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExercise?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        binding?.exerciseName?.text = exerciseList!![currentExercisePosittion + 1].getName()

        setRestProgressBar()
    }

    private fun setupExerciseView(){
        binding?.flprogressBar?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.upcomingExercise?.visibility = View.INVISIBLE
        binding?.exerciseName?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExercise?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosittion].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosittion].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosittion].getName()

        setExerciseProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(10000 , 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = (10 - restProgress)
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosittion++
               exerciseList!![currentExercisePosittion].setIsSelected(true)
               exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }

        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(30000 , 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = (30 - exerciseProgress)
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {


               if(currentExercisePosittion < exerciseList?.size!! - 1){
                   exerciseList!![currentExercisePosittion].setIsSelected(false)
                   exerciseList!![currentExercisePosittion].setIsCompleted(true)
                   exerciseAdapter!!.notifyDataSetChanged()
                   setupRestView()
               }else{
                   finish()
                   val intent = Intent(this@ExerciseActivity , FinishActivity::class.java)
                   startActivity(intent)
               }
            }

        }.start()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){

            val result = tts?.setLanguage(Locale.ENGLISH)

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS" , "The language specified is not supported")
            }
        }else{
            Log.e("TTS" , "Initialization Failed")
        }
    }

    private fun speakOut(text: String ){
        tts!!.speak(text , TextToSpeech.QUEUE_FLUSH ,null ,"")
    }

    override fun onDestroy() {
        super.onDestroy()

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
            //Shutting down the Text to Speach feature when activity is destroyed
        if(tts != null){
            tts?.stop()
            tts?.shutdown()
        }
        //Shutting down the media player
        if(player != null){
            player!!.stop()
        }

        binding = null
    }


}