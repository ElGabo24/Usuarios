package com.gapps.usuarios.mainModule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gapps.usuarios.R
import com.gapps.usuarios.common.entities.UserEntity
import com.gapps.usuarios.databinding.UserItemBinding

class UserAdapter(private var users: MutableList<UserEntity>, private var listener: OnClickListener) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view= LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        with(holder){
            setListener(user)

            binding.txtRegistro.text = "Registro: ${user.id}"
            binding.txtNombre.text = user.getFullName()
            binding.txtTelefono.text = user.telefono.toString()
            binding.txtCorreo.text = user.correo
            binding.txtLat.text = "Latitud: ${user.latitud}"
            binding.txtLng.text = "Longitud: ${user.longitud}"
        }
    }

    fun setUsers(users: List<UserEntity>){
        this.users = users as MutableList<UserEntity>
        notifyDataSetChanged()
    }

    fun add(userEntity: UserEntity){
        if(userEntity.id != 0L){
            if (!users.contains(userEntity)){
                users.add(userEntity)
                notifyItemInserted(users.size-1)
            }
        }
    }

    override fun getItemCount(): Int = users.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = UserItemBinding.bind(view)

        fun setListener(userEntity: UserEntity){
            binding.root.setOnClickListener{listener.onClick(userEntity)}

        }

    }


}