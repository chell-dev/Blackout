package me.finz0.blackout.setting;

import me.finz0.blackout.module.Module;

import java.util.function.Predicate;

public class Setting<T> {
    private final String name;
    private final Module parent;
    private T value;
    private T min;
    private T max;
    private final Predicate<Boolean> visible;

    public Setting(String name, T value, Module parent){
        this.name = name;
        this.value = value;
        this.parent = parent;
        this.visible = b -> true;
    }

    public Setting(String name, T value, T min, T max, Module parent){
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.parent = parent;
        this.visible = b -> true;
    }

    public Setting(String name, T value, Module parent, Predicate<Boolean> visible){
        this.name = name;
        this.value = value;
        this.parent = parent;
        this.visible = visible;
    }

    public Setting(String name, T value, T min, T max, Module parent, Predicate<Boolean> visible){
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.parent = parent;
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public Module getParent(){
        return parent;
    }

    public T getValue() {
        return value;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public T setValue(T value){
        this.value = value;
        return value;
    }

    public boolean isVisible(){
        return visible.test(false);
    }
}
