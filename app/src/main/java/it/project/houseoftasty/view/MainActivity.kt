package it.project.houseoftasty.view


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.project.houseoftasty.ExpireWorker
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.ActivityMainBinding
import it.project.houseoftasty.utility.checkSelfPermissionCompat
import it.project.houseoftasty.utility.requestPermissionsCompat
import it.project.houseoftasty.utility.shouldShowRequestPermissionRationaleCompat
import it.project.houseoftasty.utility.showSnackbar
import java.util.concurrent.TimeUnit


const val PERMISSION_REQUEST_CAMERA = 0
const val PERMISSION_REQUEST_GALLERY = 1

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var lastSelectedMenuItem : MenuItem
    private lateinit var cameraActivityResult: ActivityResultLauncher<Intent>
    private lateinit var galleryActivityResult: ActivityResultLauncher<Intent>
    var onCameraActivityResult: (Intent?) -> Unit = {}
    var onGalleryActivityResult: (Intent?) -> Unit = {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Ottengo riferimenti al DrawerLayout, alla NavigationView e al NavigationController
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = findNavController(R.id.fragment_container)

        // Imposto la toolbar come ActionBar
        setSupportActionBar(binding.contentMain.mainToolbar)

        // Ottengo un riferimento all'utente corrente
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser

        val dataExpired: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ExpireWorker>(12, TimeUnit.HOURS).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("Scadenza prodotti", ExistingPeriodicWorkPolicy.KEEP, dataExpired)

        // Imposto il menù in base allo stato di autenticazione
        navView.menu.clear()

        if (firebaseUser != null) {
            setNavigationConfiguration(
                R.menu.menu_authenticated, setOf(
                    R.id.nav_home,
                    R.id.nav_profile,
                    R.id.nav_explore,
                    R.id.nav_cookbook,
                    R.id.nav_collections,
                    R.id.nav_product,
                    R.id.nav_logout,
            ))
        } else {
            setNavigationConfiguration(
                R.menu.menu_unauthenticated, setOf(
                    R.id.nav_home,
                    R.id.nav_explore,
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

        // Eseguo la callback per gestire i dati ritornati da un'activity fotocamera
        fun executeOnCameraActivityResult(intent: Intent?) = run {
            onCameraActivityResult.invoke(intent)
        }

        // Eseguo la callback per gestire i dati ritornati da un'activity galleria
        fun executeOnGalleryActivityResult(intent: Intent?) = run {
            onGalleryActivityResult.invoke(intent)
        }

        // Registro una callback per gestire i dati ritornati da un'activity fotocamera
        cameraActivityResult = registerActivityResultCallback {
            data ->  executeOnCameraActivityResult(data)
        }

        // Registro un callback per gestire i dati ritornati da un'activity galleria
        galleryActivityResult = registerActivityResultCallback {
                data ->  executeOnGalleryActivityResult(data)
        }
    }

    // Funzione per cambiare il titolo della Action Bar
    fun setActionBarTitle(title: String) {
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

    // Lancia un'activity con la galleria immagini del dispositivo
    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResult.launch(galleryIntent)
    }

    // Lancia un'activity con la fotocamera del dispositivo
    private fun takePhotoFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResult.launch(cameraIntent)
    }

    // Registra la funzione di callback da eseguire al ritorno dell'activity
    private fun registerActivityResultCallback(callback: (data: Intent?) -> Unit): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result?.data != null) {
                    callback.invoke(result.data)
                }
            }
        }
    }

    /** Mostra un Dialog con cui scegliere se aprire la fotocamera o la galleria del dispositivo.
     * Successivamente alla scelta, esegue il metodo [startActivityWithPermissionRequest] con i
     * parametri specifici del caso selezionato.
     */
     fun showPictureSelectionDialog() {
        val pictureSelectionDialog = AlertDialog.Builder(this)
        val pictureSelectionDialogItems = arrayOf("Scatta dalla fotocamera", "Scegli dalla galleria")
        pictureSelectionDialog.setTitle("Seleziona un'immagine")
        pictureSelectionDialog.setItems(pictureSelectionDialogItems) { _, option ->
            when (option) {
                0 -> startActivityWithPermissionRequest(
                    Manifest.permission.CAMERA,
                    PERMISSION_REQUEST_CAMERA,
                    R.string.camera_permission_available,
                    R.string.camera_permission_not_available,
                    R.string.camera_access_required
                ) { takePhotoFromCamera() }
                1 -> startActivityWithPermissionRequest(
                    if (Build.VERSION.SDK_INT < 33)
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    else
                        Manifest.permission.READ_MEDIA_IMAGES,
                    PERMISSION_REQUEST_GALLERY,
                    R.string.gallery_permission_available,
                    R.string.gallery_permission_not_available,
                    R.string.gallery_access_required
                ) { choosePhotoFromGallery() }
            }
        }
        pictureSelectionDialog.show()
    }

    /** Controlla che il permesso sia stato garantito. In caso affermativo, lancia la funzione
     * [startActivityFunction], altrimenti richiede il permesso tramite la funzione
     * [requestPermission].
     */
    private fun startActivityWithPermissionRequest(
        permission: String,
        permissionRequestCode: Int,
        permissionAvailableMessageId: Int,
        permissionUnavailableMessageId: Int,
        permissionRequiredMessageId: Int,
        startActivityFunction: () -> Unit
    ) {
        // Check if the Camera permission has been granted
        if (checkSelfPermissionCompat(permission) ==
            PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            drawerLayout.showSnackbar(permissionAvailableMessageId, Snackbar.LENGTH_SHORT)
            startActivityFunction.invoke()
        } else {
            // Permission is missing and must be requested.
            requestPermission(
                permission,
                permissionRequestCode,
                permissionUnavailableMessageId,
                permissionRequiredMessageId
            )
        }
    }

    /**
     * Richiede il permesso. Se un rationale aggiuntivo è mostrato, l'utente deve lanciare la
     * richiesta da una SnackBar che include ulteriori informazioni.
     */
    private fun requestPermission(
        permission: String,
        permissionRequestCode: Int,
        permissionUnavailableMessageId: Int,
        permissionRequiredMessageId: Int
    ) {
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationaleCompat(permission)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            drawerLayout.showSnackbar(permissionRequiredMessageId,
                Snackbar.LENGTH_INDEFINITE, R.string.ok) {
                requestPermissionsCompat(arrayOf(permission), permissionRequestCode)
            }

        } else {
            drawerLayout.showSnackbar(permissionUnavailableMessageId, Snackbar.LENGTH_SHORT)

            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissionsCompat(arrayOf(permission), permissionRequestCode)
        }
    }

    /** Funzione di callback eseguita successivamente a richieste di permessi. */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        fun checkPermissionResultAndStartActivity(
            permissionRequestCode: Int,
            permissionGrantedMessageId: Int,
            permissionDeniedMessageId: Int,
            startActivityFunction: () -> Unit
        ) {
            if (requestCode == permissionRequestCode) {
                // Request for camera permission.
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera Activity.
                    drawerLayout.showSnackbar(permissionGrantedMessageId, Snackbar.LENGTH_SHORT)
                    startActivityFunction.invoke()
                } else {
                    // Permission request was denied.
                    drawerLayout.showSnackbar(permissionDeniedMessageId, Snackbar.LENGTH_SHORT)
                }
            }
        }

        checkPermissionResultAndStartActivity(
            PERMISSION_REQUEST_CAMERA,
            R.string.camera_permission_granted,
            R.string.camera_permission_denied
        ) { takePhotoFromCamera() }

        checkPermissionResultAndStartActivity(
            PERMISSION_REQUEST_GALLERY,
            R.string.gallery_permission_granted,
            R.string.gallery_permission_denied
        ) { choosePhotoFromGallery() }

    }

    fun hideSoftKeyboard() {
        val inputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                this.currentFocus?.windowToken ?: Binder(),
                0
            )
        }
    }
}