package ma.ensa.graphqltp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ma.ensa.graphqltp.GetAllComptesQuery
import ma.ensa.graphqltp.GetComptesForTypeQuery
import ma.ensa.graphqltp.GetTotalSoldeQuery
import ma.ensa.graphqltp.repository.BanqueRepository
import ma.ensa.graphqltp.type.TypeCompte

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"
    private val _filteredComptesState = MutableLiveData<UIState<List<GetComptesForTypeQuery.FindByType>>>()
    val filteredComptesState: LiveData<UIState<List<GetComptesForTypeQuery.FindByType>>> = _filteredComptesState
    private val repository = BanqueRepository()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _comptesState = MutableLiveData<UIState<List<GetAllComptesQuery.AllCompte>>>()
    val comptesState: LiveData<UIState<List<GetAllComptesQuery.AllCompte>>> = _comptesState

    private val _totalSoldeState = MutableLiveData<UIState<GetTotalSoldeQuery.TotalSolde?>>()
    val totalSoldeState: LiveData<UIState<GetTotalSoldeQuery.TotalSolde?>> = _totalSoldeState

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadComptes()
        loadTotalSolde()
    }

    fun loadComptesByType(type: TypeCompte) {
        coroutineScope.launch(Dispatchers.Main) {
            _filteredComptesState.value = UIState.Loading
            try {
                repository.getComptesByType(type) { result ->
                    result.fold(
                        onSuccess = { comptesList ->
                            _filteredComptesState.postValue(UIState.Success(comptesList))
                            Log.d(TAG, "Successfully fetched ${comptesList.size} comptes for type $type")
                        },
                        onFailure = { exception ->
                            _filteredComptesState.postValue(UIState.Error(exception.message ?: "Unknown error"))
                            Log.e(TAG, "Error fetching comptes by type", exception)
                        }
                    )
                }
            } catch (e: Exception) {
                _filteredComptesState.postValue(UIState.Error(e.message ?: "Unknown error"))
                Log.e(TAG, "Exception while loading comptes by type", e)
            }
        }
    }
    fun deleteCompte(id: String) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                repository.deleteCompte(id) { result ->
                    result.fold(
                        onSuccess = { _ ->
                            Log.d(TAG, "Successfully deleted compte")
                            // Reload data after successful deletion
                            loadComptes()
                            loadTotalSolde()
                        },
                        onFailure = { exception ->
                            _error.postValue(exception.message)
                            Log.e(TAG, "Error deleting compte", exception)
                        }
                    )
                }
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
                Log.e(TAG, "Exception while deleting compte", e)
            }
        }
    }
    fun loadComptes() {
        coroutineScope.launch(Dispatchers.Main) {
            _comptesState.value = UIState.Loading
            try {
                repository.getAllComptes { result ->
                    result.fold(
                        onSuccess = { comptesList ->
                            _comptesState.postValue(UIState.Success(comptesList))
                            Log.d(TAG, "Successfully fetched ${comptesList.size} comptes")
                        },
                        onFailure = { exception ->
                            _comptesState.postValue(UIState.Error(exception.message ?: "Unknown error"))
                            Log.e(TAG, "Error fetching comptes", exception)
                        }
                    )
                }
            } catch (e: Exception) {
                _comptesState.postValue(UIState.Error(e.message ?: "Unknown error"))
                Log.e(TAG, "Exception while loading comptes", e)
            }
        }
    }

    fun loadTotalSolde() {
        coroutineScope.launch(Dispatchers.Main) {
            _totalSoldeState.value = UIState.Loading
            try {
                repository.getTotalSolde { result ->
                    result.fold(
                        onSuccess = { totalSolde ->
                            _totalSoldeState.postValue(UIState.Success(totalSolde))
                            Log.d(TAG, "Successfully fetched total solde")
                        },
                        onFailure = { exception ->
                            _totalSoldeState.postValue(UIState.Error(exception.message ?: "Unknown error"))
                            Log.e(TAG, "Error fetching total solde", exception)
                        }
                    )
                }
            } catch (e: Exception) {
                _totalSoldeState.postValue(UIState.Error(e.message ?: "Unknown error"))
                Log.e(TAG, "Exception while loading total solde", e)
            }
        }
    }

    fun saveCompte(solde: Double, type: TypeCompte) {
        coroutineScope.launch(Dispatchers.Main) {
            try {
                repository.saveCompte(solde, type) { result ->
                    result.fold(
                        onSuccess = { savedCompte ->
                            Log.d(TAG, "Successfully saved compte with id: ${savedCompte.id}")
                            // Reload data after successful save
                            loadComptes()
                            loadTotalSolde()
                        },
                        onFailure = { exception ->
                            _error.postValue(exception.message)
                            Log.e(TAG, "Error saving compte", exception)
                        }
                    )
                }
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
                Log.e(TAG, "Exception while saving compte", e)
            }
        }
    }

    sealed class UIState<out T> {
        object Loading : UIState<Nothing>()
        data class Success<T>(val data: T) : UIState<T>()
        data class Error(val message: String) : UIState<Nothing>()
    }


    override fun onCleared() {
        super.onCleared()
        // Cancel any ongoing coroutines when ViewModel is cleared
        coroutineScope.launch {
            // Clean up any resources if needed
        }
    }
}
