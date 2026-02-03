package anvil.forge.base;

import anvil.forge.base.exception.InvalidSequenceException;
import anvil.forge.base.exception.InvalidSumResultException;
import anvil.forge.base.model.Action;
import anvil.forge.base.model.Variant;
import anvil.forge.base.model.Sequence;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static anvil.forge.utils.SequenceUtils.*;

@Slf4j
public class Calculator {

    public static List<Sequence> getForgingSequence(int[] input) {
        Variant variant = new Variant(input[0], input[1], input[2]);
        int result = input[3];

        List<Sequence> sequence = variant.isNoVariants()
                ? getSequenceFromVariant(result, variant)
                : getSequenceListFromManyVariants(result, variant);
        sequence.removeIf(s -> s.getAction() == Action.EMPTY);

        printSequence(sequence);
        validateResult(sequence, result);
        return sequence;
    }

    private static void validateResult(List<Sequence> sequence, int result) {
        int sum = getSum(sequence);
        log.debug(String.valueOf(sum));
        if (result != sum) {
            throw new InvalidSumResultException("Invalid number: " + sum + ", not equal to " + result);
        }
    }

    private static List<Sequence> getSequenceFromVariant(int result, Variant variant) {
        int raw = result - variant.n1() - variant.n2() - variant.n3();
        var sequence = getSequence(raw);
        sequence.add(new Sequence(1, Action.byValue(variant.n3())));
        sequence.add(new Sequence(1, Action.byValue(variant.n2())));
        sequence.add(new Sequence(1, Action.byValue(variant.n1())));
        return sequence;
    }

    private static List<Sequence> getSequence(int raw) {
        List<Sequence> sequences = new ArrayList<>();
        int currentRaw = raw;

        while (currentRaw >= 0) {
            var sequence = getPositiveSequence(currentRaw);
            if (sequence != null && !sequence.isEmpty()) {
                currentRaw -= getSum(sequence);
                sequences.addAll(sequence);
            } else {
                break;
            }
        }
        return sequences;
    }

    private static List<Sequence> getPositiveSequence(int raw) {
        //Оптимизация для маленьких значений
        if (raw == 12 || raw == 10 || raw == 8) {
            return List.of(new Sequence(raw / Action.STAMP.getValue(), Action.STAMP));
        }
        int tempCountAction = Integer.MAX_VALUE;
        if (raw % 2 == 1) {
            int stampCountAction = Math.floorDiv(raw, Action.STAMP.getValue());
            stampCountAction -= 3;
            Sequence seatSequence = stampCountAction >= 8 ? new Sequence(Math.floorDiv(stampCountAction, Action.SEAT.getValue() / Action.STAMP.getValue()), Action.SEAT) : new Sequence(0, Action.EMPTY);
            stampCountAction = stampCountAction % (Action.SEAT.getValue() / Action.STAMP.getValue());
            Sequence stampSequence = stampCountAction > 0 ? new Sequence(stampCountAction, Action.STAMP) : new Sequence(0, Action.EMPTY);
            Sequence bendSequence = new Sequence(1, Action.BEND);
            return List.of(seatSequence, bendSequence, stampSequence);
        } else {
            Action tempAction = Action.STAMP;
            int bestCountAction = 0;
            var bestAction = Action.STAMP;
            for (Action action : Action.POSITIVE_ACTIONS) {
                int resultDiv = Math.floorDiv(raw, action.getValue());
                // todo проверить что не надо tempCountAction > resultDiv
                if (resultDiv > 0 && tempCountAction >= resultDiv) {
                    tempCountAction = resultDiv;
                    tempAction = action;
                    boolean isExact = raw - tempAction.getValue() * tempCountAction == 0;
                    if (isExact) {
                        bestCountAction = resultDiv;
                        bestAction = action;
                    }
                }
            }
            if (bestCountAction != 0 && !Action.STAMP.equals(bestAction)) {
                tempCountAction = bestCountAction;
                tempAction = bestAction;
            }

            if (tempCountAction == Integer.MAX_VALUE) {
                return null;
            }
            return List.of(new Sequence(tempCountAction, tempAction));
        }
    }

    private static List<Sequence> getSequenceListFromManyVariants(int result, Variant variant) {
        Set<Variant> variants = new HashSet<>(List.of(variant));
        List<Variant> variantList = new ArrayList<>(List.of(variant));
        ListIterator<Variant> iterator = variantList.listIterator();
        List<List<Sequence>> sequences = new ArrayList<>(List.of(getSequenceFromVariant(result, variant)));
        int i = 0;
        while (iterator.hasNext()) {
            Variant current = iterator.next();
            if (current.isVariableN1()) {
                var newVariant = new Variant(current.n1() - 3, current.n2(), current.n3());
                addVariantAndSequence(result, variants, newVariant, iterator, sequences);
            }
            if (current.isVariableN2()) {
                var newVariant = new Variant(current.n1(), current.n2() - 3, current.n3());
                addVariantAndSequence(result, variants, newVariant, iterator, sequences);
            }
            if (current.isVariableN3()) {
                var newVariant = new Variant(current.n1(), current.n2(), current.n3() - 3);
                addVariantAndSequence(result, variants, newVariant, iterator, sequences);
            }
            iterator = variantList.listIterator(++i);
        }

        var firstAnySeq = sequences.getFirst();
        sequences.removeIf(e -> result == getSum(e));
        validateSequences(sequences, firstAnySeq);
        return getShortestSequence(sequences);
    }

    private static void addVariantAndSequence(int result, Set<Variant> variants, Variant newVariant,
                                              ListIterator<Variant> iterator, List<List<Sequence>> sequences) {
        if (variants.add(newVariant)) {
            iterator.add(newVariant);
            sequences.add(getSequenceFromVariant(result, newVariant));
        }
    }

    private static List<Sequence> getShortestSequence(List<List<Sequence>> sequences) {
        var shortestSequence = sequences.getFirst();
        int length = getCount(shortestSequence);
        for (List<Sequence> s : sequences) {
            int l = getCount(s);
            if (l < length) {
                length = l;
                shortestSequence = s;
            }
        }
        return shortestSequence;
    }

    private static void validateSequences(List<List<Sequence>> sequences, List<Sequence> firstAnySeq) {
        if (sequences.isEmpty()) {
            log.warn("There is no suitable sequence, print first:");
            printSequence(firstAnySeq);
            throw new InvalidSequenceException("WARNING! There is no suitable sequence!");
        }
    }
}
