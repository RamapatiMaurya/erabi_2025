package com.school.erabi.db

import android.content.Context

class StudentRepository(context: Context) {

    var db: StudentDao = AppDatabase.getInstance(context)?.studentDao()!!


    //Fetch All the Students
    fun getAllStudents(): List<Student> {
        return db.getAllStudents()
    }

    //Fetch Student by billNo
    fun getStudent(billNo: String): Student {
        return db.getStudent(billNo)
    }

    // Insert new Student
    fun insertStudent(student: Student) {
        db.insertStudent(student)
    }

    // update Student
    fun updateStudent(student: Student) {
        db.updateStudent(student)
    }

    // Delete Student
    fun deleteStudent(Student: Student) {
        db.deleteStudent(Student)
    }

}