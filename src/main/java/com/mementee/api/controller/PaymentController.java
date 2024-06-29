package com.mementee.api.controller;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    public PaymentController() {
        IamportClient iamportClient = new IamportClient("0328847288412452",
                "lhpSmGFRJeLvX2ycger4sel2I48qFyYWaZiEcvyBzAYJIx2XqBHvrx1wOtBWIE8JQiViWtvaiZ2Y585U");
    }

    @PostMapping("/verifyIamport/{rsp.imp_uid}")
    public void pay() {

    }


}
