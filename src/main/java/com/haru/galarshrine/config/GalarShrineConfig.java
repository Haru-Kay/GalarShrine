package com.haru.galarshrine.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigSerializable
@ConfigPath("config/galarshrine/config.yml")
public class GalarShrineConfig extends AbstractYamlConfig {

    private String galarNBT = "Galarian";

    public GalarShrineConfig() {
        super();
    }

    public String getGalarNBT() {
        return this.galarNBT;
    }
}
