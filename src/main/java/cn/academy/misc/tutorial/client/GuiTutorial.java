/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * This project is open-source, and it is distributed under
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * 本项目是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.academy.misc.tutorial.client;

import java.util.Collection;

import cn.lambdalib.cgui.gui.component.*;
import cn.lambdalib.util.client.article.ArticlePlotter;
import cn.lambdalib.util.client.font.IFont.FontOption;
import cn.lambdalib.util.helper.Font;

import cn.academy.core.client.ACRenderingHelper;
import cn.academy.misc.tutorial.ACTutorial;
import cn.lambdalib.cgui.gui.CGui;
import cn.lambdalib.cgui.gui.CGuiScreen;
import cn.lambdalib.cgui.gui.Widget;
import cn.lambdalib.cgui.gui.component.Transform.HeightAlign;
import cn.lambdalib.cgui.gui.event.FrameEvent;
import cn.lambdalib.cgui.gui.event.LeftClickEvent;
import cn.lambdalib.cgui.loader.xml.CGUIDocLoader;
import cn.lambdalib.util.client.HudUtils;
import cn.lambdalib.util.generic.MathUtils;
import cn.lambdalib.util.helper.Color;
import cn.lambdalib.util.helper.GameTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author WeAthFolD
 */
public class GuiTutorial extends CGuiScreen {

	static CGui loaded;
	static {
		loaded = CGUIDocLoader.load(new ResourceLocation("academy:guis/tutorial.xml"));
	}
	
	static final Color GLOW_COLOR = Color.WHITE();
	
	final EntityPlayer player;
	final Collection<ACTutorial> tutlist;
	
	Widget frame;
	Widget leftPart, rightPart;
	
	Widget listArea;
	
	Widget showWindow, rightWindow, centerPart;
	
	Widget logo0, logo1, logo2, logo3;

	Widget centerText, briefText;

	// Current displayed tutorial
	ACTutorial currentTut = null;

	public GuiTutorial() {
		player = Minecraft.getMinecraft().thePlayer;
		tutlist = ACTutorial.getLearned(player);
		
		initUI();
	}
	
	private void initUI() {
		frame = loaded.getWidget("frame").copy();
		
		leftPart = frame.getWidget("leftPart");
		listArea = leftPart.getWidget("list");
		
		rightPart = frame.getWidget("rightPart");
		
		showWindow = rightPart.getWidget("showWindow");
		rightWindow = rightPart.getWidget("rightWindow");
		centerPart = rightPart.getWidget("centerPart");
		logo0 = rightPart.getWidget("logo0");
		logo1 = rightPart.getWidget("logo1");
		logo2 = rightPart.getWidget("logo2");
		logo3 = rightPart.getWidget("logo3");

		centerText = centerPart.getWidget("text");
		briefText = rightPart.getWidget("text");
		
		showWindow.transform.doesDraw = false;
		rightWindow.transform.doesDraw = false;
		centerPart.transform.doesDraw = false;
		
		rebuildList(tutlist);

		centerPart.getWidget("text").listen(FrameEvent.class, (w, e) -> {
			if(currentTut != null) {
				ArticlePlotter plotter = currentTut.getContentPlotter(w.transform.width - 10, 8);

				glPushMatrix();
				glTranslated(0, 0, 10);

				glColorMask(false, false, false, false);
				glDepthMask(true);
				HudUtils.colorRect(0, 0, w.transform.width, w.transform.height);
				glColorMask(true, true, true, true);

				double ht = Math.max(0, plotter.getMaxHeight() - w.transform.height + 10);
				double delta = VerticalDragBar.get(centerPart.getWidget("scroll_2")).getProgress() * ht;
				glTranslated(3, 3 - delta, 0);
				glDepthFunc(GL_EQUAL);
				plotter.draw();
				glDepthFunc(GL_LEQUAL);
				glPopMatrix();
			}
		});

		centerPart.getWidget("scroll_2").listen(LeftClickEvent.class, (w, e) -> {
			System.out.println("Yahoo!");
		});

		rightWindow.getWidget("text").listen(FrameEvent.class, (w, e) -> {
			if(currentTut != null) {
				Font.font.draw("§l" + currentTut.getTitle(), 3, 3, 10, 0xffffff);

				glPushMatrix();
				glTranslated(3, 18, 0);
				ArticlePlotter plotter = currentTut.getBriefPlotter(w.transform.width - 15, 8);
				plotter.draw();
				glPopMatrix();
			}
		});
		
		listArea.transform.doesDraw = false;
		
		/* Start animation controller */ {
			blend(logo2, 0.65, 0.3);
			blend(logo0, 1.75, 0.3);
			blend(leftPart, 1.75, 0.3);
			blend(logo1, 1.3, 0.3);
			blend(logo3, 0.1, 0.3);
			blendy(logo3, 0.7, 0.4, 63, -36);
			
			long startTime = GameTimer.getAbsTime();
			logo1.listen(FrameEvent.class, (__, e) -> {
				final float ht = 5;
				final double 
					ln = 500, ln2 = 300, cl = 50, // Height and length
					b1 = 0.3, // Blend stage 1
					b2 = 0.2; // Blend stage 2
				
				glPushMatrix();
				glTranslated(logo1.transform.width / 2, logo1.transform.height / 2 + 15, 0);
				double dt = (GameTimer.getAbsTime() - startTime) / 1000.0 - 0.4;
				if(dt < 0) dt = 0;
				if(dt < b1) {
					if(dt > 0) {
						double len = MathUtils.lerp(0, ln, dt / b1);
						if(len > cl) {
							lineglow(cl, len, ht);
							lineglow(-len, -cl, ht);
						}
					}
				} else {
					double ldt = dt - b1;
					if(ldt > b2) {
						ldt = b2;
					}
					double len = ln;
					double len2 = MathUtils.lerp(ln - 2 * cl, ln2, ldt / b2);
					lineglow(ln - len2, len, ht);
					lineglow(-len, -(ln - len2), ht);
				}
				
				glPopMatrix();
				
				listArea.transform.doesDraw = dt > 2.3;
			});
		}
		
		gui.addWidget("frame", frame);
	}
	
