package com.wiryatech.gitdroid.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wiryatech.gitdroid.R
import com.wiryatech.gitdroid.ui.activities.DetailActivity
import com.wiryatech.gitdroid.ui.activities.MainActivity
import com.wiryatech.gitdroid.ui.adapters.UserAdapter
import com.wiryatech.gitdroid.ui.viewmodels.UserViewModel
import com.wiryatech.gitdroid.utils.Resource
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: UserViewModel
    private val userAdapter by lazy { UserAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).userViewModel

        initUI()
        setupRV()
    }

    private fun initUI() {
        var job: Job? = null
        search.apply{
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchUser(query.toString())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    job?.cancel()
                    job = MainScope().launch {
                        delay(750)
                        newText?.let {
                            if (it.trim().isNotEmpty()) {
                                searchUser(it)
                            }
                        }
                    }
                    return true
                }
            })
        }
        handleState()

        userAdapter.setOnItemClickListener {
            val data = it.login
            getUserDetail(data)
        }
    }

    private fun handleState() {
        viewModel.listSearch.observe(viewLifecycleOwner, { response ->
            when(response) {
                is Resource.Success -> {
                    showRV()
                    response.data?.let {
                        if (it.total_count <= 0 || it.incomplete_results) {
                            showDefaultNoData()
                        } else {
                            userAdapter.differ.submitList(it.items)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    showDefaultNoData()
                    response.message?.let {
                        tv_error.text = response.message.toString()
                        Log.e("Error", "${response.message}")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun searchUser(q: String) {
        if (q.trim().isNotEmpty()) {
            viewModel.searchUser(q)
        }
    }

    private fun setupRV() {
        rv_user.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun showRV() {
        rv_user.visibility = View.VISIBLE
        imageView.visibility = View.INVISIBLE
        tv_error.visibility = View.INVISIBLE
        hideProgressBar()
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        imageView.visibility = View.INVISIBLE
        tv_error.visibility = View.INVISIBLE
    }

    private fun showDefaultNoData() {
        imageView.visibility = View.VISIBLE
        tv_error.visibility = View.VISIBLE
        rv_user.visibility = View.INVISIBLE
        hideProgressBar()
    }

    private fun getUserDetail(data: String) {
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.USERNAME, data)
        startActivity(intent)
    }

}