package dannypx.foe.common.entity;

import dannypx.foe.FishOnMCExtras;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class FishingBobberEntityModel<T extends FishingBobberEntityState> extends EntityModel<T> {
    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of(FishOnMCExtras.MOD_ID, "fishing_bobber"), "main");
    public static final Identifier TEXTURE = Identifier.of(FishOnMCExtras.MOD_ID, "textures/entity/fishing_bobber.png");

    private static final float ANGLE_180_DEGREES = (float) (1f * Math.PI);

    public FishingBobberEntityModel(@NotNull ModelPart root) {
        super(root);
    }

    public static TexturedModelData generateModel() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("top", ModelPartBuilder.create().uv(12, 0).cuboid(-.5f, 3f, -.5f, 1f, 1f, 1f), ModelTransform.NONE);
        modelPartData.addChild("bobber", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, 0f, -1.5f, 3f, 3f, 3f), ModelTransform.of(0f, 3f, 0f, ANGLE_180_DEGREES, 0f, 0f));
        modelPartData.addChild("hook1", ModelPartBuilder.create().uv(12, 0).cuboid(-.5f, -1f, -.5f, 1f, 1f, 1f), ModelTransform.NONE);
        modelPartData.addChild("hook2", ModelPartBuilder.create().uv(12, 0).cuboid(-.5f, -2f, -.5f, 1f, 1f, 1f), ModelTransform.NONE);
        modelPartData.addChild("hook3", ModelPartBuilder.create().uv(12, 0).cuboid(-.5f, -3f, -.5f, 1f, 1f, 1f), ModelTransform.NONE);
        modelPartData.addChild("hook4", ModelPartBuilder.create().uv(12, 0).cuboid(-1.5f, -3f, -.5f, 1f, 1f, 1f), ModelTransform.NONE);
        modelPartData.addChild("hook5", ModelPartBuilder.create().uv(12, 0).cuboid(-2.5f, -2f, -.5f, 1f, 1f, 1f), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 16, 9);
    }

    @Override
    public void setAngles(T state) {
    }
}
