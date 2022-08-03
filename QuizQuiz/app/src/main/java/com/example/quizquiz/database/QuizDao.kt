package com.example.quizquiz.database

import android.content.Context
import androidx.room.*

// Data Access Object
@Dao
interface QuizDao {
    @Insert
    fun insert(quiz: Quiz): Long
    @Update
    fun update(quiz: Quiz)
    @Delete
    fun delete(quiz: Quiz)
    @Query("SELECT * FROM quiz")
    fun getAll(): List<Quiz>
}

@Database(entities=[Quiz::class], version=1)
@TypeConverters(StringListTypeConverter::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDAO(): QuizDao

    // 정적 메서드(클래스 메서드)
    companion object {
        private var INSTANCE: QuizDatabase? = null

        // 싱글턴 패턴
        fun getInstance(context: Context): QuizDatabase {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java, "database.db").build()
            }
            return INSTANCE!!
        }
    }
}
