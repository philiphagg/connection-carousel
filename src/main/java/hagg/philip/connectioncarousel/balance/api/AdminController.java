package hagg.philip.connectioncarousel.balance.api;

import hagg.philip.connectioncarousel.balance.ServerPool;
import hagg.philip.connectioncarousel.balance.strategy.LoadBalancingStrategy;
import hagg.philip.connectioncarousel.domain.ServiceInstance;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final List<LoadBalancingStrategy> strategies;
    private final ServerPool serverPool;

    public AdminController(List<LoadBalancingStrategy> strategies, ServerPool serverPool) {
        this.strategies = strategies;
        this.serverPool = serverPool;
    }

    @GetMapping("/control-panel")
    public String controlPanel(Model model) {
        model.addAttribute("strategies", strategies);
        model.addAttribute("instances", serverPool.getInstances());
        return "control-panel";
    }

    @PostMapping("/toggle-strategy")
    public String toggleStrategy(@RequestParam String name) {
        strategies.forEach(strategy -> strategy.setActive(strategy.getName().equals(name)));
        return "redirect:/admin/control-panel";
    }

    @PostMapping("/toggle-instance")
    public String toggleInstance(@RequestParam String url) {
        serverPool.getInstances().stream()
                .filter(instance -> instance.getUrl().toString().equals(url))
                .findFirst()
                .ifPresent(instance -> instance.setAlive(!instance.isAlive()));
        return "redirect:/admin/control-panel";
    }


    @PostMapping("/add-instance")
    @SneakyThrows
    public String addInstance(@RequestParam String url) {
        serverPool.addInstance(new ServiceInstance(URI.create(url).toURL()));
        return "redirect:/admin/control-panel";
    }

    @PostMapping("/remove-instance")
    @SneakyThrows
    public String removeInstance(@RequestParam String url) {
        serverPool.removeInstance(String.valueOf(URI.create(url).toURL()));
        return "redirect:/admin/control-panel";
    }
}
