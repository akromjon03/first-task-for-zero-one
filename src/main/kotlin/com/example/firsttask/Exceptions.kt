package com.example.firsttask

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

sealed class TaskExceptionHandler:RuntimeException(){
    abstract fun errorCode(): ErrorCodes
    open fun getAllArguments(): Array<Any?>? = null

    fun getErrorMessage(resourceBundle : ResourceBundleMessageSource): BaseMessage{
        val message = resourceBundle.getMessage(
            errorCode().name, getAllArguments(), LocaleContextHolder.getLocale()
        )

        return BaseMessage(errorCode().code, message);
    }
}

class UserAlreadyExistsException : TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_ALREADY_EXISTS
}

class UserNotFoundExistsException : TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_NOT_FOUND
}

class NotFoundExistsTransactionException: TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.PAYMENT_TRANSACTION_NOT_FOUND
}

class CategoryNotFoundException: TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.CATEGORY_NOT_FOUND
}

class ProductNotFoundException: TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_NOT_FOUND
}

class NotEnoughBalanceException: TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.NOT_ENOUGH_BALANCE
}

class NotEnoughProductException: TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.NOT_ENOUGH_PRODUCT
}

class TransactionNotFoundException: TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.TRANSACTION_NOT_FOUND
}

class TransactionItemNotFoundException: TaskExceptionHandler() {
    override fun errorCode() = ErrorCodes.TRANSACTION_ITEM_NOT_FOUND
}




