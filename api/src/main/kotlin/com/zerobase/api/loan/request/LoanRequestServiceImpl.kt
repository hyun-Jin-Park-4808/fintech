package com.zerobase.api.loan.request

import com.zerobase.api.loan.GenerateKey
import com.zerobase.api.loan.encrypt.EncryptComponent
import com.zerobase.domain.repository.UserInfoRepository
import com.zerobase.kafka.enum.KafkaTopic
import com.zerobase.kafka.producer.LoanRequestSender
import org.springframework.stereotype.Service

@Service
class LoanRequestServiceImpl(
        private val generateKey: GenerateKey,
        private val userInfoRepository: UserInfoRepository,
        private val encryptComponent: EncryptComponent,
        private val loanRequestSender: LoanRequestSender

): LoanRequestService {

    override fun loanRequestMain( // 대출 요청 들어오면
            loanRequestInputDto: LoanRequestDto.LoanRequestInputDto
    ): LoanRequestDto.LoanRequestResponseDto {
        val userKey = generateKey.generateUserKey() // userkey 생성

        loanRequestInputDto.userRegistrationNumber =
                encryptComponent.encryptString(loanRequestInputDto.userRegistrationNumber) // 주민등록 번호 암호화

        val userInfoDto = loanRequestInputDto.toUserInfoDto(userKey) // Dto로 정보 변경

        saverUserInfo(userInfoDto) // 유저 정보 저장

        loanRequestReview(userInfoDto) // 정보들 넘겨줌.

        loanRequestReview(userInfoDto) // 심사 요청

        return LoanRequestDto.LoanRequestResponseDto(userKey) // 결과 반환
    }

    override fun saverUserInfo(userInfoDto: UserInfoDto) =
        userInfoRepository.save(userInfoDto.toEntity())

    override fun loanRequestReview(userInfoDto: UserInfoDto) { // 카프카에 정보 보내줌.
        loanRequestSender.sendMessage(
                KafkaTopic.LOAN_REQUEST,
                userInfoDto.toLoanRequestKafkaDto()
        )
    }
}