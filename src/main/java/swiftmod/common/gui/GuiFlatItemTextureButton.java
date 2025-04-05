package swiftmod.common.gui;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import swiftmod.common.SwiftBlocks;

public class GuiFlatItemTextureButton extends GuiItemTextureButton
{
    public GuiFlatItemTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, BlockPos blockPos, Direction blockFacingDir, Direction accessDir, ItemStack itemStack)
    {
        super(screen, x, y, width, height, xTexMargin, yTexMargin, itemStack);
        m_accessDirection = accessDir;
        m_blockFacingDirection = blockFacingDir;
        m_blockPos = blockPos;
    }

    public GuiFlatItemTextureButton(GuiContainerScreen<?> screen, int x, int y, int width, int height, int xTexMargin,
            int yTexMargin, BlockPos blockPos, Direction blockFacingDir, Direction accessDir, ItemStack itemStack, IClickable onClick)
    {
        super(screen, x, y, width, height, xTexMargin, yTexMargin, itemStack, onClick);
        m_accessDirection = accessDir;
        m_blockFacingDirection = blockFacingDir;
        m_blockPos = blockPos;
    }
    
    public void setAccessDirection(Direction dir)
    {
    	m_accessDirection = dir;
    }
    
    public void setBlockPlacementDirection(Direction dir)
    {
    	m_blockFacingDirection = dir;
    }

    @Override
    protected void doRenderItem(GuiGraphics graphics)
    {
    	float x = getX();
    	float y = getY();
	    float xScale = width - m_xTexMargin;
	    float yScale = height - m_yTexMargin;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + 8.0f, y + 8.0f, 150.0f);
	    pose.mulPose((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
	    pose.scale(xScale, yScale, xScale);
    	renderFlatItemFace(graphics, m_blockPos, m_blockFacingDirection, m_accessDirection, m_itemStack, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
	    pose.popPose();
    }
    
    protected static Vector3f getRotation(Direction blockFacingDir, Direction accessDirection)
    {
    	// Originally grabbed from BlockStateProvider.
    	// This snippet rotates from the global frame to the frame of the block.
    	//float rotationX = 0.0f;
    	float rotationY = 0.0f;
        
    	// Generally helpful diagram:
    	//
    	//                   North (0)
    	//
    	//     West (270)        X        East (90)
    	//
    	//                  South (180)
    	
    	switch (blockFacingDir)
    	{
    	case SOUTH:
    		rotationY = 0.0f;
    		break;
    	case WEST:
    		rotationY = 90.0f;
    		break;
    	case NORTH:
    		rotationY = 180.0f;
    		break;
    	case EAST:
    		rotationY = 270.0f;
    		break;
    	}
    	
    	// TODO: This is broken. Fix it.
    	/*switch (accessDirection)
    	{
    	default:
    	case UP:
    	case DOWN:
    		// In this case, just match the block placement itself, since pipes are purely symmetric.
    		// In other words, we are treating the rotation in the XZ-plane as identical between
    		// the pipe and the target.
    		rotationX = 0.0f;
    		switch (blockFacingDir)
    		{
	    	default:
    		case NORTH:
    	    	rotationY = 0.0f;
    	    	break;
    		case EAST:
    	    	rotationY = 90.0f;
    	    	break;
    		case SOUTH:
    	    	rotationY = 180.0f;
    	    	break;
    		case WEST:
    	    	rotationY = 270.0f;
    	    	break;
    		}
    		break;
    		
    	case NORTH:
    		switch (blockFacingDir)
    		{
	    	default:
    		case NORTH:
    	    	rotationY = 0.0f;
    	    	break;
    		case EAST:
    	    	rotationY = -90.0f;
    	    	break;
    		case SOUTH:
    	    	rotationY = -180.0f;
    	    	break;
    		case WEST:
    	    	rotationY = -270.0f;
    	    	break;
    		}
    		break;
    		
    	case EAST:
    		switch (blockFacingDir)
    		{
	    	default:
    		case EAST:
    	    	rotationY = 0.0f;
    	    	break;
    		case SOUTH:
    	    	rotationY = -90.0f;
    	    	break;
    		case WEST:
    	    	rotationY = -180.0f;
    	    	break;
    		case NORTH:
    	    	rotationY = -270.0f;
    	    	break;
    		}
    		break;
    		
    	case SOUTH:
    		switch (blockFacingDir)
    		{
	    	default:
    		case SOUTH:
    	    	rotationY = 0.0f;
    	    	break;
    		case WEST:
    	    	rotationY = -90.0f;
    	    	break;
    		case NORTH:
    	    	rotationY = -180.0f;
    	    	break;
    		case EAST:
    	    	rotationY = -270.0f;
    	    	break;
    		}
    		break;
    		
    	case WEST:
    		switch (blockFacingDir)
    		{
	    	default:
    		case WEST:
    	    	rotationY = 0.0f;
    	    	break;
    		case NORTH:
    	    	rotationY = -90.0f;
    	    	break;
    		case EAST:
    	    	rotationY = -180.0f;
    	    	break;
    		case SOUTH:
    	    	rotationY = -270.0f;
    	    	break;
    		}
    		break;
    	}*/
    	
    	// For some reason the blocks are rendered with the back side facing forward
        // (I think because Direction.SOUTH is zero), so most (all?) blocks are rotated
        // by 180 degrees to turn them to face front.
    	switch (accessDirection)
    	{
    	case UP:
			return new Vector3f(90.0f, rotationY + 0.0f, 0.0f);
    		
    	case DOWN:
			return new Vector3f(-90.0f, rotationY + 0.0f, 0.0f);
    		
    	case NORTH:
			return new Vector3f(0.0f, rotationY + 0.0f, 0.0f);
    		
    	case EAST:
			return new Vector3f(0.0f, rotationY + 90.0f, 0.0f);
    		
    	case SOUTH:
			return new Vector3f(0.0f, rotationY + 180.0f, 0.0f);
    		
    	case WEST:
			return new Vector3f(0.0f, rotationY + 270.0f, 0.0f);
    		
    	default:
    		// This should never happen...
			return new Vector3f(0.0f, 0.0f, 0.0f);
    	}
    }
    
    // Renders one face (front on) of an item using the render information typically specified in JSON.
    // This is ripped straight from ItemRenderer, except that we only render one face. We also skip a
    // couple of edge cases, like the trident, spyglass, and compass, for various reasons (mostly because
    // they aren't placeable as blocks so they can be skipped anyway). This is also cleaned up a bit
    // so we pass in the GuiGraphics instance, etc. etc.
    public static void renderFlatItemFace(
            GuiGraphics graphics,
            BlockPos blockPos,
            Direction blockFacingDirection,
            Direction accessDirection,
            ItemStack itemStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay)
    {
        if (!itemStack.isEmpty())
        {
        	Minecraft minecraft = Minecraft.getInstance();
        	ItemRenderer itemRenderer = minecraft.getItemRenderer();
        	BakedModel bakedModel;
        	/*if (itemStack.getItem() instanceof BlockItem)
        	{
                bakedModel = itemRenderer.getModel(itemStack, minecraft.level, minecraft.player, 0);
        	}
        	else*/
        	{
            	BlockState state = minecraft.level.getBlockState(blockPos);
        		bakedModel = minecraft.getBlockRenderer().getBlockModel(state);
        	}
    	    boolean useBlockLight = !bakedModel.usesBlockLight();
    	    if (useBlockLight)
    	        Lighting.setupForFlatItems();
            
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            
            // Use the default GUI transformation, but instead of the default (30, 225, 0) rotation (or whatever is defined in JSON)
            // use our face-on transformation instead.
            @SuppressWarnings("deprecation")
			ItemTransform defaultGuiTransform = bakedModel.getTransforms().getTransform(ItemDisplayContext.FIXED);
            Vector3f rotation = getRotation(blockFacingDirection, accessDirection);
            //rotation.sub(30, 225, 0);
            rotation.add(defaultGuiTransform.rotation);
            Vector3f scale = new Vector3f(defaultGuiTransform.scale);
            //scale.mul(1.15f); // Default scaling is 0.65. Increase that by a bit to fill the button more fully.
            ItemTransform actualGuiTransform = new ItemTransform(rotation, defaultGuiTransform.translation, scale, defaultGuiTransform.rightRotation);
            actualGuiTransform.apply(false, poseStack);
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            if (!bakedModel.isCustomRenderer())
            {
                for (var model : bakedModel.getRenderPasses(itemStack, true))
                {
	                for (var renderType : model.getRenderTypes(itemStack, true))
	                {
		                VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, itemStack.hasFoil());
		                itemRenderer.renderModelLists(bakedModel, itemStack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
	                }
                }
            }
            else
            {
                IClientItemExtensions.of(itemStack).getCustomRenderer().renderByItem(itemStack, ItemDisplayContext.GUI, poseStack, bufferSource, combinedLight, combinedOverlay);
            }

            poseStack.popPose();
    	    graphics.flush();
    	    if (useBlockLight)
    	        Lighting.setupFor3DItems();
        }
    }

	// For some reason the blocks are rendered with the back side facing forward
    // (I think because Direction.SOUTH is zero), so most (all?) blocks are rotated
    // by 180 degrees to turn them to face front.
    private static float DEFAULT_ANGLE_OFFSET = 180.0f;
    
    private Direction m_accessDirection;
    private Direction m_blockFacingDirection;
    private BlockPos m_blockPos;
}
