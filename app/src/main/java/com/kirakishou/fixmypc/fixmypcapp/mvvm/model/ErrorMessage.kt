package com.kirakishou.fixmypc.fixmypcapp.mvvm.model

import android.content.Context
import com.kirakishou.fixmypc.fixmypcapp.R

/**
 * Created by kirakishou on 7/27/2017.
 */
object ErrorMessage {

    fun getRemoteErrorMessage(context: Context, errorCode: ErrorCode.Remote): String {
        val resources = context.resources

        when (errorCode) {
            ErrorCode.Remote.REC_UNKNOWN_SERVER_ERROR -> return resources.getString(R.string.rec_unknown_server_error)
            ErrorCode.Remote.REC_OK -> throw IllegalArgumentException("Should not happen since STATUS_OK is not handled by any error handlers")
            ErrorCode.Remote.REC_WRONG_LOGIN_OR_PASSWORD -> return resources.getString(R.string.rec_wrong_login_or_password)
            ErrorCode.Remote.REC_LOGIN_ALREADY_EXISTS -> return resources.getString(R.string.rec_login_already_registered)
            ErrorCode.Remote.REC_LOGIN_IS_INCORRECT -> return resources.getString(R.string.rec_login_is_incorrect)
            ErrorCode.Remote.REC_PASSWORD_IS_INCORRECT -> {
                return String.format(resources.getString(R.string.rec_wrong_login_or_password),
                        resources.getInteger(R.integer.min_password_length),
                        resources.getInteger(R.integer.max_password_length))
            }
            ErrorCode.Remote.REC_ACCOUNT_TYPE_IS_INCORRECT -> return resources.getString(R.string.rec_account_type_is_incorrect)
            ErrorCode.Remote.REC_TIMEOUT -> return "Таймаут сетевого запроса. Попробуйте повторить запрос позже"
            ErrorCode.Remote.REC_COULD_NOT_CONNECT_TO_SERVER -> return "Сервер не отвечает. Попробуйте повторить запрос позже"
            ErrorCode.Remote.REC_BAD_SERVER_RESPONSE_EXCEPTION -> return "Получен некорректный ответ от сервера. Попробуйте повторить запрос позже"
            ErrorCode.Remote.REC_COULD_NOT_RESPOND_TO_DAMAGE_CLAIM -> return "Не удалось добавить запрос к данному обхявлению. Попробуйте повторить запрос позже"
            ErrorCode.Remote.REC_DAMAGE_CLAIM_DOES_NOT_EXIST -> return "Объявление не существует"
            ErrorCode.Remote.REC_DAMAGE_CLAIM_IS_NOT_ACTIVE -> return "Объявление закрыто"
            ErrorCode.Remote.REC_NO_PHOTOS_WERE_SELECTED_TO_UPLOAD -> return "Не выбраны фото поломки"
            ErrorCode.Remote.REC_IMAGES_COUNT_EXCEEDED -> return "Нельзя отправить больше 4х фото"
            ErrorCode.Remote.REC_WIFI_IS_NOT_CONNECTED -> return "Отсутствует подключение WiFi. Если Вы хотите хотите отправлять заявки даже при отключенном WiFi - " +
                    "отключите в настройках опцию \"Запретить отправлять заявки при отключенном WiFi\""
            ErrorCode.Remote.REC_FILE_SIZE_EXCEEDED -> return "Размер одного из выбранных изображений превышает лимит"
            ErrorCode.Remote.REC_REQUEST_SIZE_EXCEEDED -> return "Размер изображений превышает лимит"
            ErrorCode.Remote.REC_ALL_FILE_SERVERS_ARE_NOT_WORKING -> return "Не удалось обработать запрос. Сервера не работают. Попробуйте повторить запрос позже"
            ErrorCode.Remote.REC_DATABASE_ERROR -> return "Ошибка БД на сервере. Попробуйте повторить запрос позже"
            ErrorCode.Remote.REC_SELECTED_PHOTO_DOES_NOT_EXISTS -> return "Не удалось прочитать фото с диска (оно было удалено или перемещено)"
            ErrorCode.Remote.REC_RESPONSE_BODY_IS_EMPTY -> return "Response body is empty!"
            ErrorCode.Remote.REC_DUPLICATE_ENTRY_EXCEPTION -> return "Нельзя отправить два одинаковых файла"
            ErrorCode.Remote.REC_BAD_ORIGINAL_FILE_NAME -> return "Попытка отправить файл не являющийся изображением"
            ErrorCode.Remote.REC_SESSION_ID_EXPIRED -> return "REC_SESSION_ID_EXPIRED"
            ErrorCode.Remote.REC_LOGIN_IS_TOO_LONG -> return "Введённый логин слишком длинный"
            ErrorCode.Remote.REC_USER_INFO_IS_EMPTY -> return "USER_INFO_IS_EMPTY"
            ErrorCode.Remote.REC_COULD_NOT_UPDATE_SESSION_ID -> return "Не удалось перелогиниться. Попробуйте повторить запрос позже"
            ErrorCode.Remote.REC_BAD_ACCOUNT_TYPE -> return "Невозможно выполнить операцию из данного аккаунта"
            ErrorCode.Remote.REC_COULD_NOT_REMOVE_RESPONDED_SPECIALISTS -> return "Ошибка на сервере"
            ErrorCode.Remote.REC_DAMAGE_CLAIM_DOES_NOT_BELONG_TO_USER -> return "Попытка доступа к данным, которые не принадлежат пользователю"
            ErrorCode.Remote.REC_COULD_NOT_FIND_PROFILE -> return "Не удалось найти профиль"
            ErrorCode.Remote.REC_EMPTY_OBSERVABLE_ERROR -> return "EMPTY_OBSERVABLE_ERROR"
            ErrorCode.Remote.REC_COULD_NOT_UPLOAD_IMAGE -> return "Не удалось загрузить фото"
            ErrorCode.Remote.REC_REPOSITORY_ERROR -> return "Неизвестная ошибка репозитория на сервере"
            ErrorCode.Remote.REC_COULD_NOT_DELETE_OLD_IMAGE -> return "Не удалось удалить предыдущее фото"
            ErrorCode.Remote.REC_STORE_ERROR -> return "Неизвестная ошибка репозитория на сервере"
            ErrorCode.Remote.REC_PROFILE_IS_NOT_FILLED_IN -> return "Профиль не заполнен"

            else -> throw IllegalArgumentException("Unknown statusCode: $errorCode")
        }
    }

    fun getLocalErrorMessage(context: Context, errorCode: ErrorCode.Local): String {
        val resources = context.resources

        when (errorCode) {
            ErrorCode.Local.LEC_MAI_DESCRIPTION_IS_NOT_SET -> return resources.getString(R.string.lec_mai_description_is_not_set)
            ErrorCode.Local.LEC_MAI_CATEGORY_IS_NOT_SET -> return resources.getString(R.string.lec_mai_category_is_not_set)
            ErrorCode.Local.LEC_MAI_PHOTOS_ARE_NOT_SET -> return resources.getString(R.string.lec_mai_photos_are_not_set)

            else -> throw IllegalArgumentException("Unknown statusCode: $errorCode")
        }
    }
}