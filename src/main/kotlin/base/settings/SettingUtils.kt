package base.settings

import java.util.prefs.Preferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class DelegateStringType(
    val key: String, val preferences: Preferences, val default: String?
) : ReadWriteProperty<Settings, String?> {

    override operator fun getValue(thisRef: Settings, property: KProperty<*>): String? {
        return preferences.get(key, default)
    }

    override operator fun setValue(thisRef: Settings, property: KProperty<*>, value: String?) {
        preferences.put(key, value)
    }
}


class DelegateFloatType(
    val key: String, val preferences: Preferences, val default: Float
) : ReadWriteProperty<Settings, Float> {

    override operator fun getValue(thisRef: Settings, property: KProperty<*>): Float {
        return preferences.getFloat(key, default)
    }

    override operator fun setValue(thisRef: Settings, property: KProperty<*>, value: Float) {
        preferences.putFloat(key, value)
    }

}


class DelegateIntType(
    val key: String, val preferences: Preferences, val default: Int
) : ReadWriteProperty<Settings, Int> {

    override operator fun getValue(thisRef: Settings, property: KProperty<*>): Int {
        return preferences.getInt(key, default);
    }

    override operator fun setValue(thisRef: Settings, property: KProperty<*>, value: Int) {
        preferences.putInt(key, value);
    }

}


class DelegateBooleanType(
    val key: String, val preferences: Preferences, val default: Boolean
) : ReadWriteProperty<Settings, Boolean> {

    override operator fun getValue(thisRef: Settings, property: KProperty<*>): Boolean {
        return preferences.getBoolean(key, default);
    }

    override operator fun setValue(thisRef: Settings, property: KProperty<*>, value: Boolean) {
        preferences.putBoolean(key, value);
    }

}


class DelegateDoubleType(
    val key: String, val preferences: Preferences, val default: Double
) : ReadWriteProperty<Settings, Double> {

    override operator fun getValue(thisRef: Settings, property: KProperty<*>): Double {
        return preferences.getDouble(key, default);
    }

    override operator fun setValue(thisRef: Settings, property: KProperty<*>, value: Double) {
        preferences.putDouble(key, value);
    }

}


