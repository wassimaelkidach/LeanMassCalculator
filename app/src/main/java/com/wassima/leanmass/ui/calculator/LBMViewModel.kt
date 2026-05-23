package com.wassima.leanmass.ui.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wassima.leanmass.data.repository.LBMRepository
import com.wassima.leanmass.data.repository.PersistenceMode
import com.wassima.leanmass.model.LBMRecord
import com.wassima.leanmass.utils.LBMCalculator
import kotlinx.coroutines.launch

class LBMViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LBMRepository(application)

    private val _currentRecord = MutableLiveData<LBMRecord?>()
    val currentRecord: LiveData<LBMRecord?> = _currentRecord

    private val _saveStatus = MutableLiveData<Boolean?>()
    val saveStatus: LiveData<Boolean?> = _saveStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun setPersistenceMode(mode: PersistenceMode) {
        repository.mode = mode
    }

    fun calculate(weight: Double, height: Double, gender: String) {
        try {
            val lbm = LBMCalculator.calculate(weight, height, gender)
            val isSatisfactory = LBMCalculator.isSatisfactory(lbm, gender)
            _currentRecord.value = LBMRecord(
                weight         = weight,
                height         = height,
                gender         = gender,
                lbmResult      = lbm,
                isSatisfactory = isSatisfactory
            )
        } catch (e: Exception) {
            _errorMessage.value = "Erreur de calcul : ${e.message}"
        }
    }

    fun saveRecord(userId: String) {
        val record = _currentRecord.value ?: run {
            _errorMessage.value = "Aucun calcul à sauvegarder"
            return
        }
        viewModelScope.launch {
            val result = repository.saveRecord(record, userId)
            _saveStatus.value = result.isSuccess
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearStatus() {
        _saveStatus.value = null
        _errorMessage.value = null
    }
}