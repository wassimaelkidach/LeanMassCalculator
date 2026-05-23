package com.wassima.leanmass.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wassima.leanmass.data.local.entity.LBMRecordEntity
import com.wassima.leanmass.data.repository.LBMRepository
import com.wassima.leanmass.model.LBMRecord
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LBMRepository(application)

    private val _cloudHistory = MutableLiveData<List<LBMRecord>>()
    val cloudHistory: LiveData<List<LBMRecord>> = _cloudHistory

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Historique local — LiveData Room (mise à jour automatique)
    fun getLocalHistory(userId: String): LiveData<List<LBMRecordEntity>> =
        repository.getLocalRecords(userId)

    // Historique cloud Firestore
    fun loadCloudHistory(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getCloudRecords(userId)
            if (result.isSuccess) {
                _cloudHistory.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    // Supprimer un enregistrement local
    fun deleteLocalRecord(entity: LBMRecordEntity) {
        viewModelScope.launch {
            repository.deleteLocalRecord(entity)
        }
    }

    // Supprimer un enregistrement cloud
    fun deleteCloudRecord(userId: String, recordId: String) {
        viewModelScope.launch {
            val result = repository.deleteCloudRecord(userId, recordId)
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}