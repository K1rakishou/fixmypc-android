package com.kirakishou.fixmypc.fixmypcapp.mvp.model.entity.response

import com.kirakishou.fixmypc.fixmypcapp.mvp.model.ErrorCode

/**
 * Created by kirakishou on 8/1/2017.
 */
class MalfunctionResponse(error: ErrorCode.Remote) : StatusResponse(error) {
}