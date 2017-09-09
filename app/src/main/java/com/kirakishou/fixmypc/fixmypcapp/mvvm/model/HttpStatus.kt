package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

/**
 * Created by kirakishou on 7/26/2017.
 */
enum class HttpStatus(val status: Int) {
    OK(200),
    CREATED(201),

    CONFLICT(409),
    UNPROCESSABLE_ENTITY(422),

    INTERNAL_SERVER_ERROR(500);


    companion object {
        fun from(value: Int): HttpStatus {
            for (status in HttpStatus.values()) {
                if (status.status == value) {
                    return status
                }
            }

            throw IllegalArgumentException("Unknown value: $value")
        }
    }
}