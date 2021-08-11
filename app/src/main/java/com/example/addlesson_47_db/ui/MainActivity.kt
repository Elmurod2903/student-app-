package com.example.addlesson_47_db.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.addlesson_47_db.`interface`.OnItemClickListener
import com.example.addlesson_47_db.R
import com.example.addlesson_47_db.adapter.StudentAdapter
import com.example.addlesson_47_db.data.StudentOpenHelper
import com.example.addlesson_47_db.model.Student
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val ACTION_INSERT_STUDENT = 1001
const val ACTION_UPDATE_STUDENT = 1000
const val KEY_STUDENT_ID = "KEY_STUDENT_ID"

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private val cd = CompositeDisposable()
    private val adapter by lazy { StudentAdapter(this, mutableListOf()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener { startActivityEditor(ACTION_INSERT_STUDENT) }
        initRv()
        loadInitItems()

    }

    private fun loadInitItems() {
        val student = StudentOpenHelper.instance(this)
            .loadStudents()
        adapter.updateData(student)


    }


    private fun startActivityEditor(action: Int, id: Long? = null) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(KEY_STUDENT_ID, id)
        startActivityForResult(intent, action)
    }

    private fun initRv() {
        // todo update--> saveEnabled
        //todo create simple callback
        // todo add ListAdapter by  diffUtils
        rv.adapter = adapter
        val swipeListener = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val it = adapter.getItemByPosition(position)
                deleteItemId(it)
                adapter.deleteByItemPosition(position)
            }

        }
        val touchHelper = ItemTouchHelper(swipeListener)
        touchHelper.attachToRecyclerView(rv)
    }

    private fun deleteItemId(it: Student) {
        StudentOpenHelper
            .instance(this)
            .loadDeleteItem(it.id)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if (resultCode == Activity.RESULT_OK && requestCode in arrayOf(
                ACTION_INSERT_STUDENT,
                ACTION_UPDATE_STUDENT
            )
        ) {
            val newStudentId = data.getLongExtra(KEY_STUDENT_ID, 0)
            val student: Student = StudentOpenHelper
                .instance(this)
                .loadStudentBYID(newStudentId) ?: return
            if (requestCode == ACTION_INSERT_STUDENT) {
                adapter.addItem(student)
            } else if (requestCode == ACTION_UPDATE_STUDENT) {
                adapter.updateItem(student)
            }
        }
    }


    override fun itemClick(it: Student) = startActivityEditor(ACTION_UPDATE_STUDENT, it.id)

    override fun onDestroy() {
        super.onDestroy()
        cd.clear()
    }
}