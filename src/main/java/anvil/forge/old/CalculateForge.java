package anvil.forge.old;

import anvil.forge.base.model.Action;
import anvil.forge.base.model.Sequence;

import java.util.*;

public class CalculateForge {

    /*
    -3 Слабо ударить
    -6 Ударить
    -9 Сильно ударить
    -15 Протянуть

    +2 Штамповать
    +7 Изогнуть
    +13 Обжать
    +16 Усадить
     */

    /*
    Кирка:
    49 итоговое число
    Штамповать последний +2
    Изогнуть не последний +7
    Протянуть не последний -15
    */

    /*
    Нож:
    63 итоговое число
    Штамповать последний +2
    Изогнуть не последний +7
    Протянуть не последний -15
    */

    /*
    Пила:
    101 итоговое число
    Ударить последний -3
    Ударить не последний -3
    */
    /*
    Молот:
    67 итоговое число
    Штамповать последний +2
    Усадить не последний +16
    */

    public void main(String[] args) {
        /*
        На вход подаётся 3 числа - это последние комбинации которыми нужно получить итоговое число

        int[] array = {-3, -3, -15, 103}; мб надо отрицательные считать или любой


        int[] array = {-15, 0, 0, 79}; не работает, починил - проверить

        3x16
        2
        7
        7
        -9
        */
        /*int countExceptions = 0;
        List<Exception> exceptionList = new ArrayList<>();
        for (int i = 0; i < 151; i++) {

            try {
                int[] array = {0, 0, 0, i};
                entryPoint(array);
            } catch (Exception e) {
                countExceptions++;
                exceptionList.add(e);
            }
        }
        System.out.println(countExceptions);
        exceptionList.forEach(System.out::println);*/

        int[] array = {0, 0, 0, 16};
        entryPoint(array);
    }

    private static void entryPoint(int[] input) {
        Variant variant = new Variant(input[0], input[1], input[2]);
        int result = input[3];

        List<Sequence> sequence;

        if (variant.isNoVariants()) {
            sequence = getSequenceList(result, variant);
        } else {
            sequence = getSequenceListFromManyVariants(result, variant);
        }
        printSequence(sequence);
        int sum = sequence.stream().mapToInt(Sequence::getSumValue).sum();
        if (result != sum) {
            throw new RuntimeException("Invalid number: " + sum + ", not equal to " + result);
        }
        System.out.println(sum);
    }

    private static void printSequence(List<Sequence> sequence) {
        for (Sequence s : sequence) {
            if (s.getAction() != Action.EMPTY) {
                System.out.println(s);
            }
        }
    }

    private static List<Sequence> getSequenceListFromManyVariants(int result, Variant variant) {
        Set<Variant> variants = new HashSet<>(List.of(variant));
        List<Variant> variantList = new ArrayList<>(List.of(variant));
        ListIterator<Variant> iterator = variantList.listIterator();
        List<List<Sequence>> sequences = new ArrayList<>(List.of(getSequenceList(result, variant)));
        int i = 0;
        while (iterator.hasNext()) {
            Variant current = iterator.next();
            if (current.isVariableN1()) {
                var newVariant = new Variant(current.n1 - 3, current.n2, current.n3);
                if (variants.add(newVariant)) {
                    iterator.add(newVariant);
                    sequences.add(getSequenceList(result, newVariant));
                }
            }
            if (current.isVariableN2()) {
                var newVariant = new Variant(current.n1, current.n2 - 3, current.n3);
                if (variants.add(newVariant)) {
                    iterator.add(newVariant);
                    sequences.add(getSequenceList(result, newVariant));
                }
            }
            if (current.isVariableN3()) {
                var newVariant = new Variant(current.n1, current.n2, current.n3 - 3);
                if (variants.add(newVariant)) {
                    iterator.add(newVariant);
                    sequences.add(getSequenceList(result, newVariant));
                }
            }
            iterator = variantList.listIterator(++i);
        }
        var seqs = sequences.stream()
                .filter(e -> result == e.stream().mapToInt(Sequence::getSumValue).sum())
                .toList();
        if (seqs.isEmpty()) {
            System.out.println("There is no suitable sequence, print first:");
            printSequence(sequences.getFirst());
            new RuntimeException("WARNING! There is no suitable sequence!");
        }
        List<Sequence> shortestSequence = seqs.getFirst();
        int length = shortestSequence.stream().mapToInt(Sequence::getCount).sum();
        for (List<Sequence> s : sequences) {
            boolean isCorrect = result == s.stream().mapToInt(Sequence::getSumValue).sum();
            int l = s.stream().mapToInt(Sequence::getCount).sum();
            if (l < length && isCorrect) {
                length = l;
                shortestSequence = s;
            }
        }
        return shortestSequence;
    }

    private static List<Sequence> getSequenceList(int result, Variant variant) {
        int raw = result - variant.n1 - variant.n2 - variant.n3;
        var sequence = getSequence(raw);
        sequence.add(new Sequence(1, Action.byValue(variant.n3)));
        sequence.add(new Sequence(1, Action.byValue(variant.n2)));
        sequence.add(new Sequence(1, Action.byValue(variant.n1)));
        return sequence;
    }

    private static List<Sequence> getSequence(int raw) {
        List<Sequence> sequences = new ArrayList<>();

        int currentRaw = raw;
        while (currentRaw >= 0) {
            var sequence = getPositiveSequence(currentRaw);
            if (sequence != null && !sequence.isEmpty()) {
                currentRaw -= sequence.stream().mapToInt(Sequence::getSumValue).sum();
                sequences.addAll(sequence);
            } else {
                break;
            }
        }

        return sequences;
    }

    private static List<Sequence> getPositiveSequence(int raw) {
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

    record Variant(int n1, int n2, int n3) {

        public boolean isNoVariants() {
            return !isVariableN1() && !isVariableN2() && !isVariableN3();
        }

        public boolean isVariableN1() {
            return n1 == -3 || n1 == -6;
        }

        public boolean isVariableN2() {
            return n2 == -3 || n2 == -6;
        }

        public boolean isVariableN3() {
            return n3 == -3 || n3 == -6;
        }
    }

}
