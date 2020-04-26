package org.github.muhatashim.translator.java;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Translator<L, R> {

    private final Class<L> leftClass;
    private final Class<R> rightClass;
    private Map<Function<L, ?>, BiConsumer<R, Object>> mappingsToRight;
    private Map<Function<R, ?>, BiConsumer<L, Object>> mappingsToLeft;

    public Translator(Class<L> leftClass, Class<R> rightClass) {
        this.leftClass = leftClass;
        this.rightClass = rightClass;
        this.mappingsToRight = new HashMap<>();
        this.mappingsToLeft = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <G, S> void addTranslationLeft(Function<R, G> getter, BiConsumer<L, S> setter, Function<G, S> mapper) {
        mappingsToLeft.put(getter, (L instance, Object obj) -> setter.accept(instance, mapper.apply((G) obj)));
    }

    @SuppressWarnings("unchecked")
    public <G, S> void addTranslationRight(Function<L, G> getter, BiConsumer<R, S> setter, Function<G, S> mapper) {
        mappingsToRight.put(getter, (R instance, Object obj) -> setter.accept(instance, mapper.apply((G) obj)));
    }

    @SuppressWarnings("unchecked")
    public <G> void addTranslation(Function<L, G> getterLeft, Function<R, G> getterRight,
                                   BiConsumer<L, G> setterLeft, BiConsumer<R, G> setterRight) {
        mappingsToRight.put(getterLeft, (BiConsumer<R, Object>) setterRight);
        mappingsToLeft.put(getterRight, (BiConsumer<L, Object>) setterLeft);
    }

    @SuppressWarnings("unchecked")
    public <GL, GR, SL, SR> void addTranslation(Function<L, GL> getterLeft, Function<R, GR> getterRight,
                                                BiConsumer<L, SL> setterLeft, BiConsumer<R, SR> setterRight,
                                                Function<GR, SL> mapToLeft,
                                                Function<GL, SR> mapToRight) {
        addTranslationLeft(getterRight, setterLeft, mapToLeft);
        addTranslationRight(getterLeft, setterRight, mapToRight);
    }


    public L translateToLeft(R rightObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        L leftObject = leftClass.getConstructor().newInstance();
        return translateToLeft(leftObject, rightObject);
    }

    public R translateToRight(L leftObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        R rightObject = rightClass.getConstructor().newInstance();
        return translateToRight(rightObject, leftObject);
    }

    public L translateToLeft(L leftObject, R rightObject) {
        mappingsToLeft.forEach((key, value) -> value.accept(leftObject, key.apply(rightObject)));
        return leftObject;
    }


    public R translateToRight(R rightObject, L leftObject) {
        mappingsToRight.forEach((key, value) -> value.accept(rightObject, key.apply(leftObject)));
        return rightObject;
    }
}
