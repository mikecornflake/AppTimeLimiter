package io.github.mikecornflake.apptimelimiter.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mikecornflake.apptimelimiter.database.dao.PackageDao
import io.github.mikecornflake.apptimelimiter.database.entities.Package
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PackageViewModel(private val packageDao: PackageDao) : ViewModel() {
    val allPackages: Flow<List<Package>> = packageDao.getAllPackages()

    fun insert(packageItem: Package) {
        viewModelScope.launch {
            packageDao.insert(packageItem)
        }
    }

    fun update(packageItem: Package) {
        viewModelScope.launch {
            packageDao.update(packageItem)
        }
    }

    fun delete(packageItem: Package) {
        viewModelScope.launch {
            packageDao.delete(packageItem)
        }
    }

    fun getPackage(packageId: Long): Flow<Package> = packageDao.getPackage(packageId)

    fun getPackage(packageName: String): Flow<Package> = packageDao.getPackage(packageName)
}