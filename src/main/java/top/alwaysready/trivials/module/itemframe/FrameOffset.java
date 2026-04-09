package top.alwaysready.trivials.module.itemframe;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Optional;

public enum FrameOffset {
    HANGING_SIGN_NORTH(BlockFace.NORTH,0.5f,0.1f,-0.18125f),
    HANGING_SIGN_EAST(BlockFace.EAST,0.5f,0.1f,-0.18125f),
    HANGING_SIGN_SOUTH(BlockFace.SOUTH,0.5f,0.1f,-0.18125f),
    HANGING_SIGN_WEST(BlockFace.WEST,0.5f,0.1f,-0.18125f),
    WALL_SIGN_NORTH(BlockFace.NORTH,0.4f,-0.375f,0.03125f),
    WALL_SIGN_EAST(BlockFace.EAST,0.4f,-0.375f,0.03125f),
    WALL_SIGN_SOUTH(BlockFace.SOUTH,0.4f,-0.375f,0.03125f),
    WALL_SIGN_WEST(BlockFace.WEST,0.4f,-0.375f,0.03125f),
    WALL_BANNER_NORTH(BlockFace.NORTH,0.8f,-0.25f,-0.40625f),
    WALL_BANNER_EAST(BlockFace.EAST,0.8f,-0.25f,-0.40625f),
    WALL_BANNER_SOUTH(BlockFace.SOUTH,0.8f,-0.25f,-0.40625f),
    WALL_BANNER_WEST(BlockFace.WEST,0.8f,-0.25f,-0.40625f),
    SIGN_NORTH(BlockFace.NORTH,0.4f,0.0625f,0.34375f),
    SIGN_NORTH_NORTH_EAST(BlockFace.NORTH_NORTH_EAST,0.4f,0.0625f,0.34375f),
    SIGN_NORTH_EAST(BlockFace.NORTH_EAST,0.4f,0.0625f,0.34375f),
    SIGN_EAST_NORTH_EAST(BlockFace.EAST_NORTH_EAST,0.4f,0.0625f,0.34375f),
    SIGN_EAST(BlockFace.EAST,0.4f,0.0625f,0.34375f),
    SIGN_EAST_SOUTH_EAST(BlockFace.EAST_SOUTH_EAST,0.4f,0.0625f,0.34375f),
    SIGN_SOUTH_EAST(BlockFace.SOUTH_EAST,0.4f,0.0625f,0.34375f),
    SIGN_SOUTH_SOUTH_EAST(BlockFace.SOUTH_SOUTH_EAST,0.4f,0.0625f,0.34375f),
    SIGN_SOUTH(BlockFace.SOUTH,0.4f,0.0625f,0.34375f),
    SIGN_SOUTH_SOUTH_WEST(BlockFace.SOUTH_SOUTH_WEST,0.4f,0.0625f,0.34375f),
    SIGN_SOUTH_WEST(BlockFace.SOUTH_WEST,0.4f,0.0625f,0.34375f),
    SIGN_WEST_SOUTH_WEST(BlockFace.WEST_SOUTH_WEST,0.4f,0.0625f,0.34375f),
    SIGN_WEST(BlockFace.WEST,0.4f,0.0625f,0.34375f),
    SIGN_WEST_NORTH_WEST(BlockFace.WEST_NORTH_WEST,0.4f,0.0625f,0.34375f),
    SIGN_NORTH_WEST(BlockFace.NORTH_WEST,0.4f,0.0625f,0.34375f),
    SIGN_NORTH_NORTH_WEST(BlockFace.NORTH_NORTH_WEST,0.4f,0.0625f,0.34375f),
    BANNER_NORTH(BlockFace.NORTH,0.8f,0.1875f,0.540625f),
    BANNER_NORTH_NORTH_EAST(BlockFace.NORTH_NORTH_EAST,0.8f,0.1875f,0.540625f),
    BANNER_NORTH_EAST(BlockFace.NORTH_EAST,0.8f,0.1875f,0.540625f),
    BANNER_EAST_NORTH_EAST(BlockFace.EAST_NORTH_EAST,0.8f,0.1875f,0.540625f),
    BANNER_EAST(BlockFace.EAST,0.8f,0.1875f,0.540625f),
    BANNER_EAST_SOUTH_EAST(BlockFace.EAST_SOUTH_EAST,0.8f,0.1875f,0.540625f),
    BANNER_SOUTH_EAST(BlockFace.SOUTH_EAST,0.8f,0.1875f,0.540625f),
    BANNER_SOUTH_SOUTH_EAST(BlockFace.SOUTH_SOUTH_EAST,0.8f,0.1875f,0.540625f),
    BANNER_SOUTH(BlockFace.SOUTH,0.8f,0.1875f,0.540625f),
    BANNER_SOUTH_SOUTH_WEST(BlockFace.SOUTH_SOUTH_WEST,0.8f,0.1875f,0.540625f),
    BANNER_SOUTH_WEST(BlockFace.SOUTH_WEST,0.8f,0.1875f,0.540625f),
    BANNER_WEST_SOUTH_WEST(BlockFace.WEST_SOUTH_WEST,0.8f,0.1875f,0.540625f),
    BANNER_WEST(BlockFace.WEST,0.8f,0.1875f,0.540625f),
    BANNER_WEST_NORTH_WEST(BlockFace.WEST_NORTH_WEST,0.8f,0.1875f,0.540625f),
    BANNER_NORTH_WEST(BlockFace.NORTH_WEST,0.8f,0.1875f,0.540625f),
    BANNER_NORTH_NORTH_WEST(BlockFace.NORTH_NORTH_WEST,0.8f,0.1875f,0.540625f),
    CARPET_UP(BlockFace.UP,1f,0f,-0.4375f,0,0,-0.53125f),
    SLAB_UP(BlockFace.UP,1f,0f,0f,0,0,-0.53125f),
    SLAB_DOWN(BlockFace.DOWN,1f,0f,0f,0,0,0.46875f);

