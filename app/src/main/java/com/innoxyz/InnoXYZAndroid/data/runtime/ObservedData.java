package com.innoxyz.InnoXYZAndroid.data.runtime;

import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObservable;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class ObservedData<T> implements IDataObservable {
    abstract public T getData();
    abstract public void setData(T data, boolean notify);

    List<IDataObserver> observers = new ArrayList<IDataObserver>();

    @Override
    public void registerObserver(IDataObserver observer) {
        if ( !observers.contains(observer) ) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IDataObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(IDataObserver observer : observers) {
            observer.update(getData());
        }
    }
}
