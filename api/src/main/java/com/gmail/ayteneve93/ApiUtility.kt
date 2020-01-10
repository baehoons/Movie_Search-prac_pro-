package com.gmail.ayteneve93

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val mGson = Gson()
fun <T> T.serializeToStringMap() : Map<String, String> = convert()
inline fun <reified T> Map<String, String>.toDataClass() : T = convert()
inline fun <I, reified O> I.convert() : O = mGson.fromJson(mGson.toJson(this), object : TypeToken<O>(){}.type)