    public static Optional<FrameOffset> getOffset(Block block, Player player,BlockFace face){
        BlockData data = block.getBlockData();
        if(data instanceof WallHangingSign wallHangingSign) return getWallHangingSignOffset(wallHangingSign,face);
        if(data instanceof HangingSign hangingSign) return getHangingSignOffset(hangingSign,face);
        if(data instanceof WallSign wallSign) return getWallSignOffset(wallSign,face);
        if(data instanceof Sign sign) return getSignOffset(sign,player.getEyeLocation().getYaw());
        Material type = block.getType();
        if(Tag.BANNERS.isTagged(type)) {
            if(data instanceof Rotatable banner) {
                return getBannerOffset(banner, player.getEyeLocation().getYaw());
            }
            if(data instanceof Directional wallBanner){
                return getWallBannerOffset(wallBanner,face);
            }
        }
        if(Tag.WOOL_CARPETS.isTagged(type)){
            return Optional.of(CARPET_UP);
        }
        if(data instanceof Slab slab){
            if(slab.getType() == Slab.Type.BOTTOM && face == BlockFace.UP){
                return Optional.of(SLAB_UP);
            }
            if(slab.getType() == Slab.Type.TOP && face == BlockFace.DOWN){
                return Optional.of(SLAB_DOWN);
            }
        }
        return Optional.empty();
    }

    public static Optional<FrameOffset> getWallHangingSignOffset(WallHangingSign data, BlockFace face){
        BlockFace signFace = data.getFacing();
        if(face != signFace && face.getOppositeFace() != signFace) return Optional.empty();
        return Optional.ofNullable(switch (face) {
            case NORTH -> HANGING_SIGN_NORTH;
            case EAST -> HANGING_SIGN_EAST;
            case SOUTH -> HANGING_SIGN_SOUTH;
            case WEST -> HANGING_SIGN_WEST;
            default -> null;
        });
    }

