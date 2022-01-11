package msa.myfit.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import msa.myfit.R
import msa.myfit.R.id.google_maps
import org.json.JSONObject


class MapFragment(private val mainActivity: AppCompatActivity) : Fragment(), OnMapReadyCallback,
    LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mLastLocation: Location
    private var mCurrLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mLocationRequest: LocationRequest

    private var firstRun: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(google_maps) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        return rootView
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(mainActivity.applicationContext)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)

            if(firstRun){
                mFusedLocationClient!!.lastLocation.addOnCompleteListener { location ->
                    val toLat = 10.3181466
                    val toLong = 123.9029382

//                    getAndDisplayDirections(location.result, toLat, toLong)
                    firstRun = false
                }
            }
        }
    }

    private fun getAndDisplayDirections(mLastLocation: Location, toLat: Double, toLong: Double) {
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=${mLastLocation.latitude},${mLastLocation.longitude}&destination=${toLat},${toLong}&key=<${resources.getString(R.string.google_maps_key)}>"
        val directionsRequest = object : StringRequest(Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
            }
        }, Response.ErrorListener {}){}
        val requestQueue = Volley.newRequestQueue(mainActivity)
        requestQueue.add(directionsRequest)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val INITIAL_PERMS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val INITIAL_REQUEST=1337;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient()
                mMap.isMyLocationEnabled = true
            }
            else{
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST)
                buildGoogleApiClient()
                mMap.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }

        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

        override fun onLocationChanged(location: Location) {

            mLastLocation = location
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker!!.remove()
            }

            val latLng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            mCurrLocationMarker = mMap.addMarker(markerOptions)

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
        }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(this.context, "connection failed", Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(p0: Int) {
        Toast.makeText(this.context, "connection suspended", Toast.LENGTH_SHORT).show()
    }
}