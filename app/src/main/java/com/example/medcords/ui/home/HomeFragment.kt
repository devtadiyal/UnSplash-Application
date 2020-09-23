package com.example.medcords.ui.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.medcords.R
import com.example.medcords.adapter.CustomAdapter
import com.example.medcords.model.PhotosResponse
import com.example.medcords.utils.isConnectedToNetwork
import com.example.medcords.viewmodel.AuthViewModelFactory
import com.example.medcords.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class HomeFragment : Fragment(), KodeinAware {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CustomAdapter
    private lateinit var photoDetails: PhotosResponse
    private var PAGE: Int = 1

    //Kodein DI injecting factory class instance
    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        return v
    }

    //method to get photo list response from viewmodel and set to adpater
    private fun getPhotosList(page: Int) {
        homeViewModel.getPhotos(requireView(), page)
        shimmer_view_genres.visibility= GONE
        shimmer_view_genres.stopShimmerAnimation()
        homeViewModel.getPhoto.observe(viewLifecycleOwner, Observer { photos ->
            photoDetails = photos
            linearLayoutManager = LinearLayoutManager(context)
            recycler_view_photos.layoutManager = linearLayoutManager
            adapter = CustomAdapter(photos.results)
            recycler_view_photos.adapter = adapter

        })
    }

    //method to get random photo response from viewmodel and load image url in glide
    private fun getRandomPhoto() {
        homeViewModel.getRandomPhoto(requireView())
        homeViewModel.getRandomPhoto.observe(viewLifecycleOwner, Observer { randomPhoto ->
            //Glide handle image caching and image resizing by default
            Glide.with(requireContext()) //7
                .load(randomPhoto?.urls?.regular)
                .listener(object : RequestListener<Drawable> { //9
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progress.visibility = GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        try {
                            progress.visibility = GONE
                        }catch (e:Exception){}
                        return false
                    }
                })
                .into(randomImage)
        })
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (context?.isConnectedToNetwork()!!) {
            //method to get random image
            getRandomPhoto()
            //method to get photos list
            getPhotosList(1)
            error_layout.visibility = GONE
            swipeRefresh.visibility = VISIBLE
            frame.visibility = VISIBLE
        } else {
            error_layout.visibility = VISIBLE
            frame.visibility = GONE
            shimmer_view_genres.visibility = GONE
            swipeRefresh.visibility = GONE
        }

        //refresh data on pull list
        swipeRefresh.setOnRefreshListener {

            // The method calls setRefreshing(false) when it's finished.
            getPhotosList(1)
            swipeRefresh.isRefreshing = false
        }

        recycler_view_photos.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                //open details fragment
                var direction =
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(photoDetails.results[position])
                Navigation.findNavController(view)
                    .navigate(direction)

            }
        })

        recycler_view_photos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    shimmer_view_genres.startShimmerAnimation()
                    PAGE++
                    getPhotosList(PAGE)

                }
            }
        })
    }


    //recyclerview item listener
    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener({
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                })
            }
        })
    }
    override fun onResume() {
        super.onResume()
        //shimmer start on resume
        shimmer_view_genres.visibility= VISIBLE
        shimmer_view_genres.startShimmerAnimation()
    }

    override fun onPause() {
        //stopping shimmer
        shimmer_view_genres.visibility= GONE
        shimmer_view_genres.stopShimmerAnimation()
        super.onPause()
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

}