    public static Optional<FrameOffset> getHangingSignOffset(HangingSign data, BlockFace face){
        BlockFace signFace = data.getRotation();
        if(face != signFace && face.getOppositeFace() != signFace) return Optional.empty();
        return Optional.ofNullable(switch (face) {
            case NORTH -> HANGING_SIGN_NORTH;
            case EAST -> HANGING_SIGN_EAST;
            case SOUTH -> HANGING_SIGN_SOUTH;
            case WEST -> HANGING_SIGN_WEST;
            default -> null;
        });
    }

    public static Optional<FrameOffset> getWallSignOffset(WallSign data, BlockFace face){
        BlockFace signFace = data.getFacing();
        if(face != signFace) return Optional.empty();
        return Optional.ofNullable(switch (face) {
            case NORTH -> WALL_SIGN_NORTH;
            case EAST -> WALL_SIGN_EAST;
            case SOUTH -> WALL_SIGN_SOUTH;
            case WEST -> WALL_SIGN_WEST;
            default -> null;
        });
    }

    public static Optional<FrameOffset> getSignOffset(Sign data, float playerYaw){
        BlockFace signFace = data.getRotation();
        FrameOffset offset = switch (signFace) {
            case NORTH -> SIGN_NORTH;
            case NORTH_NORTH_EAST -> SIGN_NORTH_NORTH_EAST;
            case NORTH_EAST -> SIGN_NORTH_EAST;
            case EAST_NORTH_EAST -> SIGN_EAST_NORTH_EAST;
            case EAST -> SIGN_EAST;
            case EAST_SOUTH_EAST -> SIGN_EAST_SOUTH_EAST;
            case SOUTH_EAST -> SIGN_SOUTH_EAST;
            case SOUTH_SOUTH_EAST -> SIGN_SOUTH_SOUTH_EAST;
            case SOUTH -> SIGN_SOUTH;
            case SOUTH_SOUTH_WEST -> SIGN_SOUTH_SOUTH_WEST;
            case SOUTH_WEST -> SIGN_SOUTH_WEST;
            case WEST_SOUTH_WEST -> SIGN_WEST_SOUTH_WEST;
            case WEST -> SIGN_WEST;
            case WEST_NORTH_WEST -> SIGN_WEST_NORTH_WEST;
            case NORTH_WEST -> SIGN_NORTH_WEST;
            case NORTH_NORTH_WEST -> SIGN_NORTH_NORTH_WEST;
            default -> null;
        };
        if(offset == null) return Optional.empty();
        float dif = Math.abs(playerYaw - offset.getYaw());
        if(dif > 90 && dif < 270){
            offset = offset.getOpposite();
        }
        return Optional.of(offset);
    }

    public static Optional<FrameOffset> getBannerOffset(Rotatable data, float playerYaw){
        BlockFace bannerFace = data.getRotation();
        FrameOffset offset = switch (bannerFace) {
            case NORTH -> BANNER_NORTH;
            case NORTH_NORTH_EAST -> BANNER_NORTH_NORTH_EAST;
            case NORTH_EAST -> BANNER_NORTH_EAST;
            case EAST_NORTH_EAST -> BANNER_EAST_NORTH_EAST;
            case EAST -> BANNER_EAST;
            case EAST_SOUTH_EAST -> BANNER_EAST_SOUTH_EAST;
            case SOUTH_EAST -> BANNER_SOUTH_EAST;
            case SOUTH_SOUTH_EAST -> BANNER_SOUTH_SOUTH_EAST;
            case SOUTH -> BANNER_SOUTH;
            case SOUTH_SOUTH_WEST -> BANNER_SOUTH_SOUTH_WEST;
            case SOUTH_WEST -> BANNER_SOUTH_WEST;
            case WEST_SOUTH_WEST -> BANNER_WEST_SOUTH_WEST;
            case WEST -> BANNER_WEST;
            case WEST_NORTH_WEST -> BANNER_WEST_NORTH_WEST;
            case NORTH_WEST -> BANNER_NORTH_WEST;
            case NORTH_NORTH_WEST -> BANNER_NORTH_NORTH_WEST;
            default -> null;
        };
        if(offset == null) return Optional.empty();
        float dif = Math.abs(playerYaw - offset.getYaw());
        if(dif > 90 && dif < 270){
            return Optional.empty();
        }
        return Optional.of(offset);
    }

