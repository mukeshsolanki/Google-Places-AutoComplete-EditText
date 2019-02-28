package `in`.madapps.placesautocomplete

import android.text.TextUtils
import android.util.Log
import androidx.annotation.Nullable
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

/**
 * Created by mukeshsolanki on 28/02/19.
 */
class PlaceAPI {

  fun autocomplete(input: String): ArrayList<Place>? {
    if (TextUtils.isEmpty(apiKey)) {
      throw RuntimeException("Please initialize the api before using it")
    }
    var resultList: ArrayList<Place>? = null

    var conn: HttpURLConnection? = null
    val jsonResults = StringBuilder()

    try {
      val sb = StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON)
      sb.append("?key=$apiKey")
      sb.append("&input=" + URLEncoder.encode(input, "utf8"))

      val url = URL(sb.toString())
      conn = url.openConnection() as HttpURLConnection
      val inputStreamReader = InputStreamReader(conn.inputStream)

      // Load the results into a StringBuilder
      var read: Int
      val buff = CharArray(1024)
      loop@ do {
        read = inputStreamReader.read(buff)
        when {
          read != -1 -> jsonResults.append(buff, 0, read)
          else -> break@loop
        }
      } while (true)

    } catch (e: MalformedURLException) {
      Log.e(TAG, "Error processing Places API URL", e)
      return resultList
    } catch (e: IOException) {
      Log.e(TAG, "Error connecting to Places API", e)
      return resultList
    } finally {
      conn?.disconnect()
    }

    try {
      // Create a JSON object hierarchy from the results
      val jsonObj = JSONObject(jsonResults.toString())
      val predsJsonArray = jsonObj.getJSONArray("predictions")

      // Extract the Place descriptions from the results
      resultList = ArrayList(predsJsonArray.length())
      for (i in 0 until predsJsonArray.length()) {
        resultList.add(
          Place(
            predsJsonArray.getJSONObject(i).getString("place_id"),
            predsJsonArray.getJSONObject(i).getString("description")
          )
        )
      }
    } catch (e: JSONException) {
      val errorJson = JSONObject(jsonResults.toString())
      if (errorJson.has("error_message")) {
        Log.e(TAG, errorJson.getString("error_message"))
      } else {
        Log.e(TAG, "Cannot process JSON results", e)
      }
    }

    return resultList
  }

  fun initialize(key: String) {
    apiKey = key
  }

  @Nullable
  fun fetchDetails(placeId: String, listener: OnPlacesDetailsListener) {
    if (TextUtils.isEmpty(apiKey)) {
      throw RuntimeException("Please initialize the api before using it")
    }

    Thread(Runnable {
      var conn: HttpURLConnection? = null
      val jsonResults = StringBuilder()
      try {
        val sb = StringBuilder(PLACES_API_BASE + TYPE_DETAIL + OUT_JSON)
        sb.append("?key=$apiKey")
        sb.append("$PARAM_PLACE_ID$placeId")
        val url = URL(sb.toString())
        conn = url.openConnection() as HttpURLConnection
        val inputStreamReader = InputStreamReader(conn.inputStream)
        var read: Int
        val buff = CharArray(1024)
        loop@ do {
          read = inputStreamReader.read(buff)
          when {
            read != -1 -> jsonResults.append(buff, 0, read)
            else -> break@loop
          }
        } while (true)
        val jsonObj = JSONObject(jsonResults.toString())
        val resultJsonObject = jsonObj.getJSONObject("result")
        val addressArray = resultJsonObject.getJSONArray("address_components")
        val address = ArrayList<Address>()
        (0 until addressArray.length()).forEach { i ->
          val addressObject = addressArray.getJSONObject(i)
          val addressTypeArray = addressObject.getJSONArray("types")
          val addressType = ArrayList<String>()
          (0 until addressTypeArray.length()).forEach { j ->
            addressType.add(addressTypeArray.getString(j))
          }
          address.add(
            Address(
              addressObject.getString("long_name"),
              addressObject.getString("short_name"),
              addressType
            )
          )
        }
        listener.onPlaceDetailsFetched(
          PlaceDetails(
            resultJsonObject.getString("id"),
            resultJsonObject.getString("name"),
            address
          )
        )
      } catch (e: JSONException) {
        val errorJson = JSONObject(jsonResults.toString())
        if (errorJson.has("error_message")) {
          Log.e(TAG, errorJson.getString("error_message"), e)
          listener.onError(errorJson.getString("error_message"))
        } else {
          Log.e(TAG, "Cannot process JSON results", e)
          listener.onError("Cannot process JSON results")
        }
      } catch (e: MalformedURLException) {
        Log.e(TAG, "Error processing Places API URL", e)
        listener.onError("Error processing Places API URL")
      } catch (e: IOException) {
        Log.e(TAG, "Error connecting to Places API", e)
        listener.onError("Error connecting to Places API")
      } finally {
        conn?.disconnect()
      }
    }).start()
  }

  companion object {
    private val TAG = PlaceAPI::class.java.simpleName
    private val PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place"
    private val TYPE_AUTOCOMPLETE = "/autocomplete"
    private val TYPE_DETAIL = "/details"
    private val PARAM_PLACE_ID = "&placeid="
    private val OUT_JSON = "/json"
    private var apiKey = ""
  }
}