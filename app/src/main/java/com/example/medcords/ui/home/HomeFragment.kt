package com.example.medcords.ui.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.medcords.R
import com.example.medcords.adapter.UserAdapter
import com.example.medcords.datastore.Preferences
import com.example.medcords.model.Result
import com.example.medcords.network.Resource
import com.example.medcords.viewmodel.AuthViewModelFactory
import com.example.medcords.viewmodel.HomeViewModel
import com.example.medcords.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class HomeFragment : Fragment(), KodeinAware {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var photoDetails: PagedList<Result>

    //Kodein DI injecting factory class instance
    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private val preferences: Preferences by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        return v
    }

    //method to get random photo response from viewmodel and load image url in glide
    private fun getRandomPhoto() {
        //hit api
        homeViewModel.getRandomPhoto()
        //getting response from api
        homeViewModel.getRandomPhoto.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    //Glide handle image caching and image resizing by default
                    Glide.with(requireContext()) //7
                        .load(it?.value.urls?.regular)
                        .listener(object : RequestListener<Drawable> { //9
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {

                                return false
                            }
                        })
                        .into(randomImage)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getRandomPhoto()
        val userAdapter = UserAdapter()
        linearLayoutManager = LinearLayoutManager(context)
        recycler_view_photos.layoutManager = linearLayoutManager
        userViewModel.userPagedList.observe(requireActivity(), Observer {
            photoDetails = it
            userAdapter.submitList(it)
        })
        recycler_view_photos.adapter = userAdapter

        //refresh data on pull list
        swipeRefresh.setOnRefreshListener {
            userViewModel.refresh()
            swipeRefresh.isRefreshing = false
            // The method calls setRefreshing(false) when it's finished.
        }

        recycler_view_photos.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                //open details fragment
                var direction =
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(photoDetails[position] as Result)
                Navigation.findNavController(view)
                    .navigate(direction)

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


    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }


}