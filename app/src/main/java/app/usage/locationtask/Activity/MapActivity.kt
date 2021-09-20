package app.usage.locationtask.Activity

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.usage.locationtask.FieldSelector
import app.usage.locationtask.R
import app.usage.locationtask.database.DataBaseHelper
import app.usage.locationtask.database.UserModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.io.IOException
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var ivSaveData: ImageView? = null
    private var ivSavePlaces: ImageView? = null
    private var textSearch: TextView? = null
    private var fieldSelector: FieldSelector? = null
    private var strLongitudeMap: Double = 0.0
    private var strLatitudeMap: Double = 0.0
    private var onCameraIdleListener: OnCameraIdleListener? = null
    private var mMap: GoogleMap? = null
    var AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var databaseHelper: DataBaseHelper
    var placeName: String = ""
    var countryCode: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        databaseHelper = DataBaseHelper(this@MapActivity)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key), Locale.US)
        }
        fieldSelector = FieldSelector()
        onGoogleMap()

        textSearch = findViewById<TextView>(R.id.txt_search)

        textSearch!!.setOnClickListener {

            try {

//
                val autocompleteIntent = Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN,
                        fieldSelector!!.getAllFields()
                )
                        .setCountry("il") //                            .setCountry(locale1)
                        .build(this@MapActivity)
                startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }


        val extras = intent.extras
        if (extras != null) {
            val FirstName = extras.getString("FirstName")
            val profileEmail = extras.getString("profileEmail")
            val profileId = extras.getString("profileId")
            val personPhotoUrl = extras.getString("personPhotoUrl")
            //The key argument here must match that used in the other activity


//            val profileImage = findViewById<ImageView>(R.id.profileImage)
//            val name = findViewById<TextView>(R.id.name)
//            val email = findViewById<TextView>(R.id.email)
//            val userId = findViewById<TextView>(R.id.userId)
//
//            Glide.with(this@MapActivity).load(personPhotoUrl).into(profileImage)

        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ivSaveData = findViewById(R.id.ivSaveData)

        ivSaveData!!.setOnClickListener {


            AddDataToDataBase(placeName, strLatitudeMap, strLongitudeMap, countryCode)

        }
        ivSavePlaces = findViewById(R.id.ivSavePlaces)
        ivSavePlaces!!.setOnClickListener {
            val intent = Intent(this@MapActivity, FavoritePlaces::class.java)
            startActivity(intent)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        Common.getCurrentLanguage(activity, false, false);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val latLng = place.latLng.toString()
                val tempArray = latLng.substring(latLng.indexOf("(") + 1, latLng.lastIndexOf(")"))
                        .split(",".toRegex()).toTypedArray()
                val latitude = tempArray[0].toDouble()
                val longitude = tempArray[1].toDouble()
                loadMapView(latitude, longitude)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status: Status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun loadMapView(latitude: Double, longitude: Double) {
        strLatitudeMap = latitude
        strLongitudeMap = longitude
        if (mMap != null) {
            mMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                    strLatitudeMap,
                                    strLongitudeMap
                            ), 16f
                    )
            )
            //            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(strLatitudeMap, strLongitudeMap), 16));
        }
//        configureCameraIdle()
    }

    private fun onGoogleMap() {
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Create a new Places client instance.
        val placesClient = Places.createClient(this)

        // Initialize the AutocompleteSupportFragment.
        // Initialize the AutocompleteSupportFragment.
//


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        mMap!!.setOnCameraIdleListener(onCameraIdleListener);
        mMap!!.setOnCameraIdleListener {
            val latLng: LatLng = mMap!!.cameraPosition.target
            //                Geocoder geocoder = new Geocoder(activity);
            val geocoder = Geocoder(this@MapActivity)
            strLatitudeMap = latLng.latitude
            strLongitudeMap = latLng.longitude
            try {
                val addressList: List<Address>? =
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    try {
                        val locality: String = addressList[0].getAddressLine(0)

                        if (addressList[0].countryCode.isNotEmpty()) {
                            countryCode = addressList[0].postalCode
                        } else {
                            countryCode = ""
                        }

//                    val country: String = addressList[0].countryName
//                    val city: String = addressList[0].locality
                        if (locality.isNotEmpty()) {
                            textSearch!!.text = locality
                            placeName = locality
                        }

                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        loadMapView(-34.0, 151.0)
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f))
    }

    private fun configureCameraIdle() {
        onCameraIdleListener = OnCameraIdleListener {
            val latLng: LatLng = mMap!!.cameraPosition.target
            //                Geocoder geocoder = new Geocoder(activity);
            val geocoder = Geocoder(this@MapActivity)
            strLatitudeMap = latLng.latitude
            strLongitudeMap = latLng.longitude
            try {
                val addressList: List<Address>? =
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    val locality: String = addressList[0].getAddressLine(0)
                    val country: String = addressList[0].countryName
                    val city: String = addressList[0].locality
                    try {
                        if (locality != null && !locality.isEmpty()) {

                            textSearch!!.text = locality
//                            txtAddressFull.setText(locality)
                            //                            checkValidAddress(doubleLatChef, doubleLangChef, strLatitudeMap, strLongitudeMap);
                        } else if (city != null && !city.isEmpty()) {
                            textSearch!!.text = city
//                            txtAddressFull.setText(city)
                            //                            checkValidAddress(doubleLatChef, doubleLangChef, strLatitudeMap, strLongitudeMap);
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //        CurrentLocation = false
        }
    }

    //region database
    //DataBase Add User in the Table  Work
    private fun AddDataToDataBase(
            placeName: String,
            strLatitudeMap: Double,
            strLongitudeMap: Double,
            countryCode: String
    ) {
        if (!databaseHelper.checkUser(placeName)) {

            val user = UserModel(
                    name = placeName,
                    lat = strLatitudeMap.toString(),
                    Long = strLongitudeMap.toString(),
                    pinCode = countryCode

            )
            databaseHelper.addUser(user)

            // Snack Bar to show success message that record saved successfully
            Toast.makeText(this, "Place is now in your favorite list", Toast.LENGTH_SHORT).show()

        } else {
            // Snack Bar to show error message that record already exists

            Toast.makeText(this, "its all ready in your favorite list ", Toast.LENGTH_SHORT).show()
        }


    }


    // endregion


}