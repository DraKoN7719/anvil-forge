package anvil.forge.base.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class Sequence {
    private int count;
    private Action action;

    public Sequence(int count, Action action) {
        this.count = count;
        this.action = action;
    }

    public int getSumValue() {
        return count * action.getValue();
    }

    public Sequence(Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "{\"count\":\"" + count + "\",\"action\":\"" + action + "\"}";
    }
}
