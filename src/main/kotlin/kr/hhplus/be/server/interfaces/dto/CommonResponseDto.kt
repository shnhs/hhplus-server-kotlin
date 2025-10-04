package kr.hhplus.be.server.interfaces.dto

data class CommonResponseDto<T>(
    val code: Int = 0,
    val message: String = "",
    val result: T? = null
) {
    constructor(result: T) : this(
        code = 0,
        message = "",
        result = result
    )
}