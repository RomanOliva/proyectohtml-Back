package com.example.ms_warframe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=clave-solo-para-pruebas-de-al-menos-32-caracteres")
class MsWarframeApplicationTests {

	@Test
	void contextLoads() {
	}

}
