package br.com.creditas.kotlinspringsample

import br.com.creditas.auth.testsupport.AutoConfigureCreditasAuthTest
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@AutoConfigureCreditasAuthTest
class SampleTest {

    @Test
    fun `Fluent assertions example with 'Kluent' lib`() {
        val computedList = listOf(1, 2, 3)
        val desiredList = listOf(1, 2, 3)

        computedList shouldContainSame desiredList
        computedList.any { it == 4 } shouldBe false
    }
}
