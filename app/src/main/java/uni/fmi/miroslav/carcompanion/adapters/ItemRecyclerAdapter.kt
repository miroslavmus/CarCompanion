package uni.fmi.miroslav.carcompanion.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import uni.fmi.miroslav.carcompanion.R
import uni.fmi.miroslav.carcompanion.models.Model
import uni.fmi.miroslav.carcompanion.models.ModelFix
import uni.fmi.miroslav.carcompanion.models.ModelImage
import uni.fmi.miroslav.carcompanion.models.ModelItem
import java.lang.Exception
import java.util.ArrayList

class ItemRecyclerAdapter constructor( private val clickListener: ModelListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{


    private var errorTag: String = "No view holder found!"

    private var items: List<Model> = ArrayList()

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        context = when (clickListener)
            { is Activity -> { clickListener
            } is Fragment -> { clickListener.requireContext()
            } else -> throw Exception("Invalid target! (clickListener is not Fragment/Activity)")
        }

        when (clickListener){
            is OnItemClickListener ->
            {
                errorTag = "Error in modelItem holder"
                return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_list_item,
                    parent,
                    false
                ), clickListener
                 , context
            ) }
            is OnImageClickListener ->
            {
                errorTag = "Error in modelImage holder"
                return ImageViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_list_item,
                        parent,
                        false
                    ), clickListener
                     , context
                ) }
            is OnFixClickListener ->
            {
                errorTag = "Error in modelFix holder"
                return FixViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_list_item,
                        parent,
                        false
                    ), clickListener
                     , context
                ) }
        }
        throw Exception(errorTag)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ItemViewHolder -> {
                holder.bind(items[position] as ModelItem)
            }
            is ImageViewHolder -> {
                holder.bind(items[position] as ModelImage)
            }
            is FixViewHolder -> {
                holder.bind(items[position] as ModelFix)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(itemList: List<Model>){
        items = itemList
    }

    class ItemViewHolder constructor( itemView: View, private var onItemClickListener: OnItemClickListener, private val context: Context): RecyclerView.ViewHolder(itemView){

        //define views for item to be shown
        private val itemLayout: ConstraintLayout = itemView.findViewById(R.id.itemLayout)
        private val picture : ImageView = itemView.findViewById(R.id.item_image)
        val name : TextView = itemView.findViewById(R.id.item_name)
        val message : TextView = itemView.findViewById(R.id.item_message)
        val value1 : TextView = itemView.findViewById(R.id.item_value1)
        val value2 : TextView = itemView.findViewById(R.id.item_value2)
        val field1 : TextView = itemView.findViewById(R.id.item_field1)
        val field2 : TextView = itemView.findViewById(R.id.item_field2)

        //bind items
        fun bind(item: ModelItem){
            picture.setImageResource(picture.resources.getIdentifier(item.picturePath, "drawable", context.packageName))
            name.text = item.name
            message.text = item.message
            value1.text = item.valueField1Text
            value2.text = item.valueField2Text
            field1.text = item.changeField1Text
            field2.text = item.changeField2Text

            itemLayout.setOnClickListener { onItemClickListener.onItemClick(item, it) }
        }
    }

    class ImageViewHolder constructor(itemView: View, private var onImageClickListener: OnImageClickListener, private val context: Context): RecyclerView.ViewHolder(itemView){

        //define image view
        private val picture : ImageView = itemView.findViewById(R.id.imageItem)

        //bind picture
        fun bind(item: ModelImage){
            picture.setImageResource(picture.resources.getIdentifier(item.image, "drawable", context.packageName))

            picture.setOnClickListener{
                onImageClickListener.onImageClick(item.image)
            }
        }
    }

    class FixViewHolder constructor(itemView: View, private var onFixClickListener: OnFixClickListener, val context: Context): RecyclerView.ViewHolder(itemView){

        //define views for item to be shown
        private val itemLayout: LinearLayout = itemView.findViewById(R.id.fixItemLayout)
        private val fixString: TextView = itemView.findViewById(R.id.stringFixTextView)

        fun bind(item: ModelFix){
            val string =  "${context.getString(R.string.km)} : ${item.km}; ${context.getString(
                R.string.date
            )} : ${item.date};"
            fixString.text = string
            itemLayout.setOnClickListener { onFixClickListener.onFixClick(item, it) }
        }
    }


    interface ModelListener

    interface OnItemClickListener : ModelListener{
        fun onItemClick(modelItem: ModelItem, view: View)
    }
    interface OnImageClickListener : ModelListener {
        fun onImageClick(image: String)
    }
    interface OnFixClickListener : ModelListener {
        fun onFixClick(modelItem: ModelFix, view: View)
    }
}