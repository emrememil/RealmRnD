package com.appops.realmrnd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appops.realmrnd.model.Employee
import kotlinx.android.synthetic.main.edit_employee_dialog.view.*
import kotlinx.android.synthetic.main.employee_item.view.*

class Adapter : RecyclerView.Adapter<Adapter.EmployeeViewHolder>() {

    private var employeeList = ArrayList<Employee>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return employeeList.size
    }

    override fun onBindViewHolder(holder: Adapter.EmployeeViewHolder, position: Int) {
        val employee = employeeList[position]

        holder.itemView.apply {
            employeeID.text = employee.ID.toString()
            employeeName.text = employee.firstName
            employeeSurname.text = employee.lastName
            employeeDepartment.text = employee.department
            btnEdit.setOnClickListener {
                onItemClickListener?.let { it(employee, "update") }
            }

            btnDel.setOnClickListener {
                onItemClickListener?.let { it(employee, "delete") }
            }
        }
    }

    fun setEmployeesList(employees: ArrayList<Employee>) {
        employeeList = employees
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((Employee, String) -> Unit)? = null

    fun setOnItemClickListener(listener: (Employee, String) -> Unit) {
        onItemClickListener = listener
    }

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