	private void rebuildList(Collection<ACTutorial> list) {
		listArea.removeComponent("ElementList");
		ElementList el = new ElementList();
		for(ACTutorial t : list) {
			Widget w = new Widget();
			w.transform.setSize(72, 12);
			w.addComponent(new Tint());
			
			TextBox box = new TextBox(new FontOption(10));
			box.content = t.getTitle();
			box.localized = true;
			box.heightAlign = HeightAlign.CENTER;

			w.listen(LeftClickEvent.class, (__, e) ->
			{
				if(currentTut == null) {
					// Start blending view area!
					for(Widget old : new Widget[] { logo2, logo0, logo1, logo3 }) {
						blend(old, 0, 0.3, true);
					}
					centerPart.transform.doesDraw = true;
					rightWindow.transform.doesDraw = true;
				}
				currentTut = t;
			});
			
			w.addComponent(box);
			el.addWidget(w);
		}
		listArea.addComponent(el);
	}
	
	private void lineglow(double x0, double x1, float ht) {
		ACRenderingHelper.drawGlow(x0, -1, x1-x0, ht-2, 5, GLOW_COLOR);
		glColor4d(1, 1, 1, 1);
		ACRenderingHelper.lineSegment(x0, 0, x1, 0, ht);
	}

	private void blend(Widget w, double start, double tin) {
		blend(w, start, tin, false);
	}
	
	private void blend(Widget w, double start, double tin, boolean reverse) {
		DrawTexture dt = DrawTexture.get(w);
		long startTime = GameTimer.getAbsTime();
		double startAlpha = dt.color.a;
		dt.color.a = reverse ? startAlpha : 0;
		
		w.listen(FrameEvent.class, (__, e) -> 
		{
			double delta = (GameTimer.getAbsTime() - startTime) / 1000.0;
			double alpha = startAlpha *
					MathUtils.clampd(0, 1, delta < start ? 0 : (delta - start < tin ? (delta - start ) / tin : 1));
			if(reverse) {
				alpha = 1 - alpha;
				if(alpha == 0) {
					w.dispose();
				}
			}
			dt.color.a = alpha;
		});
	}
	
	private void blendy(Widget w, double start, double tin, double y0, double y1) {
		long startTime = GameTimer.getAbsTime();
		w.transform.y = y0;
		w.dirty = true;
		
		w.listen(FrameEvent.class, (__, e) ->
		{
			double delta = (GameTimer.getAbsTime() - startTime) / 1000.0;
			double lambda = delta < start ? 0 : (delta - start < tin ? (delta - start ) / tin : 1);
			w.transform.y = MathUtils.lerp(y0, y1, lambda);
			w.dirty = true;
		});
	}
	
}
