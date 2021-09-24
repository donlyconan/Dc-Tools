

fun <T> List<T>.findIndex(compare: (item: T) -> Boolean): Int {
    for ((id, item) in withIndex()) {
        if (compare.invoke(item)) {
            return id
        }
    }
    return -1
}



