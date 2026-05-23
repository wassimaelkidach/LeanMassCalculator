package com.wassima.leanmass.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wassima.leanmass.data.local.entity.LBMRecordEntity

@Dao
interface LBMRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: LBMRecordEntity)

    @Query("SELECT * FROM lbm_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllRecords(userId: String): LiveData<List<LBMRecordEntity>>

    @Delete
    suspend fun deleteRecord(record: LBMRecordEntity)

    @Query("DELETE FROM lbm_records WHERE id = :id")
    suspend fun deleteById(id: Int)
}