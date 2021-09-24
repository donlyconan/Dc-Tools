package base.observable

class Subject<T> {
    private val observables = ArrayList<Observable<T>>()
    private var currentData: T? = null

    fun observe(observable: Observable<T>) {
        observables.add(observable)
    }

    fun cancel(observable: Observable<T>) {
        observables.remove(observable)
    }

    fun summit(newData: T) {
        if(newData != currentData) {
            for (observable in observables) {
                observable.onChanged(newData)
            }
            currentData = newData
        }
    }

}