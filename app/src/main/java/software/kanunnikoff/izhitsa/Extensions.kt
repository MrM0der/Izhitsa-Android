package software.kanunnikoff.izhitsa

import java.math.BigDecimal

fun Int.usd(): BigDecimal {
    return this.toBigDecimal()
}

infix fun Int.percentOf(value: BigDecimal): BigDecimal {
    return value * this.toBigDecimal() / 100.toBigDecimal()
}