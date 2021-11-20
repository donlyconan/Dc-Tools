package base.observable

public interface Observable<T> {
    fun onChanged(values: T?)
}