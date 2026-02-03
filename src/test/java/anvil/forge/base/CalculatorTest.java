package anvil.forge.base;

import anvil.forge.base.exception.InvalidSumResultException;
import anvil.forge.base.model.Sequence;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;

import static anvil.forge.base.Calculator.getForgingSequence;

public class CalculatorTest {

    @ParameterizedTest
    @MethodSource("source")
    void test(int expected) {
        int[] input = {0, 0, 0, expected};
        if (expected == 1 || expected == 3 || expected == 5) {
            Assertions.assertThrows(InvalidSumResultException.class, () -> getForgingSequence(input));
        } else {
            var actual = getForgingSequence(input);
            Assertions.assertEquals(expected, actual.stream().mapToInt(Sequence::getSumValue).sum());
        }
    }

    static IntStream source() {
        return IntStream.range(1, 151);
    }
}
