package dev.tontech.authentication_firebase_sample_yt.data.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dev.tontech.authentication_firebase_sample_yt.data.model.Employee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseRealTimeDatabaseRepository(db: FirebaseDatabase) {
    private val refDb: DatabaseReference = db.getReference("Employee")
    private val _employee = MutableStateFlow<Employee?>(null)
    val employee: StateFlow<Employee?>
        get() = _employee.asStateFlow()

    fun writeMessageIntoDatabase(message: Employee) {
        refDb.setValue(message)
    }

    fun readEmployeeIntoDatabase() {
        refDb.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _employee.value = snapshot.getValue<Employee>()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("DATABASE ERROR", "ERROR RETURN")
            }
        })
    }
}