package com.haru.galarshrine.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigSerializable
@ConfigPath("config/galarshrine/config.yml")
public class GalarShrineConfig extends AbstractYamlConfig {

    private String galarNBT = "Galarian";
    private String orbItem = "pixelmon:common_stone";
    private String unoName = "Orb of Psychic Souls";;
    private String dosName = "Orb of Fighting Souls";;
    private String tresName = "Orb of Dark Souls";

    public GalarShrineConfig() {
        super();
    }

    public String getGalarNBT() {
        return this.galarNBT;
    }
    public String getOrbItem() {
        return this.orbItem;
    }
    public String getUnoName() {
        return this.unoName;
    }
    public String getDosName() {
        return this.dosName;
    }
    public String getTresName() {
        return this.tresName;
    }
}
