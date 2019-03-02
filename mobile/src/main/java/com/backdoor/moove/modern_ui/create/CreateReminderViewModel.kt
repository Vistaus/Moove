package com.backdoor.moove.modern_ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.backdoor.moove.data.Place
import com.backdoor.moove.data.Reminder
import com.backdoor.moove.data.RoomDb
import com.backdoor.moove.utils.LocationEvent
import com.backdoor.moove.utils.TimeUtils
import com.backdoor.moove.utils.launchDefault
import kotlinx.coroutines.runBlocking
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*

class CreateReminderViewModel(val uuId: String) : ViewModel(), KoinComponent {

    private val db: RoomDb by inject()
    private val locationEvent: LocationEvent by inject()

    val loadedReminder = db.reminderDao().loadById(uuId)
    var reminder: Reminder = Reminder()
    var original: Reminder? = null
    var isReminderEdited = false
    var isMessage: Boolean = false
    var isDelayAdded: Boolean = false
    var isLeave: Boolean = false

    var day: Int = 0
    var month: Int = 0
    var year: Int = 0

    var hour: Int = 0
    var minute: Int = 0
    var isPaused: Boolean = false
    var isSaving: Boolean = false

    init {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
    }

    fun saveAndStart(reminder: Reminder, addPlace: Boolean = false) {
        launchDefault {
            runBlocking {
                locationEvent.withReminder(reminder).start()
                if (addPlace) {
                    val old = db.placeDao().getByCoord(reminder.latitude, reminder.longitude)
                    if (old == null) {
                        val place = Place().apply {
                            createdAt = TimeUtils.gmtDateTime
                            latitude = reminder.latitude
                            longitude = reminder.longitude
                            markerColor = reminder.markerColor
                            name = reminder.summary
                        }
                        db.placeDao().insert(place)
                    }
                }
            }
        }
    }

    fun pauseReminder(reminder: Reminder) {
        if (reminder.isActive) {
            launchDefault {
                locationEvent.withReminder(reminder).pause()
            }
        }
    }

    fun resumeReminder(reminder: Reminder) {
        if (reminder.isActive) {
            launchDefault {
                locationEvent.withReminder(reminder).resume()
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        launchDefault {
            runBlocking {
                locationEvent.withReminder(reminder).stop()
                db.reminderDao().delete(reminder)
            }
        }
    }

    class Factory(val id: String) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CreateReminderViewModel(id) as T
        }
    }
}
