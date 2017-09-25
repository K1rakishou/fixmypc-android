package com.kirakishou.fixmypc.fixmypcapp.mvvm.model.entity.response

import com.kirakishou.fixmypc.fixmypcapp.mvvm.model.ErrorCode

/**
 * Created by kirakishou on 9/25/2017.
 */
class RespondToDamageClaimResponse(errorCode: ErrorCode.Remote) : StatusResponse(errorCode)