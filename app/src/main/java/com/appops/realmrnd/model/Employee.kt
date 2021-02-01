package com.appops.realmrnd.model

import io.realm.*
import io.realm.annotations.PrimaryKey
import java.lang.Exception

//Employee Object
open class Employee(
    @PrimaryKey
    open var ID: Int = 0,
    open var firstName: String = "",
    open var lastName: String = "",
    open var department: String = ""
) : RealmObject() {

    fun copy(
        ID: Int = this.ID,
        firstName: String = this.firstName,
        lastName: String = this.lastName,
        department: String = this.department
    ) = Employee(ID, firstName, lastName, department)
}

interface EmployeeInterface {
    fun addEmployee(realm: Realm, student: Employee): Boolean
    fun deleteEmployee(realm: Realm, ID: Int): Boolean
    fun editEmployee(realm: Realm, student: Employee): Boolean
    fun getEmployee(realm: Realm, studentId: Int): Employee?
    fun removeLastEmployee(realm: Realm)
}

//Employee Model

class EmployeeModel : EmployeeInterface {
    override fun addEmployee(realm: Realm, employee: Employee): Boolean = try {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(employee)
        realm.commitTransaction()
        true
    }catch (e: Exception){
        println(e)
        false
    }

    override fun deleteEmployee(realm: Realm, ID: Int): Boolean = try {
        realm.beginTransaction()
        realm.where(Employee :: class.java).equalTo("ID", ID).findFirst()?.deleteFromRealm()
        realm.commitTransaction()
        true
    } catch (e: Exception) {
        println(e)
        false
    }

    override fun editEmployee(realm: Realm, employee: Employee): Boolean = try {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(employee)
        realm.commitTransaction()
        true
    } catch (e: Exception) {
        println(e)
        false
    }

    override fun getEmployee(realm: Realm, employeeID: Int): Employee? {
        return realm.where(Employee::class.java).equalTo("ID",employeeID).findFirst()
    }

    override fun removeLastEmployee(realm: Realm) {
        realm.beginTransaction()
        getLastEmployee(realm)?.deleteFromRealm()
        realm.commitTransaction()
    }

    fun getLastEmployee(realm: Realm): Employee? {
        return realm.where(Employee::class.java).findAll().last()
    }

    fun getEmployees(realm: Realm) : RealmResults<Employee>{
        return realm.where(Employee::class.java).findAll()
    }
}