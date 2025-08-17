package com.dobongsoon.BuyDobong.domain.store.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MarketName {
    SINDOBONG("신도봉시장"),
    BANGHAKDONG("방학동도깨비시장"),
    SINCHANG("신창시장"),
    CHANGDONG("창동골목시장"),
    SSANGMUN("쌍문시장"),
    BAEGUN("백운시장");

    private final String label;
    MarketName(String label) { this.label = label; }
    public String getLabel() { return label; }
}
