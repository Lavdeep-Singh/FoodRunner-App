package com.example.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.room.Room
import com.example.foodrunner.R
import database.CartDatabase
import database.CartEntity


// TODO: Rename parameter arguments, choose names that match
// the com.example.bookhub.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this com.example.bookhub.fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var getProfile:String
    lateinit var et_name:TextView
    lateinit var et_number:TextView
    lateinit var et_email:TextView
    lateinit var et_address:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this com.example.bookhub.fragment
        val view= inflater.inflate(R.layout.fragment_profile, container, false)

        val shared_user_id:SharedPreferences=requireActivity().getSharedPreferences(getString(R.string.reg_user_id),Context.MODE_PRIVATE)
        val shared_user_name:SharedPreferences=requireActivity().getSharedPreferences(getString(R.string.reg_name),Context.MODE_PRIVATE)
        val shared_user_email:SharedPreferences=requireActivity().getSharedPreferences(getString(R.string.reg_email),Context.MODE_PRIVATE)
        val shared_user_mobile_number:SharedPreferences=requireActivity().getSharedPreferences(getString(R.string.reg_mobile_number),Context.MODE_PRIVATE)
        val shared_user_address:SharedPreferences=requireActivity().getSharedPreferences(getString(R.string.reg_address),Context.MODE_PRIVATE)

        et_name=view.findViewById(R.id.tv_name)
        et_number=view.findViewById(R.id.tv_number)
        et_address=view.findViewById(R.id.tv_address)
        et_email=view.findViewById(R.id.tv_email)

        val sharedPreferences1:SharedPreferences=
            requireActivity().getSharedPreferences(getString(R.string.prefrences_file_name1),Context.MODE_PRIVATE)

       /* getProfile= sharedPreferences1.getString("dataFile1", 2222.toString()).toString()
        val data=RetrieveCredentials(activity as Context,getProfile).execute().get()
        println("my data is $data")
        println("my data is ${data[0]} is number")

        */

        val number=shared_user_mobile_number.getString("mobile_number","n/a")
        val address=shared_user_address.getString("address","n/a")
        val name=shared_user_name.getString("name","n/a")
        val email=shared_user_email.getString("email","n/a")

        et_name.setText(name)
        et_number.setText(number)
        et_email.setText(email)
        et_address.setText(address)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this com.example.bookhub.fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of com.example.bookhub.fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}