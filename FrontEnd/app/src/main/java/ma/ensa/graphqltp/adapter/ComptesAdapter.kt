package ma.ensa.graphqltp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.graphqltp.GetAllComptesQuery
import ma.ensa.graphqltp.R

class ComptesAdapter(
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ComptesAdapter.CompteViewHolder>() {
    private var comptes: List<GetAllComptesQuery.AllCompte> = listOf()

    fun updateList(newList: List<GetAllComptesQuery.AllCompte>) {
        comptes = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compte, parent, false)
        return CompteViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.bind(comptes[position])
    }

    override fun getItemCount() = comptes.size

    class CompteViewHolder(
        view: View,
        private val onDeleteClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val accountIdText: TextView = view.findViewById(R.id.accountIdText)
        private val soldeText: TextView = view.findViewById(R.id.soldeText)
        private val typeText: TextView = view.findViewById(R.id.typeText)
        private val dateText: TextView = view.findViewById(R.id.dateText)
        private val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)

        fun bind(compte: GetAllComptesQuery.AllCompte) {
            accountIdText.text = " ID: ${compte.id}"
            soldeText.text = "Solde: ${compte.solde} "
            typeText.text = " ${compte.type}"
            dateText.text = "${compte.dateCreation}"

            deleteButton.setOnClickListener {
                compte.id?.let { id -> onDeleteClick(id) }
            }
        }
    }
}