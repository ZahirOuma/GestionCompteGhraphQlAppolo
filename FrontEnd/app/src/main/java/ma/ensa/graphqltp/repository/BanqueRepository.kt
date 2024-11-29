package ma.ensa.graphqltp.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import ma.ensa.graphqltp.DeleteCompteMutation
import ma.ensa.graphqltp.GetAllComptesQuery
import ma.ensa.graphqltp.GetComptesForTypeQuery
import ma.ensa.graphqltp.GetTotalSoldeQuery
import ma.ensa.graphqltp.SaveCompteMutation
import ma.ensa.graphqltp.type.CompteInput
import ma.ensa.graphqltp.type.TypeCompte


class BanqueRepository {
    private val TAG = "BanqueRepository"
    private val apolloClient = ApolloClient.Builder()
        .serverUrl("http://10.0.2.2:8082/graphql")
        .build()

    suspend fun getAllComptes(callback: (Result<List<GetAllComptesQuery.AllCompte>>) -> Unit) {
        try {
            Log.d(TAG, "Fetching all comptes...")
            val response = apolloClient.query(GetAllComptesQuery()).execute()
            if (response.hasErrors()) {
                val error = response.errors?.first()?.message ?: "Unknown error"
                Log.e(TAG, "Error fetching comptes: $error")
                callback(Result.failure(Exception(error)))
            } else {
                val comptes = response.data?.allComptes?.filterNotNull() ?: emptyList()
                Log.d(TAG, "Fetched ${comptes.size} comptes")
                callback(Result.success(comptes))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching comptes", e)
            callback(Result.failure(e))
        }
    }

    suspend fun getTotalSolde(callback: (Result<GetTotalSoldeQuery.TotalSolde>) -> Unit) {
        try {
            Log.d(TAG, "Fetching total solde...")
            val response = apolloClient.query(GetTotalSoldeQuery()).execute()
            if (response.hasErrors()) {
                val error = response.errors?.first()?.message ?: "Unknown error"
                Log.e(TAG, "Error fetching total solde: $error")
                callback(Result.failure(Exception(error)))
            } else {
                response.data?.totalSolde?.let { totalSolde ->
                    Log.d(TAG, "Successfully fetched total solde")
                    callback(Result.success(totalSolde))
                } ?: callback(Result.failure(Exception("Total solde response was null")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching total solde", e)
            callback(Result.failure(e))
        }
    }

    suspend fun saveCompte(solde: Double, type: TypeCompte, callback: (Result<SaveCompteMutation.SaveCompte>) -> Unit) {
        try {
            Log.d(TAG, "Saving compte with solde: $solde and type: $type")
            val input = CompteInput(
                solde = solde,
                type = Optional.Present(type)
            )
            val response = apolloClient.mutation(SaveCompteMutation(input)).execute()
            if (response.hasErrors()) {
                val error = response.errors?.first()?.message ?: "Unknown error"
                Log.e(TAG, "Error saving compte: $error")
                callback(Result.failure(Exception(error)))
            } else {
                response.data?.saveCompte?.let { savedCompte ->
                    Log.d(TAG, "Compte saved successfully with id: ${savedCompte.id}")
                    callback(Result.success(savedCompte))
                } ?: callback(Result.failure(Exception("Save response was null")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while saving compte", e)
            callback(Result.failure(e))
        }
    }

    suspend fun getComptesByType(type: TypeCompte, callback: (Result<List<GetComptesForTypeQuery.FindByType>>) -> Unit) {
        try {
            Log.d(TAG, "Fetching comptes for type: $type")
            val response = apolloClient.query(GetComptesForTypeQuery(type)).execute()
            if (response.hasErrors()) {
                val error = response.errors?.first()?.message ?: "Unknown error"
                Log.e(TAG, "Error fetching comptes by type: $error")
                callback(Result.failure(Exception(error)))
            } else {
                val comptes = response.data?.findByType?.filterNotNull() ?: emptyList()
                Log.d(TAG, "Fetched ${comptes.size} comptes for type $type")
                callback(Result.success(comptes))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching comptes by type", e)
            callback(Result.failure(e))
        }
    }

    suspend fun deleteCompte(id: String, callback: (Result<String>) -> Unit) {
        try {
            Log.d(TAG, "Deleting compte with id: $id")
            val response = apolloClient.mutation(DeleteCompteMutation(id)).execute()
            if (response.hasErrors()) {
                val error = response.errors?.first()?.message ?: "Unknown error"
                Log.e(TAG, "Error deleting compte: $error")
                callback(Result.failure(Exception(error)))
            } else {
                response.data?.deleteCompte?.let { result ->
                    Log.d(TAG, "Successfully deleted compte")
                    callback(Result.success(result))
                } ?: callback(Result.failure(Exception("Delete response was null")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while deleting compte", e)
            callback(Result.failure(e))
        }
    }
}