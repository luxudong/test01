package com.innoxyz.InnoXYZAndroid.data.runtime.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
public interface IDataObservable {
    public void registerObserver(IDataObserver observer);
    public void removeObserver(IDataObserver observer);
    public void notifyObservers();
}
