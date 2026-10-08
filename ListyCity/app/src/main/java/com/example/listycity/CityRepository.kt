
package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {

    private val _cities = mutableStateListOf<City>()

    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")

    init {
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                _cities.clear()

                for (document in snapshot.documents) {
                    val city = document.toObject(City::class.java)

                    if (city != null) {
                        _cities.add(city)
                    }
                }
            }
        }
    }

    val cities: List<City>
        get() = _cities

    fun addCity(city: City) {
        citiesRef.add(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        citiesRef
            .whereEqualTo("name", oldCity.name)
            .whereEqualTo("province", oldCity.province)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    citiesRef.document(document.id).set(updatedCity)
                }
            }
    }

    fun deleteCity(city: City) {
        citiesRef
            .whereEqualTo("name", city.name)
            .whereEqualTo("province", city.province)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    citiesRef.document(document.id).delete()
                }
            }
    }
}
