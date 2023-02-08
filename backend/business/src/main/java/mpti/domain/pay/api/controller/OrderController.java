package mpti.domain.pay.api.controller;

import mpti.domain.pay.application.KakaoPayService;
import mpti.domain.pay.dto.ApproveResponse;
import mpti.domain.pay.dto.ReadyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@CrossOrigin("http://localhost:8080")
// 세션에 저장된 겂을 사용할때 쓰는 어노테이션, session에서 없으면 model까지 훑어서 찾아냄.
//@SessionAttributes({"tid","order"})
public class OrderController {

    @Autowired
    KakaoPayService kakaopayService;

    private String tid;

    // 카카오페이결제 요청
    @GetMapping("/order/pay")
    public @ResponseBody ReadyResponse payReady(@RequestParam(name = "total_amount") int totalAmount, Model model) throws IOException {

        System.out.println();
        System.out.println("=======================");
        System.out.println("order/pay 시작");
        System.out.println("=======================");
        System.out.println();

        // 카카오 결제 준비하기	- 결제요청 service 실행.
        ReadyResponse readyResponse = kakaopayService.payReady(totalAmount);
        tid = readyResponse.getTid();
        // 요청처리후 받아온 결재고유 번호(tid)를 모델에 저장
//        model.addAttribute("tid", readyResponse.getTid());
//        log.info("결재고유 번호: " + readyResponse.getTid());
        // Order정보를 모델에 저장
//        model.addAttribute("order",order);

        return readyResponse; // 클라이언트에 보냄.(tid,next_redirect_pc_url이 담겨있음.)
    }

    // 결제승인요청
    @GetMapping("/order/pay/completed/{pg_token}")
    public ResponseEntity<ApproveResponse> payCompleted(@PathVariable("pg_token") String pgToken) {

        System.out.println();
        System.out.println("=======================");
        System.out.println("order/pay  // COMPLETED 시작");
        System.out.println("=======================");
        System.out.println();

        System.out.println("pgToken = " + pgToken);
        System.out.println("tid = " + tid);

//        log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
//        log.info("주문정보: " + order);
//        log.info("결재고유 번호: " + tid);

        // 카카오 결재 요청하기
        ApproveResponse approveResponse = kakaopayService.payApprove(tid, pgToken);

        // 5. payment 저장
        //	orderNo, payMathod, 주문명.
        // - 카카오 페이로 넘겨받은 결재정보값을 저장.
//        Payment payment = Payment.builder()
//                .paymentClassName(approveResponse.getItem_name())
//                .payMathod(approveResponse.getPayment_method_type())
//                .payCode(tid)
//                .build();

//        orderService.saveOrder(order,payment);

        return ResponseEntity.ok(approveResponse);
    }
    // 결제 취소시 실행 url
    @GetMapping("/order/pay/cancel")
    public String payCancel() {
        return "redirect:/carts";
    }

    // 결제 실패시 실행 url
    @GetMapping("/order/pay/fail")
    public String payFail() {
        return "redirect:/carts";
    }


}
