package com.rpsg.rpg.system.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.rpsg.rpg.object.rpgobj.HeroObj;
import com.rpsg.rpg.object.rpgobj.IRPGObject;
import com.rpsg.rpg.object.rpgobj.NPC;
import com.rpsg.rpg.object.script.Script;
import com.rpsg.rpg.system.base.ThreadPool;
import com.rpsg.rpg.utils.display.FG;
import com.rpsg.rpg.utils.display.Msg;
import com.rpsg.rpg.view.GameView;
import com.rpsg.rpg.view.GameViews;

public class MapControler {
	
	public static int MAP_MAX_OUT_X=300;
	public static int MAP_MAX_OUT_Y=200;
	public static HeroObj hero;
	public static List<IRPGObject> drawlist=new ArrayList<IRPGObject>();
	public static NPC npc;
	public static void init(GameView gv){
		hero=new HeroObj(gv.global.headHeroImage);
		hero.generatePosition(gv.global.heroMapx, gv.global.heroMapy, gv.global.heroMapz,gv.map);
		hero.currentImageNo=gv.global.heroFace;
		gv.stage.addActor(hero);
		if(gv.global.npcs.isEmpty())
			for(TiledObjectGroup objGroup:gv.map.objectGroups){
				int layer=Integer.parseInt(objGroup.properties.get("layer"));
				for(TiledObject obj:objGroup.objects){
					if(obj.type.equals("NPC")){
						try {
							npc=(NPC)Class.forName("com.rpsg.rpg.game.object."+obj.name).getConstructor(String.class,Integer.class,Integer.class).newInstance(obj.properties.get("IMAGE")+".png",obj.width,obj.height);
							npc.init();
							npc.generatePosition(obj.x/48, obj.y/48, layer, gv.map);
							gv.stage.addActor(npc);
							ThreadPool.pool.add(npc.threadPool);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		else{
			System.out.println("loading:3");
			for(NPC n:gv.global.npcs){
				System.out.println("before");
				n.scripts=new HashMap<String, Class<? extends Script>>();
				n.threadPool=new LinkedList<Script>();
				n.init();
				
				gv.stage.addActor(n);
				ThreadPool.pool.add(n.threadPool);
				n.images=NPC.generateImages(n.imgPath, n.bodyWidth, n.bodyHeight);
				npc=n;
			}
		}
	}
	
	public synchronized static void draw(SpriteBatch batch,GameView gv){
		int size=gv.map.layers.size();
		for(int i=0;i<size;i++){
			drawlist.clear();
			gv.render.render(gv.camera,new int[]{i});
			SpriteBatch sb=gv.stage.getSpriteBatch();
			sb.begin();
			sb.setProjectionMatrix(gv.camera.combined);
			for(Actor a:gv.stage.getActors())
				if(a instanceof IRPGObject){
					IRPGObject c = (IRPGObject)a;
					if(c.layer==i)
						drawlist.add(c);
				}
			Collections.sort(drawlist);
			for(IRPGObject ir:drawlist)
				ir.draw(sb, 1f);
			sb.end();
		}
		FG.draw(batch);
		Msg.draw(batch);
//		System.out.println(hero.getX()+" "+hero.getY()+" "+gv.camera.position.x+" "+gv.camera.position.y+" "+hero.mapx+" "+hero.mapy);
	}
	
	public static void logic(GameView gv){
	}
	
	public static void dispose(){
		hero.dispose();
		for(Actor a:GameViews.gameview.stage.getActors())
			if(a instanceof IRPGObject)
				((IRPGObject)a).dispose();
		GameViews.gameview.stage.getActors().clear();
	}
	
	public static List<NPC> getNPCs(){
		List<NPC> list=new ArrayList<NPC>();
		for(Actor a:GameViews.gameview.stage.getActors())
			if(a instanceof NPC)
				list.add((NPC)a);
		return list;
	}
}
