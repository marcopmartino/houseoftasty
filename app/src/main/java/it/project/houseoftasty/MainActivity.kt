package it.project.houseoftasty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navView)

        if(firebaseUser!=null){
            navView.menu.clear()
            navView.inflateMenu(R.menu.after_login)
        }

        //Inizializzo la toolbar da utilizzare per aprire il drawer
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer,R.string.closeDrawer)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //Imposto listener sul drawer
        navView.setNavigationItemSelectedListener(this)

        //Imposto prima pagina dell'app
        setToolbarTitle("House of Tasty")
        changeFragment(HomeFragment())
    }

    //Switch dei vari elementi nel drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean{
        drawerLayout.closeDrawers()
        when(item.itemId){
            R.id.nav_login -> {
                setToolbarTitle("Login")
                changeFragment(LoginFragment())
            }
            R.id.nav_registrer -> {
                setToolbarTitle("Registrati")
                changeFragment(RegisterFragment())
            }
            R.id.nav_profile -> {
                setToolbarTitle("Profilo")
                changeFragment(ProfileFragment())
            }
            R.id.nav_logout -> {
                setToolbarTitle("Logout")
                changeFragment(LogoutFragment())
            }
            R.id.nav_product -> {
                setToolbarTitle("I miei prodotti")
                changeFragment(MyProductFragment())
            }
            else -> {
                setToolbarTitle("House of Tasty")
                changeFragment(HomeFragment())
            }
        }
        return true
    }

    //Funzione per cambiare fragment
    fun changeFragment(frag: Fragment){
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragment_container, frag).commit()
    }

    //Funzione per cambiare titolo nella toolbar
    fun setToolbarTitle(title:String){
        supportActionBar?.title= title
    }
}