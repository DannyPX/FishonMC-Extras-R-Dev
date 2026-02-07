package dannypx.foe.screens.element;

import dannypx.foe.FishOnMCExtras;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoxElement extends Element {
    //region Fields
    private final MinecraftClient minecraftClient;
    private final TextRenderer textRenderer;

    private final Identifier BOX_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/box_atlas.png");
    private final Identifier BOX_SOLID_TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/gui/sprites/elements/box_solid_atlas.png");

    private final boolean isSolid;
    private final int x;
    private final int y;
    private final int z;
    //endregion

    public BoxElement(MinecraftClient minecraftClient, int x, int y, int width, int height, boolean isSolid) {
        super(width,
                height,
                x,
                y,
                null,
                Text.literal("Box Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
        this.isSolid = isSolid;
        this.x = x;
        this.y = y;
        this.z = -1;
    }

    public BoxElement(MinecraftClient minecraftClient, int x, int y, int z, int width, int height, boolean isSolid) {
        super(width,
                height,
                x,
                y,
                null,
                Text.literal("Box Element"),
                false);
        this.minecraftClient = minecraftClient;
        this.textRenderer = minecraftClient.textRenderer;
        this.isSolid = isSolid;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //region Methods
    @Override
    public void render(DrawContext drawContext, RenderTickCounter tickCounter) {
        this.renderBox(drawContext);
    }

    private void renderBox(DrawContext drawContext) {
        Identifier TEXTURE = isSolid ? BOX_SOLID_TEXTURE : BOX_TEXTURE;
        if(this.z != -1) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.0f, 0.0f, this.z);
        }

        // Top Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                this.x, this.y,
                0, 0,
                5, 5,
                5, 5,
                15,15
        );

        // Top
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                this.x + 5, this.y,
                5, 0,
                this.width - 10, 5,
                5, 5,
                15, 15
        );

        // Top Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                x + this.width - 5, this.y,
                10, 0,
                5, 5,
                5, 5,
                15, 15
        );

        // Centre Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                this.x, this.y + 5,
                0, 5,
                5, this.height - 10,
                5, 5,
                15,15
        );

        // Centre
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                this.x + 5, this.y + 5,
                5, 5,
                this.width - 10, this.height - 10,
                5, 5,
                15, 15
        );

        // Centre Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                x + this.width - 5, this.y + 5,
                10, 5,
                5, this.height - 10,
                5, 5,
                15, 15
        );

        // Bottom Left
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                this.x, y + this.height - 5,
                0, 10,
                5, 5,
                5, 5,
                15,15
        );

        // Bottom
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                this.x + 5, y + this.height - 5,
                5, 10,
                this.width - 10, 5,
                5, 5,
                15, 15
        );

        // Bottom Right
        drawContext.drawTexture(RenderLayer::getGuiTextured,
                TEXTURE,
                x + this.width - 5, y + this.height - 5,
                10, 10,
                5, 5,
                5, 5,
                15, 15
        );

        if(this.z != -1) {
            drawContext.getMatrices().pop();
        }
    }
    //endregion
}
