package com.digitaldream.linkskool.utils;

public class AsyncTaskResult<T> {

    private T mResult;
    private Exception mException;

    public AsyncTaskResult(T sResult) {
        super();
        mResult = sResult;
    }

    public AsyncTaskResult(Exception sException) {
        super();
        mException = sException;
    }

    public T getResult() {
        return mResult;
    }

    public Exception getException() {
        return mException;
    }
}
