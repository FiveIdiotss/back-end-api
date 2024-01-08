package mementee.mementee;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        public void dbInit() {
            System.out.println("Init1" + this.getClass());
//            Member member = new Member("TEST_MEMBER", "1234", "1234", "1234", "1234");
//            Member member2 = new Member("TEST_MEMBER", "12345", "12345", "1234", "1234");
//
//            Owner owner = new Owner("TEST_OWNER", "1234", "1234", "1234", "1234");
//            Admin admin = new Admin("관리자", "1234", "1234", "1234");
//
//            Hotel hotel = new Hotel("TEST_HOTEL", "1234", "TEST");
//            hotel.setOwner(owner);
//            HotelPhoto hotelPhoto = new HotelPhoto("241", hotel);;
//            HotelPhoto hotelPhoto2 = new HotelPhoto("2414", hotel);
//
//            em.persist(member);
//            em.persist(member2);
//
//            em.persist(owner);
//            em.persist(admin);
//
//            em.persist(hotel);
//
//            em.persist(hotelPhoto);
//            em.persist(hotelPhoto2);

        }
    }
}
