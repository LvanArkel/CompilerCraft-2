package nl.lvanarkel.compilercraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.media.jfxmedia.logging.Logger;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import nl.lvanarkel.compilercraft.CompilerCraft;
import nl.lvanarkel.compilercraft.container.CompilerContainer;
import nl.lvanarkel.compilercraft.network.GeneratePKT;
import nl.lvanarkel.compilercraft.network.PacketHandler;
import nl.lvanarkel.compilercraft.tileentity.CompilerTileEntity;
import nl.lvanarkel.compilercraft.util.BlockPosUtils;

import java.util.List;

public class CompilerScreen extends ContainerScreen<CompilerContainer> {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(CompilerCraft.MODID, "textures/gui/container/compiler.png");
    private static Button generateButton;

    private static final int WORD_WRAPPING = 20;

    public CompilerScreen(CompilerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.xSize = 176;
        this.ySize = 222;

    }

    @Override
    protected void init() {
        super.init();
        generateButton = new Button(this.guiLeft+120,this.guiTop+103,font.getStringWidth("Generate")+6,20,"Generate",
                (button) -> {
                    //Send a message to the server to start the generation process.
                    PacketHandler.INSTANCE.sendToServer(new GeneratePKT(container.tileEntity.getPos()));
                });
        this.addButton(generateButton);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        if (generateButton != null) {
            generateButton.active = fullGPS();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s)/2), 6.0F, 0x404040);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize-96+2), 0x404040);
        ItemStack gps = container.tileEntity.inventory.getStackInSlot(0);
        if (!gps.isEmpty()) {
            CompoundNBT nbt = gps.getTag();
            String topleftText = "tl: ";
            String bottomrightText = "br: ";
            if (nbt != null) {
                int[] tlcoord = nbt.getIntArray("tl");
                int[] brcoord = nbt.getIntArray("br");
                topleftText += tlcoord.length == 0 ? "" : BlockPosUtils.formatBlockPos(new BlockPos(tlcoord[0], tlcoord[1], tlcoord[2]));
                bottomrightText += brcoord.length == 0 ? "" : BlockPosUtils.formatBlockPos(new BlockPos(brcoord[0], brcoord[1], brcoord[2]));
            }
            this.font.drawString(topleftText, 118f, 55f, 0x404040);
            this.font.drawString(bottomrightText, 118f, 65f, 0x404040);
        }
        int yText = 19;
        this.font.drawSplitString(container.tileEntity.getLogs().toString(),
                9, 19, 100, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F,1.0F,1.0F,1.0F);
        getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int startX = this.guiLeft;
        int startY = this.guiTop;

        blit(startX, startY, 0, 0, this.xSize, this.ySize, 256, 256);

    }

    private boolean fullGPS() {
        ItemStack gps = container.tileEntity.inventory.getStackInSlot(0);
        return !gps.isEmpty() &&
                gps.getTag() != null &&
                gps.getTag().getIntArray("tl").length > 0 &&
                gps.getTag().getIntArray("br").length > 0;
    }
}
