package com.example.addlesson_47_db.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.addlesson_47_db.model.Student
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleJust
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.Schedulers.io
import java.lang.Exception

const val STUDENT_TABLE = "STUDENT"
const val NAME_COLUMN = "NAME"
const val AGE_COLUMN = "AGE"
const val COURSE_COLUMN = "COURSE"
const val ID_COLUMN = "ID"

class StudentOpenHelper(context: Context) : SQLiteOpenHelper(context, "healers.db", null, 1) {

    private val columns = arrayOf(ID_COLUMN, NAME_COLUMN, AGE_COLUMN, COURSE_COLUMN)

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) return
        val sqlQuery = """
CREATE TABLE $STUDENT_TABLE(
            $ID_COLUMN INTEGER PRIMARY KEY AUTOINCREMENT,
            $NAME_COLUMN TEXT NOT NULL,
            $AGE_COLUMN INTEGER NOT NULL DEFAULT 22,
            $COURSE_COLUMN INTEGER NOT NULL DEFAULT 4);
        """.trimIndent()
        db.execSQL(sqlQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    private fun onContentValius(item: Student): ContentValues? {
        val cv = ContentValues()
        cv.put(NAME_COLUMN, item.name)
        cv.put(AGE_COLUMN, item.age)
        cv.put(COURSE_COLUMN, item.course)
        return cv
    }

    fun insertStudent(item: Student): Long {
        val cv = onContentValius(item)
        return writableDatabase.insert(
            STUDENT_TABLE,
            null,
            cv
        )

    }

    fun loadStudentBYID(studentId: Long): Student? {
        val selection = "ID=?"
        val args = arrayOf("$studentId")
        var cursor: Cursor? = null
        return try {
            cursor = readableDatabase.query(
                STUDENT_TABLE,
                columns,
                selection,
                args,
                null,
                null,
                null,
                null
            )
            val idIndex = cursor.getColumnIndex(ID_COLUMN)
            val nameIndex = cursor.getColumnIndex(NAME_COLUMN)
            val ageIndex = cursor.getColumnIndex(AGE_COLUMN)
            val courseIndex = cursor.getColumnIndex(COURSE_COLUMN)
            val name: String
            val age: Int
            val id: Long
            val course: Int

            cursor.moveToFirst()
            name = cursor.getString(nameIndex)
            id = cursor.getLong(idIndex)
            age = cursor.getInt(ageIndex)
            course = cursor.getInt(courseIndex)
            Student(name, age, course, id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            cursor?.close()
        }
    }

    @SuppressLint("Recycle")
    fun loadStudents(): MutableList<Student> {
        val cursor = readableDatabase.query(
            STUDENT_TABLE,
            columns,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val idIndex = cursor.getColumnIndex(ID_COLUMN)
        val nameIndex = cursor.getColumnIndex(NAME_COLUMN)
        val ageIndex = cursor.getColumnIndex(AGE_COLUMN)
        val courseIndex = cursor.getColumnIndex(COURSE_COLUMN)
        var name: String
        var age: Int
        var id: Long
        var course: Int
        val studentlist = mutableListOf<Student>()
        while (cursor.moveToNext()) {
            name = cursor.getString(nameIndex)
            id = cursor.getLong(idIndex)
            age = cursor.getInt(ageIndex)
            course = cursor.getInt(courseIndex)
            studentlist.add(Student(name, age, course, id))

        }
        return studentlist

    }

    fun updateStudent(item: Student): Int {
        val cv = onContentValius(item)
        val whereClause = "ID=?"
        val args = arrayOf("${item.id}")
        return writableDatabase
            .update(
                STUDENT_TABLE,
                cv,
                whereClause,
                args
            )


    }

    fun loadDeleteItem(id: Long): Int {
        val whereClause = "ID=?"
        val whereArgs = arrayOf("$id")
        return writableDatabase
            .delete(
                STUDENT_TABLE,
                whereClause,
                whereArgs
            )
    }

    companion object {
          var instance: StudentOpenHelper? = null
        fun instance(context: Context): StudentOpenHelper {
            if (instance == null) {
                instance = StudentOpenHelper(context)
            }
            return instance!!
        }
    }
}