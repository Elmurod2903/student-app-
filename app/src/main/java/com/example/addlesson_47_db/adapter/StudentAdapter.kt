package com.example.addlesson_47_db.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.addlesson_47_db.`interface`.OnItemClickListener
import com.example.addlesson_47_db.R
import com.example.addlesson_47_db.model.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_view.*

class StudentAdapter(
    context: Context,
    private val list: MutableList<Student>
) : RecyclerView.Adapter<StudentAdapter.StudentVH>() {
    private val inflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(context) }
    private val listener: OnItemClickListener = context as OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentVH {
        val view = inflater.inflate(R.layout.item_view, parent, false)
        return StudentVH(view, listener)
    }

    override fun onBindViewHolder(holder: StudentVH, position: Int) {
        val bind = list[position]
        holder.onBind(bind)
    }

    override fun getItemCount() = list.size


    fun addItem(student: Student) {
        list += student
        notifyItemInserted(list.size)
    }

    fun updateData(newStudentList: List<Student>) {
        val oldSize = list.size
        list.clear()
        notifyItemRangeRemoved(0, oldSize)
        list.addAll(newStudentList)
        notifyItemRangeInserted(0, newStudentList.size)
    }

    fun updateItem(student: Student) {
        val it: Student = list.first { it.id == student.id }
        list.remove(it)
        notifyItemRemoved(list.indexOf(it))
        list += (student)
        notifyItemInserted(list.size - 1)

    }

    fun deleteByItemPosition(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItemByPosition(position: Int): Student {
        return list[position]
    }

    class StudentVH(override val containerView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        private var studentItem: Student? = null

        init {
            containerView.setOnClickListener { listener.itemClick(studentItem!!) }
        }

        fun onBind(it: Student) {
            this.studentItem = it
            tv_name.text = it.name
            tv_age.text = "${it.age}"
        }

    }
}