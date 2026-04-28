package com.kippu.trace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kippu.trace.data.AppDatabase
import com.kippu.trace.data.EventRepository
import com.kippu.trace.model.DateEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository
    val allEvents: StateFlow<List<DateEvent>>

    init {
        val eventDao = AppDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        allEvents = repository.allEvents.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addEvent(event: DateEvent) {
        viewModelScope.launch {
            repository.insert(event)
        }
    }

    fun deleteEvent(event: DateEvent) {
        viewModelScope.launch {
            repository.delete(event)
        }
    }
}
