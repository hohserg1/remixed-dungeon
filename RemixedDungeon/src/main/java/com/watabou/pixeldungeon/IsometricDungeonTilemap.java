package com.watabou.pixeldungeon;

import com.nyrds.platform.compatibility.RectF;
import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.utils.PointF;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class IsometricDungeonTilemap extends ClassicDungeonTilemap {
    final ClassicDungeonTilemap comparedView;

    public IsometricDungeonTilemap(@NotNull Level level, String tiles) {
        super(level, tiles);
        comparedView = new ClassicDungeonTilemap(level, tiles);
    }

    @Override
    public @Nullable Image tile(int pos) {
        return super.tile(pos);
    }

    @Override
    public void updateCell(int cell, Level level) {
        super.updateCell(cell, level);
        comparedView.updateCell(cell, level);
    }

    @Override
    public void updateAll() {
        super.updateAll();
    }

    @Override
    public PointF tileToWorld(int pos) {
        return super.tileToWorld(pos);
    }

    @Override
    public @NotNull PointF tileCenterToWorld(int pos) {
        return super.tileCenterToWorld(pos);
    }

    @Override
    public int screenToTile(int x, int y) {
        return super.screenToTile(x, y);
    }

    @Override
    protected void updateVertices() {
        float w = cellW * 4;

        for (int y = updated.top; y < updated.bottom; y++) {
            int pos = y * mapWidth + updated.left;
            quads.position(16 * pos);
            for (int x = updated.left; x < updated.right; x++) {

                float x1 = w * (x - y);
                float x2 = w * (x + 1 - y);
                float x3 = x1;
                float x4 = w * (x - y - 1);

                float y1 = w / 2 * (y + x);
                float y2 = w / 2 * (y + x + 1);
                float y3 = w / 2 * (y + 1 + x + 1);
                float y4 = y2;

                RectF uv = tileset.get(data[pos++]);
                if (uv != null) {
                    vertices[0] = x1;
                    vertices[1] = y1;

                    vertices[2] = uv.left;
                    vertices[3] = uv.top;

                    vertices[4] = x2;
                    vertices[5] = y2;

                    vertices[6] = uv.right;
                    vertices[7] = uv.top;

                    vertices[8] = x3;
                    vertices[9] = y3;

                    vertices[10] = uv.right;
                    vertices[11] = uv.bottom;

                    vertices[12] = x4;
                    vertices[13] = y4;

                    vertices[14] = uv.left;
                    vertices[15] = uv.bottom;
                } else {
                    Arrays.fill(vertices, 0);
                }

                quads.put(vertices);

            }
        }

        updated.setEmpty();
    }

    @Override
    public void draw() {
        super.draw();
        comparedView.draw();
    }

    public static class WallsTilemap extends IsometricDungeonTilemap{

        public WallsTilemap(@NotNull Level level, String tiles) {
            super(level, tiles);
        }
    }
}
