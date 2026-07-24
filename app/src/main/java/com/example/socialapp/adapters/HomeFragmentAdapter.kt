package com.example.socialapp.adapters

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.data.Post
import com.example.socialapp.databinding.HomeFragmentFeedBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Things to come to mind when creating a Recycler view
//1) we need an adapter class -> The class(in this case HomeFragmentAdapter) must inherit from the adapter class that is nested in the public Recycler view class
// still on 1, to extend the adapter class, the adapter class requires a parameter of the viewHolder class so as to tell the recycler view what type of viewHolder it(adapter class) will use


class HomeFragmentAdapter (
    //Any class or fragment implementing this adapter, would receive the callbacks that are based on user actions
    // a callback is a piece of code that you give to another another objet, when the objet detect something it alls the allbak
    // now the homeFragmentScreen gives the callbacks to HomeFragment
    // detect user action all on the specific user action callback
    private val onLikeClick:(Post)->Unit,

    private val onCommentClick:(Post)->Unit,

    private val onShareClick:(Post)->Unit,

    private val onProfileClick:(Post)->Unit,

    private val onMoreClick:(Post)->Unit,

    private val onReadMoreClick:(Post)->Unit
) :
    ListAdapter<Post, HomeFragmentAdapter.ReferenceToViewHolderViews>(diffCallback) {


    // The inner holds reference to the viewHolder
    //You will later using in onBindViewHolder function to access view in the viewHolder layout
    inner class ReferenceToViewHolderViews(val binding: HomeFragmentFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.tvContentProfileName.text = post.name

            Glide
                .with(binding.root)
                .load(post.profileImagePic)
                .into(binding.imContentProfilePic)
            binding.imContentProfilePic.setOnClickListener {
                onProfileClick(post)
            }

            binding.tvContentContent.text = post.content

            binding.tvReadMore.text = if (post.isExpanded) "read less" else "read more"
            binding.tvReadMore.setOnClickListener {
                onReadMoreClick(post)
            }


            if (post.imagePostList.isNotEmpty()){
                Glide.with(binding.root)
                    .load(post.imagePostList.first()).
                    into(binding.ImContentDisplay)
            }

            val formatter = SimpleDateFormat("dd MM yyyy, h: mm a", Locale.getDefault()) // This gives output has this 18 Jul 2026, 9:45 AM
            binding.tvTimeStamp.text = formatter.format(Date(post.timestamp))
            binding.follow.text =
                if (post.isFollowing) "Following" else "Follow"

            binding.ibLikeButton.setOnClickListener {
                onLikeClick(post)
            }

            binding.ibCommentButton.setOnClickListener {
                onCommentClick(post)
            }

            binding.ibShareButton.setOnClickListener {
                onShareClick(post)
            }

            binding.ibMoreOptionsButton.setOnClickListener {
                onMoreClick(post)
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReferenceToViewHolderViews {
        val view =
            HomeFragmentFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReferenceToViewHolderViews(view)  // constructor call (ReferenceToViewHolderViews(view)) instantiate the inner class
    }

    override fun onBindViewHolder(
        holder: ReferenceToViewHolderViews,
        position: Int
    ) {

        val post = getItem(position) // Give me the Post stored at this position in the ListAdapter's current list
        holder.bind(post) // The content are then binded to the viewHolder view



//        btnFollow.setOnClickListener {
//
//            var currentState = sharedPref.getBoolean("following_${post.userId}", false)
//            currentState = !currentState
//
//            if (currentState) {
//                holder.binding.follow.text = "Following"
//
//            } else {
//                holder.binding.follow.text = "Follow"
//            }
//
//            sharedPref.edit { putBoolean("following_${post.userId}", currentState) }
//            initialFollowState = currentState

//            val currentState = sharedPref.getBoolean("following", false)
//            val newState = !currentState
//            editor.putBoolean("following", newState).apply()
//
//            holder.binding.follow.text = if (newState) "following" else "follow"


//            val postOwnerId = post.userId
//            val nameOfUser = FirebaseAuth.getInstance().currentUser?.displayName
//
//            val iD = FirebaseAuth.getInstance().currentUser?.uid
//            val addFollowingPost = Notification(
//                following = "true",
//                userThatFollowedId = iD,
//                nameOfUser = nameOfUser,
//                userBeingFollowedId = postOwnerId,
//                timeStamp = System.currentTimeMillis()
//
//            )
//            FirebaseFirestore.getInstance().collection("Notification").add(addFollowingPost)

//            btnFollow.text = "Following"

    }



        // Set text first
//        tvContent.text = post.content
//
//        tvContent.maxLines = Int.MAX_VALUE
//
//        tvName.text = post.name
//
//        if (post.imagePostList.isNotEmpty()) {
//            context?.let {
//                Glide.with(it)
//                    .load(post.imagePostList[0])
//                    .into(imContentDisplay)
//
//            }
//        } else {
//            holder.binding.ImContentDisplay.visibility = View.GONE
//        }

//        val profileImage = UserSession.currentUser?.userProfileImage
//        if (profileImage != null) {
//            context?.let {
//                Glide.with(it)
//                    .load(profileImage)
//                    .into(imUserProfilePicture)
//            }
//        } else {
//            Log.d("userImage", "E no dey here o")
//        }


        // Reset the view states explicitly
//        tvContent.maxLines = if (post.isExpanded) Int.MAX_VALUE else 3
//        tvReadMore.text = if (post.isExpanded) "Read Less" else "Read More"
//
//
//        // Check line count AFTER layout
//        tvContent.post {
//            Log.d("DEBUG", "Line count: ${holder.binding.tvContentContent.lineCount}")
//            if (tvContent.lineCount >= 3) {
//                tvReadMore.visibility = View.VISIBLE
//            } else {
//                tvReadMore.visibility = View.GONE
//            }
//        }

        // Toggle on click
//        tvReadMore.setOnClickListener {
//            post.isExpanded = !post.isExpanded
//            notifyItemChanged(position)  // Rebind to refresh the UI
//        }


//        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//        val formattedDate = sdf.format(Date(post.timestamp))
//        holder.binding.tvTimeStamp.text = formattedDate
//    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(
                oldItem: Post,
                newItem: Post
            ): Boolean {
                return oldItem.documentId == newItem.documentId
            }

            override fun areContentsTheSame(
                oldItem: Post,
                newItem: Post
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


}

class ColouredSpace(private val space: Int, private val colour: Int) :
    RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDraw(c, parent, state)

        // This code line below is to like get the left
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount =
            parent.childCount // get the no of item(inflated viewHolder) that is presently showing
        for (williams in 0 until childCount - 1) {
            val child = parent.getChildAt(williams)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val topOfDivider = child.bottom + params.bottomMargin
            val dividerAppearance = topOfDivider + space


            //
            c.drawRect(
                left.toFloat(),
                topOfDivider.toFloat(),
                right.toFloat(),
                dividerAppearance.toFloat(),
                paint
            )
        }

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position =
            parent.getChildAdapterPosition(view)  // get the viewHolder position on the list
        val itemCount = state.itemCount // get the number of viewHolder in the list

        if (position < itemCount - 1) {
            outRect.bottom = space
        }

    }

}