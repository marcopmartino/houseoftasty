package it.project.houseoftasty.view


import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var lastSelectedMenuItem : MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        // Ottengo riferimenti al DrawerLayout, alla NavigationView e al NavigationController
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.fragment_container)

        // Imposto la toolbar come ActionBar
        setSupportActionBar(binding.contentMain.mainToolbar)

        // Ottengo un riferimento all'utente corrente
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        // Imposto il menù in base allo stato di autenticazione
        navView.menu.clear()

        if (firebaseUser != null) {
            setNavigationConfiguration(
                R.menu.menu_authenticated, setOf(
                    R.id.nav_home,
                    R.id.nav_profile,
                    R.id.nav_product,
                    R.id.nav_cookbook,
                    R.id.nav_logout
            ))
        } else {
            setNavigationConfiguration(
                R.menu.menu_unauthenticated, setOf(
                    R.id.nav_home,
                    R.id.nav_cookbook,
                    R.id.nav_logout
            ))
        }

        // Connetto il DrawerLayout alla navigazione nell’Activity:
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configuro il NavigationView per usarlo con il NavigationController:
        navView.setupWithNavController(navController)

        // Imposto il titolo della ActionBar
        setActionBarTitle("Home")

        // Disabilito l'item "Home"
        lastSelectedMenuItem = binding.navView.menu.getItem(0)
        lastSelectedMenuItem.isEnabled = false

        /* La navigazione vera e propria può avvenire in automatico, senza bisogno di un listener
        sul Navigation Drawer, tuttavia è stato necessario personalizzare il comportamento
        per via di un bug con la Action Bar in caso di click su un item del menù già selezionato */
        navView.setNavigationItemSelectedListener { menuItem ->

            // Aggiorna l'ultimo item selezionato
            updateSelectedMenuItem(menuItem)

            // Per mantenere il comportamento automatico tipico della Navigation view
            onNavDestinationSelected(menuItem, navController)

            // Chiude il Navigation Drawer
            drawerLayout.closeDrawer(GravityCompat.START)

            // Il listener si aspetta un Boolean come tipo di ritorno
            true
        }

    }

    // Funzione per cambiare il titolo della Action Bar
    fun setActionBarTitle(title: String){
        supportActionBar?.title = title
    }

    // Abilita il precedente item e disabilita quello appena selezionato
    private fun updateSelectedMenuItem(menuItem: MenuItem) {
        lastSelectedMenuItem.isEnabled = true
        lastSelectedMenuItem = menuItem
        menuItem.isEnabled = false
    }

    // Funzione per impostare le schermate di primo livello
    private fun setNavigationConfiguration(menuId: Int, topViewsIds: Set<Int>) {
        navView.inflateMenu(menuId)
        appBarConfiguration = AppBarConfiguration(topViewsIds, drawerLayout)
    }

/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
*/
    // Delega al navController la gestione del comportamento della freccia per tornare indietro
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}