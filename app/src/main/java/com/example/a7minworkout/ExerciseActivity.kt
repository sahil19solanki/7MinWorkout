package com.example.a7minworkout

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minworkout.databinding.ActivityExerciseBinding
import com.example.a7minworkout.databinding.DialogueBackbuttonBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val exercount: Long = 30
    private val restcount: Long = 10

    private var tts: TextToSpeech? = null

    private var binding: ActivityExerciseBinding? = null
    private var timer: CountDownTimer? = null
    private var timerProgress = 0

    private var exertimer: CountDownTimer? = null
    private var exertimerProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition: Int = -1

    private var rvExerAdapter: ExerAdapter? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
//        binding?.?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)\
        binding?.rvExer?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        tts = TextToSpeech(this, this)

        toolBar()
        setrestprog()

        exerciseList = Constants.defaultExrerciseList()

        setExerStatusRv()



    }

    private fun setExerStatusRv() {
        binding?.rvExer?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rvExerAdapter = ExerAdapter(exerciseList!!)

        binding?.rvExer?.adapter = rvExerAdapter
    }

    private fun setrestprog() {
        binding?.flProgressbar?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.exerTV?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.flProgressbarExer?.visibility = View.INVISIBLE
        binding?.upcomlabel?.visibility = View.VISIBLE
        binding?.upcomName?.visibility = View.VISIBLE


        if (timer != null) {
            timer?.cancel()
            timerProgress = 0

        }

        restprog()
    }

    private fun setexerprog() {
        binding?.flProgressbar?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.exerTV?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.flProgressbarExer?.visibility = View.VISIBLE
        binding?.upcomlabel?.visibility = View.INVISIBLE
        binding?.upcomName?.visibility = View.INVISIBLE
        if (exertimer != null) {
            exertimer?.cancel()
            exertimerProgress = 0

        }
        speakIt(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.exerTV?.setText(exerciseList!![currentExercisePosition].getName())
        exerprog()
    }


    private fun toolBar() {

        setSupportActionBar(binding?.toolb)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolb?.setNavigationOnClickListener {
            customDialogueForbackButton()
        }
    }

    private fun customDialogueForbackButton() {
        val dialog: Dialog = Dialog(this)
        val dialogBinding = DialogueBackbuttonBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.tvYes.setOnClickListener {
            this@ExerciseActivity.finish()
            dialog.dismiss()
        }
        dialogBinding.tvNo.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        customDialogueForbackButton()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer?.cancel()
            timerProgress = 0

        }
        if (exertimer != null) {
            exertimer?.cancel()
            exertimerProgress = 0

        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        binding = null
    }

    private fun restprog() {
        binding?.ProgressBar?.progress = timerProgress

        timer = object : CountDownTimer(restcount * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerProgress++
                binding?.ProgressBar?.progress = 10 - timerProgress
                binding?.Ctext?.text = (millisUntilFinished / 1000).toString()
                binding?.upcomName?.text = exerciseList!![currentExercisePosition + 1].getName()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                rvExerAdapter!!.notifyDataSetChanged()
                setexerprog()
            }

        }.start()
    }

    private fun exerprog() {
        exertimerProgress=0
        binding?.ProgressBarExer?.progress = exertimerProgress

        timer = object : CountDownTimer(exercount * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exertimerProgress++
                binding?.ProgressBarExer?.progress = 30 - exertimerProgress
                binding?.CtextExer?.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {

                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    rvExerAdapter!!.notifyDataSetChanged()
                    setrestprog()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }

        }.start()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Not supported")
            } else {
                Log.e("TTS", "Initialization Failed!")
            }
        }
    }

    private fun speakIt(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}