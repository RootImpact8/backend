package com.example.rootimpact.domain.farm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KamisPriceResponse {

    private List<PriceInfo> priceList;

    @Getter
    @Setter
    public static class PriceInfo {
        private String itemName;   // 품목명 (예: "딸기")
        private String marketName; // 시장명 (예: "서울 가락시장")
        private String unit;       // 단위 (예: "1kg")
        private String priceToday; // 오늘 가격 (예: "5000")
        private String priceYesterday; // 어제 가격 (예: "4800")

        public String getPriceDifference() {
            int today = Integer.parseInt(priceToday.replace(",", ""));
            int yesterday = Integer.parseInt(priceYesterday.replace(",", ""));
            int diff = today - yesterday;
            return diff > 0 ? "+" + diff : String.valueOf(diff);
        }
    }

    public KamisPriceResponse(List<PriceInfo> priceList) {
        this.priceList = priceList;
    }
}
