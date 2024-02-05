package com.example.test

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://randomuser.me/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.Main) {
            val response = service.getRandomUsers(60)

            if (response.isSuccessful) {
                val randomUserResponse = response.body()
                val userList: List<RandomUser> = randomUserResponse?.results ?: emptyList()

                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, userList.map { "${it.name.first} ${it.name.last}" })

                val listView: ListView = findViewById(R.id.listView)
                listView.adapter = adapter
            } else {
                // Обработка ошибки
            }
        }
    }

    data class RandomUserResponse(val results: List<RandomUser>)

    data class RandomUser(val gender: String, val name: Name, val email: String)

    data class Name(val title: String, val first: String, val last: String)

    interface ApiService {
        @GET("/api")
        suspend fun getRandomUsers(@Query("results") results: Int): Response<RandomUserResponse>
    }
}