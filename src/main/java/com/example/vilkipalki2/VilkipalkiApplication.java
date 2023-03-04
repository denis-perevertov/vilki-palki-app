package com.example.vilkipalki2;

import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.models.Order;
import com.example.vilkipalki2.repos.AppUserRepository;
import com.example.vilkipalki2.repos.CategoryRepository;
import com.example.vilkipalki2.repos.MenuItemRepository;
import com.example.vilkipalki2.repos.OrderRepository;
import com.example.vilkipalki2.services.OrderService;
import com.example.vilkipalki2.services.UserService;
import com.example.vilkipalki2.util.AppUserRole;
import com.example.vilkipalki2.util.Gender;
import com.example.vilkipalki2.util.Language;
import com.example.vilkipalki2.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@Controller
public class VilkipalkiApplication extends SpringBootServletInitializer {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(VilkipalkiApplication.class, args);
	}

	@GetMapping("/test")
	public @ResponseBody String test() {
		return "TEST";
	}

	//заполнение заказами

//	@EventListener(ApplicationReadyEvent.class)
//	public void fillOrders() {
//
//		Random random = new Random();
//		Address address = new Address("testStreet");
//
//		for(int i = 0; i < 101; i++) {
//			Order order = new Order();
//			order.setAddress(address);
//			order.setDatetime(LocalDateTime.of(random.nextInt(2019, 2023),
//					                           random.nextInt(1, 12),
//					                           random.nextInt(1, 28),
//					                           0, 0));
//			order.setUser_id(random.nextInt(9, 32));
//			order.setStatus(OrderStatus.getStatusByNumber(i % 5));
//
//			List<MenuItem> orderItemList = new ArrayList<>();
//
//			for(int j = 0; j < random.nextInt(1, 4); j++) {
//				MenuItem testItem = new MenuItem();
//				testItem.setId(random.nextInt(22, 35));
//				orderItemList.add(testItem);
//			}
//
//			order.setItemList(orderItemList);
//
//			orderService.createOrder(order);
//		}
//	}

}
