package ovh.gabrielhuav.flasklogin

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Modelos para Login y Registro
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val mensaje: String, val token: String)

// Modelos para las Tareas
data class Tarea(val id: Int? = null, val titulo: String, val descripcion: String)
data class TareaResponse(val mensaje: String, val tarea: Tarea)
data class MensajeResponse(val mensaje: String)

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun registrar(@Body request: LoginRequest): Response<Void>

    // --- RUTAS DEL CRUD (Requieren el Token) ---

    @GET("tareas")
    suspend fun obtenerTareas(@Header("Authorization") token: String): Response<List<Tarea>>

    @POST("tareas")
    suspend fun crearTarea(@Header("Authorization") token: String, @Body tarea: Tarea): Response<TareaResponse>

    @PUT("tareas/{id}")
    suspend fun actualizarTarea(@Header("Authorization") token: String, @Path("id") id: Int, @Body tarea: Tarea): Response<TareaResponse>

    @DELETE("tareas/{id}")
    suspend fun borrarTarea(@Header("Authorization") token: String, @Path("id") id: Int): Response<MensajeResponse>
}