package com.example.addlesson_47_db.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.addlesson_47_db.R
import com.example.addlesson_47_db.data.StudentOpenHelper
import com.example.addlesson_47_db.model.Student
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.android.synthetic.main.activity_editor.*
import org.w3c.dom.Text

class EditorActivity : AppCompatActivity() {
    private var isSaveEnabled = false
    private var isUpdate = false
    private val cd = CompositeDisposable()
    private var studentId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        setUpValidate()
        loadFromEditor()

    }

    private fun loadFromEditor() {
        studentId = intent.getLongExtra(KEY_STUDENT_ID, -1)
        isUpdate = studentId > 0
        val student = StudentOpenHelper.instance(this)
            .loadStudentBYID(studentId)
        student?.apply {
            tv_name.setText(name)
            tv_age.setText("$age")
            tv_course.setText("$course")
        }
    }

    private fun setUpValidate() {
        val d = Observable.combineLatest(
            tv_name.textChanges()
                .skipInitialValue()
                .map { it.isNotEmpty() },
            tv_age.textChanges()
                .skipInitialValue()
                .map { it.isNotEmpty() && !TextUtils.isEmpty(it) },
            tv_course.textChanges()
                .skipInitialValue()
                .map { it.isNotEmpty() && !TextUtils.isEmpty(it) },
            { t1, t2, t3 -> t1 && t2 && t3 }
        ).doOnNext { isSaveEnabled = it }
            .subscribe({ invalidateOptionsMenu() },
                { Log.d("Error", it.message) })
        cd.add(d)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.save_item) {
            if (isUpdate) {
                updateItem()
            } else {
                insertItem()
            }
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val name = tv_name.text.toString()
        val age = tv_age.text.toString().toInt()
        val course = tv_course.text.toString().toInt()
        val student = Student(name, age, course, studentId)

        val id = StudentOpenHelper
            .instance(this)
            .updateStudent(student)
        Log.d("Update", "insertItem $id")
        onfinished(studentId)
    }

    private fun onfinished(id: Long) {
        intent.putExtra(KEY_STUDENT_ID, id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun insertItem() {
        val name = tv_name.text.toString()
        val age = tv_age.text.toString().toInt()
        val course = tv_course.text.toString().toInt()
        val student = Student(name, age, course)

     val id=  StudentOpenHelper
            .instance(this)
            .insertStudent(student)
          onfinished(id)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.isEnabled = isSaveEnabled
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        cd.clear()
    }

}