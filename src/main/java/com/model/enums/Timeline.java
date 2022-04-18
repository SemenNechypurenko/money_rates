package com.model.enums;

public enum Timeline {
    DAY {
        @Override
        public Integer getNumberOfMinutes() {
            return 24*60;
        }
    }, HOUR {
        @Override
        public Integer getNumberOfMinutes() {
            return 60;
        }
    }, MINUTE {
        @Override
        public Integer getNumberOfMinutes() {
            return 1;
        }
    };
    public abstract Integer getNumberOfMinutes();
}
