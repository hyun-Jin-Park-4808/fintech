package com.zerobase.consumer.service

import com.zerobase.consumer.dto.ReviewResponseDto
import com.zerobase.domain.domain.LoanReview
import com.zerobase.domain.repository.LoanReviewRepository
import com.zerobase.kafka.dto.LoanRequestDto
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class LoanRequestService(
        private val loanReviewRepository: LoanReviewRepository
) {
    companion object {
        const val nginxUrl = "http://nginx:8085/css/api/v1/request"
    }

    fun loanRequest(loanRequestDto: LoanRequestDto) {
        // TODO: CB Component 로 요청 보내기 -> 응답값을 DB에 저장하기
        val reviewResult = loanRequestToCb(loanRequestDto)

        saveLoanReviewData(reviewResult.toLoanReviewEntity())
    }

    fun loanRequestToCb(loanRequestDto: LoanRequestDto): ReviewResponseDto {
        val restTemplate = RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(1000))
                .setReadTimeout(Duration.ofMillis(1000))
                .build()

        return restTemplate.postForEntity(nginxUrl, loanRequestDto, ReviewResponseDto::class.java).body!!
    }

    private fun saveLoanReviewData(loanReview: LoanReview) = loanReviewRepository.save(loanReview)
}