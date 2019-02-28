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
class PlacesAutoCompleteAdapter(mContext: Context, val placesApi: PlaceAPI) :
  ArrayAdapter<Place>(mContext, R.layout.autocomplete_list_item), Filterable {

  var resultList: ArrayList<Place>? = ArrayList()

  override fun getCount(): Int {
    return resultList!!.size
  }

  override fun getItem(position: Int): Place? {
    return resultList!![position]
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    var view = convertView
    val viewHolder: ViewHolder
    if (view == null) {
      viewHolder = ViewHolder()
      val inflater = LayoutInflater.from(context)
      view = inflater.inflate(R.layout.autocomplete_list_item, parent, false)
      viewHolder.description = view.findViewById(R.id.autocompleteText) as TextView
      view.tag = viewHolder
    } else {
      viewHolder = view.tag as ViewHolder
    }
    val place = resultList!![position]
    viewHolder.description?.text = place.description
    return view!!
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

  internal class ViewHolder {
    var description: TextView? = null
  }
}