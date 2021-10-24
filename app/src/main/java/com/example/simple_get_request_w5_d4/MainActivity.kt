package com.example.simple_get_request_w5_d4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.simple_get_request_w5_d4.Constants.url
import com.example.simple_get_request_w5_d4.Constants.urlTag
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAPI()
    }

    private fun requestAPI(){
        CoroutineScope(IO).launch {
            Log.d("MAIN", "fetch advice")
            val advice = async { fetchAdvice() }.await()
            if(advice?.isNotEmpty() == true){ updateTextView(advice) }
            else{ Log.d("MAIN", "Unable to get data") }
        }
    }


    private fun fetchAdvice(): Names?{
        Log.d("MAIN", "went inside fetch")
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        val call: Call<Names> = apiInterface!!.getAdvice()
        var advice : Names? = null
        try {
            val response = call.execute()
            advice = response.body()
            Log.d("MAIN", "read advice")
        }
        catch (e: Exception){Log.d("MAIN", "ISSUE: $e")}


        Log.d("MAIN", "advice is ${advice.toString()}")
        return advice
    }

    private suspend fun updateTextView(advice: Names?) {
        withContext(Main){
            val tv = findViewById<TextView>(R.id.tv)
            tv.text = advice.toString()
        }
    }
}


object Constants{
    const val url = "https://dojo-recipes.herokuapp.com/"
    const val urlTag = "people/"
}
interface APIInterface {
    @GET(urlTag)
    fun getAdvice(): Call<Names>
}
class APIClient {
    private var retrofit : Retrofit? = null
    fun getClient() : Retrofit? {
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}