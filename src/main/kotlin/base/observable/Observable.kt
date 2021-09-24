package base.observable

interface Observable<T> {
    fun onChanged(values: T?)
}