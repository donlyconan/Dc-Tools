package base.extenstion

import java.text.SimpleDateFormat
import java.util.*

fun SimpleDateFormat.fromLong(date: Long): String? {
    return format(Date(date))
}