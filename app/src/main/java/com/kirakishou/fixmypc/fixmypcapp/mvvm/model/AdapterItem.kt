package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 7/31/2017.
 */
open class AdapterItem<Value> {
    private var type: Int = -1
    var value: Fickle<Value> = Fickle.empty()

    constructor(value: Value, type: AdapterItemType) {
        this.type = type.ordinal
        this.value = Fickle.of(value)
    }

    constructor(type: AdapterItemType) {
        this.type = type.ordinal
    }

    fun setType(type: AdapterItemType) {
        this.type = type.ordinal
    }

    fun getType(): Int = type
}