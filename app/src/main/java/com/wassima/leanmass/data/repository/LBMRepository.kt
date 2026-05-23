package com.wassima.leanmass.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.wassima.leanmass.data.local.AppDatabase
import com.wassima.leanmass.data.local.entity.LBMRecordEntity
import com.wassima.leanmass.data.remote.FirestoreRepository
import com.wassima.leanmass.model.LBMRecord

enum class PersistenceMode { LOCAL, CLOUD }

class LBMRepository(context: Context) {

    private val dao           = AppDatabase.getDatabase(context).lbmRecordDao()
    private val firestoreRepo = FirestoreRepository()

    var mode: PersistenceMode = PersistenceMode.LOCAL

    // Sauvegarder un calcul
    suspend fun saveRecord(record: LBMRecord, userId: String): Result<Unit> {
        return when (mode) {
            PersistenceMode.LOCAL -> {
                dao.insertRecord(record.toEntity(userId))
                Result.success(Unit)
            }
            PersistenceMode.CLOUD -> firestoreRepo.saveRecord(record)
        }
    }

    // Historique local (LiveData — mise à jour automatique)
    fun getLocalRecords(userId: String): LiveData<List<LBMRecordEntity>> =
        dao.getAllRecords(userId)

    // Historique cloud
    suspend fun getCloudRecords(userId: String): Result<List<LBMRecord>> =
        firestoreRepo.getRecords(userId)

    // Suppression locale
    suspend fun deleteLocalRecord(entity: LBMRecordEntity) =
        dao.deleteRecord(entity)

    // Suppression cloud
    suspend fun deleteCloudRecord(userId: String, recordId: String) =
        firestoreRepo.deleteRecord(userId, recordId)

    // Conversion LBMRecord → LBMRecordEntity
    private fun LBMRecord.toEntity(userId: String) = LBMRecordEntity(
        userId          = userId,
        weight          = weight,
        height          = height,
        gender          = gender,
        lbmResult       = lbmResult,
        isSatisfactory  = isSatisfactory,
        timestamp       = timestamp
    )
}