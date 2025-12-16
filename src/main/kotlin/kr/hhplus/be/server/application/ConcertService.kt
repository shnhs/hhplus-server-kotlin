package kr.hhplus.be.server.application

import kr.hhplus.be.server.infrastructure.persistence.ConcertJpaRepo
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertResponseDto
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val concertJpaRepo: ConcertJpaRepo
) {

    /**
     * 콘서트 정보 조회
     */
    fun getConcertDetail(concertId: String)
            : ConcertResponseDto {
        val concertEntity = concertJpaRepo.findByUuid(concertId)
            ?: throw IllegalStateException("존재하지 않는 콘서트 입니다.")

        return concertEntity.toDto()
    }
}