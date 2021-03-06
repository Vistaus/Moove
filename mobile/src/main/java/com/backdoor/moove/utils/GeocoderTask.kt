package com.backdoor.moove.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Job

/**
 * Copyright 2016 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object GeocoderTask {

    private var mJob: Job? = null

    fun findAddresses(context: Context, address: String, listener: ((List<Address>) -> Unit)?) {
        cancelJob()
        val geocoder = Geocoder(context)
        mJob = launchDefault {
            val addresses: MutableList<Address> = mutableListOf()
            try {
                addresses.addAll(geocoder.getFromLocationName(address, 5))
            } catch (e: Exception) {
            }
            withUIContext {
                listener?.invoke(addresses)
            }
            mJob = null
        }
    }

    fun findAddress(context: Context, lat: Double, lon: Double): Address? {
        val geocoder = Geocoder(context)
        var address: Address? = null
        try {
            address = geocoder.getFromLocation(lat, lon, 1)?.first()
        } catch (e: Exception) {
        }
        return address
    }

    fun cancelJob() {
        mJob?.cancel()
    }
}
