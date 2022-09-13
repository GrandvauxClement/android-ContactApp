package fr.grandvauxclement.contactappexam.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "lastname")
    val lastname: String,
    @ColumnInfo(name = "firstName")
    val firstname: String,
    @ColumnInfo(name = "company")
    val company: String,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "num_tel")
    val numTel: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "sector_activity")
    val sectorActivity: String,
    @ColumnInfo(name = "favors")
    val favors: Int
)