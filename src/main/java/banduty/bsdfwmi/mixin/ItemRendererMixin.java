package banduty.bsdfwmi.mixin;

import banduty.bsdfwmi.BsDFWMI;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntityRenderer.class)
public class ItemRendererMixin {
    @Unique
    private static int bsDFWMI$renderAmount(ItemStack stackSize) {
        int i = 1;
        if (stackSize.getCount() > BsDFWMI.CONFIG.common.getMaxGroundStack() * 0.75) {
            i = 5;
        } else if (stackSize.getCount() > BsDFWMI.CONFIG.common.getMaxGroundStack() * 0.5) {
            i = 4;
        } else if (stackSize.getCount() > BsDFWMI.CONFIG.common.getMaxGroundStack() * 0.25) {
            i = 3;
        } else if (stackSize.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    /**
     * @author
     * Banduty
     * @reason
     * Render the actual size
     */
    @Overwrite
    public static void renderStack(ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, BakedModel model, boolean depth, Random random) {
        int i = bsDFWMI$renderAmount(stack);
        float f = model.getTransformation().ground.scale.x();
        float g = model.getTransformation().ground.scale.y();
        float h = model.getTransformation().ground.scale.z();
        float k;
        float l;
        if (!depth) {
            float j = -0.0F * (float)(i - 1) * 0.5F * f;
            k = -0.0F * (float)(i - 1) * 0.5F * g;
            l = -0.09375F * (float)(i - 1) * 0.5F * h;
            matrices.translate(j, k, l);
        }

        for(int m = 0; m < i; ++m) {
            matrices.push();
            if (m > 0) {
                if (depth) {
                    k = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    l = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float n = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrices.translate(k, l, n);
                } else {
                    k = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    l = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    matrices.translate(k, l, 0.0F);
                }
            }

            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
            matrices.pop();
            if (!depth) {
                matrices.translate(0.0F * f, 0.0F * g, 0.09375F * h);
            }
        }

    }
}
