package logisticspipes.renderer.newpipe.tube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logisticspipes.LogisticsPipes;
import logisticspipes.interfaces.ITubeOrientation;
import logisticspipes.pipes.basic.CoreUnroutedPipe;
import logisticspipes.pipes.tubes.HSTubeSpeedup;
import logisticspipes.pipes.tubes.HSTubeSpeedup.SpeedupDirection;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.object3d.interfaces.I3DOperation;
import logisticspipes.proxy.object3d.interfaces.IModel3D;
import logisticspipes.proxy.object3d.operation.LPColourMultiplier;
import logisticspipes.proxy.object3d.operation.LPRotation;
import logisticspipes.proxy.object3d.operation.LPScale;
import logisticspipes.proxy.object3d.operation.LPTranslation;
import logisticspipes.renderer.newpipe.IHighlightPlacementRenderer;
import logisticspipes.renderer.newpipe.ISpecialPipeRenderer;
import logisticspipes.renderer.newpipe.LogisticsNewRenderPipe;
import logisticspipes.renderer.newpipe.RenderEntry;

import net.minecraft.util.ResourceLocation;

public class SpeedupTubeRenderer implements ISpecialPipeRenderer, IHighlightPlacementRenderer {

	private SpeedupTubeRenderer() {}

	public static final SpeedupTubeRenderer instance = new SpeedupTubeRenderer();

	static Map<SpeedupDirection, List<IModel3D>> tubeSpeedupBase = new HashMap<SpeedupDirection, List<IModel3D>>();

	//Global Access
	public static Map<SpeedupDirection, IModel3D> tubeSpeedup = new HashMap<SpeedupDirection, IModel3D>();

	private static final ResourceLocation TEXTURE = new ResourceLocation("logisticspipes", "textures/blocks/pipes/HS-Speedup.png");

	static {
		SpeedupTubeRenderer.loadModels();
	}

	public static void loadModels() {
		try {
			Map<String, IModel3D> pipePartModels = SimpleServiceLocator.cclProxy.parseObjModels(LogisticsPipes.class.getResourceAsStream("/logisticspipes/models/HSTube-Speedup_result.obj"), 7, new LPScale(1 / 100f));

			//tubeTurnMounts
			for (SpeedupDirection turn : SpeedupDirection.values()) {
				SpeedupTubeRenderer.tubeSpeedupBase.put(turn, new ArrayList<IModel3D>());
			}
			for (Entry<String, IModel3D> entry : pipePartModels.entrySet()) {
				if (entry.getKey().startsWith("Side ") || entry.getKey().contains(" Side ") || entry.getKey().endsWith(" Side")) {
					SpeedupTubeRenderer.tubeSpeedupBase.get(SpeedupDirection.EAST).add(LogisticsNewRenderPipe.compute(entry.getValue().twoFacedCopy().apply(new LPTranslation(0.0, 0.0, 0.0)).apply(new LPRotation(-Math.PI / 2, 0, 1, 0))));
					SpeedupTubeRenderer.tubeSpeedupBase.get(SpeedupDirection.NORTH).add(LogisticsNewRenderPipe.compute(entry.getValue().twoFacedCopy().apply(new LPTranslation(0.0, 0.0, 1.0))));
					SpeedupTubeRenderer.tubeSpeedupBase.get(SpeedupDirection.WEST).add(LogisticsNewRenderPipe.compute(entry.getValue().twoFacedCopy().apply(new LPTranslation(-1.0, 0.0, 1.0)).apply(new LPRotation(Math.PI / 2, 0, 1, 0))));
					SpeedupTubeRenderer.tubeSpeedupBase.get(SpeedupDirection.SOUTH).add(LogisticsNewRenderPipe.compute(entry.getValue().twoFacedCopy().apply(new LPTranslation(-1.0, 0.0, 0.0)).apply(new LPRotation(Math.PI, 0, 1, 0))));
				}
			}
			if (SpeedupTubeRenderer.tubeSpeedupBase.get(SpeedupDirection.NORTH).size() != 4) {
				throw new RuntimeException("Couldn't load Tube Side. Only loaded " + SpeedupTubeRenderer.tubeSpeedupBase.get(SpeedupDirection.NORTH).size());
			}

			for (SpeedupDirection turn : SpeedupDirection.values()) {
				SpeedupTubeRenderer.tubeSpeedup.put(turn, SimpleServiceLocator.cclProxy.combine(SpeedupTubeRenderer.tubeSpeedupBase.get(turn)));
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void renderToList(CoreUnroutedPipe pipe, List<RenderEntry> objectsToRender) {
		if (pipe instanceof HSTubeSpeedup) {
			HSTubeSpeedup tube = (HSTubeSpeedup) pipe;
			if (tube.getOrientation() != null) {
				for (IModel3D model : SpeedupTubeRenderer.tubeSpeedupBase.get(tube.getOrientation().getRenderOrientation())) {
					objectsToRender.add(new RenderEntry(model, SpeedupTubeRenderer.TEXTURE));
				}
			}
		}
	}

	@Override
	public void renderHighlight(ITubeOrientation orientation) {
		SpeedupTubeRenderer.tubeSpeedup.get(orientation.getRenderOrientation()).copy().render(new I3DOperation[] { LPColourMultiplier.instance(LogisticsPipes.LogisticsPipeBlock.getBlockColor() << 8 | 0xFF) });
		LogisticsNewRenderPipe.renderBoxWithDir(((SpeedupDirection) orientation.getRenderOrientation()).getDir1());
	}
}
