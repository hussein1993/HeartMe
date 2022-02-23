package com.example.heartme

import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SubmitBloodTestResults : AppCompatActivity() {
    lateinit var testName : EditText
    lateinit var testValue: EditText
    var res : String? = null
    lateinit var testResult : TextView

    lateinit var outcome_layout : LinearLayout
    lateinit var submit :Button
    lateinit var img_result : ImageView
    lateinit var result_diagnosis : TextView
    var responseList :ArrayList<DataResponse> = ArrayList<DataResponse>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_blood_test_results)

        testName = findViewById<EditText>(R.id.test_name_text_id)
        testValue = findViewById(R.id.res_text_id)
        testResult = findViewById(R.id.result_text)
        outcome_layout = findViewById(R.id.res_layout)
        img_result = findViewById(R.id.image_res)
        result_diagnosis = findViewById(R.id.res_diagnosis)
        submit = findViewById(R.id.submit_btn)
        outcome_layout.visibility = View.INVISIBLE
        submit.setOnClickListener {
            submitTest()
        }
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute(Runnable {
            parseJson()
            handler.post(Runnable {
                //UI Thread work here
            })
        })





    }

    override fun onResume() {
        super.onResume()
    }
    fun submitTest(){
       val stringTestName = testName.text.toString()
       try {
           val resVal = testValue.text.toString().toInt()
           checkTest(stringTestName,resVal)
       }catch(e : Exception){
           e.printStackTrace()
       }
    }

    fun checkTest(testName : String , testVal :Int){
        var index =-1
        var max =0
        for (i in 0..responseList.size-1){
            val currCount = responseList.get(i).isSimilarTest(testName)
            if(currCount > max){
                max = currCount
                index = i
            }
        }

         handleUiResult(index, testVal)

    }

    private fun handleUiResult(index: Int,testVal: Int) {
        outcome_layout.visibility = View.VISIBLE
        if(index >-1) {
            var currTest = responseList.get(index)
            testResult.text = currTest.name
            img_result.visibility = View.VISIBLE
            result_diagnosis.visibility = View.VISIBLE
            val resVal = currTest.threshold?.let {
                if(testVal > it){
                    result_diagnosis.text = "Bad!"
                    result_diagnosis.setTextColor(Color.RED)
                    img_result.setImageResource(R.drawable.ic_sad_smiley___white)
                } else{
                    result_diagnosis.text = "Good!"
                    result_diagnosis.setTextColor(Color.BLUE)
                    img_result.setImageResource(R.drawable.ic_button___happy)
                }
            }

        }else{
            testResult.text = "Unkown"
            img_result.visibility = View.INVISIBLE
            result_diagnosis.visibility = View.INVISIBLE
            Toast.makeText(this,"Unkown Test",Toast.LENGTH_LONG)
        }
    }

    fun parseJson(){
        val queue = Volley.newRequestQueue(this)
        val url = "https://s3.amazonaws.com/s3.helloheart.home.assignment/bloodTestConfig.json"
        val jsonReq = StringRequest(Request.Method.GET,url, { response ->
            res = response.toString()
            val jObj = JSONObject(res)
            val jArray = jObj.getJSONArray("bloodTestConfig")
            for( i in 0..jArray.length()-1 ){
                val currJson = jArray.getJSONObject(i)
                val name = currJson.get("name").toString()
                val threshold = currJson.get("threshold").toString()
                responseList.add(DataResponse(name,threshold.toInt()))
            }
        }, { error ->
                error.printStackTrace()
        })
        queue.add(jsonReq)
    }
}