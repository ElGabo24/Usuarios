package com.gapps.usuarios.mainModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gapps.usuarios.R
import com.gapps.usuarios.addModule.RegistroFragment
import com.gapps.usuarios.addModule.viewModel.AddViewModel
import com.gapps.usuarios.common.entities.UserEntity
import com.gapps.usuarios.databinding.ActivityMainBinding
import com.gapps.usuarios.mainModule.adapter.UserAdapter
import com.gapps.usuarios.mainModule.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: UserAdapter
    private lateinit var mLinearLayoutManager: RecyclerView.LayoutManager

    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mAddViewModel: AddViewModel
    var mAdd: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        setupViewModel()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mAdapter = UserAdapter(mutableListOf())
        mLinearLayoutManager = LinearLayoutManager(this)

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            adapter = mAdapter
        }
    }


    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getUsers().observe(this, {users ->
            mAdapter.setUsers(users)
        })

        mAddViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        mAddViewModel.getUserSelected().observe(this,{ storeEntity ->
            mAdapter.add(storeEntity)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_registrar, menu)
        return mAdd
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_registar -> {
                launchRegistroFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchRegistroFragment(storeEntity: UserEntity = UserEntity()) {
        mAddViewModel.setUserSelected(storeEntity)

        val fragment = RegistroFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

    }

}