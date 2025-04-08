// ✅ DemoPlayerController.java
package com.example.Demotion.Domain.Demo.Controller;

import com.example.Demotion.Domain.Demo.Repository.DemoRepository;
import com.example.Demotion.Domain.Demo.Entity.Demo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DemoPlayerController {

    private final DemoRepository demoRepository;

    @GetMapping("/embed/{embedCode}")
    public String showDemoPlayer(@PathVariable String embedCode, Model model) {
        Demo demo = demoRepository.findByEmbedCode(embedCode)
                .orElseThrow(() -> new IllegalArgumentException("❌ 유효하지 않은 embedCode 입니다."));

        model.addAttribute("title", demo.getTitle());
        model.addAttribute("embedCode", embedCode);
        return "demo-player"; // templates/demo-player.html
    }
}
