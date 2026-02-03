package anvil.forge.base.model;

public record Variant(int n1, int n2, int n3) {

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
