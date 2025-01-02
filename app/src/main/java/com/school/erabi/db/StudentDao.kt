package com.school.erabi.db

import androidx.room.*

@Dao
interface StudentDao {

    @Insert
    fun insertStudent(student: Student)

    @Query("Select * from Student order by month desc")
    fun getAllStudents(): List<Student>

    @Query("Select sum(total) from Student where month= :month and year = :year")
    fun getTotalFee(month : Int, year : Int): String

    @Query("Select count(*) from Student where month= :month and year = :year")
    fun getTotalStudent(month : Int, year : Int): String

    @Query("Select * from Student where billNo= :billNo LIMIT 1")
    fun getStudent(billNo : String): Student


    @Update
    fun updateStudent(student: Student)

    @Delete
    fun deleteStudent(student: Student)

}