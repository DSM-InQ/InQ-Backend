package kr.hs.dsm.inq.common.error

abstract class CustomException(
    val errorProperty: ErrorProperty
) : RuntimeException()
