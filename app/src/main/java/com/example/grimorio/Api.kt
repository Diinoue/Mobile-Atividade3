package com.example.grimorio

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Envelope padrão da API: { "data": [ ... ] }
data class Resposta<T>(@SerializedName("data") val dados: List<T>?)

data class Personagem(
    @SerializedName("name") val nome: String?,
    @SerializedName("species") val especie: String?,
    @SerializedName("house") val casa: String?
)

data class Criatura(
    @SerializedName("name") val nome: String?,
    @SerializedName("image") val imagem: String?
)

// Interface da API (mesmo padrão da CatFactApi)
interface HogwartsApi {
    @GET("api/characters")
    suspend fun getPersonagens(@Query("search") nome: String): Resposta<Personagem>

    @GET("api/creatures")
    suspend fun getCriaturas(@Query("search") nome: String): Resposta<Criatura>
}

fun criarApi(): HogwartsApi = Retrofit.Builder()
    .baseUrl("https://hogwarts-api.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(HogwartsApi::class.java)

// Validação antes de consultar a API: retorna a mensagem de erro ou null se for válido
fun validar(nome: String): String? = when {
    nome.isEmpty() -> "Digite um nome para buscar"
    nome.length < 3 -> "Digite pelo menos 3 caracteres"
    else -> null
}
