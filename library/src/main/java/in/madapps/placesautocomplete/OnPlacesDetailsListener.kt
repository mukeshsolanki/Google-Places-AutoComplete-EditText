package `in`.madapps.placesautocomplete

/**
 * Created by mukeshsolanki on 28/02/19.
 */
interface OnPlacesDetailsListener {
  fun onPlaceDetailsFetched(placeDetails: PlaceDetails)
  fun onError(errorMessage: String)
}