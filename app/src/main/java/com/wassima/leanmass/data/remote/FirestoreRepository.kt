package com.wassima.leanmass.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wassima.leanmass.model.LBMRecord
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    private fun recordsCollection(userId: String) =
        db.collection("users").document(userId).collection("records")

    suspend fun saveRecord(record: LBMRecord): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Utilisateur non connecté"))
            val data = hashMapOf(
                "weight"         to record.weight,
                "height"         to record.height,
                "gender"         to record.gender,
                "lbmResult"      to record.lbmResult,
                "isSatisfactory" to record.isSatisfactory,
                "timestamp"      to record.timestamp
            )
            recordsCollection(uid).add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecords(userId: String): Result<List<LBMRecord>> {
        return try {
            val snapshot = recordsCollection(userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            val records = snapshot.documents.mapNotNull { doc ->
                LBMRecord(
                    id              = doc.id,
                    userId          = userId,
                    weight          = doc.getDouble("weight")          ?: 0.0,
                    height          = doc.getDouble("height")          ?: 0.0,
                    gender          = doc.getString("gender")          ?: "male",
                    lbmResult       = doc.getDouble("lbmResult")       ?: 0.0,
                    isSatisfactory  = doc.getBoolean("isSatisfactory") ?: false,
                    timestamp       = doc.getLong("timestamp")         ?: 0L
                )
            }
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecord(userId: String, recordId: String): Result<Unit> {
        return try {
            recordsCollection(userId).document(recordId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}