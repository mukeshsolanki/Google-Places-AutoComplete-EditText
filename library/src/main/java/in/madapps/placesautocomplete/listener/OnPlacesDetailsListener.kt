package `in`.madapps.placesautocomplete.listener

import `in`.madapps.placesautocomplete.model.PlaceDetails

/**
 * Created by mukeshsolanki on 28/02/19.
 */
interface OnPlacesDetailsListener {
  fun onPlaceDetailsFetched(placeDetails: PlaceDetails)
  fun onError(errorMessage: String)
}