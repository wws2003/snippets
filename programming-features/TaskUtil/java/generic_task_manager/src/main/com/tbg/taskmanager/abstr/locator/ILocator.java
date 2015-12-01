package com.tbg.taskmanager.abstr.locator;

/**
 * Created by wws2003 on 10/30/15.
 */
public interface ILocator<T> {
    void pushItem(T item, long itemId) throws Exception;
    T getItem(long itemId);
    void removeItem(long itemId) throws Exception;
    void clearAllItems();
}
