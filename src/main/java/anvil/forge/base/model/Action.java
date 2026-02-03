package anvil.forge.base.model;

import anvil.forge.base.exception.UnknownActionException;

import java.util.Arrays;
import java.util.List;

public enum Action {
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
    EMPTY(0),
    WEAK_HIT(-3),
    HIT(-6),
    STRONG_HIT(-9),
    STRETCH(-15),
    STAMP(2),
    BEND(7),
    COMPRESS(13),
    SEAT(16);

    public static final List<Action> ALL_ACTIONS = Arrays.stream(Action.values()).toList();
    public static final List<Action> POSITIVE_ACTIONS = List.of(STAMP, BEND, COMPRESS, SEAT);
    public static final List<Action> NEGATIVE_ACTIONS = List.of(WEAK_HIT, HIT, STRONG_HIT, STRETCH);

    private final int value;

    Action(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Action byValue(int value) {
        for (Action action : ALL_ACTIONS) {
            if (action.getValue() == value) {
                return action;
            }
        }
        throw new UnknownActionException("Unknown action value: " + value);
    }

    @Override
    public String toString() {
        return "{\"name\":\"" + name() + "\",\"value\":\"value\"" + value + "\"}";
    }
}
