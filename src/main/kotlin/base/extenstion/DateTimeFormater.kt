package base.extenstion

import java.text.SimpleDateFormat
import java.util.*

public fun SimpleDateFormat.fromLong(date: Long): String? {
    return format(Date(date))
}