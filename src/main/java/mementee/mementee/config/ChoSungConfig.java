package mementee.mementee.config;

public class ChoSungConfig {

    //초성 검색 로직
    public static String extractChoSung(String str) {
        StringBuilder choSung = new StringBuilder();

        for (char ch : str.toCharArray()) {
            if (ch >= '가' && ch <= '힣') {
                int uniVal = ch - '가';
                int choSungIndex = uniVal / 588; // 초성 인덱스 계산
                choSung.append((char)('ㄱ' + choSungIndex));
            } else {
                choSung.append(ch);
            }
        }
        return choSung.toString();
    }
}