    public static Optional<FrameOffset> getWallBannerOffset(Directional data, BlockFace face){
        BlockFace bannerFace = data.getFacing();
        if(face != bannerFace) return Optional.empty();
        return Optional.ofNullable(switch (face) {
            case NORTH -> WALL_BANNER_NORTH;
            case EAST -> WALL_BANNER_EAST;
            case SOUTH -> WALL_BANNER_SOUTH;
            case WEST -> WALL_BANNER_WEST;
            default -> null;
        });
    }

    private final float yaw;
    private final float pitch;
    private final float scale;
    private final float offX;
    private final float offY;
    private final float offZ;
    private final float transX;
    private final float transY;
    private final float transZ;

    FrameOffset(BlockFace face, float scale,float frontOffset, float yOffset) {
        this(face,scale,frontOffset,yOffset,0,0.5f,0);
    }

    FrameOffset(BlockFace face, float scale,float offsetFront, float offsetY,float transX,float transY,float transZ) {
        this.transX = transX;
        this.transY = transY;
        this.transZ = transZ;
        Vector dir = face.getDirection();
        switch (face){
            case NORTH -> yaw = 0;
            case NORTH_NORTH_EAST -> yaw = 22.5f;
            case NORTH_EAST -> yaw = 45;
            case EAST_NORTH_EAST -> yaw = 67.5f;
            case EAST -> yaw = 90;
            case EAST_SOUTH_EAST -> yaw = 112.5f;
            case SOUTH_EAST -> yaw = 135;
            case SOUTH_SOUTH_EAST -> yaw = 157.5f;
            case SOUTH -> yaw = 180;
            case SOUTH_SOUTH_WEST -> yaw = 202.5f;
            case SOUTH_WEST -> yaw = 225;
            case WEST_SOUTH_WEST -> yaw = 247.5f;
            case WEST -> yaw = 270;
            case WEST_NORTH_WEST -> yaw = 292.5f;
            case NORTH_WEST -> yaw = 315;
            case NORTH_NORTH_WEST -> yaw = 337.5f;
            default -> yaw = 0;
        }
        switch (face){
            case UP -> pitch = 90;
            case DOWN -> pitch = -90;
            default -> pitch = 0;
        }
        this.scale = scale;
        this.offX = (float) (dir.getX()*offsetFront+0.5);
        this.offY = (float) (dir.getY()*offsetFront+offsetY);
        this.offZ = (float) (dir.getZ()*offsetFront+0.5);
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getScale() {
        return scale;
    }

    public float getOffX() {
        return offX;
    }

    public float getOffY() {
        return offY;
    }

    public float getOffZ() {
        return offZ;
    }

    public float getTransX() {
        return transX;
    }

    public float getTransY() {
        return transY;
    }

    public float getTransZ() {
        return transZ;
    }

    public Location getLocation(Location blockLoc){
        Location ret = blockLoc.clone()
                .add(getOffX(), getOffY(), getOffZ());
        ret.setYaw(getYaw());
        ret.setPitch(getPitch());
        return ret;
    }

    private FrameOffset getOpposite() {
        return switch (this) {
            case HANGING_SIGN_NORTH -> HANGING_SIGN_SOUTH;
            case HANGING_SIGN_EAST -> HANGING_SIGN_WEST;
            case HANGING_SIGN_SOUTH -> HANGING_SIGN_NORTH;
            case HANGING_SIGN_WEST -> HANGING_SIGN_EAST;
            case WALL_SIGN_NORTH -> WALL_SIGN_SOUTH;
            case WALL_SIGN_EAST -> WALL_SIGN_WEST;
            case WALL_SIGN_SOUTH -> WALL_SIGN_NORTH;
            case WALL_SIGN_WEST -> WALL_SIGN_EAST;
            case WALL_BANNER_NORTH -> WALL_BANNER_SOUTH;
            case WALL_BANNER_EAST -> WALL_BANNER_WEST;
            case WALL_BANNER_SOUTH -> WALL_BANNER_NORTH;
            case WALL_BANNER_WEST -> WALL_BANNER_EAST;
            case SIGN_NORTH -> SIGN_SOUTH;
            case SIGN_NORTH_NORTH_EAST -> SIGN_SOUTH_SOUTH_WEST;
            case SIGN_NORTH_EAST -> SIGN_SOUTH_WEST;
            case SIGN_EAST_NORTH_EAST -> SIGN_WEST_SOUTH_WEST;
            case SIGN_EAST -> SIGN_WEST;
            case SIGN_EAST_SOUTH_EAST -> SIGN_WEST_NORTH_WEST;
            case SIGN_SOUTH_EAST -> SIGN_NORTH_WEST;
            case SIGN_SOUTH_SOUTH_EAST -> SIGN_NORTH_NORTH_WEST;
            case SIGN_SOUTH -> SIGN_NORTH;
            case SIGN_SOUTH_SOUTH_WEST -> SIGN_NORTH_NORTH_EAST;
            case SIGN_SOUTH_WEST -> SIGN_NORTH_EAST;
            case SIGN_WEST_SOUTH_WEST -> SIGN_EAST_NORTH_EAST;
            case SIGN_WEST -> SIGN_EAST;
            case SIGN_WEST_NORTH_WEST -> SIGN_EAST_SOUTH_EAST;
            case SIGN_NORTH_WEST -> SIGN_SOUTH_EAST;
            case SIGN_NORTH_NORTH_WEST -> SIGN_SOUTH_SOUTH_EAST;
            case BANNER_NORTH -> BANNER_SOUTH;
            case BANNER_NORTH_NORTH_EAST -> BANNER_SOUTH_SOUTH_WEST;
            case BANNER_NORTH_EAST -> BANNER_SOUTH_WEST;
            case BANNER_EAST_NORTH_EAST -> BANNER_WEST_SOUTH_WEST;
            case BANNER_EAST -> BANNER_WEST;
            case BANNER_EAST_SOUTH_EAST -> BANNER_WEST_NORTH_WEST;
            case BANNER_SOUTH_EAST -> BANNER_NORTH_WEST;
            case BANNER_SOUTH_SOUTH_EAST -> BANNER_NORTH_NORTH_WEST;
            case BANNER_SOUTH -> BANNER_NORTH;
            case BANNER_SOUTH_SOUTH_WEST -> BANNER_NORTH_NORTH_EAST;
            case BANNER_SOUTH_WEST -> BANNER_NORTH_EAST;
            case BANNER_WEST_SOUTH_WEST -> BANNER_EAST_NORTH_EAST;
            case BANNER_WEST -> BANNER_EAST;
            case BANNER_WEST_NORTH_WEST -> BANNER_EAST_SOUTH_EAST;
            case BANNER_NORTH_WEST -> BANNER_SOUTH_EAST;
            case BANNER_NORTH_NORTH_WEST -> BANNER_SOUTH_SOUTH_EAST;
            case CARPET_UP -> CARPET_UP;
            case SLAB_UP -> SLAB_DOWN;
            case SLAB_DOWN -> SLAB_UP;
        };
    }
}
