package it.project.houseoftasty

import it.project.houseoftasty.viewModel.RegisterViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private lateinit var profile: MutableMap<String, Any>
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp(){
        profile.putAll(
            mapOf<String, Any>(
                "email" to "mailDiTest@test.com",
                "password" to "password1234",
                "nome" to "Mario",
                "cognome" to "Rossi",
                "username" to "marRoss")
        )
        registerViewModel = RegisterViewModel()
    }

    @Test
    suspend fun registration_isSuccessful(){
        assertFailsWith<Exception>(
            "Errore nell'inserimento del profilo",
            block = {
                runBlocking {
                registerViewModel.insertProfile(profile)
            }})
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}