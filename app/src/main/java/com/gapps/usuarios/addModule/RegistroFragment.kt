package com.gapps.usuarios.addModule

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gapps.usuarios.R
import com.gapps.usuarios.addModule.viewModel.AddViewModel
import com.gapps.usuarios.common.entities.UserEntity
import com.gapps.usuarios.common.utils.Constants
import com.gapps.usuarios.common.utils.ImageController
import com.gapps.usuarios.databinding.FragmentRegistroBinding
import com.gapps.usuarios.mainModule.MainActivity
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout


class RegistroFragment : Fragment() {

    private lateinit var mBinding: FragmentRegistroBinding

    private lateinit var mAddViewModel: AddViewModel

    private var mActivity: MainActivity? = null
    private lateinit var mUserEntity: UserEntity

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    private var haConcedidoPermisos = false

    private var imageUri: Uri? = null

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAddViewModel = ViewModelProvider(requireActivity()).get(AddViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRegistroBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupTextFields()
        mBinding.btnGuadar.setOnClickListener { registro() }
        mBinding.btnCoordenadas.setOnClickListener { verificarPermisos() }
        mBinding.imgPhoto.setOnClickListener { selectFromGallery()}

        activityResultLauncher = registerForActivityResult(StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data!!.data

                mBinding.imgPhoto.setImageURI(imageUri)
            }
        }
    }

    private fun selectFromGallery() {
        ImageController.selectPhotoFromGallery(activityResultLauncher)
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.registrar_datos)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        var item: MenuItem
        item = menu.findItem(R.id.action_registar)
        item.setVisible(false)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        hidekeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mAddViewModel.setResult(Any())

        setHasOptionsMenu(false)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun imprimirUbicacion(ubicacion: Location) {

        with(mBinding){
            txtLat.text = ubicacion.latitude.toString()
            txtLng.text = ubicacion.longitude.toString()
        }
    }

    fun onPermisosConcedidos() {
        // Hasta aquí sabemos que los permisos ya están concedidos
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    imprimirUbicacion(it)
                } else {
                    Log.d(Constants.LOG_TAG, "No se pudo obtener la ubicación")
                }
            }
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    Log.d(Constants.LOG_TAG, "Se recibió una actualización")
                    for (location in locationResult.locations) {
                        imprimirUbicacion(location)
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.d(Constants.LOG_TAG, "Tal vez no solicitaste permiso antes")
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO) {
            val todosLosPermisosConcedidos =
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (grantResults.isNotEmpty() && todosLosPermisosConcedidos) {
                haConcedidoPermisos = true;
                onPermisosConcedidos()
            } else {
                Log.d(Constants.LOG_TAG, "Uno o más permisos fueron denegados")
            }
        }
    }

    private fun verificarPermisos() {
        val permisos = arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        // Segundo plano para Android Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permisos.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        val permisosComoArray = permisos.toTypedArray()
        if (tienePermisos(permisosComoArray)) {
            haConcedidoPermisos = true
            onPermisosConcedidos()
            Log.d(Constants.LOG_TAG, "Los permisos ya fueron concedidos")
        } else {
            solicitarPermisos(permisosComoArray)
        }
    }


    private fun solicitarPermisos(permisos: Array<String>) {
        Log.d(Constants.LOG_TAG, "Solicitando permisos...")
        requestPermissions(
            permisos,
            Constants.CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO
        )
    }

    private fun tienePermisos(permisos: Array<String>): Boolean {
        return permisos.all {
            return ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }




    private fun registro() {
        if (validateFields(mBinding.tilNombre, mBinding.tilApellidos) && validateFieldCorreo() && validateFieldTelefono()){
            with(mUserEntity){
                name = mBinding.etNombre.text.toString().trim()
                apellido = mBinding.etApellidos.text.toString().trim()
                correo = mBinding.etCorreo.text.toString().trim()
                telefono = mBinding.etTelefono.text.toString().toLong()
                latitud = mBinding.txtLat.text.toString().trim()
                longitud = mBinding.txtLng.text.toString().trim()
                photoImg = imageUri?.let { ImageController.savImage(requireContext(), mUserEntity.id, it) }.toString().trim()
            }
                mAddViewModel.saveUser(mUserEntity)
        }
    }

    private fun setupTextFields() {
        with(mBinding) {
            etNombre.addTextChangedListener { validateFields(tilNombre) }
            etApellidos.addTextChangedListener { validateFields(tilApellidos) }
            etTelefono.addTextChangedListener { validateFieldTelefono() }
            etCorreo.addTextChangedListener { validateFieldCorreo() }
        }
    }

    private fun validateFieldCorreo() : Boolean {
        val email = mBinding.tilCorreo.editText?.text.toString()

        return if (email.isEmpty()){
            mBinding.tilCorreo.error = getString(R.string.helper_required)
            false
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            mBinding.tilCorreo.error = getString(R.string.valid_correo)
            false
        }else{
            mBinding.tilCorreo.error = null
            true
        }

    }

    private fun validateFieldTelefono(): Boolean {
        val telefono = mBinding.tilTelefono.editText?.text.toString()

        return if (telefono.isEmpty()){
            mBinding.tilTelefono.error = getString(R.string.helper_required)
            false
        } else if (telefono.length != 10){
            mBinding.tilTelefono.error = getString(R.string.telefono_digitos)
            false
        } else {
            mBinding.tilTelefono.error = null
            true
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else textField.error = null
        }

        if (!isValid) Snackbar.make(
            mBinding.root,
            R.string.register_message_valid,
            Snackbar.LENGTH_SHORT
        ).show()

        return isValid
    }

    private fun setupViewModel() {
        mAddViewModel.getUserSelected().observe(viewLifecycleOwner, {
            mUserEntity = it

            setupActionBar()
        })

        mAddViewModel.getResult().observe(viewLifecycleOwner, { result ->
            hidekeyboard()

            when (result) {
                is Long -> {
                    mUserEntity.id = result

                    mAddViewModel.setUserSelected(mUserEntity)

                    Toast.makeText(
                        mActivity,
                        R.string.message_save_success, Toast.LENGTH_SHORT
                    ).show()

                    mActivity?.onBackPressed()
                }
            }
        })
    }

    private fun hidekeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null){
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }

}