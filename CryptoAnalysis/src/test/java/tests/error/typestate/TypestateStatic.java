package tests.error.typestate;

public class TypestateStatic {

    private TypestateStatic() {}

    public static TypestateStatic createTypestate() {
        return new TypestateStatic();
    }

    public static TypestateStatic createTypestate(@SuppressWarnings("unused") String s) {
        return new TypestateStatic();
    }

    public static TypestateStatic createTypestate(
            @SuppressWarnings("unused") String s, @SuppressWarnings("unused") int i) {
        return new TypestateStatic();
    }

    public void operation1() {}

    public void operation2() {}
}
