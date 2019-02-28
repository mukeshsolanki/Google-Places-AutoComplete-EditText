package `in`.madapps.placesautocomplete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

/**
 * Created by mukeshsolanki on 28/02/19.
 */
class PlacesAutoCompleteAdapter(
  val mContext: Context,
  val mResource: Int,
  val placesApi: PlaceAPI
) :
  ArrayAdapter<Place>(mContext, mResource), Filterable {

  var resultList: ArrayList<Place>? = ArrayList()

  override fun getCount(): Int {
    return resultList!!.size
  }

  override fun getItem(position: Int): Place? {
    return resultList!![position]
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view: View
    val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    view = inflater.inflate(R.layout.autocomplete_list_item, null, false)
    val autocompleteTextView = view.findViewById(R.id.autocompleteText) as TextView
    val place = resultList!![position]
    autocompleteTextView.text = place.description
    return view
  }

  override fun getFilter(): Filter {
    return object : Filter() {
      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        if (results != null && results.count > 0) {
          notifyDataSetChanged()
        } else {
          notifyDataSetInvalidated()
        }
      }

      override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filterResults = FilterResults()
        if (constraint != null) {
          resultList = placesApi.autocomplete(constraint.toString())
          filterResults.values = resultList
          filterResults.count = resultList!!.size
        }
        return filterResults
      }
    }
  }
}