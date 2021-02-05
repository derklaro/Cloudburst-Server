package org.cloudburstmc.server.level.particle;

import com.nukkitx.protocol.bedrock.data.LevelEventType;
import org.cloudburstmc.math.vector.Vector3f;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class HugeExplodeSeedParticle extends GenericParticle {
    public HugeExplodeSeedParticle(Vector3f pos) {
        super(pos, LevelEventType.PARTICLE_HUGE_EXPLODE_SEED);
    }
}
