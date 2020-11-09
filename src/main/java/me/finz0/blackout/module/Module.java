package me.finz0.blackout.module;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.managers.ModuleManager;
import me.finz0.blackout.setting.Bind;
import me.finz0.blackout.setting.Setting;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

public abstract class Module {
    private final String name;
    private final Category category;
    public final Setting<Bind> bind;
    private boolean enabled;
    private final Setting<BindBehaviour> bindBehaviour;

    public Module(String name, Category category){
        this.name = name;
        this.category = category;
        this.bind = register("Bind", new Bind(0)); // 0 = none
        this.enabled = false;
        bindBehaviour = register("BindMode", BindBehaviour.TOGGLE);
        ModuleManager.register(this, name);
    }

    public Module(String name, Category category, boolean enabled, Setting<Bind> bind, Setting<BindBehaviour> bindBehaviour){
        this.name = name;
        this.category = category;
        this.bind = bind;
        this.enabled = enabled;
        this.bindBehaviour = bindBehaviour;
        ModuleManager.register(this, name);
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }
	
	// used for array list which is not actually implemented
    public String getHudInfo(){
        return "";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable(){
        enabled = true;
        MinecraftForge.EVENT_BUS.register(this);
        Blackout.getInstance().moduleManager.getEnabledModules().remove(this);
        Blackout.getInstance().moduleManager.getEnabledModules().add(this);
        onEnable();
    }

    public void disable(){
        enabled = false;
        MinecraftForge.EVENT_BUS.unregister(this);
        Blackout.getInstance().moduleManager.getEnabledModules().remove(this);
        onDisable();
    }

    public boolean toggle(){
        if(isEnabled()) disable();
        else enable();
        return isEnabled();
    }

    public BindBehaviour getBindBehaviour(){
        return bindBehaviour.getValue();
    }

    protected void onEnable(){} // called when the module is enabled
    protected void onDisable(){} // called when the module is disabled
    public void onUpdate(){} // called every client tick
    public void preRender2D(){} // called before the game overlay is rendered
    public void postRender2D(){} // called after the hotbar is rendered
    public void onRender3D(){} // for 3D rendering

    public enum Category{
        COMBAT, RENDER, MISC, GUI, CLIENT
    }

    public enum BindBehaviour{
        TOGGLE, HOLD
    }

	// used to register a setting
    protected Setting register(String name, Object value){
        return Blackout.getInstance().settingManager.register(new Setting<>(name, value, this));
    }

	// used to register number settings, min and max are used for sliders
	// make sure to format the numbers properly - if its a float setting use 69f, for double use 69d, otherwise its gonna assume its and integer and throw and exception
    protected Setting register(String name, Object value, Object min, Object max){
        return Blackout.getInstance().settingManager.register(new Setting<>(name, value, min, max, this));
    }

    protected Setting register(String name, Object value, Predicate<Boolean> visibility){
        return Blackout.getInstance().settingManager.register(new Setting<>(name, value, this, visibility));
    }

    protected Setting register(String name, Object value, Object min, Object max, Predicate<Boolean> visibility){
        return Blackout.getInstance().settingManager.register(new Setting<>(name, value, min, max, this, visibility));
    }
}
