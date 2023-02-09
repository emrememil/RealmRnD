package com.appops.realmrnd

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.appops.realmrnd.model.Employee
import com.appops.realmrnd.model.EmployeeModel
import com.appops.realmrnd.secure.encryption.EncryptionProvider
import com.appops.realmrnd.secure.key.RealmEncryptionKeyProvider
import com.appops.realmrnd.secure.key.RealmEncryptionKeyProvider.Util
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var employeeModel: EmployeeModel
    lateinit var realm: Realm
    var showJsonText = ""
    lateinit var employeeAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        employeeModel = EmployeeModel()

        employeeAdapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(this)


        Realm.init(this)

        val realmEncryptionKeyProvider = RealmEncryptionKeyProvider(this)

        val encryptionKey = realmEncryptionKeyProvider.secureRealmKey
        val formattedKey = Util.getFormattedSecureKey(encryptionKey)
        Log.e("SECURE KEY", formattedKey)

        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .encryptionKey(encryptionKey)
            .build()
        realm = Realm.getInstance(config)


        if (employeeModel.getEmployees(realm).count() > 0) {
            showEmployees(realm)
        }

        add_button.setOnClickListener {
            showAddDialog()
        }

        del_button.setOnClickListener {
            if (employeeModel.getEmployees(realm).count() > 0){
                employeeModel.removeLastEmployee(realm)
                showEmployees(realm)
            }
        }

        showJson.setOnClickListener {
            AlertDialog.Builder(this).setMessage(showJsonText).create().show()
        }

        employeeAdapter.setOnItemClickListener { employee, tag ->
            when (tag) {
                "update" -> {
                    showDialog(employee)
                }
                "delete" -> {
                    employeeModel.deleteEmployee(realm, employee.ID)
                    showEmployees(realm)
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAddDialog() {
        var newID = 1

        if (employeeModel.getEmployees(realm).count() > 0) {
            val v = employeeModel.getLastEmployee(realm)
            newID = v!!.ID + 1
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.edit_employee_dialog)
        val etID = dialog.findViewById(R.id.etID) as EditText
        val name = dialog.findViewById(R.id.etName) as EditText
        val surname = dialog.findViewById(R.id.etSurname) as EditText
        val department = dialog.findViewById(R.id.etDepartment) as EditText
        val updateButton = dialog.findViewById(R.id.btnUpdate) as Button
        val cancelButton = dialog.findViewById(R.id.btnCancel) as Button

        updateButton.text = "ADD"


        etID.setText(newID.toString())


        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        updateButton.setOnClickListener {
            val new = Employee(
                newID,
                name.text.toString(),
                surname.text.toString(),
                department.text.toString()
            )
            employeeModel.addEmployee(realm, new)
            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
            showEmployees(realm)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDialog(employee: Employee) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.edit_employee_dialog)
        val etID = dialog.findViewById(R.id.etID) as EditText
        val name = dialog.findViewById(R.id.etName) as EditText
        val surname = dialog.findViewById(R.id.etSurname) as EditText
        val department = dialog.findViewById(R.id.etDepartment) as EditText
        val updateButton = dialog.findViewById(R.id.btnUpdate) as Button
        val cancelButton = dialog.findViewById(R.id.btnCancel) as Button
        etID.setText(employee.ID.toString())
        name.setText(employee.firstName)
        surname.setText(employee.lastName)
        department.setText(employee.department)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        updateButton.setOnClickListener {
            val new = Employee(
                employee.ID,
                name.text.toString(),
                surname.text.toString(),
                department.text.toString()
            )
            employeeModel.editEmployee(realm, new)
            Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()
            showEmployees(realm)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEmployees(realm: Realm) {
        showJsonText = ""
        val results = employeeModel.getEmployees(realm)
        showJsonText = results.asJSON()
        val employeeList = ArrayList<Employee>()
        results.forEach { result ->
            employeeList.add(result)
        }
        employeeAdapter.setEmployeesList(employeeList)
        recyclerView.adapter = employeeAdapter
    }
}