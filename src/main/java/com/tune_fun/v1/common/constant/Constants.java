package com.tune_fun.v1.common.constant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class Constants {

    public static final Object NULL_OBJECT = null;

    public static final Number NULL_NUMBER = null;

    public static final String NULL_STRING = null;

    public static final Boolean NULL_BOOLEAN = null;

    public static final String EMPTY_STRING = "";

    public static final String SPACE = " ";

    public static final String COMMA = ",";

    public static final String COLON = ":";

    public static final String SEMICOLON = ";";

    public static final String DOT = ".";

    public static final LocalDateTime LOCAL_DATE_TIME_MIN = LocalDateTime.of(
            LocalDate.of(1970, 1, 1), LocalTime.of(0, 0, 1)
    );

    public static final class CacheNames {
        public static final String VOTE_PAPER = "votePaper";
        public static final String VOTE_CHOICE = "voteChoice";
    }

    public static final class NicknameFragment {

        public static final String[] PREFIX_NAMES = {
                "행복한", "멋진", "멋있는", "귀여운", "예쁜", "잘생긴", "똑똑한", "졸린", "피곤한", "배고픈", "궁금한", "친절한", "평범한",
                "활발한", "조용한", "빠른", "느린", "재빠른", "강한", "영리한", "우아한", "독특한", "재밌는", "발랄한", "따뜻한", "눈부신",
                "명랑한", "빛나는", "깜찍한", "차분한", "이상한", "행운의"
        };

        public static final String[] ANIMAL_NAMES = {
                "사자", "호랑이", "표범", "기린", "코끼리", "코뿔소", "하마", "펭귄", "독수리", "타조", "캥거루", "고래", "칠면조", "직박구리",
                "청설모", "메추라기", "앵무새", "스라소니", "판다", "오소리", "오리", "거위", "백조", "두루미", "고슴도치", "두더지", "우파루파",
                "너구리", "카멜레온", "이구아나", "노루", "제비", "까치", "고라니", "수달", "당나귀", "순록", "염소", "공작", "바다표범", "들소",
                "참새", "물개", "바다사자", "얼룩말", "산양", "카피바라", "북극곰", "퓨마", "코요테", "라마", "딱따구리", "돌고래", "까마귀",
                "낙타", "여우", "사슴", "늑대", "재규어", "알파카", "다람쥐", "담비", "사막여우", "북극여우", "꽃사슴", "해달", "강아지",
                "고양이", "햄스터", "기니피그", "왈라비", "마못", "물범", "토끼", "미어캣", "북극곰", "코알라", "디프만"
        };

    }

}
