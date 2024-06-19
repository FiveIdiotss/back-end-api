package com.mementee;

import com.mementee.api.domain.Major;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.School;
import com.mementee.api.domain.enumtype.Gender;
import com.mementee.api.dto.memberDTO.CreateMemberRequest;
import com.mementee.api.service.MemberService;
import com.mementee.api.service.SchoolService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;
    private final MemberService memberService;

//    @PostConstruct
//    public void init() {
//        initService.dbInit();
//    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final SchoolService schoolService;

        public void dbInit() {
            System.out.println("Init1" + this.getClass());

            List<String> universities = Arrays.asList(
                    "가야대학교", "가천대학교", "가톨릭관동대학교", "가톨릭대학교", "강남대학교", "강릉원주대학교",
                    "강서대학교", "강원대학교", "건국대학교 GLOCAL캠퍼스", "건국대학교", "건양대학교", "경기대학교",
                    "경남대학교", "경동대학교", "경북대학교", "경상국립대학교", "경성대학교", "경운대학교", "경일대학교",
                    "경희대학교", "계명대학교", "고려대학교 세종캠퍼스", "고려대학교", "고신대학교", "공주대학교",
                    "광운대학교", "광주대학교", "국민대학교", "군산대학교", "극동대학교", "금오공과대학교", "김천대학교",
                    "나사렛대학교", "남부대학교", "남서울대학교", "단국대학교", "대구가톨릭대학교", "대구대학교",
                    "대구한의대학교", "대전대학교", "대진대학교", "동국대학교 WISE캠퍼스", "동국대학교", "동명대학교",
                    "동서대학교", "동신대학교", "동아대학교", "동양대학교", "동의대학교", "명지대학교", "목원대학교",
                    "목포대학교", "목포해양대학교", "배재대학교", "백석대학교", "부경대학교", "부산대학교", "부산외국어대학교",
                    "삼육대학교", "상명대학교", "상지대학교", "서강대학교", "서경대학교", "서울과학기술대학교", "서울대학교",
                    "서울시립대학교", "서원대학교", "선문대학교", "성공회대학교", "성균관대학교", "세명대학교", "세종대학교",
                    "세한대학교", "송원대학교", "수원대학교", "순천대학교", "순천향대학교", "숭실대학교", "신라대학교",
                    "신한대학교", "아주대학교", "안동대학교", "안양대학교", "연세대학교 미래캠퍼스", "연세대학교",
                    "영남대학교", "영산대학교", "용인대학교", "우석대학교", "우송대학교", "울산대학교", "원광대학교",
                    "유원대학교", "을지대학교", "인제대학교", "인천대학교", "인하대학교", "전남대학교", "전북대학교",
                    "전주대학교", "제주국제대학교", "제주대학교", "조선대학교", "중부대학교", "중앙대학교", "중원대학교",
                    "차의과학대학교", "창신대학교", "창원대학교", "청주대학교", "초당대학교", "총신대학교", "충남대학교",
                    "충북대학교", "평택대학교", "포항공과대학교", "한경국립대학교", "한국공학대학교", "한국교통대학교",
                    "한국국제대학교", "한국외국어대학교", "한국항공대학교", "한국해양대학교", "한남대학교", "한동대학교",
                    "한라대학교", "한림대학교", "한밭대학교", "한서대학교", "한성대학교", "한세대학교", "한신대학교",
                    "한양대학교 ERICA캠퍼스", "한양대학교", "협성대학교", "호남대학교", "호서대학교", "홍익대학교",
                    "화성의과학대학교", "광주여자대학교", "덕성여자대학교", "동덕여자대학교", "서울여자대학교", "성신여자대학교",
                    "숙명여자대학교", "이화여자대학교", "가톨릭상지대학교", "강동대학교", "강릉영동대학교", "강원관광대학교",
                    "강원도립대학교", "거제대학교", "경기과학기술대학교", "경남도립거창대학", "경남도립남해대학", "경민대학교",
                    "경복대학교", "경북과학대학교", "경북도립대학교", "경북전문대학교", "계명문화대학교", "계원예술대학교",
                    "고구려대학교", "광양보건대학교", "광주보건대학교", "구미대학교", "국제대학교", "군산간호대학교", "군장대학교",
                    "기독간호대학교", "김포대학교", "김해대학교", "농협대학교", "대경대학교", "대구공업대학교", "대구과학대학교",
                    "대구보건대학교", "대덕대학교", "대림대학교", "대원대학교", "대전과학기술대학교", "대전보건대학교",
                    "동강대학교", "동남보건대학교", "동서울대학교", "동아방송예술대학교", "동아보건대학교", "동양미래대학교",
                    "동원과학기술대학교", "동원대학교", "두원공과대학교", "마산대학교", "명지전문대학", "목포과학대학교",
                    "문경대학교", "백석문화대학교", "백제예술대학교", "부천대학교", "삼육보건대학교", "서라벌대학교", "서영대학교",
                    "서일대학교", "서정대학교", "선린대학교", "성운대학교", "세경대학교", "송곡대학교", "송호대학교", "수성대학교",
                    "수원여자대학교", "순천제일대학교", "신구대학교", "신성대학교", "신안산대학교", "아주자동차대학교",
                    "안동과학대학교", "안산대학교", "여주대학교", "연성대학교", "연암공과대학교", "연암대학교", "영남외국어대학",
                    "영남이공대학교", "영진전문대학교", "오산대학교", "용인예술과학대학교", "우송정보대학", "울산과학대학교",
                    "웅지세무대학교", "원광보건대학교", "유한대학교", "인덕대학교", "인천재능대학교", "인하공업전문대학", "장안대학교",
                    "전남과학대학교", "전남도립대학교", "전북과학대학교", "전주기전대학", "전주비전대학교", "제주관광대학교",
                    "제주한라대학교", "조선간호대학교", "조선이공대학교", "진주보건대학교", "창원문성대학교", "청강문화산업대학교",
                    "청암대학교", "춘해보건대학교", "충남도립대학교", "충북도립대학교", "충북보건과학대학교", "충청대학교",
                    "포항대학교", "한국골프대학교", "한국관광대학교", "한국승강기대학교", "한국영상대학교", "한림성심대학교",
                    "한영대학교", "혜전대학교", "호산대학교", "경인여자대학교", "배화여자대학교", "부산여자대학교", "서울여자간호대학교", "수원여자대학교",
                    "숭의여자대학교", "한양여자대학교", "감리교신학대학교", "광신대학교", "광주가톨릭대학교", "대신대학교", "대전가톨릭대학교", "대전신학대학교",
                    "부산장신대학교", "서울신학대학교", "서울장신대학교", "서울한영대학교", "수원가톨릭대학교", "아신대학교", "영남신학대학교", "영산선학대학교",
                    "인천가톨릭대학교", "장로회신학대학교", "중앙승가대학교", "칼빈대학교", "한국성서대학교", "한일장신대학교", "호남신학대학교", "국방대학교",
                    "육군사관학교", "육군3사관학교", "공군사관학교", "해군사관학교", "국군간호사관학교", "경찰대학", "한국농수산대학",
                    "한국전통문화대학교", "광주과학기술원", "대구경북과학기술원", "울산과학기술원", "한국과학기술원", "한국에너지공과대학교", "ICT폴리텍대학",
                    "한국폴리텍대학", "국제예술대학교", "백석예술대학교", "정화예술대학교"
            );

            Collections.sort(universities);
            for (String u : universities) {
                School school = new School(u);
                em.persist(school);
            }

            School ICT폴리텍대학 = schoolService.findSchoolById(1L);
            School 가야대학교 = schoolService.findSchoolById(2L);
            School 가천대학교 = schoolService.findSchoolById(3L);
            School 서경대학교 = schoolService.findSchoolById(138L);
            School 삼육대학교 = schoolService.findSchoolById(133L);


            List<String> 가천대과목록 = Arrays.asList("국어국문학과", "영어영문학과", "동양어문학과", "유럽어문학과", "법학과", "행정학과",
                    "경영학과", "글로벌경제학과", "관광경영학과", "회계·세무학과", "사회복지학과", "유아교육학과",
                    "보건정책·관리학과", "언론영상광고학과", "경찰·안보학과", "응용통계학과", "교육학과", "특수상담치료학과", "금융수학과",
                    "의상학과", "식품영양학과", "나노-나노물리학", "나노-나노화학", "나노-생명과학", "간호학과", "보건학과", "약학과", "도시계획학과",
                    "조경학과", "건축학과-건축학", "건축학과-건축공학", "건축학과-살내건축학", "산업공학과", "설비·소방공학과-설비공학", "설비·소방공학과-소방방재공학",
                    "기계공학과", "식품생명공학과", "토목환경공학과", "인공지능학과", "신소재공학과", "바이오나노융합학과", "IT융합공학과-컴퓨터공학", "게임영상공학과",
                    "AI·소프트웨어학과", "정보보호학과", "차세대스마트에너지시스템융합학과", "나노-화공생명공학", "나노-전기공학", "나노-전자공학", "나노-에너지IT",
                    "미래형자동차공학전공", "AI·소프트웨어학과", "반도체전공", "배터리공학전공", "바이오헬스의공학전공", "한의학과", "공연예술학과", "조소과",
                    "디자인학과-시각디자인", "디자인학과-산업디자인", "회화과", "음악학과", "체육학과", "융합의과학과", "의학과");

            for (String m : 가천대과목록) {
                Major major = new Major(m, 가천대학교);
                em.persist(major);
            }

            Major major1 = new Major("컴퓨터공학과", ICT폴리텍대학);
            Major major2 = new Major("전기공학과", ICT폴리텍대학);

            Major major3 = new Major("컴퓨터공학과", 가야대학교);
            Major major4 = new Major("건축공학과", 가야대학교);

            Major major5 = new Major("컴퓨터공학과", 서경대학교);
            Major major6 = new Major("메카트로닉스학과", 삼육대학교);

            em.persist(major1);
            em.persist(major2);
            em.persist(major3);
            em.persist(major4);
            em.persist(major5);
            em.persist(major6);
        }
    }
}
