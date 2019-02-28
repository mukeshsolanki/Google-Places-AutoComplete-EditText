package `in`.madapps.placeautocompleteedittext

import `in`.madapps.placesautocomplete.OnPlacesDetailsListener
import `in`.madapps.placesautocomplete.Place
import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.PlaceDetails
import `in`.madapps.placesautocomplete.PlacesAutoCompleteAdapter
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.autocomplete

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val placesApi = PlaceAPI()
    placesApi.initialize(getString(R.string.mapsApiKey))
    autocomplete.setAdapter(
      PlacesAutoCompleteAdapter(
        this,
        R.layout.autocomplete_list_item,
        placesApi
      )
    )
    autocomplete.onItemClickListener =
      AdapterView.OnItemClickListener { parent, _, position, _ ->
        val place = parent.getItemAtPosition(position) as Place
//        autocomplete.setText(place.description)
        placesApi.fetchDetails(place.id, object : OnPlacesDetailsListener {
          override fun onError(errorMessage: String) {
            Log.d("placeData", errorMessage)
          }

          override fun onPlaceDetailsFetched(placeDetails: PlaceDetails) {
            Log.d("placeData", placeDetails.toString())
            autocomplete.setText(placeDetails.name)
          }
        })
      }
  }
}
