package ma.ensa.graphqltp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import ma.ensa.graphqltp.adapter.ComptesAdapter
import ma.ensa.graphqltp.data.MainViewModel
import ma.ensa.graphqltp.type.TypeCompte
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import android.graphics.drawable.ColorDrawable



class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var statsCard: View
    private lateinit var typeGroup: RadioGroup

    private val viewModel: MainViewModel by viewModels()
    private val comptesAdapter = ComptesAdapter(
        onDeleteClick = { id ->
            showDeleteConfirmationDialog(id)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupRecyclerView()
        setupAddButton()
        setupTypeFilter()
        observeViewModel()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.comptesRecyclerView)
        addButton = findViewById(R.id.addCompteButton)
        statsCard = findViewById(R.id.statsCard)
        typeGroup = findViewById(R.id.typeGroup)
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            adapter = comptesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupAddButton() {
        addButton.setOnClickListener {
            showAddCompteDialog()
        }
    }

    private fun setupTypeFilter() {
        typeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.courantRadio -> viewModel.loadComptesByType(TypeCompte.COURANT)
                R.id.epargneRadio -> viewModel.loadComptesByType(TypeCompte.EPARGNE)
                R.id.allRadio -> viewModel.loadComptes()
            }
        }
    }

    private fun showDeleteConfirmationDialog(id: String) {
        // Créer le dialogue
        val alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Supprimer le Compte")
            .setMessage("Voulez-vous vraiment supprimer ce compte ?")
            .setPositiveButton("Supprimer", null) // Placeholder, le clic sera géré plus tard
            .setNegativeButton("Annuler", null)
            .create()

        // Appliquer une couleur d'arrière-plan personnalisée
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#C69940")))

        alertDialog.show()

        // Personnaliser les boutons
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        positiveButton.setTextColor(Color.BLACK) // Couleur de texte
        positiveButton.setBackgroundColor(Color.parseColor("#F8DCA5")) // Couleur de fond

        negativeButton.setTextColor(Color.BLACK)
        negativeButton.setBackgroundColor(Color.parseColor("#F8DCA5"))

        // Gérer le clic pour "Supprimer"
        positiveButton.setOnClickListener {
            viewModel.deleteCompte(id) // Supprimer le compte
            alertDialog.dismiss() // Fermer le dialogue après suppression
        }
    }


    private fun observeViewModel() {
        // Observer les comptes
        viewModel.comptesState.observe(this) { state ->
            when (state) {
                is MainViewModel.UIState.Loading -> {
                    // Afficher une UI de chargement si nécessaire
                }
                is MainViewModel.UIState.Success -> {
                    comptesAdapter.updateList(state.data)
                }
                is MainViewModel.UIState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Observer les comptes filtrés
        viewModel.filteredComptesState.observe(this) { state ->
            when (state) {
                is MainViewModel.UIState.Loading -> {
                    // Afficher une UI de chargement pour les comptes filtrés
                }
                is MainViewModel.UIState.Success -> {
                    val adaptedComptes = state.data.map { filtered ->
                        GetAllComptesQuery.AllCompte(
                            filtered.id,
                            filtered.solde,
                            filtered.dateCreation,
                            filtered.type
                        )
                    }
                    comptesAdapter.updateList(adaptedComptes)
                }
                is MainViewModel.UIState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }

    private fun showAddCompteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_compte, null)
        val soldeInput = dialogView.findViewById<TextInputEditText>(R.id.soldeInput)
        val typeGroup = dialogView.findViewById<RadioGroup>(R.id.typeGroup)

        // Créer le dialogue
        val alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Ajouter un Nouveau Compte")
            .setView(dialogView)
            .setPositiveButton("Ajouter", null) // Placeholder, le clic sera géré plus tard
            .setNegativeButton("Annuler", null)
            .create()

        // Appliquer une couleur d'arrière-plan personnalisée
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F5F0E1")))

        alertDialog.show()

        // Personnaliser les boutons
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        positiveButton.setTextColor(Color.parseColor("#8B5E3C")) // Couleur de texte
        positiveButton.setBackgroundColor(Color.parseColor("#D3BBA3")) // Couleur de fond

        negativeButton.setTextColor(Color.parseColor("#8B5E3C"))
        negativeButton.setBackgroundColor(Color.parseColor("#D3BBA3"))

        // Gérer le clic pour "Ajouter"
        positiveButton.setOnClickListener {
            val solde = soldeInput.text.toString().toDoubleOrNull()
            val selectedId = typeGroup.checkedRadioButtonId
            val typeRadioButton = dialogView.findViewById<RadioButton>(selectedId)
            val type = when (typeRadioButton.text.toString().uppercase()) {
                "COURANT" -> TypeCompte.COURANT
                "EPARGNE" -> TypeCompte.EPARGNE
                else -> TypeCompte.COURANT
            }

            if (solde != null) {
                viewModel.saveCompte(solde, type)
                alertDialog.dismiss() // Fermer le dialogue après succès
            } else {
                Toast.makeText(this, "Veuillez entrer un montant valide", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
