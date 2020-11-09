package me.finz0.blackout.gui.button.sub;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.finz0.blackout.Blackout;
import me.finz0.blackout.gui.ClickGUI;
import me.finz0.blackout.gui.button.ModuleButton;
import me.finz0.blackout.gui.button.SubButton;
import me.finz0.blackout.setting.Setting;
import me.finz0.blackout.util.Wrapper;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SliderButton<T> extends SubButton {
    SliderButton(int x, int y, ModuleButton parent, Setting<T> setting) {
        super(x, y, parent.getWidth() - 2, Wrapper.getFontRenderer().FONT_HEIGHT + 4, parent);
        this.setting = setting;
    }

    private Setting<T> setting;

    boolean dragging = false;
    private boolean typing = false;

    int sliderWidth = 0;

    @Override
    public boolean isVisible(){
        return setting.isVisible();
    }

    @Override
    public void guiClosed(){
        typing = false;
    }

    protected void updateSlider(int mouseX){
    }

    @Override
    public void draw(int mouseX, int mouseY){
        updateSlider(mouseX);

        //left margin
        Gui.drawRect(getX(), getY(), getX() + 2, getY() + getHeight(), new Color(10, 10, 10, 200).getRGB());
        //background
        Gui.drawRect(getX() + 2 + sliderWidth, getY(), getX() + 2 + getWidth(), getY() + getHeight() - 1, getColor(mouseX, mouseY));
        //slider
        Gui.drawRect(getX() + 2, getY(), getX() + 2 + sliderWidth, getY() + getHeight() - 1, getSliderColor(mouseX, mouseY));
        //bottom
        Gui.drawRect(getX() + 2, getY() + getHeight() - 1, getX() + 2 + getWidth(), getY() + getHeight(), new Color(10, 10, 10, 200).getRGB());
        //setting name
        Wrapper.getFontRenderer().drawStringWithShadow(setting.getName(), getX() + 4, getY() + 2, -1);
        //setting value
        String val = "" + ChatFormatting.GRAY + setting.getValue();
        if(typing) val = input.isEmpty() ? "_" : input;
        Wrapper.getFontRenderer().drawStringWithShadow(val, getX() + getWidth() - Wrapper.getFontRenderer().getStringWidth(val), getY() + 2, -1);
    }

    private int getColor(int mouseX, int mouseY){
        Color color = new Color(50, 50, 50, 200);
        boolean hovered = mouseX > getX() + 2 + sliderWidth && mouseY > getY() && mouseX < getX() + 2 + getWidth() && mouseY < getY() + getHeight() - 1;
        return hovered ? color.brighter().brighter().getRGB() : color.getRGB();
    }

    private int getSliderColor(int mouseX, int mouseY){
        Color color = Blackout.getInstance().clientSettings.getColorr(200);
        boolean hovered = mouseX > getX() + 2 && mouseY > getY() && mouseX < getX() + 2 + sliderWidth && mouseY < getY() + getHeight() - 1;
        return hovered ? color.darker().darker().getRGB() : color.getRGB();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (mouseX > getX() + 2 && mouseY > getY() && mouseX < getX() + 2 + getWidth() && mouseY < getY() + getHeight() - 1)
            switch (button) {
                case 0:
                    dragging = true;
                    break;
                case 1:
                    typing = !typing;
                    ClickGUI.sliderTyping = typing;
                    break;
            }
    }

    private ArrayList<Character> validChars = Lists.newArrayList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ',', '-'
    );

    private String input = "";

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(!typing){
            input = "";
            return;
        }

        switch (keyCode){
            case Keyboard.KEY_ESCAPE:
                typing = false;
                ClickGUI.sliderTyping = false;
                input = "";
                return;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                valueEntered(input);
                typing = false;
                ClickGUI.sliderTyping = false;
                input = "";
                return;
            case Keyboard.KEY_BACK:
            case Keyboard.KEY_DELETE:
                input = StringUtils.chop(input);
                return;
        }

        if(validChars.contains(typedChar)){
            if(!input.equals("") && typedChar == '-') return;
            input += typedChar;
        }
    }

    protected void valueEntered(String in){
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if(dragging) dragging = false;
    }

    public static class IntSlider extends SliderButton{
        public IntSlider(Setting<Integer> setting, int x, int y, ModuleButton parent) {
            super(x, y, parent, setting);
            this.settingI = setting;
        }

        private Setting<Integer> settingI;

        @Override
        protected void updateSlider(int mouseX){
            //yes this math stuff is skidded
            double diff = Math.min(getWidth(), Math.max(0, mouseX - getX()));

            double min = settingI.getMin().doubleValue();
            double max = settingI.getMax().doubleValue();

            sliderWidth = (int) (getWidth() * (settingI.getValue() - min) / (max - min));

            if (dragging) {
                if (diff == 0) {
                    settingI.setValue(settingI.getMin());
                } else {
                    DecimalFormat format = new DecimalFormat("##");
                    String newValue = format.format(((diff / getWidth()) * (max - min) + min));
                    settingI.setValue(Integer.parseInt(newValue));
                }
            }
        }

        @Override
        protected void valueEntered(String in){
            int i;

            try {
                i = Integer.parseInt(in.replace(',', '.'));
            } catch (Exception e){
                return;
            }

            if(i < settingI.getMin() || i > settingI.getMax())
                return;

            settingI.setValue(i);
        }
    }

    public static class FloatSlider extends SliderButton{
        public FloatSlider(Setting<Float> setting, int x, int y, ModuleButton parent) {
            super(x, y, parent, setting);
            this.settingF = setting;
        }

        private Setting<Float> settingF;

        @Override
        protected void updateSlider(int mouseX){
            //yes this math stuff is skidded
            float diff = Math.min(getWidth(), Math.max(0, mouseX - getX()));

            float min = settingF.getMin();
            float max = settingF.getMax();

            sliderWidth = (int) (getWidth() * (settingF.getValue() - min) / (max - min));

            if (dragging) {
                if (diff == 0) {
                    settingF.setValue(settingF.getMin());
                } else {
                    DecimalFormat format = new DecimalFormat("##.0");
                    String newValue = format.format(((diff / getWidth()) * (max - min) + min));
                    settingF.setValue(Float.parseFloat(newValue));
                }
            }
        }

        @Override
        protected void valueEntered(String in){
            float f;

            try {
                f = Float.parseFloat(in.replace(',', '.'));
            } catch (Exception e){
                return;
            }

            if(f < settingF.getMin() || f > settingF.getMax())
                return;

            settingF.setValue(f);
        }
    }

    public static class DoubleSlider extends SliderButton{
        public DoubleSlider(Setting<Double> setting, int x, int y, ModuleButton parent) {
            super(x, y, parent, setting);
            this.settingD = setting;
        }

        private Setting<Double> settingD;

        @Override
        protected void updateSlider(int mouseX){
            //yes this math stuff is skidded
            float diff = Math.min(getWidth(), Math.max(0, mouseX - getX()));

            double min = settingD.getMin();
            double max = settingD.getMax();

            sliderWidth = (int) (getWidth() * (settingD.getValue() - min) / (max - min));

            if (dragging) {
                if (diff == 0) {
                    settingD.setValue(settingD.getMin());
                } else {
                    DecimalFormat format = new DecimalFormat("##.0");
                    String newValue = format.format(((diff / getWidth()) * (max - min) + min));
                    settingD.setValue(Double.parseDouble(newValue));
                }
            }
        }

        @Override
        protected void valueEntered(String in){
            double d;

            try {
                d = Double.parseDouble(in.replace(',', '.'));
            } catch (Exception e){
                return;
            }

            if(d < settingD.getMin() || d > settingD.getMax())
                return;

            settingD.setValue(d);
        }
    }
}
