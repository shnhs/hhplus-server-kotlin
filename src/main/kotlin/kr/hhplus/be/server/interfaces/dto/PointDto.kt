package kr.hhplus.be.server.interfaces.dto

class PointDto {

    data class PointChartRequestDto(
        val userId: String,
        val amount: Int
    )
}