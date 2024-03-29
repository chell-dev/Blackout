package me.finz0.blackout.gui;

import me.finz0.blackout.Blackout;
import me.finz0.blackout.hud.HudComponent;
import me.finz0.blackout.module.Module;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {

    private List<Panel> panels;
    public static boolean bindListening = false;
    public static boolean sliderTyping = false;
    public static boolean colorTyping = false;

    public ClickGUI(){
        panels = new ArrayList<>();
        int x = 10;
        for(Module.Category c : Module.Category.values()){
            Panel panel = new Panel(c, x, 10);
            panels.add(panel);
            x += panel.getWidth() + 10;
        }
    }

    /**
     * Draws the screen and all the components in it.
     *
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for(Panel p : panels){
            p.draw(mouseX, mouseY);
        }

        for(HudComponent c : Blackout.getInstance().hudComponentManager.getComponents()){
            c.renderInGui(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     *
     * @param typedChar
     * @param keyCode
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for(Panel p : panels){
            p.keyTyped(typedChar, keyCode);
        }

        if(keyCode == 1 || (!bindListening && !sliderTyping && !colorTyping && keyCode == Blackout.getInstance().moduleManager.getModuleByName("ClickGUI").bind.getValue().getKeyCode())) {
            mc.displayGuiScreen(null);

            if (mc.currentScreen == null)
            {
                mc.setIngameFocus();
            }
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     *
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Panel p : panels){
            p.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for(HudComponent c : Blackout.getInstance().hudComponentManager.getComponents()){
            c.mouseClicked(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     *
     * @param mouseX
     * @param mouseY
     * @param state
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for(Panel p : panels){
            p.mouseReleased(mouseX, mouseY, state);
        }

        for(HudComponent c : Blackout.getInstance().hudComponentManager.getComponents()){
            c.mouseReleased(mouseX, mouseY, state);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed() {
        for(HudComponent c : Blackout.getInstance().hudComponentManager.getComponents()){
            c.onGuiClosed();
        }
        for(Panel p : panels){
            p.guiClosed();
        }

        bindListening = false;
        sliderTyping = false;
        colorTyping = false;
        super.onGuiClosed();
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    @Override
    public void drawDefaultBackground() {
        //super.drawDefaultBackground();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
