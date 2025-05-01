package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Domain.Demo.Entity.Demo;
import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  //앱 서비스를 제공하는 컨틀롤러로, 반환값은 자동으로 json으로 변환된다
@RequestMapping("/api/demo") // 이 컨트롤러의 기본 경로
@RequiredArgsConstructor //final로 생성된 필드를 자동으로 생성자에게 주입
public class DemoController {

    private final DemoRepository demoRepository;

    @PostMapping
    public ResponseEntity<Demo> createDemo(@RequestBody Demo demo) {  //클라이언트가 보낸 json데이터를 demo라는 객체로 자동 매핑해준다.
        Demo saved = demoRepository.save(demo); //받은 데모 객체를 db에 저장
        return ResponseEntity.ok(saved); //저장된 객체를 200응답과 함께 json으로 클라이언트에게 돌려준다.
    }
}
