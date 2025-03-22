package com.technotrade.pts2.pts2testapp.gui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.technotrade.pts2.pts2testapp.entity.Event;
import com.technotrade.pts2.pts2testapp.entity.EventCommand;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BaseViewModel extends ViewModel {
    protected MutableLiveData<ConcurrentLinkedQueue<Event<EventCommand<?>>>> mViewModelCommandQueue;

    public BaseViewModel() {
        mViewModelCommandQueue = new MutableLiveData<>();
        mViewModelCommandQueue.setValue(new ConcurrentLinkedQueue<>());
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public LiveData<ConcurrentLinkedQueue<Event<EventCommand<?>>>> getViewModelCommandQueue() {
        return mViewModelCommandQueue;
    }

    public void sendViewModelCommandEvent(EventCommand<?> command) {
        ConcurrentLinkedQueue<Event<EventCommand<?>>> currentQueue = mViewModelCommandQueue.getValue();
        if (currentQueue != null) {
            currentQueue.add(new Event<>(command, true));
            mViewModelCommandQueue.postValue(currentQueue);
        }
    }

    public void sendViewModelCommandEvent(String command, Object data) {
        EventCommand<?> eventCommand = new EventCommand<>(command, data);
        sendViewModelCommandEvent(eventCommand);
    }
}