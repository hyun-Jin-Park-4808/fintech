package com.zerobase.api.loan.review

import com.zerobase.api.exception.CustomException
import com.zerobase.api.exception.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackageClasses = [LoanReviewController::class]) // LoanReviewController 에 대해서만 예외처리할 것임.
class LoanReviewControllerAdvice {

    @ExceptionHandler(CustomException::class) // CustionException 예외가 발생하면 아래 함수가 동작됨.
    fun customExceptionHandler(customException: CustomException) =
            ErrorResponse(customException).toResponseEntity()
}