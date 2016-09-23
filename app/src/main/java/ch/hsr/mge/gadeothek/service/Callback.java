package ch.hsr.mge.gadgeothek.service;

public interface Callback<T> {
    void onCompletion(T input);
    void onError(String message);
}
