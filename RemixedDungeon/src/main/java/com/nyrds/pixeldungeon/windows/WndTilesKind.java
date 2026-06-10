package com.nyrds.pixeldungeon.windows;

import com.nyrds.LuaInterface;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.windows.HBox.Align;
import com.nyrds.platform.storage.CommonPrefs;
import com.nyrds.platform.storage.Preferences;
import com.nyrds.util.GuiProperties;
import com.watabou.noosa.Image;
import com.watabou.noosa.Text;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.TilemapMode;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

public class WndTilesKind extends Window {

	private static final int WIDTH = 100;

	public WndTilesKind() {

		super();

		VBox vbox = new VBox();
		vbox.setAlign(VBox.Align.Center);
		vbox.setGap(4);

		Text title = PixelScene.createMultiline(R.string.WndTilesKind_Title, GuiProperties.titleFontSize());
        int w = 200;
        title.maxWidth(w);

        vbox.addRow(w, HBox.Align.Center, title);


		Image image = new Image("ui/xyz_tiles.png");
        vbox.addRow(w, HBox.Align.Center, image);

		Text info = PixelScene.createMultiline(R.string.WndTilesKind_text, GuiProperties.regularFontSize());
        info.maxWidth(w);

        vbox.addRow(w, HBox.Align.Center, info);

		RedButton newTiles = new RedButton(R.string.WndTilesKind_NewLook) {
			@Override
			public void onClick() {
                setTilesMode(TilemapMode._2_5D);
			}
		};
		newTiles.autoSize();
        newTiles.enable(Dungeon.isometricModeAllowed);

		RedButton classicTiles = new RedButton(R.string.WndTilesKind_ClassicLook) {
			@Override
			public void onClick() {
                setTilesMode(TilemapMode.classic);
			}
		};
		classicTiles.autoSize();

        RedButton isometricTiles = new RedButton(R.string.WndTilesKind_IsometricLook) {
            @Override
            public void onClick() {
                setTilesMode(TilemapMode.isometric);
            }
        };
        isometricTiles.autoSize();


        vbox.addRow(w, Align.Center, newTiles, new EmptySpace(10, 1), classicTiles, new EmptySpace(10, 1), isometricTiles);

		add(vbox);

		vbox.layout();
        vbox.setSize(w, vbox.height() + 8);
		vbox.layout();
        resize(w, (int) vbox.height());
	}

	@LuaInterface
	public boolean shownBefore() {
		return Preferences.INSTANCE.getBoolean(CommonPrefs.KEY_TILES_QUESTION_ASKED, false);
	}

    private void setTilesMode(TilemapMode newTiles) {
		Preferences.INSTANCE.put(CommonPrefs.KEY_TILES_QUESTION_ASKED, true);
        Preferences.INSTANCE.put(CommonPrefs.KEY_TILEMAP_MODE, newTiles.ordinal());
        Dungeon.setPreferredTilemapMode(newTiles);
		hide();
	}
}
