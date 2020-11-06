package me.finz0.blackout.hud.components;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.hud.HudComponent;
import me.finz0.blackout.module.gui.Watermark;
import me.finz0.blackout.util.Wrapper;

public class WatermarkComponent extends HudComponent<Watermark> {
    public WatermarkComponent() {
        super("Watermark", 2, 2, Watermark.INSTANCE);
    }

    @Override
    public void render() {
        super.render();
        Wrapper.getFontRenderer().drawStringWithShadow(Blackout.getInstance().toString(), x, y, Blackout.getInstance().clientSettings.getColor());
        width = Wrapper.getFontRenderer().getStringWidth(Blackout.getInstance().toString());
    }
}
