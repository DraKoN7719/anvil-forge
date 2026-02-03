package anvil.forge;

import anvil.forge.base.Calculator;

public class Main {

    public static void main(String[] args) {
        Calculator.getForgingSequence(
                new int[]{
                        Integer.parseInt(args[0]),
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3])
                });
    }
}