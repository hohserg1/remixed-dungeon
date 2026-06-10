
package com.watabou.pixeldungeon;

import com.nyrds.LuaInterface;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

import com.watabou.utils.Rect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

public abstract class DungeonTilemap extends Tilemap {

	public static final int SIZE = 16;
	public static final String XYZ = "xyz";

	static protected Level level;


	public DungeonTilemap(@NotNull Level level, String tiles ) {
		super(tiles, TextureCache.getFilm(tiles, SIZE, SIZE));
		DungeonTilemap.level = level;

		map(level.map, level.getWidth());
	}


	static public @NotNull DungeonTilemap factory(Level level) {
		String tiles = level.getTilesTex();

		TextureFilm probe = TextureCache.getFilm(tiles, SIZE, SIZE);

        switch (Dungeon.getPreferredTilemapMode()) {
            case classic:
                if (probe.size() == 256) {
                    return new VariativeDungeonTilemap(level, tiles);
                }

                return new ClassicDungeonTilemap(level, tiles);

            case _2_5D:
                return new XyzDungeonTilemap(level, tiles);

            case isometric:
                return new IsometricDungeonTilemap(level, tiles);

            default:
                return new ClassicDungeonTilemap(level, tiles);
        }
	}


	@LuaInterface
	static public int getDecoTileForTerrain(Level level, int cell, int terrain) {
		String tiles = level.getTilesTex();

		TextureFilm probe = TextureCache.getFilm(tiles, SIZE, SIZE);

		if(probe.size() == 256) {
			return VariativeDungeonTilemap.getDecoTileForTerrain(level, cell, terrain);
		}

		return terrain;
	}

	@LuaInterface
	static public @NotNull String kind(Level level) {
		String tiles = level.getTilesTex();

		TextureFilm probe = TextureCache.getFilm(tiles, SIZE, SIZE);

		if (tiles.contains("_xyz")) {
			return XYZ;
		}

		if(probe.size() == 256) {
			return "x";
		}

		return "classic";
	}


	public int screenToTile(int x, int y) {
		Point p = camera().screenToCamera(x, y).offset(this.point().negate()).invScale(SIZE).floor();
		return level.cell(p.x, p.y);
	}

	@Override
	public boolean overlapsPoint(float x, float y) {
		return true;
	}

	public void discover(int pos) {

		final Image tile = tile(pos);

		if(tile==null) {
			return;
		}

		tile.point(tileToWorld(pos));

		// For bright mode
		tile.rm = tile.gm = tile.bm = rm;
		tile.ra = tile.ga = tile.ba = ra;
		getParent().add(tile);

		getParent().add(new AlphaTweener(tile, 0, 0.6f) {
			protected void onComplete() {
				tile.killAndErase();
				killAndErase();
			}
		});

	}

	@Nullable
	public abstract Image tile(int pos);

    public PointF tileToWorld(int pos) {
		return new PointF(level.cellX(pos), level.cellY(pos)).scale(SIZE);
	}

	@Contract("_ -> new")
    public @NotNull PointF tileCenterToWorld(int pos) {
		return new PointF((level.cellX(pos) + 0.5f) * SIZE,
				(level.cellY(pos) + 0.5f) * SIZE);
	}

	@Override
	public boolean overlapsScreenPoint(int x, int y) {
		return true;
	}

	public abstract void updateCell(int cell, Level level);

    protected boolean isAnyWallCell(int cell) {
        if (!level.cellValid(cell)) {
            return true;
        }

        if (!level.mapped[cell]) {
            return true;
        }

        return isSpWallCell(cell) || isWallCell(cell);
    }

    protected boolean isSpWallCell(int cell) {
        if (!level.cellValid(cell)) {
            return true;
        }

        switch (level.map[cell]) {
            case Terrain.BOOKSHELF:
            case Terrain.SECRET_DOOR:
                return true;
        }

        return false;
    }

    protected boolean isWallCell(int cell) {
        if (!level.cellValid(cell)) {
            return true;
        }

        switch (level.map[cell]) {
            case Terrain.WALL:
            case Terrain.WALL_DECO:
            case Terrain.SECRET_DOOR:
                return true;
        }
        return false;
    }

    protected boolean isDoorCell(int cell) {
        if (!level.cellValid(cell)) {
            return false;
        }

        switch (level.map[cell]) {
            case Terrain.DOOR:
            case Terrain.OPEN_DOOR:
            case Terrain.LOCKED_DOOR:
                return true;
        }
        return false;
    }

    protected void iterateCells(int fromX, int toX, int fromY, int toY, CellAction everyCell, RowAction everyRow) {
        int mapHeight = data.length / mapWidth;
        for (int y = Math.max(0, fromY); y < Math.min(mapHeight, toY); y++) {
            int cell = y * mapWidth + updated.left;
            everyRow.apply(cell, y);
            for (int x = Math.max(0, fromX); x < Math.min(mapWidth, toX); x++) {
                everyCell.apply(cell, x, y);
            }
        }
    }
    protected void iterateCells(Rect area, CellAction everyCell, RowAction everyRow) {
        iterateCells(area.left,area.right, area.top, area.bottom, everyCell,everyRow)
    }

    protected interface CellAction {
        void apply(int cell, int x, int y);
    }

    protected interface RowAction {
        void apply(int cell, int y);
    }

    public abstract void updateAll();

}
