package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.exceptions

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 8/25/2017.
 */
class ApiException(val errorCode: ErrorCode.Remote,
                   val statusCode: Int) : RuntimeException()