package cn.nukkit.entity;

import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerFlag;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import cn.nukkit.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.entity.data.EntityFlag.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityHuman extends EntityHumanType {

    protected UUID uuid;

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }

    @Override
    protected float getBaseOffset() {
        return this.getEyeHeight();
    }

    protected Skin skin;

    @Override
    public int getNetworkId() {
        return -1;
    }

    public EntityHuman(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public Skin getSkin() {
        return skin;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    protected void initEntity() {
        this.setPlayerFlag(PlayerFlag.SLEEP, false);
        this.setFlag(GRAVITY, true);

        if (!(this instanceof Player)) {
            if (this.namedTag.contains("NameTag")) {
                this.setNameTag(this.namedTag.getString("NameTag"));
            }

            if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                CompoundTag skinTag = this.namedTag.getCompound("Skin");
                if (!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false);
                }
                Skin newSkin = new Skin();
                if (skinTag.contains("ModelId")) {
                    newSkin.setSkinId(skinTag.getString("ModelId"));
                }
                if (skinTag.contains("Data")) {
                    byte[] data = skinTag.getByteArray("Data");
                    if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                        int width = skinTag.getInt("SkinImageWidth");
                        int height = skinTag.getInt("SkinImageHeight");
                        newSkin.setSkinData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setSkinData(data);
                    }
                }
                if (skinTag.contains("CapeId")) {
                    newSkin.setCapeId(skinTag.getString("CapeId"));
                }
                if (skinTag.contains("CapeData")) {
                    byte[] data = skinTag.getByteArray("CapeData");
                    if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                        int width = skinTag.getInt("CapeImageWidth");
                        int height = skinTag.getInt("CapeImageHeight");
                        newSkin.setCapeData(new SerializedImage(width, height, data));
                    } else {
                        newSkin.setCapeData(data);
                    }
                }
                if (skinTag.contains("GeometryName")) {
                    newSkin.setGeometryName(skinTag.getString("GeometryName"));
                }
                if (skinTag.contains("SkinResourcePatch")) {
                    newSkin.setSkinResourcePatch(new String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("GeometryData")) {
                    newSkin.setGeometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("AnimationData")) {
                    newSkin.setAnimationData(new String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8));
                }
                if (skinTag.contains("PremiumSkin")) {
                    newSkin.setPremium(skinTag.getBoolean("PremiumSkin"));
                }
                if (skinTag.contains("PersonaSkin")) {
                    newSkin.setPersona(skinTag.getBoolean("PersonaSkin"));
                }
                if (skinTag.contains("CapeOnClassicSkin")) {
                    newSkin.setCapeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"));
                }
                if (skinTag.contains("AnimatedImageData")) {
                    ListTag<CompoundTag> list = skinTag.getList("AnimatedImageData", CompoundTag.class);
                    for (CompoundTag animationTag : list.getAll()) {
                        float frames = animationTag.getFloat("Frames");
                        int type = animationTag.getInt("Type");
                        byte[] image = animationTag.getByteArray("Image");
                        int width = animationTag.getInt("ImageWidth");
                        int height = animationTag.getInt("ImageHeight");
                        skin.getAnimations().add(new SkinAnimation(new SerializedImage(width, height, image), type, frames));
                    }
                }
                this.setSkin(newSkin);
            }

            this.uuid = Utils.dataToUUID(String.valueOf(this.getId()).getBytes(StandardCharsets.UTF_8), this.getSkin()
                    .getSkinData().data, this.getNameTag().getBytes(StandardCharsets.UTF_8));
        }

        super.initEntity();
    }

    @Override
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (skin != null) {
            CompoundTag skinTag = new CompoundTag()
                    .putByteArray("Data", this.getSkin().getSkinData().data)
                    .putInt("SkinImageWidth", this.getSkin().getSkinData().width)
                    .putInt("SkinImageHeight", this.getSkin().getSkinData().height)
                    .putString("ModelId", this.getSkin().getSkinId())
                    .putString("CapeId", this.getSkin().getCapeId())
                    .putByteArray("CapeData", this.getSkin().getCapeData().data)
                    .putInt("CapeImageWidth", this.getSkin().getCapeData().width)
                    .putInt("CapeImageHeight", this.getSkin().getCapeData().height)
                    .putByteArray("SkinResourcePatch", this.getSkin().getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", this.getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("AnimationData", this.getSkin().getAnimationData().getBytes(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", this.getSkin().isPremium())
                    .putBoolean("PersonaSkin", this.getSkin().isPersona())
                    .putBoolean("CapeOnClassicSkin", this.getSkin().isCapeOnClassic());
            List<SkinAnimation> animations = this.getSkin().getAnimations();
            if (!animations.isEmpty()) {
                ListTag<CompoundTag> animationsTag = new ListTag<>("AnimationImageData");
                for (SkinAnimation animation : animations) {
                    animationsTag.add(new CompoundTag()
                            .putFloat("Frames", animation.frames)
                            .putInt("Type", animation.type)
                            .putInt("ImageWidth", animation.image.width)
                            .putInt("ImageHeight", animation.image.height)
                            .putByteArray("Image", animation.image.data));
                }
                skinTag.putList(animationsTag);
            }
            this.namedTag.putCompound("Skin", skinTag);
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (this != player && !this.hasSpawned.contains(player)) {
            this.hasSpawned.add(player);

            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (this instanceof Player)
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, ((Player) this).getLoginChainData().getXUID(), new Player[]{player});
            else
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, new Player[]{player});

            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getName();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = this.getInventory().getItemInHand();
            pk.dataMap.putAll(this.getData());
            player.dataPacket(pk);

            this.inventory.sendArmorContents(player);

            if (this.riding != null) {
                SetEntityLinkPacket pkk = new SetEntityLinkPacket();
                pkk.vehicleUniqueId = this.riding.getId();
                pkk.riderUniqueId = this.getId();
                pkk.type = 1;
                pkk.immediate = 1;

                player.dataPacket(pkk);
            }

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), new Player[]{player});
            }
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.contains(player)) {

            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player);
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (inventory != null && (!(this instanceof Player) || ((Player) this).loggedIn)) {
                for (Player viewer : this.inventory.getViewers()) {
                    viewer.removeWindow(this.inventory);
                }
            }

            super.close();
        }
    }

    public boolean getPlayerFlag(PlayerFlag flag) {
        byte bitSet = this.getByteData(EntityData.PLAYER_FLAGS);
        return (bitSet & (1 << flag.getId())) != 0;
    }

    public void setPlayerFlag(PlayerFlag flag, boolean value) {
        byte bitSet = this.getByteData(EntityData.PLAYER_FLAGS);
        int mask = 1 << flag.getId();
        if (value) {
            bitSet |= mask;
        } else {
            bitSet &= ~mask;
        }
        setByteData(EntityData.PLAYER_FLAGS, bitSet);
    }

    public boolean isSneaking() {
        return getFlag(SNEAKING);
    }

    public void setSneaking(boolean value) {
        this.setFlag(SNEAKING, value);
    }

    public void setSneaking() {
        this.setSneaking(true);
    }

    public boolean isSwimming() {
        return this.getFlag(SWIMMING);
    }

    public void setSwimming(boolean value) {
        this.setFlag(SWIMMING, value);
    }

    public void setSwimming() {
        this.setSwimming(true);
    }

    public boolean isSprinting() {
        return this.getFlag(SPRINTING);
    }

    public void setSprinting(boolean value) {
        this.setFlag(SPRINTING, value);
    }

    public void setSprinting() {
        this.setSprinting(true);
    }

    public boolean isGliding() {
        return this.getFlag(GLIDING);
    }

    public void setGliding(boolean value) {
        this.setFlag(GLIDING, value);
    }

    public void setGliding() {
        this.setGliding(true);
    }

}
