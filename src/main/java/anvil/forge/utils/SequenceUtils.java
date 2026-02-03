package anvil.forge.utils;

import anvil.forge.base.model.Action;
import anvil.forge.base.model.Sequence;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public final class SequenceUtils {

    private SequenceUtils() {
    }

    public static void printSequence(List<Sequence> sequence) {
        for (Sequence s : sequence) {
            if (s.getAction() != Action.EMPTY) {
                log.info(s.toString());
            }
        }
    }

    public static int getSum(List<Sequence> sequence) {
        return sequence.stream().mapToInt(Sequence::getSumValue).sum();
    }

    public static int getCount(List<Sequence> sequence) {
        return sequence.stream().mapToInt(Sequence::getCount).sum();
    }
}
