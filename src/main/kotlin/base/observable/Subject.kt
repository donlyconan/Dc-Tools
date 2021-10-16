package base.observable

import base.logger.Log

class Subject<T> {
    private val observables = ArrayList<Observable<T>>()
    private var currentData: T? = null

    fun observe(observable: Observable<T>) {
        Log.d("Observe: " + observable.javaClass.simpleName)
        observables.add(observable)
    }

    fun remove(observable: Observable<T>) {
        observables.remove(observable)
    }

    fun summit(newData: T) {
        for (observable in observables) {
            observable.onChanged(newData)
        }
    }

}