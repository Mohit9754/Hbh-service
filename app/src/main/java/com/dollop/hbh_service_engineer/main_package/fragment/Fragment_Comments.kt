package com.dollop.hbh_service_engineer.main_package.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dollop.hbh_service_engineer.databinding.FragmentCommentsBinding

class Fragment_Comments() : Fragment() {

    private lateinit var comment: String
    private lateinit var binding:FragmentCommentsBinding

    constructor(comment: String):this()
    {
        this.comment = comment
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCommentsBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init()
    {
        binding.tvComment.text = comment
    }
}