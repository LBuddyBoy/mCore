package dev.minechase.core.mcore_rest;

import dev.minechase.core.rest.CoreApplication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest(classes = CoreApplication.class)
class McoreRestApplicationTests {

	@Test
	void contextLoads() {
		String secret = "test123";
		String jwt = Jwts.builder()
				.setSubject("admin")
				.setIssuedAt(new Date())
//				.setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();

		System.out.println(jwt);
	}

}
